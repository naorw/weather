package org.radilabs.weather.ui.today

import org.radilabs.weather.cache.Freshness
import org.radilabs.weather.weather.WeatherError

sealed class TodayUiState {
    data object Loading : TodayUiState()
    data class Ready(
        val snapshot: TodaySnapshot,
        val acquiring: Boolean = false,
        val note: String? = null,
        val statusLine: String = "LIVE",
        val freshness: Freshness = Freshness.Live,
    ) : TodayUiState()
    data class Failed(val error: WeatherError) : TodayUiState()
}
