package org.radilabs.weather.cache

import org.radilabs.weather.weather.WeatherSnapshot

const val CACHE_SCHEMA_VERSION = 1
const val CACHE_PROVIDER = "openweather"

data class CachedWeather(
    val cacheKey: String,
    val schemaVersion: Int,
    val provider: String,
    val fetchedAtMs: Long,
    val snapshot: WeatherSnapshot,
)

interface SnapshotCache {
    fun read(cacheKey: String): CachedWeather?
    fun write(record: CachedWeather)
}
