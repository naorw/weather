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
    val precipChance: Int,
)

data class DayPoint(
    val day: String,
    val condition: GlyphId,
    val precipChance: Int,
    val lowC: Int,
    val highC: Int,
    val partial: Boolean = false,
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
    val hours: List<HourPoint>,
    val days: List<DayPoint>,
    val wind: String,
    val precipitation: String,
    val humidity: String,
    val pressure: String,
    val visibility: String,
)

val sampleStockholm = TodaySnapshot(
    location = "Stockholm SE",
    temperatureC = 14,
    condition = "Broken cloud",
    conditionId = GlyphId.PartlyCloudy,
    highC = 16,
    lowC = 9,
    feelsLikeC = 12,
    updatedAt = "21:40 CEST",
    hours = listOf(
        HourPoint("22", 14, GlyphId.PartlyCloudy, 10),
        HourPoint("23", 13, GlyphId.Overcast, 20),
        HourPoint("00", 12, GlyphId.Overcast, 25),
        HourPoint("01", 11, GlyphId.Drizzle, 40),
        HourPoint("02", 11, GlyphId.Rain, 55),
        HourPoint("03", 10, GlyphId.Rain, 50),
        HourPoint("04", 10, GlyphId.Overcast, 30),
        HourPoint("05", 9, GlyphId.PartlyCloudy, 15),
    ),
    days = listOf(
        DayPoint("Fri", GlyphId.PartlyCloudy, 20, 9, 16, partial = true),
        DayPoint("Sat", GlyphId.Rain, 70, 8, 13),
        DayPoint("Sun", GlyphId.Overcast, 40, 7, 14),
        DayPoint("Mon", GlyphId.Clear, 5, 8, 18),
        DayPoint("Tue", GlyphId.Drizzle, 35, 10, 17, partial = true),
    ),
    wind = "WSW 4 m/s",
    precipitation = "0.2 mm",
    humidity = "72%",
    pressure = "1014 hPa",
    visibility = "18 km",
)
