package org.radilabs.weather.map

import org.junit.Assert.assertEquals
import org.junit.Test

class MapViewLifecycleTest {
    @Test
    fun onCreateRunsBeforeObserverRegistration() {
        val order = mutableListOf<String>()
        runMapAttach(
            onCreate = { order += "onCreate" },
            registerObserver = { order += "registerObserver" },
        )
        assertEquals(listOf("onCreate", "registerObserver"), order)
    }
}
