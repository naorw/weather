package org.radilabs.weather.ui.today

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.radilabs.weather.weather.AirQuality
import org.radilabs.weather.weather.AirQualityCategory
import org.radilabs.weather.weather.ConditionCategory
import org.radilabs.weather.weather.Coordinates
import org.radilabs.weather.weather.CurrentConditions
import org.radilabs.weather.weather.DailySummary
import org.radilabs.weather.weather.ForecastPoint
import org.radilabs.weather.weather.Location
import org.radilabs.weather.weather.Precipitation
import org.radilabs.weather.weather.WeatherSnapshot
import org.radilabs.weather.weather.Wind
import org.radilabs.weather.weather.AirComponents
import java.time.Instant

class PresentTest {
    @Test
    fun partialDayLabelIsMutedPart() {
        assertEquals("part.", partialDayLabel(true))
        assertEquals("", partialDayLabel(false))
    }

    @Test
    fun omittedPrecipIsDashNotZero() {
        assertEquals("—", formatPrecipChance(null))
        assertEquals("0%", formatPrecipChance(0))
    }

    @Test
    fun mapsSnapshotWithoutCallingHourly() {
        val snapshot = presentSnapshot(sample())
        assertEquals("Stockholm SE", snapshot.location)
        assertEquals("20", snapshot.hours[0].hour)
        assertEquals(40, snapshot.hours[0].precipChance)
        assertNull(snapshot.hours[1].precipChance)
        assertEquals("21", snapshot.days[0].date)
        assertTrue(snapshot.days[0].partial)
        assertEquals("Fair", snapshot.airQuality?.category)
        assertEquals(2, snapshot.airQuality?.index)
        assertEquals(240.0, snapshot.windDeg)
    }

    @Test
    fun unknownGlyphForUnknownCondition() {
        assertEquals(GlyphId.Unknown, ConditionCategory.Unknown.toGlyph())
        assertEquals(GlyphId.Thunderstorm, ConditionCategory.Thunderstorm.toGlyph())
    }

    private fun sample(): WeatherSnapshot {
        val at = Instant.parse("2024-08-21T18:00:00Z").toEpochMilli()
        return WeatherSnapshot(
            location = Location("Stockholm", Coordinates(59.3293, 18.0686), "SE", 7200),
            current = CurrentConditions(
                observedAtMs = at,
                temperatureC = 14.2,
                feelsLikeC = 13.1,
                highC = 15.4,
                lowC = 12.8,
                condition = ConditionCategory.Cloudy,
                conditionText = "broken clouds",
                visibilityM = 10000.0,
                wind = Wind(4.1, 240.0, 7.2),
                precipitation = Precipitation(rainMm = 0.2),
                humidityPercent = 72.0,
                pressureHpa = 1014.0,
            ),
            points = listOf(
                ForecastPoint(
                    atMs = at,
                    temperatureC = 13.5,
                    condition = ConditionCategory.LightRain,
                    conditionText = "light rain",
                    precipitation = Precipitation(40.0, 0.31, null),
                    wind = Wind(3.2),
                ),
                ForecastPoint(
                    atMs = at + 3 * 3600_000,
                    temperatureC = 11.0,
                    condition = ConditionCategory.Overcast,
                    conditionText = "overcast",
                    precipitation = Precipitation(),
                    wind = Wind(2.0),
                ),
            ),
            days = listOf(
                DailySummary("2024-08-21", 13.5, 11.0, ConditionCategory.LightRain, Precipitation(40.0), 2, true),
            ),
            airQuality = AirQuality(
                observedAtMs = at,
                openWeatherAqi = 2,
                category = AirQualityCategory.Fair,
                components = AirComponents(),
            ),
            fetchedAtMs = 1L,
        )
    }
}
