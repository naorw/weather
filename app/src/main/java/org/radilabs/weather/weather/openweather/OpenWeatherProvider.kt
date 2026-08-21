package org.radilabs.weather.weather.openweather

import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import org.radilabs.weather.places.Place
import org.radilabs.weather.places.PlaceSource
import org.radilabs.weather.places.placeFromCoordinates
import org.radilabs.weather.weather.Coordinates
import org.radilabs.weather.weather.WeatherError
import org.radilabs.weather.weather.WeatherProvider
import org.radilabs.weather.weather.WeatherSnapshot
import org.radilabs.weather.weather.aggregateDaily
import org.radilabs.weather.weather.errorFromHttpStatus
import org.radilabs.weather.weather.redactSecrets
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

class OpenWeatherProvider(
    private val getApiKey: () -> String?,
    private val http: OkHttpClient = defaultClient(),
    private val origin: HttpUrl = "https://api.openweathermap.org".toHttpUrl(),
    private val nowMs: () -> Long = { System.currentTimeMillis() },
) : WeatherProvider {

    override fun getSnapshot(coordinates: Coordinates): WeatherSnapshot {
        val key = requireKey()
        val currentBody = getJson(OpenWeatherPaths.CURRENT, weatherParams(coordinates), key)
        val forecastBody = getJson(OpenWeatherPaths.FORECAST, weatherParams(coordinates), key)
        val currentJson = parseObject(currentBody, "current")
        val forecastJson = parseObject(forecastBody, "forecast")
        rejectEmbeddedError(currentJson)
        rejectEmbeddedError(forecastJson)
        val location = normalizeLocation(currentJson, "Stockholm")
        val current = normalizeCurrent(currentJson)
        val points = normalizeForecastPoints(forecastJson)
        val days = aggregateDaily(points, location.timezoneOffsetSeconds)
        val air = try {
            val airJson = parseObject(getJson(OpenWeatherPaths.AIR, weatherParams(coordinates), key), "air")
            rejectEmbeddedError(airJson)
            normalizeAirQuality(airJson)
        } catch (error: WeatherError) {
            if (error.code == WeatherError.Code.Auth || error.code == WeatherError.Code.MissingKey) throw error
            null
        }
        return WeatherSnapshot(
            location = location,
            current = current,
            points = points,
            days = days,
            airQuality = air,
            fetchedAtMs = nowMs(),
        )
    }

    override fun searchPlaces(query: String): List<Place> {
        val key = requireKey()
        val q = query.trim()
        if (q.isEmpty()) return emptyList()
        val body = getJson(
            OpenWeatherPaths.GEO_DIRECT,
            mapOf("q" to q, "limit" to "5"),
            key,
        )
        return normalizeGeoHits(parseArray(body, "geocoding"), PlaceSource.Search)
    }

    override fun reverseGeocode(coordinates: Coordinates): Place {
        val key = requireKey()
        val body = getJson(
            OpenWeatherPaths.GEO_REVERSE,
            mapOf(
                "lat" to coordinates.latitude.toString(),
                "lon" to coordinates.longitude.toString(),
                "limit" to "1",
            ),
            key,
        )
        val hits = normalizeGeoHits(parseArray(body, "reverse geocoding"), PlaceSource.Device)
        return hits.firstOrNull() ?: placeFromCoordinates(
            latitude = coordinates.latitude,
            longitude = coordinates.longitude,
            displayName = "Device location",
            source = PlaceSource.Device,
        )
    }

    private fun requireKey(): String {
        val key = getApiKey()?.trim().orEmpty()
        if (key.isEmpty()) {
            throw WeatherError(WeatherError.Code.MissingKey, "OpenWeather API key is not configured.")
        }
        return key
    }

    private fun weatherParams(coordinates: Coordinates): Map<String, String> {
        return mapOf(
            "lat" to coordinates.latitude.toString(),
            "lon" to coordinates.longitude.toString(),
            "units" to "metric",
        )
    }

    private fun getJson(path: String, query: Map<String, String>, apiKey: String): String {
        val builder = origin.newBuilder().encodedPath(path)
        query.forEach { (name, value) -> builder.addQueryParameter(name, value) }
        builder.addQueryParameter("appid", apiKey)
        val request = Request.Builder().url(builder.build()).get().build()
        val response = try {
            http.newCall(request).execute()
        } catch (error: IOException) {
            throw mapTransport(error)
        }
        response.use { body ->
            val status = body.code
            val text = try {
                body.body?.string().orEmpty()
            } catch (error: IOException) {
                throw mapTransport(error)
            }
            if (status == 401 || status == 403 || status == 404 || status == 429 || status >= 500) {
                throw errorFromHttpStatus(status)
            }
            if (!body.isSuccessful) {
                throw WeatherError(
                    WeatherError.Code.Unknown,
                    redactSecrets("Weather provider returned HTTP $status."),
                    status,
                )
            }
            return text
        }
    }

    private fun rejectEmbeddedError(root: JSONObject) {
        val raw = if (root.has("cod") && !root.isNull("cod")) root.opt("cod") else return
        val status = raw?.toString()?.toIntOrNull() ?: return
        if (status != 200) throw errorFromHttpStatus(status)
    }

    companion object {
        fun defaultClient(): OkHttpClient {
            return OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .callTimeout(25, TimeUnit.SECONDS)
                .build()
        }

        private fun mapTransport(error: IOException): WeatherError {
            val timeout = error is SocketTimeoutException ||
                error is java.io.InterruptedIOException ||
                error.message?.contains("timeout", ignoreCase = true) == true
            return if (timeout) {
                WeatherError(WeatherError.Code.Timeout, "Weather provider request timed out.", cause = error)
            } else {
                WeatherError(WeatherError.Code.Network, "Network unavailable for weather request.", cause = error)
            }
        }
    }
}
