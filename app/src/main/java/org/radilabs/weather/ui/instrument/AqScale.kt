package org.radilabs.weather.ui.instrument

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.radilabs.weather.ui.theme.Wx

@Composable
fun AqScale(index: Int, category: String, modifier: Modifier = Modifier) {
    val clamped = index.coerceIn(0, 5)
    Column(
        modifier.semantics {
            contentDescription = "OpenWeather air quality $clamped of 5, $category. Not EPA AQI."
        },
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Wx.space1),
        ) {
            (1..5).forEach { tick ->
                Box(
                    Modifier
                        .weight(1f)
                        .height(6.dp)
                        .background(if (tick <= clamped) Wx.accent else Wx.surfaceRaised),
                )
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = Wx.space1),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("OW 1–5", color = Wx.textMuted, fontSize = Wx.meta, letterSpacing = 1.sp)
            Text("$clamped  $category", color = Wx.text, fontSize = Wx.meta)
        }
    }
}
