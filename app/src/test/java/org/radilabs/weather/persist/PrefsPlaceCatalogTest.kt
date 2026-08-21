package org.radilabs.weather.persist

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.radilabs.weather.places.PlaceSource
import org.radilabs.weather.places.STOCKHOLM_PLACE
import org.radilabs.weather.places.placeFromCoordinates
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PrefsPlaceCatalogTest {
    @Test
    fun persistsSavedAndActiveAcrossInstances() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = context.getSharedPreferences("test_places", Context.MODE_PRIVATE).also { it.edit().clear().commit() }
        val first = PrefsPlaceCatalog(prefs)
        first.save(placeFromCoordinates(48.8566, 2.3522, "Paris", "FR", source = PlaceSource.Search))
        first.save(STOCKHOLM_PLACE)
        val second = PrefsPlaceCatalog(prefs)
        assertEquals(2, second.saved().size)
        assertEquals(STOCKHOLM_PLACE.cacheKey, second.active().cacheKey)
    }
}
