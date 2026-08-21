package org.radilabs.weather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.radilabs.weather.persist.ApiKeyStore
import org.radilabs.weather.ui.WeatherRoot
import org.radilabs.weather.ui.theme.WeatherTheme
import org.radilabs.weather.weather.openweather.OpenWeatherProvider

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val store = ApiKeyStore.create(this)
        val provider = OpenWeatherProvider(getApiKey = { store.read() })
        setContent {
            WeatherTheme {
                WeatherRoot(apiKeyStore = store, provider = provider)
            }
        }
    }
}
