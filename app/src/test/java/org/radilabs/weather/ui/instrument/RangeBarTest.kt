package org.radilabs.weather.ui.instrument

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RangeBarTest {
    @Test
    fun normalizesAgainstSharedScale() {
        val fractions = rangeBarFractions(low = 10, high = 20, scaleMin = 0, scaleMax = 40)
        assertEquals(0.25f, fractions.start, 0.001f)
        assertEquals(0.25f, fractions.span, 0.001f)
    }

    @Test
    fun swappedHighLowStillFillsPositiveSpan() {
        val fractions = rangeBarFractions(low = 18, high = 8, scaleMin = 8, scaleMax = 18)
        assertEquals(0f, fractions.start, 0.001f)
        assertTrue(fractions.span > 0.01f)
    }

    @Test
    fun identicalScaleStillDrawsAMark() {
        val fractions = rangeBarFractions(low = 12, high = 12, scaleMin = 12, scaleMax = 12)
        assertTrue(fractions.span >= 0.02f)
    }
}
