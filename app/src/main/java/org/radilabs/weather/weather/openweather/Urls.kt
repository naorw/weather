package org.radilabs.weather.weather.openweather

internal object OpenWeatherPaths {
    const val CURRENT = "/data/2.5/weather"
    const val FORECAST = "/data/2.5/forecast"
    const val AIR = "/data/2.5/air_pollution"
}

internal fun weatherQuery(lat: Double, lon: Double, apiKey: String): String {
    return "lat=$lat&lon=$lon&units=metric&appid=$apiKey"
}
