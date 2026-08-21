package org.radilabs.weather.weather

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class WeatherErrorTest {
    @Test
    fun httpMapping() {
        assertEquals(WeatherError.Code.Auth, errorFromHttpStatus(401).code)
        assertEquals(WeatherError.Code.RateLimit, errorFromHttpStatus(429).code)
        assertEquals(WeatherError.Code.NotFound, errorFromHttpStatus(404).code)
        assertEquals(WeatherError.Code.Provider, errorFromHttpStatus(503).code)
    }

    @Test
    fun redactsAppId() {
        val redacted = redactSecrets("https://api.example/data?lat=1&appid=super-secret&units=metric")
        assertFalse(redacted.contains("super-secret"))
        assertTrue(redacted.contains("appid=redacted"))
    }

    @Test
    fun mapsUnknownThrowablesWithoutCast() {
        val mapped = IllegalStateException("boom").toWeatherError()
        assertEquals(WeatherError.Code.Unknown, mapped.code)
        val original = WeatherError(WeatherError.Code.Network, "offline")
        assertEquals(WeatherError.Code.Network, original.toWeatherError().code)
    }
}

private fun assertTrue(condition: Boolean) {
    org.junit.Assert.assertTrue(condition)
}
