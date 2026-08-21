package org.radilabs.weather.map

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.radilabs.weather.places.PlaceSource
import org.radilabs.weather.places.STOCKHOLM_PLACE
import org.radilabs.weather.places.placeFromCoordinates

class WeatherOverlayTest {
    @Test
    fun precipitationUrlUsesMaps1LayerAndKey() {
        val url = overlayTileTemplate(WeatherOverlay.Precipitation, "secret-key")!!
        assertTrue(url.startsWith("https://tile.openweathermap.org/map/precipitation_new/"))
        assertTrue(url.contains("{z}/{x}/{y}.png"))
        assertTrue(url.contains("appid=secret-key"))
        assertFalse(overlayRequestPreview(WeatherOverlay.Precipitation, "secret-key")!!.contains("secret-key"))
    }

    @Test
    fun cloudsUrlUsesCloudsNew() {
        val url = overlayTileTemplate(WeatherOverlay.Clouds, "k")!!
        assertTrue(url.contains("/clouds_new/"))
    }

    @Test
    fun noneHasNoTiles() {
        assertNull(overlayTileTemplate(WeatherOverlay.None, "k"))
        assertEquals(OverlayPlan.Hidden, planOverlay(WeatherOverlay.None, "k"))
    }

    @Test
    fun missingKeyDoesNotBuildTiles() {
        assertEquals(OverlayPlan.MissingKey, planOverlay(WeatherOverlay.Precipitation, null))
        assertEquals(OverlayPlan.MissingKey, planOverlay(WeatherOverlay.Precipitation, "  "))
        assertTrue(overlayNoteFor(OverlayPlan.MissingKey)!!.contains("Basemap still works"))
    }

    @Test
    fun cameraTargetUsesActivePlaceCoordinates() {
        val place = placeFromCoordinates(59.3293, 18.0686, "Stockholm", source = PlaceSource.Saved)
        assertEquals(STOCKHOLM_PLACE.coordinates.latitude, mapCameraTarget(place).first, 0.0001)
        assertEquals(STOCKHOLM_PLACE.coordinates.longitude, mapCameraTarget(place).second, 0.0001)
    }

    @Test
    fun switchingPlaceChangesCameraTarget() {
        val a = placeFromCoordinates(59.3293, 18.0686, "Stockholm", source = PlaceSource.Saved)
        val b = placeFromCoordinates(40.7128, -74.0060, "New York", source = PlaceSource.Saved)
        assertTrue(mapCameraTarget(a) != mapCameraTarget(b))
        assertEquals(b.displayName, "New York")
    }

    @Test
    fun markerGeoJsonUsesPlaceLonLatOrder() {
        val place = placeFromCoordinates(59.3293, 18.0686, "Stockholm", source = PlaceSource.Saved)
        val json = activePlaceGeoJson(place)
        assertTrue(json.contains("[${place.coordinates.longitude},${place.coordinates.latitude}]"))
    }

    @Test
    fun onlyDeclaredOverlaysAreSupported() {
        assertEquals(
            listOf("NONE", "PRECIP MAP", "CLOUD COVER"),
            WeatherOverlay.entries.map { it.controlLabel },
        )
        assertTrue(WeatherOverlay.Precipitation.legendBody.contains("Not observed radar"))
        assertTrue(WeatherOverlay.Clouds.legendBody.contains("Not infrared"))
    }
}
