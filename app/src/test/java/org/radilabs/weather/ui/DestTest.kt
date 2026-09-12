package org.radilabs.weather.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class DestTest {
    @Test
    fun shellHasFourDestinations() {
        assertEquals(listOf("Today", "Map", "Cities", "Settings"), Dest.entries.map { it.label })
        assertEquals("MAP", Dest.Radar.label.uppercase())
    }
}
