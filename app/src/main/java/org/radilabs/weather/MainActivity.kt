package org.radilabs.weather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.radilabs.weather.cache.FileSnapshotCache
import org.radilabs.weather.location.DeviceLocator
import org.radilabs.weather.persist.ApiKeyStore
import org.radilabs.weather.persist.PrefsPlaceCatalog
import org.radilabs.weather.session.WeatherSession
import org.radilabs.weather.ui.WeatherRoot
import org.radilabs.weather.ui.theme.WeatherTheme
import org.radilabs.weather.weather.openweather.OpenWeatherProvider
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val store = ApiKeyStore.create(this)
        val provider = OpenWeatherProvider(getApiKey = { store.read() })
        val session = WeatherSession(
            catalog = PrefsPlaceCatalog.create(this),
            cache = FileSnapshotCache(File(filesDir, "weather-cache")),
            provider = provider,
        )
        val locator = DeviceLocator(this)
        setContent {
            WeatherTheme {
                WeatherRoot(apiKeyStore = store, session = session, locator = locator)
            }
        }
    }
}
