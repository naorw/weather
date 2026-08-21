package org.radilabs.weather.ui.instrument

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import org.radilabs.weather.ui.theme.Wx

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text,
        color = Wx.textMuted,
        fontSize = Wx.heading,
        letterSpacing = 2.sp,
        modifier = modifier,
    )
}

@Composable
fun TechnicalLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text,
        color = Wx.textMuted,
        fontSize = Wx.meta,
        letterSpacing = 1.5.sp,
        modifier = modifier,
    )
}
