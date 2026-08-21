package org.radilabs.weather

import org.junit.Assert.assertEquals
import org.junit.Test

class AppVersionTest {
    @Test
    fun labelUsesRuntimeMetadataNotHardcodedPhase() {
        val label = AppVersion("0.1.0", 6).label()
        assertEquals("Weather 0.1.0 (6) · org.radilabs.weather", label)
    }
}
