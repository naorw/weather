package org.radilabs.weather.weather

import java.time.Instant
import java.time.ZoneOffset

data class Coordinates(
    val latitude: Double,
    val longitude: Double,
)

val STOCKHOLM = Coordinates(latitude = 59.3293, longitude = 18.0686)

enum class ConditionCategory {
    Clear,
    PartlyCloudy,
    Cloudy,
    Overcast,
    Drizzle,
    LightRain,
    Rain,
    HeavyRain,
    Thunderstorm,
    LightSnow,
    Snow,
    Fog,
    Unknown,
}

data class Location(
    val displayName: String,
    val coordinates: Coordinates,
    val country: String? = null,
    val timezoneOffsetSeconds: Int,
)

data class Wind(
    val speedMps: Double,
    val directionDeg: Double? = null,
    val gustMps: Double? = null,
)

data class Precipitation(
    val probabilityPercent: Double? = null,
    val rainMm: Double? = null,
    val snowMm: Double? = null,
)

data class CurrentConditions(
    val observedAtMs: Long,
    val temperatureC: Double,
    val feelsLikeC: Double,
    val highC: Double? = null,
    val lowC: Double? = null,
    val condition: ConditionCategory,
    val conditionText: String,
    val visibilityM: Double? = null,
    val cloudPercent: Double? = null,
    val wind: Wind,
    val precipitation: Precipitation,
    val humidityPercent: Double? = null,
    val pressureHpa: Double? = null,
)

data class ForecastPoint(
    val atMs: Long,
    val temperatureC: Double,
    val feelsLikeC: Double? = null,
    val condition: ConditionCategory,
    val conditionText: String,
    val precipitation: Precipitation,
    val wind: Wind,
    val humidityPercent: Double? = null,
    val pressureHpa: Double? = null,
)

data class DailySummary(
    val localDate: String,
    val highC: Double,
    val lowC: Double,
    val condition: ConditionCategory,
    val precipitation: Precipitation,
    val pointCount: Int,
    val partial: Boolean,
)

data class AirQuality(
    val observedAtMs: Long,
    val openWeatherAqi: Int,
    val category: AirQualityCategory,
    val components: AirComponents,
)

enum class AirQualityCategory {
    Good,
    Fair,
    Moderate,
    Poor,
    VeryPoor,
    Unknown,
}

data class AirComponents(
    val co: Double? = null,
    val no: Double? = null,
    val no2: Double? = null,
    val o3: Double? = null,
    val so2: Double? = null,
    val pm25: Double? = null,
    val pm10: Double? = null,
    val nh3: Double? = null,
)

data class WeatherSnapshot(
    val location: Location,
    val current: CurrentConditions,
    val points: List<ForecastPoint>,
    val days: List<DailySummary>,
    val airQuality: AirQuality? = null,
    val fetchedAtMs: Long,
)

fun localDateKey(atMs: Long, timezoneOffsetSeconds: Int): String {
    return Instant.ofEpochMilli(atMs)
        .atOffset(ZoneOffset.ofTotalSeconds(timezoneOffsetSeconds))
        .toLocalDate()
        .toString()
}
