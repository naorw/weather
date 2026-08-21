package org.radilabs.weather.session

import org.radilabs.weather.cache.CACHE_PROVIDER
import org.radilabs.weather.cache.CACHE_SCHEMA_VERSION
import org.radilabs.weather.cache.CachedWeather
import org.radilabs.weather.cache.Freshness
import org.radilabs.weather.cache.SnapshotCache
import org.radilabs.weather.cache.classifyFreshness
import org.radilabs.weather.cache.freshnessLabel
import org.radilabs.weather.places.Place
import org.radilabs.weather.places.PlaceCatalog
import org.radilabs.weather.ui.today.TodaySnapshot
import org.radilabs.weather.ui.today.presentSnapshot
import org.radilabs.weather.weather.WeatherError
import org.radilabs.weather.weather.WeatherProvider
import org.radilabs.weather.weather.WeatherSnapshot

data class SessionView(
    val place: Place,
    val snapshot: TodaySnapshot?,
    val freshness: Freshness,
    val statusLine: String,
    val acquiring: Boolean,
    val note: String? = null,
    val error: WeatherError? = null,
    val generation: Long,
)

class WeatherSession(
    private val catalog: PlaceCatalog,
    private val cache: SnapshotCache,
    private val provider: WeatherProvider,
    private val nowMs: () -> Long = { System.currentTimeMillis() },
    private val present: (WeatherSnapshot) -> TodaySnapshot = ::presentSnapshot,
) {
    private var generation = 0L
    private var inFlightKey: String? = null

    fun active(): Place = catalog.active()

    fun saved(): List<Place> = catalog.saved()

    fun save(place: Place): Place = catalog.save(place)

    fun remove(cacheKey: String): Place = catalog.remove(cacheKey)

    fun activate(place: Place): Place = catalog.setActive(place)

    fun beginGeneration(place: Place): Long {
        generation += 1
        inFlightKey = place.cacheKey
        return generation
    }

    fun isCurrent(generation: Long, cacheKey: String): Boolean {
        return this.generation == generation && catalog.active().cacheKey == cacheKey
    }

    fun shouldReuseInFlight(cacheKey: String): Boolean {
        return inFlightKey == cacheKey
    }

    fun clearInFlight(generation: Long) {
        if (this.generation == generation) inFlightKey = null
    }

    fun viewFromCache(place: Place, acquiring: Boolean): SessionView {
        val cached = cache.read(place.cacheKey)
        val now = nowMs()
        if (cached == null) {
            return SessionView(
                place = place,
                snapshot = null,
                freshness = Freshness.Missing,
                statusLine = if (acquiring) "ACQUIRING WEATHER" else "NO CACHE",
                acquiring = acquiring,
                generation = generation,
            )
        }
        val freshness = classifyFreshness(cached.fetchedAtMs, now, live = false)
        return SessionView(
            place = place,
            snapshot = present(cached.snapshot),
            freshness = freshness,
            statusLine = if (acquiring) {
                "ACQUIRING WEATHER"
            } else {
                freshnessLabel(freshness, cached.fetchedAtMs, now) ?: "CACHED"
            },
            acquiring = acquiring,
            generation = generation,
        )
    }

    fun applySuccess(generation: Long, place: Place, weather: WeatherSnapshot): SessionView? {
        if (!isCurrent(generation, place.cacheKey)) return null
        cache.write(
            CachedWeather(
                cacheKey = place.cacheKey,
                schemaVersion = CACHE_SCHEMA_VERSION,
                provider = CACHE_PROVIDER,
                fetchedAtMs = weather.fetchedAtMs,
                snapshot = weather,
            ),
        )
        clearInFlight(generation)
        return SessionView(
            place = catalog.active(),
            snapshot = present(weather),
            freshness = Freshness.Live,
            statusLine = "LIVE",
            acquiring = false,
            generation = generation,
        )
    }

    fun applyFailure(generation: Long, place: Place, error: WeatherError): SessionView? {
        if (!isCurrent(generation, place.cacheKey)) return null
        clearInFlight(generation)
        val cached = viewFromCache(place, acquiring = false)
        return if (cached.snapshot != null) {
            cached.copy(note = "${error.title.uppercase()} · refresh failed", statusLine = cached.statusLine)
        } else {
            SessionView(
                place = place,
                snapshot = null,
                freshness = Freshness.Missing,
                statusLine = error.title.uppercase(),
                acquiring = false,
                error = error,
                generation = generation,
            )
        }
    }

    fun fetchSnapshot(place: Place) = provider.getSnapshot(place.coordinates)

    fun search(query: String) = provider.searchPlaces(query)

    fun reverse(placeHint: Place) = try {
        provider.reverseGeocode(placeHint.coordinates)
    } catch (_: WeatherError) {
        placeHint
    }
}
