package org.radilabs.weather.ui.today

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.radilabs.weather.ui.instrument.AqScale
import org.radilabs.weather.ui.instrument.Hairline
import org.radilabs.weather.ui.instrument.RangeBar
import org.radilabs.weather.ui.instrument.SectionLabel
import org.radilabs.weather.ui.instrument.TechnicalLabel
import org.radilabs.weather.ui.instrument.WindCompass
import org.radilabs.weather.ui.theme.Wx
import org.radilabs.weather.weather.WeatherError

@Composable
fun TodayScreen(
    state: TodayUiState,
    onRefresh: () -> Unit,
    footer: String = "WX / 3-HOUR FREE FORECAST",
) {
    val ready = state as? TodayUiState.Ready
    val snapshot = ready?.snapshot
    val dayMin = snapshot?.days?.minOfOrNull { it.lowC }
    val dayMax = snapshot?.days?.maxOfOrNull { it.highC }
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Wx.space4, vertical = Wx.space3),
    ) {
        StatusRow(state = state, onRefresh = onRefresh)
        when {
            snapshot != null -> {
                Hero(snapshot)
                Hairline(Modifier.padding(vertical = Wx.space4))
                ThreeHourStrip(snapshot.hours)
                Hairline(Modifier.padding(vertical = Wx.space4))
                DayList(snapshot.days, dayMin, dayMax)
                Hairline(Modifier.padding(vertical = Wx.space4))
                Atmosphere(snapshot)
            }
            state is TodayUiState.Loading -> {
                Spacer(Modifier.height(Wx.space6))
                Text("ACQUIRING WEATHER", color = Wx.amber, fontSize = Wx.heading, letterSpacing = 2.sp)
                Text(
                    "No numeric placeholders while the instrument waits.",
                    color = Wx.textMuted,
                    fontSize = Wx.body,
                    modifier = Modifier.padding(top = Wx.space2),
                )
            }
            state is TodayUiState.Failed -> {
                Spacer(Modifier.height(Wx.space6))
                Text(state.error.title.uppercase(), color = Wx.warning, fontSize = Wx.heading, letterSpacing = 2.sp)
                Text(
                    state.error.message ?: "Weather request failed.",
                    color = Wx.text,
                    fontSize = Wx.body,
                    modifier = Modifier.padding(top = Wx.space2),
                )
                Text(
                    failedHint(state.error.code),
                    color = Wx.textMuted,
                    fontSize = Wx.meta,
                    modifier = Modifier.padding(top = Wx.space3),
                )
            }
        }
        Spacer(Modifier.height(Wx.space6))
        Text(
            footer,
            color = Wx.disabled,
            fontSize = Wx.meta,
            letterSpacing = 1.5.sp,
        )
        Spacer(Modifier.height(Wx.space4))
    }
}

@Composable
private fun StatusRow(state: TodayUiState, onRefresh: () -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val (color, text) = when (state) {
            TodayUiState.Loading -> Wx.amber to "ACQUIRING WEATHER"
            is TodayUiState.Ready -> when {
                state.acquiring -> Wx.amber to "ACQUIRING WEATHER"
                state.note != null -> Wx.amber to state.note
                else -> {
                    val live = state.statusLine == "LIVE"
                    (if (live) Wx.textMuted else Wx.amber) to state.statusLine
                }
            }
            is TodayUiState.Failed -> Wx.warning to state.error.title.uppercase()
        }
        Text(
            text,
            color = color,
            fontSize = Wx.meta,
            modifier = Modifier
                .weight(1f)
                .padding(end = Wx.space2),
        )
        Text(
            "REFRESH",
            color = Wx.accent,
            fontSize = Wx.nav,
            letterSpacing = 1.5.sp,
            modifier = Modifier
                .height(Wx.touchMin)
                .clickable(onClick = onRefresh)
                .padding(horizontal = Wx.space2, vertical = Wx.space3)
                .semantics { contentDescription = "Refresh weather" },
        )
    }
}

@Composable
private fun Hero(snapshot: TodaySnapshot) {
    Text(
        text = snapshot.location.uppercase(),
        color = Wx.accent,
        fontSize = Wx.location,
        fontWeight = FontWeight.Medium,
        letterSpacing = 3.sp,
    )
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "${snapshot.temperatureC}°",
            color = Wx.text,
            fontSize = Wx.temp,
            fontWeight = FontWeight.Light,
            modifier = Modifier.semantics {
                contentDescription = "Current temperature ${snapshot.temperatureC} degrees"
            },
        )
        Spacer(Modifier.width(Wx.space3))
        WeatherGlyph(snapshot.conditionId, Modifier.padding(top = 8.dp), Wx.accent)
    }
    Text(
        snapshot.condition.uppercase(),
        color = Wx.textMuted,
        fontSize = Wx.heading,
        letterSpacing = 2.sp,
    )
    Spacer(Modifier.height(Wx.space2))
    Text(
        "H ${formatDegree(snapshot.highC)}   L ${formatDegree(snapshot.lowC)}   FEELS ${snapshot.feelsLikeC}°",
        color = Wx.text,
        fontSize = Wx.value,
    )
    Text(
        "OBS ${snapshot.updatedAt}   HUM ${snapshot.humidity}   ${snapshot.pressure}",
        color = Wx.amber,
        fontSize = Wx.meta,
        modifier = Modifier.padding(top = Wx.space1),
    )
}

@Composable
private fun ThreeHourStrip(hours: List<HourPoint>) {
    SectionLabel("3-HOUR STEPS")
    Text(
        "LOCAL TIME · NOT HOURLY",
        color = Wx.disabled,
        fontSize = Wx.meta,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(top = Wx.space1),
    )
    Row(
        Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(top = Wx.space2),
        horizontalArrangement = Arrangement.spacedBy(Wx.space3),
    ) {
        hours.forEach { hour ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(Wx.touchMin)
                    .semantics {
                        contentDescription =
                            "Three-hour step ${hour.hour} local, ${hour.tempC} degrees, precip ${formatPrecipChance(hour.precipChance)}"
                    },
            ) {
                Text(hour.hour, color = Wx.textMuted, fontSize = Wx.meta)
                Spacer(Modifier.height(Wx.space1))
                WeatherGlyph(hour.condition)
                Text("${hour.tempC}°", color = Wx.text, fontSize = Wx.value)
                Text(formatPrecipChance(hour.precipChance), color = Wx.accent, fontSize = Wx.meta)
            }
        }
    }
}

@Composable
private fun DayList(days: List<DayPoint>, dayMin: Int?, dayMax: Int?) {
    SectionLabel("NEXT DAYS")
    days.forEach { day ->
        val marker = partialDayLabel(day.partial)
        Row(
            Modifier
                .fillMaxWidth()
                .height(Wx.touchMin)
                .semantics {
                    contentDescription = buildString {
                        append("${day.day} ${day.date}, ${day.lowC} to ${day.highC} degrees")
                        if (day.partial) append(", incomplete forecast coverage")
                    }
                },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                day.day.uppercase(),
                color = Wx.text,
                fontSize = Wx.body,
                modifier = Modifier.width(36.dp),
            )
            Text(
                day.date,
                color = Wx.textMuted,
                fontSize = Wx.meta,
                modifier = Modifier.width(24.dp),
            )
            WeatherGlyph(day.condition)
            Text(
                marker,
                color = Wx.textMuted,
                fontSize = Wx.meta,
                modifier = Modifier
                    .width(36.dp)
                    .padding(start = 4.dp),
            )
            Text(
                formatPrecipChance(day.precipChance),
                color = Wx.accent,
                fontSize = Wx.meta,
                modifier = Modifier.width(36.dp),
            )
            Text("${day.lowC}°", color = Wx.textMuted, fontSize = Wx.meta, modifier = Modifier.width(28.dp))
            RangeBar(
                low = day.lowC,
                high = day.highC,
                scaleMin = dayMin ?: day.lowC,
                scaleMax = dayMax ?: day.highC,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = Wx.space2),
            )
            Text("${day.highC}°", color = Wx.text, fontSize = Wx.meta, modifier = Modifier.width(28.dp))
        }
    }
}

@Composable
private fun Atmosphere(snapshot: TodaySnapshot) {
    SectionLabel("ATMOSPHERE")
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = Wx.space3),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        TechnicalLabel("WIND")
        Row(verticalAlignment = Alignment.CenterVertically) {
            WindCompass(snapshot.windDeg, Modifier.padding(end = Wx.space2))
            Text(snapshot.wind, color = Wx.text, fontSize = Wx.value)
        }
    }
    MetricRow("PRECIPITATION", snapshot.precipitation)
    MetricRow("HUMIDITY", snapshot.humidity)
    MetricRow("PRESSURE", snapshot.pressure)
    MetricRow("VISIBILITY", snapshot.visibility)
    Spacer(Modifier.height(Wx.space3))
    TechnicalLabel("AIR QUALITY")
    val aq = snapshot.airQuality
    if (aq == null) {
        Text(
            "Unavailable",
            color = Wx.disabled,
            fontSize = Wx.meta,
            modifier = Modifier.padding(top = Wx.space2),
        )
    } else {
        AqScale(aq.index, aq.category, Modifier.padding(top = Wx.space2))
    }
}

@Composable
private fun MetricRow(label: String, value: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = Wx.space3),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        TechnicalLabel(label)
        Text(value, color = Wx.text, fontSize = Wx.value)
    }
}

private fun failedHint(code: WeatherError.Code): String {
    return when (code) {
        WeatherError.Code.MissingKey -> "Save a key in Settings. Nothing is bundled in the APK."
        WeatherError.Code.Auth -> "The provider rejected this key."
        WeatherError.Code.RateLimit -> "Wait, then refresh. Free-plan budget applies."
        WeatherError.Code.Network, WeatherError.Code.Timeout -> "Check connectivity, then refresh."
        WeatherError.Code.Malformed, WeatherError.Code.Provider, WeatherError.Code.Unknown, WeatherError.Code.NotFound ->
            "Refresh, or try again later."
    }
}
