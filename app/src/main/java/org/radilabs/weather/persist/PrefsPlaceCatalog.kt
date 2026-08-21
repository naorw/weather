package org.radilabs.weather.persist

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import org.radilabs.weather.places.MemoryPlaceCatalog
import org.radilabs.weather.places.Place
import org.radilabs.weather.places.PlaceCatalog
import org.radilabs.weather.places.PlaceSource
import org.radilabs.weather.places.STOCKHOLM_PLACE
import org.radilabs.weather.places.placeFromCoordinates

class PrefsPlaceCatalog(private val prefs: SharedPreferences) : PlaceCatalog {
    private val memory: MemoryPlaceCatalog

    init {
        val saved = readList(prefs.getString(SAVED, null))
        val active = readPlace(prefs.getString(ACTIVE, null)) ?: saved.firstOrNull() ?: STOCKHOLM_PLACE
        memory = MemoryPlaceCatalog(initialActive = active, initialSaved = saved)
    }

    override fun active(): Place = memory.active()

    override fun saved(): List<Place> = memory.saved()

    override fun save(place: Place): Place = persist { memory.save(place) }

    override fun remove(cacheKey: String): Place = persist { memory.remove(cacheKey) }

    override fun setActive(place: Place): Place = persist { memory.setActive(place) }

    private fun persist(block: () -> Place): Place {
        val result = block()
        prefs.edit()
            .putString(SAVED, writeList(memory.saved()))
            .putString(ACTIVE, writePlace(memory.active()))
            .apply()
        return result
    }

    companion object {
        private const val PREFS = "weather_places"
        private const val SAVED = "saved"
        private const val ACTIVE = "active"

        fun create(context: Context): PrefsPlaceCatalog {
            return PrefsPlaceCatalog(
                context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE),
            )
        }

        internal fun writePlace(place: Place): String {
            return JSONObject()
                .put("cacheKey", place.cacheKey)
                .put("displayName", place.displayName)
                .put("lat", place.coordinates.latitude)
                .put("lon", place.coordinates.longitude)
                .put("country", place.country)
                .put("region", place.region)
                .put("source", place.source.name)
                .toString()
        }

        internal fun readPlace(raw: String?): Place? {
            if (raw.isNullOrBlank()) return null
            return try {
                val obj = JSONObject(raw)
                val lat = obj.getDouble("lat")
                val lon = obj.getDouble("lon")
                val source = runCatching { PlaceSource.valueOf(obj.optString("source")) }.getOrDefault(PlaceSource.Saved)
                val name = obj.optString("displayName").ifBlank { return null }
                placeFromCoordinates(
                    latitude = lat,
                    longitude = lon,
                    displayName = name,
                    country = obj.optString("country").ifBlank { null },
                    region = obj.optString("region").ifBlank { null },
                    source = source,
                )
            } catch (_: Exception) {
                null
            }
        }

        internal fun writeList(places: List<Place>): String {
            val array = JSONArray()
            places.forEach { array.put(JSONObject(writePlace(it))) }
            return array.toString()
        }

        internal fun readList(raw: String?): List<Place> {
            if (raw.isNullOrBlank()) return emptyList()
            return try {
                val array = JSONArray(raw)
                (0 until array.length()).mapNotNull { readPlace(array.optJSONObject(it)?.toString()) }
            } catch (_: Exception) {
                emptyList()
            }
        }
    }
}
