package org.radilabs.weather.session

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.radilabs.weather.cache.MemorySnapshotCache
import org.radilabs.weather.cache.record
import org.radilabs.weather.places.MemoryPlaceCatalog
import org.radilabs.weather.places.Place
import org.radilabs.weather.places.PlaceSource
import org.radilabs.weather.places.placeFromCoordinates
import org.radilabs.weather.weather.ConditionCategory
import org.radilabs.weather.weather.Coordinates
import org.radilabs.weather.weather.CurrentConditions
import org.radilabs.weather.weather.Location
import org.radilabs.weather.weather.Precipitation
import org.radilabs.weather.weather.WeatherError
import org.radilabs.weather.weather.WeatherProvider
import org.radilabs.weather.weather.WeatherSnapshot
import org.radilabs.weather.weather.Wind

class WeatherSessionTest {
    @Test
    fun laterCityWinsRace() {
        val a = placeFromCoordinates(59.3293, 18.0686, "Stockholm", source = PlaceSource.Saved)
        val b = placeFromCoordinates(48.8566, 2.3522, "Paris", source = PlaceSource.Saved)
        val catalog = MemoryPlaceCatalog(initialActive = a)
        val cache = MemorySnapshotCache()
        val session = WeatherSession(catalog, cache, UnusedProvider, nowMs = { 1_000L })
        val genA = session.beginGeneration(a)
        catalog.setActive(b)
        val genB = session.beginGeneration(b)
        assertNull(session.applySuccess(genA, a, snap(14.0)))
        val view = session.applySuccess(genB, b, snap(22.0))
        assertNotNull(view)
        assertEquals(b.cacheKey, view!!.place.cacheKey)
        assertEquals(22, view.snapshot!!.temperatureC)
        assertNull(cache.read(a.cacheKey))
        assertEquals(22.0, cache.read(b.cacheKey)!!.snapshot.current.temperatureC, 0.0)
    }

    @Test
    fun failedRefreshKeepsCache() {
        val place = placeFromCoordinates(59.3293, 18.0686, "Stockholm", source = PlaceSource.Default)
        val cache = MemorySnapshotCache()
        cache.write(record(place.cacheKey, 14.0))
        val session = WeatherSession(MemoryPlaceCatalog(place), cache, UnusedProvider, nowMs = { 1_000L })
        val gen = session.beginGeneration(place)
        val view = session.applyFailure(gen, place, WeatherError(WeatherError.Code.Network, "offline"))
        assertEquals(14, view!!.snapshot!!.temperatureC)
        assertEquals(14.0, cache.read(place.cacheKey)!!.snapshot.current.temperatureC, 0.0)
        assertEquals("OFFLINE · refresh failed", view.note)
    }

    @Test
    fun startupShowsCacheThenLive() {
        val place = placeFromCoordinates(59.3293, 18.0686, "Stockholm", source = PlaceSource.Default)
        val cache = MemorySnapshotCache()
        cache.write(record(place.cacheKey, 11.0))
        val session = WeatherSession(MemoryPlaceCatalog(place), cache, UnusedProvider, nowMs = { 60_000L })
        val cached = session.viewFromCache(place, acquiring = true)
        assertEquals(11, cached.snapshot!!.temperatureC)
        assertEquals("ACQUIRING WEATHER", cached.statusLine)
        val gen = session.beginGeneration(place)
        val live = session.applySuccess(gen, place, snap(12.0).copy(fetchedAtMs = 60_000L))
        assertEquals("LIVE", live!!.statusLine)
        assertEquals(12, live.snapshot!!.temperatureC)
    }
}

private object UnusedProvider : WeatherProvider {
    override fun getSnapshot(coordinates: Coordinates) = error("unused")
    override fun searchPlaces(query: String) = emptyList<Place>()
    override fun reverseGeocode(coordinates: Coordinates) = error("unused")
}

private fun snap(temp: Double) = WeatherSnapshot(
    location = Location("X", Coordinates(1.0, 2.0), timezoneOffsetSeconds = 0),
    current = CurrentConditions(
        observedAtMs = 1L,
        temperatureC = temp,
        feelsLikeC = temp,
        condition = ConditionCategory.Clear,
        conditionText = "clear",
        wind = Wind(1.0),
        precipitation = Precipitation(),
    ),
    points = emptyList(),
    days = emptyList(),
    fetchedAtMs = 1L,
)
