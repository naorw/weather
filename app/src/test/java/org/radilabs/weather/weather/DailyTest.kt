package org.radilabs.weather.weather

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

import java.time.Instant

class DailyTest {
    @Test
    fun groupsByLocationOffsetNotPhoneZone() {
        val offset = 7200
        val points = listOf(
            point(Instant.parse("2024-08-21T18:00:00Z").toEpochMilli(), 13.0, ConditionCategory.Rain, 40.0, 0.31),
            point(Instant.parse("2024-08-21T21:00:00Z").toEpochMilli(), 11.0, ConditionCategory.Overcast, 10.0, null),
            point(Instant.parse("2024-08-22T00:00:00Z").toEpochMilli(), 10.0, ConditionCategory.Clear, 0.0, null),
        )
        val days = aggregateDaily(points, offset)
        assertEquals(listOf("2024-08-21", "2024-08-22"), days.map { it.localDate })
        val first = days[0]
        assertEquals(13.0, first.highC, 0.0)
        assertEquals(11.0, first.lowC, 0.0)
        assertEquals(ConditionCategory.Rain, first.condition)
        assertEquals(40.0, first.precipitation.probabilityPercent)
        assertEquals(0.31, first.precipitation.rainMm)
        assertTrue(first.partial)
        assertTrue(days[1].partial)
        assertEquals(8, COMPLETE_DAY_POINTS)
    }

    private fun point(
        atMs: Long,
        temp: Double,
        condition: ConditionCategory,
        pop: Double?,
        rain: Double?,
    ) = ForecastPoint(
        atMs = atMs,
        temperatureC = temp,
        condition = condition,
        conditionText = "x",
        precipitation = Precipitation(probabilityPercent = pop, rainMm = rain),
        wind = Wind(0.0),
    )
}
