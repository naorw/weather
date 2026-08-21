package org.radilabs.weather.cache

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.radilabs.weather.weather.ConditionCategory
import org.radilabs.weather.weather.Coordinates
import org.radilabs.weather.weather.CurrentConditions
import org.radilabs.weather.weather.Location
import org.radilabs.weather.weather.Precipitation
import org.radilabs.weather.weather.WeatherSnapshot
import org.radilabs.weather.weather.Wind
import java.io.File
import kotlin.io.path.createTempDirectory

class SnapshotCacheTest {
    @Test
    fun writesAndReadsPerKeyAndDropsIncompatibleSchema() {
        val dir = createTempDirectory("wx-cache").toFile()
        val cache = FileSnapshotCache(dir)
        val a = record("59.3293:18.0686", 14.0)
        val b = record("48.8566:2.3522", 22.0)
        cache.write(a)
        cache.write(b)
        assertEquals(14.0, cache.read(a.cacheKey)!!.snapshot.current.temperatureC, 0.0)
        assertEquals(22.0, cache.read(b.cacheKey)!!.snapshot.current.temperatureC, 0.0)
        val bad = File(dir, "59.3293_18.0686.json")
        bad.writeText(gsonRecord(schema = 99))
        assertNull(cache.read(a.cacheKey))
    }

    @Test
    fun rejectsNonFiniteTemperature() {
        val cache = MemorySnapshotCache()
        cache.write(record("1.0000:2.0000", Double.NaN))
        assertNull(cache.read("1.0000:2.0000"))
    }
}

class StalenessTest {
    @Test
    fun classifiesCachedAndStale() {
        val now = 1_000_000L
        assertEquals(Freshness.Live, classifyFreshness(now - 10, now, live = true))
        assertEquals(Freshness.Cached, classifyFreshness(now - 60_000, now, live = false))
        assertEquals(Freshness.Stale, classifyFreshness(now - FRESH_MS, now, live = false))
        assertEquals(Freshness.Missing, classifyFreshness(null, now, live = false))
        assertEquals("STALE · UPDATED 47 MIN AGO", freshnessLabel(Freshness.Stale, now - 47 * 60_000, now))
    }
}

fun record(key: String, temp: Double) = CachedWeather(
    cacheKey = key,
    schemaVersion = CACHE_SCHEMA_VERSION,
    provider = CACHE_PROVIDER,
    fetchedAtMs = 10L,
    snapshot = WeatherSnapshot(
        location = Location("X", Coordinates(1.0, 2.0), timezoneOffsetSeconds = 0),
        current = CurrentConditions(
            observedAtMs = 10L,
            temperatureC = temp,
            feelsLikeC = temp,
            condition = ConditionCategory.Clear,
            conditionText = "clear",
            wind = Wind(1.0),
            precipitation = Precipitation(),
        ),
        points = emptyList(),
        days = emptyList(),
        fetchedAtMs = 10L,
    ),
)

private fun gsonRecord(schema: Int): String {
    return """{"cacheKey":"59.3293:18.0686","schemaVersion":$schema,"provider":"openweather","fetchedAtMs":10,"snapshot":{"location":{"displayName":"X","coordinates":{"latitude":1,"longitude":2},"timezoneOffsetSeconds":0},"current":{"observedAtMs":10,"temperatureC":14.0,"feelsLikeC":14.0,"condition":"Clear","conditionText":"clear","wind":{"speedMps":1.0},"precipitation":{}},"points":[],"days":[],"fetchedAtMs":10}}"""
}
