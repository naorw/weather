package org.radilabs.weather.places

import org.junit.Assert.assertEquals
import org.junit.Test

class LocationIdentityTest {
    @Test
    fun cacheKeyIsFourDecimalPlaces() {
        assertEquals("59.3293:18.0686", locationCacheKey(59.3293, 18.0686))
        assertEquals("59.3293:18.0686", locationCacheKey(59.32934, 18.06855))
    }

    @Test
    fun catalogDedupesAndFallsBack() {
        val catalog = MemoryPlaceCatalog()
        val first = catalog.save(placeFromCoordinates(59.3293, 18.0686, "Stockholm", "SE", source = PlaceSource.Search))
        catalog.save(placeFromCoordinates(59.32934, 18.06855, "Stockholm again", "SE", source = PlaceSource.Search))
        assertEquals(1, catalog.saved().size)
        assertEquals("Stockholm again", catalog.saved().single().displayName)
        val other = catalog.save(placeFromCoordinates(48.8566, 2.3522, "Paris", "FR", source = PlaceSource.Search))
        assertEquals(2, catalog.saved().size)
        catalog.remove(other.cacheKey)
        assertEquals(first.cacheKey, catalog.active().cacheKey)
        catalog.remove(catalog.active().cacheKey)
        assertEquals(STOCKHOLM_PLACE.cacheKey, catalog.active().cacheKey)
        assertEquals(emptyList<Place>(), catalog.saved())
    }

    @Test
    fun devicePlaceIsActiveWithoutSaving() {
        val catalog = MemoryPlaceCatalog()
        val device = placeFromCoordinates(40.7128, -74.0060, "Device location", source = PlaceSource.Device)
        catalog.setActive(device)
        assertEquals(device.cacheKey, catalog.active().cacheKey)
        assertEquals(0, catalog.saved().size)
    }
}
