package org.radilabs.weather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.radilabs.weather.persist.ApiKeyStore
import org.radilabs.weather.ui.WeatherRoot
import org.radilabs.weather.ui.theme.WeatherTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val store = ApiKeyStore.create(this)
        setContent {
            WeatherTheme {
                WeatherRoot(apiKeyStore = store)
            }
        }
    }
}
