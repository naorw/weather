package org.radilabs.weather.weather.openweather

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.radilabs.weather.weather.ConditionCategory
import org.radilabs.weather.weather.WeatherError

class NormalizeTest {
    @Test
    fun currentMapsMetricUnitsAndCondition() {
        val current = normalizeCurrent(parseObject(readFixture("current.json"), "current"))
        assertEquals(14.2, current.temperatureC, 0.0)
        assertEquals(13.1, current.feelsLikeC, 0.0)
        assertEquals(4.1, current.wind.speedMps, 0.0)
        assertEquals(0.2, current.precipitation.rainMm)
        assertEquals(ConditionCategory.Cloudy, current.condition)
        assertEquals("broken clouds", current.conditionText)
        assertEquals(1_724_260_800_000L, current.observedAtMs)
        assertEquals(1014.0, current.pressureHpa)
        assertEquals(10000.0, current.visibilityM)
    }

    @Test
    fun forecastSkipsMalformedPoints() {
        val points = normalizeForecastPoints(parseObject(readFixture("forecast.json"), "forecast"))
        assertEquals(3, points.size)
        assertEquals(ConditionCategory.LightRain, points[0].condition)
        assertEquals(40.0, points[0].precipitation.probabilityPercent)
        assertEquals(0.31, points[0].precipitation.rainMm)
        assertEquals(0.0, points[2].precipitation.probabilityPercent)
    }

    @Test
    fun airQualityIsOpenWeatherIndex() {
        val air = normalizeAirQuality(parseObject(readFixture("air.json"), "air"))
        assertEquals(2, air.openWeatherAqi)
        assertEquals(org.radilabs.weather.weather.AirQualityCategory.Fair, air.category)
        assertEquals(4.12, air.components.pm25)
    }

    @Test
    fun malformedCurrentThrows() {
        try {
            normalizeCurrent(parseObject("{\"main\":{}}", "current"))
            throw AssertionError("expected malformed")
        } catch (error: WeatherError) {
            assertEquals(WeatherError.Code.Malformed, error.code)
        }
    }

    private fun readFixture(name: String): String {
        val stream = requireNotNull(javaClass.classLoader?.getResourceAsStream("openweather/$name")) {
            "missing fixture $name"
        }
        return stream.bufferedReader().readText()
    }
}
