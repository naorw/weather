package org.radilabs.weather.ui.today

enum class GlyphId {
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

data class HourPoint(
    val hour: String,
    val tempC: Int,
    val condition: GlyphId,
    val precipChance: Int?,
)

data class DayPoint(
    val day: String,
    val date: String,
    val condition: GlyphId,
    val precipChance: Int?,
    val lowC: Int,
    val highC: Int,
    val partial: Boolean = false,
)

data class AirQualityView(
    val index: Int,
    val category: String,
)

data class TodaySnapshot(
    val location: String,
    val temperatureC: Int,
    val condition: String,
    val conditionId: GlyphId,
    val highC: Int?,
    val lowC: Int?,
    val feelsLikeC: Int,
    val updatedAt: String,
    val humidity: String,
    val pressure: String,
    val hours: List<HourPoint>,
    val days: List<DayPoint>,
    val wind: String,
    val windDeg: Double?,
    val precipitation: String,
    val visibility: String,
    val airQuality: AirQualityView?,
)

fun partialDayLabel(partial: Boolean): String = if (partial) "part." else ""

fun formatPrecipChance(percent: Int?): String = percent?.let { "$it%" } ?: "—"

fun formatDegree(value: Int?): String = if (value == null) "—" else "$value°"

fun airQualityLabel(category: org.radilabs.weather.weather.AirQualityCategory): String {
    return when (category) {
        org.radilabs.weather.weather.AirQualityCategory.Good -> "Good"
        org.radilabs.weather.weather.AirQualityCategory.Fair -> "Fair"
        org.radilabs.weather.weather.AirQualityCategory.Moderate -> "Moderate"
        org.radilabs.weather.weather.AirQualityCategory.Poor -> "Poor"
        org.radilabs.weather.weather.AirQualityCategory.VeryPoor -> "Very poor"
        org.radilabs.weather.weather.AirQualityCategory.Unknown -> "Unknown"
    }
}
