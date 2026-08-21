package org.radilabs.weather.weather

import org.junit.Assert.assertEquals
import org.junit.Test

class ConditionsTest {
    @Test
    fun mapsKnownOpenWeatherIds() {
        assertEquals(ConditionCategory.Thunderstorm, mapOpenWeatherCondition(201))
        assertEquals(ConditionCategory.Drizzle, mapOpenWeatherCondition(300))
        assertEquals(ConditionCategory.LightRain, mapOpenWeatherCondition(500))
        assertEquals(ConditionCategory.HeavyRain, mapOpenWeatherCondition(502))
        assertEquals(ConditionCategory.Rain, mapOpenWeatherCondition(501))
        assertEquals(ConditionCategory.LightSnow, mapOpenWeatherCondition(600))
        assertEquals(ConditionCategory.Snow, mapOpenWeatherCondition(601))
        assertEquals(ConditionCategory.Fog, mapOpenWeatherCondition(741))
        assertEquals(ConditionCategory.Clear, mapOpenWeatherCondition(800))
        assertEquals(ConditionCategory.PartlyCloudy, mapOpenWeatherCondition(801))
        assertEquals(ConditionCategory.Cloudy, mapOpenWeatherCondition(803))
        assertEquals(ConditionCategory.Overcast, mapOpenWeatherCondition(804))
        assertEquals(ConditionCategory.Unknown, mapOpenWeatherCondition(999))
    }

    @Test
    fun representativeConditionUsesSeverity() {
        assertEquals(
            ConditionCategory.Thunderstorm,
            pickRepresentativeCondition(
                listOf(ConditionCategory.Clear, ConditionCategory.Thunderstorm, ConditionCategory.Rain),
            ),
        )
    }

    @Test
    fun airQualityScaleIsOpenWeatherNotEpa() {
        assertEquals(AirQualityCategory.Fair, airQualityCategory(2))
        assertEquals(AirQualityCategory.Unknown, airQualityCategory(9))
    }
}
