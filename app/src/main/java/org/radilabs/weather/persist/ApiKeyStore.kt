package org.radilabs.weather.persist

import android.content.Context
import android.content.SharedPreferences

/**
 * App-private OpenWeather key storage.
 *
 * MODE_PRIVATE keeps the file inside the app sandbox. It is not a
 * cryptographic vault against a device owner with root. It does prevent
 * bundling the key into the APK. There is no backend.
 */
class ApiKeyStore(private val prefs: SharedPreferences) {
    fun isConfigured(): Boolean = read() != null

    fun read(): String? {
        val value = prefs.getString(KEY, null)?.trim().orEmpty()
        return value.ifEmpty { null }
    }

    fun save(raw: String) {
        val trimmed = raw.trim()
        require(trimmed.isNotEmpty()) { "empty key" }
        prefs.edit().putString(KEY, trimmed).apply()
    }

    fun remove() {
        prefs.edit().remove(KEY).apply()
    }

    fun maskedLabel(): String {
        val key = read() ?: return "Not configured"
        val tail = key.takeLast(4)
        return "Configured · ••••$tail"
    }

    companion object {
        private const val PREFS = "weather_secrets"
        private const val KEY = "openweather_api_key"

        fun create(context: Context): ApiKeyStore {
            return ApiKeyStore(
                context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE),
            )
        }
    }
}
