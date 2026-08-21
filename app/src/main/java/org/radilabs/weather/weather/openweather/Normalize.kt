package org.radilabs.weather.weather.openweather

import org.json.JSONObject
import org.radilabs.weather.weather.AirComponents
import org.radilabs.weather.weather.AirQuality
import org.radilabs.weather.weather.CurrentConditions
import org.radilabs.weather.weather.ForecastPoint
import org.radilabs.weather.weather.Location
import org.radilabs.weather.weather.Precipitation
import org.radilabs.weather.weather.WeatherError
import org.radilabs.weather.weather.Wind
import org.radilabs.weather.weather.airQualityCategory
import org.radilabs.weather.weather.mapOpenWeatherCondition

internal fun JSONObject.optFinite(key: String): Double? {
    if (!has(key) || isNull(key)) return null
    val value = optDouble(key, Double.NaN)
    return if (value.isFinite()) value else null
}

internal fun JSONObject.requireObject(key: String, label: String): JSONObject {
    return optJSONObject(key) ?: throw WeatherError(WeatherError.Code.Malformed, "Weather payload missing $label.")
}

internal fun normalizeLocation(root: JSONObject, fallbackName: String): Location {
    val coordSource = root.optJSONObject("coord")
        ?: root.optJSONObject("city")?.optJSONObject("coord")
        ?: throw WeatherError(WeatherError.Code.Malformed, "Weather payload missing coordinates.")
    val lat = coordSource.optFinite("lat")
    val lon = coordSource.optFinite("lon")
    if (lat == null || lon == null) {
        throw WeatherError(WeatherError.Code.Malformed, "Weather payload missing coordinates.")
    }
    val city = root.optJSONObject("city") ?: root
    val sys = root.optJSONObject("sys") ?: JSONObject()
    val name = city.optString("name").ifBlank { root.optString("name") }.ifBlank { fallbackName }
    val country = sequenceOf(city.optString("country"), sys.optString("country"))
        .firstOrNull { it.isNotBlank() }
    val timezone = city.optFinite("timezone") ?: root.optFinite("timezone") ?: 0.0
    return Location(
        displayName = name,
        coordinates = org.radilabs.weather.weather.Coordinates(lat, lon),
        country = country,
        timezoneOffsetSeconds = timezone.toInt(),
    )
}

internal fun normalizeCurrent(root: JSONObject): CurrentConditions {
    val main = root.requireObject("main", "current temperatures")
    val temperatureC = main.optFinite("temp")
    val feelsLikeC = main.optFinite("feels_like")
    val observed = root.optFinite("dt")
    if (temperatureC == null || feelsLikeC == null || observed == null) {
        throw WeatherError(WeatherError.Code.Malformed, "Current weather payload missing required fields.")
    }
    val info = weatherInfo(root)
    val clouds = root.optJSONObject("clouds")?.optFinite("all")
    return CurrentConditions(
        observedAtMs = (observed * 1000).toLong(),
        temperatureC = temperatureC,
        feelsLikeC = feelsLikeC,
        highC = main.optFinite("temp_max"),
        lowC = main.optFinite("temp_min"),
        condition = mapOpenWeatherCondition(info.first),
        conditionText = info.second,
        visibilityM = root.optFinite("visibility"),
        cloudPercent = clouds,
        wind = windFrom(root),
        precipitation = precipitationFrom(root, null),
        humidityPercent = main.optFinite("humidity"),
        pressureHpa = main.optFinite("pressure"),
    )
}

internal fun normalizeForecastPoints(root: JSONObject): List<ForecastPoint> {
    val list = root.optJSONArray("list")
        ?: throw WeatherError(WeatherError.Code.Malformed, "Forecast payload missing list.")
    val points = mutableListOf<ForecastPoint>()
    for (i in 0 until list.length()) {
        val entry = list.optJSONObject(i) ?: continue
        try {
            points.add(normalizeForecastPoint(entry))
        } catch (error: WeatherError) {
            if (error.code == WeatherError.Code.Malformed) continue else throw error
        }
    }
    if (points.isEmpty()) {
        throw WeatherError(WeatherError.Code.Malformed, "Forecast contained no usable points.")
    }
    return points
}

internal fun normalizeAirQuality(root: JSONObject): AirQuality {
    val list = root.optJSONArray("list")
        ?: throw WeatherError(WeatherError.Code.Malformed, "Air quality payload missing list.")
    val first = list.optJSONObject(0)
        ?: throw WeatherError(WeatherError.Code.Malformed, "Air quality payload missing observation.")
    val observed = first.optFinite("dt")
        ?: throw WeatherError(WeatherError.Code.Malformed, "Air quality payload missing time.")
    val aqi = first.optJSONObject("main")?.optFinite("aqi")?.toInt()
        ?: throw WeatherError(WeatherError.Code.Malformed, "Air quality payload missing index.")
    val c = first.optJSONObject("components") ?: JSONObject()
    return AirQuality(
        observedAtMs = (observed * 1000).toLong(),
        openWeatherAqi = aqi,
        category = airQualityCategory(aqi),
        components = AirComponents(
            co = c.optFinite("co"),
            no = c.optFinite("no"),
            no2 = c.optFinite("no2"),
            o3 = c.optFinite("o3"),
            so2 = c.optFinite("so2"),
            pm25 = c.optFinite("pm2_5"),
            pm10 = c.optFinite("pm10"),
            nh3 = c.optFinite("nh3"),
        ),
    )
}

private fun weatherInfo(payload: JSONObject): Pair<Int, String> {
    val weather = payload.optJSONArray("weather")
    if (weather == null || weather.length() == 0) {
        throw WeatherError(WeatherError.Code.Malformed, "Weather payload missing condition list.")
    }
    val first = weather.optJSONObject(0)
        ?: throw WeatherError(WeatherError.Code.Malformed, "Weather payload missing condition.")
    val id = first.optFinite("id")?.toInt()
        ?: throw WeatherError(WeatherError.Code.Malformed, "Weather payload missing condition id.")
    val text = first.optString("description").ifBlank { "unknown" }
    return id to text
}

private fun windFrom(payload: JSONObject): Wind {
    val wind = payload.optJSONObject("wind") ?: return Wind(speedMps = 0.0)
    val speed = wind.optFinite("speed") ?: return Wind(speedMps = 0.0)
    return Wind(
        speedMps = speed,
        directionDeg = wind.optFinite("deg"),
        gustMps = wind.optFinite("gust"),
    )
}

private fun precipitationFrom(payload: JSONObject, pop: Double?): Precipitation {
    val percent = pop?.let { (it * 100.0).coerceIn(0.0, 100.0) }
    fun amount(obj: JSONObject?): Double? {
        if (obj == null) return null
        return obj.optFinite("1h") ?: obj.optFinite("3h")
    }
    return Precipitation(
        probabilityPercent = percent,
        rainMm = amount(payload.optJSONObject("rain")),
        snowMm = amount(payload.optJSONObject("snow")),
    )
}

private fun normalizeForecastPoint(entry: JSONObject): ForecastPoint {
    val main = entry.requireObject("main", "forecast temperatures")
    val temperatureC = main.optFinite("temp")
        ?: throw WeatherError(WeatherError.Code.Malformed, "Forecast point missing temperature.")
    val observed = entry.optFinite("dt")
        ?: throw WeatherError(WeatherError.Code.Malformed, "Forecast point missing time.")
    val info = weatherInfo(entry)
    val pop = entry.optFinite("pop")
    return ForecastPoint(
        atMs = (observed * 1000).toLong(),
        temperatureC = temperatureC,
        feelsLikeC = main.optFinite("feels_like"),
        condition = mapOpenWeatherCondition(info.first),
        conditionText = info.second,
        precipitation = precipitationFrom(entry, pop),
        wind = windFrom(entry),
        humidityPercent = main.optFinite("humidity"),
        pressureHpa = main.optFinite("pressure"),
    )
}

internal fun parseObject(body: String, label: String): JSONObject {
    return try {
        JSONObject(body)
    } catch (error: Exception) {
        throw WeatherError(WeatherError.Code.Malformed, "Weather payload was not $label JSON.", cause = error)
    }
}
