package org.radilabs.weather.ui.today

import org.radilabs.weather.weather.WeatherError

sealed class TodayUiState {
    data object Loading : TodayUiState()
    data class Ready(val snapshot: TodaySnapshot) : TodayUiState()
    data class Failed(val error: WeatherError) : TodayUiState()
}
