package org.radilabs.weather.weather.openweather

import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
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
        val key = getApiKey()?.trim().orEmpty()
        if (key.isEmpty()) {
            throw WeatherError(WeatherError.Code.MissingKey, "OpenWeather API key is not configured.")
        }
        val currentBody = getJson(OpenWeatherPaths.CURRENT, coordinates, key)
        val forecastBody = getJson(OpenWeatherPaths.FORECAST, coordinates, key)
        val currentJson = parseObject(currentBody, "current")
        val forecastJson = parseObject(forecastBody, "forecast")
        rejectEmbeddedError(currentJson)
        rejectEmbeddedError(forecastJson)
        val location = normalizeLocation(currentJson, "Stockholm")
        val current = normalizeCurrent(currentJson)
        val points = normalizeForecastPoints(forecastJson)
        val days = aggregateDaily(points, location.timezoneOffsetSeconds)
        val air = try {
            val airJson = parseObject(getJson(OpenWeatherPaths.AIR, coordinates, key), "air")
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

    private fun getJson(path: String, coordinates: Coordinates, apiKey: String): String {
        val url = origin.newBuilder()
            .encodedPath(path)
            .addQueryParameter("lat", coordinates.latitude.toString())
            .addQueryParameter("lon", coordinates.longitude.toString())
            .addQueryParameter("units", "metric")
            .addQueryParameter("appid", apiKey)
            .build()
        val request = Request.Builder().url(url).get().build()
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
