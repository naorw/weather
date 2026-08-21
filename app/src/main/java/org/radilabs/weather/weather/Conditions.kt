package org.radilabs.weather.weather

private val severity = mapOf(
    ConditionCategory.Thunderstorm to 100,
    ConditionCategory.HeavyRain to 90,
    ConditionCategory.Snow to 85,
    ConditionCategory.Rain to 80,
    ConditionCategory.LightSnow to 75,
    ConditionCategory.LightRain to 70,
    ConditionCategory.Drizzle to 60,
    ConditionCategory.Fog to 40,
    ConditionCategory.Overcast to 30,
    ConditionCategory.Cloudy to 25,
    ConditionCategory.PartlyCloudy to 20,
    ConditionCategory.Clear to 10,
    ConditionCategory.Unknown to 0,
)

fun mapOpenWeatherCondition(id: Int): ConditionCategory {
    return when {
        id in 200 until 300 -> ConditionCategory.Thunderstorm
        id in 300 until 400 -> ConditionCategory.Drizzle
        id == 500 || id == 520 -> ConditionCategory.LightRain
        id == 502 || id == 503 || id == 504 || id == 522 || id == 531 -> ConditionCategory.HeavyRain
        id in 500 until 600 -> ConditionCategory.Rain
        id == 600 || id == 612 || id == 620 -> ConditionCategory.LightSnow
        id in 600 until 700 -> ConditionCategory.Snow
        id in 700 until 800 -> ConditionCategory.Fog
        id == 800 -> ConditionCategory.Clear
        id == 801 -> ConditionCategory.PartlyCloudy
        id == 802 || id == 803 -> ConditionCategory.Cloudy
        id == 804 -> ConditionCategory.Overcast
        else -> ConditionCategory.Unknown
    }
}

fun pickRepresentativeCondition(categories: List<ConditionCategory>): ConditionCategory {
    if (categories.isEmpty()) return ConditionCategory.Unknown
    return categories.maxBy { severity[it] ?: 0 }
}

fun airQualityCategory(aqi: Int): AirQualityCategory {
    return when (aqi) {
        1 -> AirQualityCategory.Good
        2 -> AirQualityCategory.Fair
        3 -> AirQualityCategory.Moderate
        4 -> AirQualityCategory.Poor
        5 -> AirQualityCategory.VeryPoor
        else -> AirQualityCategory.Unknown
    }
}
