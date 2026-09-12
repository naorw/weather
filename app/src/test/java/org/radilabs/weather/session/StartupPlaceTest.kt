package org.radilabs.weather.session

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.radilabs.weather.location.LocationUnavailableException
import org.radilabs.weather.places.MemoryPlaceCatalog
import org.radilabs.weather.places.PlaceSource
import org.radilabs.weather.places.placeFromCoordinates
import org.radilabs.weather.weather.Coordinates

class StartupPlaceTest {
    @Before
    fun resetPolicy() {
        SessionPlacePolicy.resetForTests()
    }
    private val stockholm = placeFromCoordinates(59.3293, 18.0686, "Stockholm", source = PlaceSource.Saved)
    private val parisCoords = Coordinates(48.8566, 2.3522)

    @Test
    fun withoutPermissionKeepsPreviousAndDoesNotCallLocation() = runTest {
        var located = false
        val catalog = MemoryPlaceCatalog(initialActive = stockholm, initialSaved = listOf(stockholm))
        val result = resolveStartupPlace(
            hasPermission = false,
            previous = catalog.active(),
            currentCoordinates = {
                located = true
                parisCoords
            },
            reverse = { error("should not reverse") },
            activate = { error("should not activate") },
        )
        assertFalse(located)
        assertFalse(result.usedDevice)
        assertEquals(stockholm.cacheKey, result.place.cacheKey)
        assertEquals(1, catalog.saved().size)
        assertEquals(stockholm.cacheKey, catalog.active().cacheKey)
    }

    @Test
    fun withPermissionActivatesDevicePlaceWithoutSaving() = runTest {
        val catalog = MemoryPlaceCatalog(initialActive = stockholm, initialSaved = listOf(stockholm))
        val result = resolveStartupPlace(
            hasPermission = true,
            previous = catalog.active(),
            currentCoordinates = { parisCoords },
            reverse = { hint ->
                hint.copy(displayName = "Paris", country = "FR", source = PlaceSource.Device)
            },
            activate = { catalog.setActive(it) },
        )
        assertTrue(result.usedDevice)
        assertEquals("Paris", result.place.displayName)
        assertEquals(PlaceSource.Device, result.place.source)
        assertEquals(1, catalog.saved().size)
        assertEquals(stockholm.cacheKey, catalog.saved().single().cacheKey)
        assertEquals(result.place.cacheKey, catalog.active().cacheKey)
        assertTrue(catalog.saved().none { it.cacheKey == result.place.cacheKey })
    }

    @Test
    fun locationFailureKeepsPreviousAndSavedList() = runTest {
        val catalog = MemoryPlaceCatalog(initialActive = stockholm, initialSaved = listOf(stockholm))
        val result = resolveStartupPlace(
            hasPermission = true,
            previous = catalog.active(),
            currentCoordinates = { throw LocationUnavailableException("unavailable") },
            reverse = { error("should not reverse") },
            activate = { error("should not activate") },
        )
        assertFalse(result.usedDevice)
        assertEquals(stockholm.cacheKey, result.place.cacheKey)
        assertEquals(PlaceSource.Saved, result.place.source)
        assertEquals(1, catalog.saved().size)
        assertEquals(stockholm.cacheKey, catalog.active().cacheKey)
    }

    @Test
    fun locationFailureDoesNotKeepDeviceSourceOnFallback() = runTest {
        val staleDevice = placeFromCoordinates(59.3293, 18.0686, "Stockholm", source = PlaceSource.Device)
        val catalog = MemoryPlaceCatalog(initialActive = staleDevice, initialSaved = listOf(stockholm))
        val result = resolveStartupPlace(
            hasPermission = true,
            previous = catalog.active(),
            currentCoordinates = { throw LocationUnavailableException("unavailable") },
            reverse = { error("should not reverse") },
            activate = { catalog.setActive(it) },
        )
        assertFalse(result.usedDevice)
        assertEquals(staleDevice.cacheKey, result.place.cacheKey)
        assertEquals(PlaceSource.Saved, result.place.source)
        assertEquals(PlaceSource.Saved, catalog.active().source)
        assertEquals(1, catalog.saved().size)
    }

    @Test
    fun reverseFailureStillKeepsPrevious() = runTest {
        val catalog = MemoryPlaceCatalog(initialActive = stockholm, initialSaved = listOf(stockholm))
        val result = resolveStartupPlace(
            hasPermission = true,
            previous = catalog.active(),
            currentCoordinates = { parisCoords },
            reverse = { error("geocode down") },
            activate = { catalog.setActive(it) },
        )
        assertFalse(result.usedDevice)
        assertEquals(stockholm.cacheKey, result.place.cacheKey)
        assertEquals(1, catalog.saved().size)
    }

    @Test
    fun unresolvedReverseHintKeepsPrevious() = runTest {
        val catalog = MemoryPlaceCatalog(initialActive = stockholm, initialSaved = listOf(stockholm))
        val result = resolveStartupPlace(
            hasPermission = true,
            previous = catalog.active(),
            currentCoordinates = { parisCoords },
            reverse = { hint -> hint },
            activate = { catalog.setActive(it) },
        )
        assertFalse(result.usedDevice)
        assertEquals(stockholm.cacheKey, result.place.cacheKey)
        assertEquals(PlaceSource.Saved, result.place.source)
        assertEquals(1, catalog.saved().size)
    }

    @Test
    fun laterManualActivateWinsOverStartupDevice() {
        val catalog = MemoryPlaceCatalog(initialActive = stockholm, initialSaved = listOf(stockholm))
        val device = placeFromCoordinates(48.8566, 2.3522, "Paris", source = PlaceSource.Device)
        catalog.setActive(device)
        val manual = placeFromCoordinates(51.5074, -0.1278, "London", source = PlaceSource.Saved)
        catalog.save(manual)
        assertEquals(manual.cacheKey, catalog.active().cacheKey)
        assertEquals(2, catalog.saved().size)
        assertTrue(catalog.saved().none { it.source == PlaceSource.Device })
    }

    @Test
    fun coldStartGenerationAllowsRotationRetry() {
        val first = SessionPlacePolicy.tryBeginColdStart()
        assertEquals(1, first)
        val second = SessionPlacePolicy.tryBeginColdStart()
        assertEquals(2, second)
        SessionPlacePolicy.abortColdStart(first!!)
        assertEquals(2, second)
        SessionPlacePolicy.completeColdStart(second!!)
        assertNull(SessionPlacePolicy.tryBeginColdStart())
        SessionPlacePolicy.resetForTests()
        val retry = SessionPlacePolicy.tryBeginColdStart()
        SessionPlacePolicy.abortColdStart(retry!!)
        assertEquals(2, SessionPlacePolicy.tryBeginColdStart())
    }

    @Test
    fun manualChoiceBlocksLaterStartupActivate() = runTest {
        val catalog = MemoryPlaceCatalog(initialActive = stockholm, initialSaved = listOf(stockholm))
        SessionPlacePolicy.markManualChoice()
        val london = placeFromCoordinates(51.5074, -0.1278, "London", source = PlaceSource.Saved)
        catalog.save(london)
        val result = resolveStartupPlace(
            hasPermission = true,
            previous = stockholm,
            currentCoordinates = { parisCoords },
            reverse = { hint -> hint.copy(displayName = "Paris", source = PlaceSource.Device) },
            activate = { place ->
                if (SessionPlacePolicy.hasManualChoice()) catalog.active() else catalog.setActive(place)
            },
        )
        assertEquals(london.cacheKey, result.place.cacheKey)
        assertEquals(london.cacheKey, catalog.active().cacheKey)
        assertTrue(catalog.saved().none { it.cacheKey == result.place.cacheKey && it.source == PlaceSource.Device })
    }
}
