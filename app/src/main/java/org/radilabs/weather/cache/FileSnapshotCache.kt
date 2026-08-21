package org.radilabs.weather.cache

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import org.radilabs.weather.weather.WeatherSnapshot
import java.io.File

class FileSnapshotCache(
    private val directory: File,
    private val gson: Gson = Gson(),
) : SnapshotCache {
    init {
        directory.mkdirs()
    }

    override fun read(cacheKey: String): CachedWeather? {
        val file = fileFor(cacheKey)
        if (!file.isFile) return null
        return try {
            val record = gson.fromJson(file.readText(), CachedWeather::class.java) ?: return null
            if (record.schemaVersion != CACHE_SCHEMA_VERSION || record.provider != CACHE_PROVIDER) {
                file.delete()
                return null
            }
            if (record.cacheKey != cacheKey) {
                file.delete()
                return null
            }
            if (!record.snapshot.current.temperatureC.isFinite()) {
                file.delete()
                return null
            }
            record
        } catch (_: JsonSyntaxException) {
            file.delete()
            null
        } catch (_: Exception) {
            file.delete()
            null
        }
    }

    override fun write(record: CachedWeather) {
        if (record.schemaVersion != CACHE_SCHEMA_VERSION) return
        if (record.provider != CACHE_PROVIDER) return
        if (!record.snapshot.current.temperatureC.isFinite()) return
        fileFor(record.cacheKey).writeText(gson.toJson(record))
    }

    private fun fileFor(cacheKey: String): File {
        val safe = cacheKey.replace(':', '_')
        return File(directory, "$safe.json")
    }
}

class MemorySnapshotCache : SnapshotCache {
    private val rows = mutableMapOf<String, CachedWeather>()

    override fun read(cacheKey: String): CachedWeather? {
        val record = rows[cacheKey] ?: return null
        if (record.schemaVersion != CACHE_SCHEMA_VERSION || record.provider != CACHE_PROVIDER) {
            rows.remove(cacheKey)
            return null
        }
        return record
    }

    override fun write(record: CachedWeather) {
        if (record.schemaVersion != CACHE_SCHEMA_VERSION) return
        if (record.provider != CACHE_PROVIDER) return
        if (!record.snapshot.current.temperatureC.isFinite()) return
        rows[record.cacheKey] = record
    }
}

fun CachedWeather.usableSnapshot(): WeatherSnapshot = snapshot
