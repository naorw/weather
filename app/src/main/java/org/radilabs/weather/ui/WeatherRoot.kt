package org.radilabs.weather.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.radilabs.weather.persist.ApiKeyStore
import org.radilabs.weather.ui.placeholder.PlaceholderScreen
import org.radilabs.weather.ui.settings.SettingsScreen
import org.radilabs.weather.ui.theme.Wx
import org.radilabs.weather.ui.today.TodayScreen
import org.radilabs.weather.ui.today.TodayUiState
import org.radilabs.weather.ui.today.presentSnapshot
import org.radilabs.weather.weather.STOCKHOLM
import org.radilabs.weather.weather.WeatherError
import org.radilabs.weather.weather.WeatherProvider

@Composable
fun WeatherRoot(apiKeyStore: ApiKeyStore, provider: WeatherProvider) {
    var dest by remember { mutableStateOf(Dest.Today) }
    var today by remember { mutableStateOf<TodayUiState>(TodayUiState.Loading) }
    val scope = rememberCoroutineScope()
    fun load() {
        today = TodayUiState.Loading
        scope.launch {
            today = withContext(Dispatchers.IO) {
                try {
                    TodayUiState.Ready(presentSnapshot(provider.getSnapshot(STOCKHOLM)))
                } catch (error: WeatherError) {
                    TodayUiState.Failed(error)
                } catch (error: Exception) {
                    TodayUiState.Failed(
                        WeatherError(WeatherError.Code.Unknown, "Weather request failed.", cause = error),
                    )
                }
            }
        }
    }
    LaunchedEffect(Unit) { load() }
    Column(
        Modifier
            .fillMaxSize()
            .background(Wx.base)
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = Wx.space4, vertical = Wx.space3),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Wx.space3),
        ) {
            Text("WX", color = Wx.accent, fontSize = 14.sp, fontWeight = FontWeight.Medium, letterSpacing = 3.sp)
            Text("WEATHER", color = Wx.text, fontSize = 14.sp, letterSpacing = 4.sp)
        }
        Box(
            Modifier
                .fillMaxWidth()
                .height(Wx.hairline)
                .background(Wx.border),
        )
        Box(Modifier.weight(1f).fillMaxWidth()) {
            when (dest) {
                Dest.Today -> TodayScreen(state = today, onRefresh = { load() })
                Dest.Radar -> PlaceholderScreen(
                    "Radar",
                    "Map and radar layers belong to a later phase.",
                )
                Dest.Cities -> PlaceholderScreen(
                    "Cities",
                    "Search, saved cities, and device location belong to a later phase.",
                )
                Dest.Settings -> SettingsScreen(apiKeyStore)
            }
        }
        Box(
            Modifier
                .fillMaxWidth()
                .height(Wx.hairline)
                .background(Wx.border),
        )
        Row(
            Modifier
                .fillMaxWidth()
                .background(Wx.surface)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .height(Wx.touchMin),
        ) {
            Dest.entries.forEach { item ->
                val active = dest == item
                Box(
                    Modifier
                        .weight(1f)
                        .height(Wx.touchMin)
                        .clickable { dest = item }
                        .then(
                            if (active) {
                                Modifier.border(Wx.hairline, Wx.accent)
                            } else {
                                Modifier
                            },
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        item.label.uppercase(),
                        color = if (active) Wx.accent else Wx.textMuted,
                        fontSize = Wx.nav,
                        letterSpacing = 1.5.sp,
                    )
                }
            }
        }
    }
}
