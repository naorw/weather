package org.radilabs.weather.weather.openweather

import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.radilabs.weather.weather.STOCKHOLM
import org.radilabs.weather.weather.WeatherError
import java.util.concurrent.TimeUnit

class OpenWeatherProviderTest {
    @Test
    fun missingKeyDoesNotCallNetwork() {
        val server = MockWebServer()
        server.start()
        try {
            val provider = OpenWeatherProvider(
                getApiKey = { null },
                http = OkHttpClient(),
                origin = server.url("/").newBuilder().build().let {
                    it.newBuilder().encodedPath("/").build()
                },
            )
            try {
                provider.getSnapshot(STOCKHOLM)
                throw AssertionError("expected missing key")
            } catch (error: WeatherError) {
                assertEquals(WeatherError.Code.MissingKey, error.code)
            }
            assertEquals(0, server.requestCount)
        } finally {
            server.shutdown()
        }
    }

    @Test
    fun snapshotFromFixturesAndRedactsAppId() {
        val server = MockWebServer()
        server.enqueue(MockResponse().setBody(read("current.json")))
        server.enqueue(MockResponse().setBody(read("forecast.json")))
        server.enqueue(MockResponse().setBody(read("air.json")))
        server.start()
        try {
            val origin = server.url("/").newBuilder().encodedPath("/").build()
            val provider = OpenWeatherProvider(
                getApiKey = { "secret-test-key" },
                http = OkHttpClient(),
                origin = origin,
                nowMs = { 1L },
            )
            val snapshot = provider.getSnapshot(STOCKHOLM)
            assertEquals("Stockholm", snapshot.location.displayName)
            assertEquals("SE", snapshot.location.country)
            assertEquals(7200, snapshot.location.timezoneOffsetSeconds)
            assertEquals(14.2, snapshot.current.temperatureC, 0.0)
            assertEquals(3, snapshot.points.size)
            assertTrue(snapshot.days.isNotEmpty())
            assertEquals(2, snapshot.airQuality?.openWeatherAqi)
            val recorded = server.takeRequest()
            val url = recorded.requestUrl.toString()
            assertTrue(url.contains("appid=secret-test-key"))
            assertTrue(!org.radilabs.weather.weather.redactSecrets(url).contains("secret-test-key"))
        } finally {
            server.shutdown()
        }
    }

    @Test
    fun authStatusIsDistinct() {
        val server = MockWebServer()
        server.enqueue(MockResponse().setResponseCode(401).setBody("""{"cod":401}"""))
        server.start()
        try {
            val provider = OpenWeatherProvider(
                getApiKey = { "bad" },
                http = OkHttpClient(),
                origin = server.url("/").newBuilder().encodedPath("/").build(),
            )
            try {
                provider.getSnapshot(STOCKHOLM)
                throw AssertionError("expected auth")
            } catch (error: WeatherError) {
                assertEquals(WeatherError.Code.Auth, error.code)
            }
        } finally {
            server.shutdown()
        }
    }

    @Test
    fun airFailureDoesNotDropWeather() {
        val server = MockWebServer()
        server.enqueue(MockResponse().setBody(read("current.json")))
        server.enqueue(MockResponse().setBody(read("forecast.json")))
        server.enqueue(MockResponse().setResponseCode(500).setBody("no"))
        server.start()
        try {
            val provider = OpenWeatherProvider(
                getApiKey = { "k" },
                http = OkHttpClient(),
                origin = server.url("/").newBuilder().encodedPath("/").build(),
            )
            val snapshot = provider.getSnapshot(STOCKHOLM)
            assertNull(snapshot.airQuality)
            assertEquals(14.2, snapshot.current.temperatureC, 0.0)
        } finally {
            server.shutdown()
        }
    }

    @Test
    fun timeoutMapsToTimeout() {
        val server = MockWebServer()
        server.enqueue(MockResponse().setBodyDelay(2, TimeUnit.SECONDS).setBody("{}"))
        server.start()
        try {
            val client = OkHttpClient.Builder().callTimeout(200, TimeUnit.MILLISECONDS).build()
            val provider = OpenWeatherProvider(
                getApiKey = { "k" },
                http = client,
                origin = server.url("/").newBuilder().encodedPath("/").build(),
            )
            try {
                provider.getSnapshot(STOCKHOLM)
                throw AssertionError("expected timeout")
            } catch (error: WeatherError) {
                assertEquals(WeatherError.Code.Timeout, error.code)
            }
        } finally {
            server.shutdown()
        }
    }

    @Test
    fun searchMapsDistinctPlaces() {
        val server = MockWebServer()
        server.enqueue(MockResponse().setBody(read("geo-direct.json")))
        server.start()
        try {
            val provider = OpenWeatherProvider(
                getApiKey = { "k" },
                http = OkHttpClient(),
                origin = server.url("/").newBuilder().encodedPath("/").build(),
            )
            val hits = provider.searchPlaces("Stockholm")
            assertEquals(2, hits.size)
            assertEquals("SE", hits[0].country)
            assertEquals("Wisconsin", hits[1].region)
            assertTrue(hits[0].cacheKey != hits[1].cacheKey)
        } finally {
            server.shutdown()
        }
    }

    private fun read(name: String): String {
        return javaClass.classLoader!!.getResourceAsStream("openweather/$name")!!.bufferedReader().readText()
    }
}
