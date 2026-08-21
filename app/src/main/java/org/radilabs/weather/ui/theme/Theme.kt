package org.radilabs.weather.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val scheme = darkColorScheme(
    primary = Wx.accent,
    onPrimary = Wx.base,
    background = Wx.base,
    onBackground = Wx.text,
    surface = Wx.surface,
    onSurface = Wx.text,
    surfaceVariant = Wx.surfaceRaised,
    onSurfaceVariant = Wx.textMuted,
    outline = Wx.border,
    error = Wx.warning,
    onError = Wx.text,
    secondary = Wx.amber,
    onSecondary = Wx.base,
)

@Composable
fun WeatherTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = scheme.copy(scrim = Color.Transparent),
        content = content,
    )
}
