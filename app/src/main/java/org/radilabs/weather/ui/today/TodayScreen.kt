package org.radilabs.weather.ui.today

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.radilabs.weather.ui.theme.Wx
import kotlin.math.max

@Composable
fun TodayScreen(
    state: TodayUiState,
    onRefresh: () -> Unit,
) {
    val snapshot = (state as? TodayUiState.Ready)?.snapshot
    val dayMin = snapshot?.days?.minOfOrNull { it.lowC }
    val dayMax = snapshot?.days?.maxOfOrNull { it.highC }
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Wx.space4, vertical = Wx.space3),
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StatusLine(state)
            Text(
                "REFRESH",
                color = Wx.accent,
                fontSize = Wx.nav,
                letterSpacing = 1.5.sp,
                modifier = Modifier
                    .height(Wx.touchMin)
                    .clickable(onClick = onRefresh)
                    .padding(horizontal = Wx.space2, vertical = Wx.space3),
            )
        }
        if (snapshot == null) {
            Spacer(Modifier.height(Wx.space6))
            return@Column
        }
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
            "H ${deg(snapshot.highC)}   L ${deg(snapshot.lowC)}   FEELS ${snapshot.feelsLikeC}°",
            color = Wx.text,
            fontSize = Wx.value,
        )
        Text(
            "UPDATED ${snapshot.updatedAt}",
            color = Wx.amber,
            fontSize = Wx.meta,
            modifier = Modifier.padding(top = Wx.space1),
        )
        Hairline(Modifier.padding(vertical = Wx.space4))
        Label("NEXT HOURS")
        Row(
            Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(top = Wx.space2),
            horizontalArrangement = Arrangement.spacedBy(Wx.space4),
        ) {
            snapshot.hours.forEach { hour ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(hour.hour, color = Wx.textMuted, fontSize = Wx.meta)
                    Spacer(Modifier.height(Wx.space1))
                    WeatherGlyph(hour.condition)
                    Text("${hour.tempC}°", color = Wx.text, fontSize = Wx.value)
                    Text("${hour.precipChance}%", color = Wx.accent, fontSize = Wx.meta)
                }
            }
        }
        Hairline(Modifier.padding(vertical = Wx.space4))
        Label("NEXT DAYS")
        snapshot.days.forEach { day ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(Wx.touchMin)
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(day.day.uppercase(), color = Wx.text, fontSize = Wx.body, modifier = Modifier.width(40.dp))
                WeatherGlyph(day.condition)
                Text(
                    if (day.partial) "part." else "    ",
                    color = Wx.textMuted,
                    fontSize = Wx.meta,
                    modifier = Modifier
                        .width(36.dp)
                        .padding(start = 4.dp),
                )
                Text("${day.precipChance}%", color = Wx.accent, fontSize = Wx.meta, modifier = Modifier.width(36.dp))
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
        Hairline(Modifier.padding(vertical = Wx.space4))
        Label("ATMOSPHERE")
        AtmosphereRow("WIND", snapshot.wind)
        AtmosphereRow("PRECIPITATION", snapshot.precipitation)
        AtmosphereRow("HUMIDITY", snapshot.humidity)
        AtmosphereRow("PRESSURE", snapshot.pressure)
        AtmosphereRow("VISIBILITY", snapshot.visibility)
        Spacer(Modifier.height(Wx.space6))
        Text(
            "WX / STOCKHOLM / OPENWEATHER FREE",
            color = Wx.disabled,
            fontSize = Wx.meta,
            letterSpacing = 1.5.sp,
        )
        Spacer(Modifier.height(Wx.space4))
    }
}

@Composable
private fun StatusLine(state: TodayUiState) {
    val (color, text) = when (state) {
        TodayUiState.Loading -> Wx.amber to "ACQUIRING WEATHER"
        is TodayUiState.Ready -> Wx.textMuted to "LIVE"
        is TodayUiState.Failed -> Wx.warning to "${state.error.title.uppercase()} · ${state.error.message}"
    }
    Text(text, color = color, fontSize = Wx.meta, modifier = Modifier.fillMaxWidth(0.72f))
}

private fun deg(value: Int?): String = if (value == null) "—" else "$value°"

@Composable
private fun Label(text: String) {
    Text(text, color = Wx.textMuted, fontSize = Wx.heading, letterSpacing = 2.sp)
}

@Composable
private fun Hairline(modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .height(Wx.hairline)
            .background(Wx.border),
    )
}

@Composable
private fun AtmosphereRow(label: String, value: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = Wx.space3),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, color = Wx.textMuted, fontSize = Wx.meta, letterSpacing = 1.5.sp)
        Text(value, color = Wx.text, fontSize = Wx.value)
    }
}

@Composable
private fun RangeBar(low: Int, high: Int, scaleMin: Int, scaleMax: Int, modifier: Modifier = Modifier) {
    val span = max(1, scaleMax - scaleMin).toFloat()
    val start = ((low - scaleMin) / span).coerceIn(0f, 1f)
    val end = ((high - scaleMin) / span).coerceIn(0f, 1f)
    Row(
        modifier.height(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(Modifier.weight(max(0.001f, start)))
        Box(
            Modifier
                .weight(max(0.001f, end - start))
                .height(6.dp)
                .background(Wx.accent),
        )
        Spacer(Modifier.weight(max(0.001f, 1f - end)))
    }
}
