package org.radilabs.weather.ui.today

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import org.radilabs.weather.ui.theme.Wx

@Composable
fun WeatherGlyph(id: GlyphId, modifier: Modifier = Modifier, color: Color = Wx.text) {
    Canvas(modifier.size(24.dp)) {
        val s = Stroke(width = size.minDimension * 0.08f, cap = StrokeCap.Square)
        val cx = size.width / 2f
        val cy = size.height / 2f
        val r = size.minDimension * 0.28f
        when (id) {
            GlyphId.Clear -> {
                drawCircle(color, radius = r * 0.55f, center = Offset(cx, cy), style = s)
                for (i in 0 until 8) {
                    val a = Math.toRadians((i * 45).toDouble())
                    val inner = r * 0.9f
                    val outer = r * 1.45f
                    drawLine(
                        color,
                        Offset(cx + inner * Math.cos(a).toFloat(), cy + inner * Math.sin(a).toFloat()),
                        Offset(cx + outer * Math.cos(a).toFloat(), cy + outer * Math.sin(a).toFloat()),
                        strokeWidth = s.width,
                        cap = StrokeCap.Square,
                    )
                }
            }
            GlyphId.PartlyCloudy -> {
                drawCircle(color, radius = r * 0.45f, center = Offset(cx * 0.7f, cy * 0.7f), style = s)
                drawCircle(color, radius = r * 0.7f, center = Offset(cx * 1.1f, cy * 1.15f), style = s)
            }
            GlyphId.Cloudy, GlyphId.Overcast -> {
                drawCircle(color, radius = r * 0.65f, center = Offset(cx, cy), style = s)
                drawCircle(color, radius = r * 0.45f, center = Offset(cx * 0.65f, cy * 1.1f), style = s)
            }
            GlyphId.Drizzle, GlyphId.LightRain, GlyphId.Rain, GlyphId.HeavyRain -> {
                drawCircle(color, radius = r * 0.55f, center = Offset(cx, cy * 0.75f), style = s)
                val drops = if (id == GlyphId.HeavyRain) 4 else 3
                for (i in 0 until drops) {
                    val x = cx * (0.55f + i * 0.28f)
                    drawLine(color, Offset(x, cy * 1.25f), Offset(x, cy * 1.55f), s.width, StrokeCap.Square)
                }
            }
            GlyphId.Thunderstorm -> {
                drawCircle(color, radius = r * 0.55f, center = Offset(cx, cy * 0.7f), style = s)
                drawLine(color, Offset(cx * 1.1f, cy * 1.05f), Offset(cx * 0.85f, cy * 1.35f), s.width * 1.4f, StrokeCap.Square)
                drawLine(color, Offset(cx * 0.85f, cy * 1.35f), Offset(cx * 1.15f, cy * 1.35f), s.width * 1.4f, StrokeCap.Square)
                drawLine(color, Offset(cx * 1.15f, cy * 1.35f), Offset(cx * 0.9f, cy * 1.7f), s.width * 1.4f, StrokeCap.Square)
            }
            GlyphId.LightSnow, GlyphId.Snow -> {
                drawCircle(color, radius = r * 0.5f, center = Offset(cx, cy * 0.7f), style = s)
                drawCircle(color, radius = r * 0.12f, center = Offset(cx * 0.7f, cy * 1.45f))
                drawCircle(color, radius = r * 0.12f, center = Offset(cx * 1.3f, cy * 1.45f))
            }
            GlyphId.Fog -> {
                drawLine(color, Offset(cx * 0.35f, cy * 0.7f), Offset(cx * 1.65f, cy * 0.7f), s.width, StrokeCap.Square)
                drawLine(color, Offset(cx * 0.45f, cy), Offset(cx * 1.55f, cy), s.width, StrokeCap.Square)
                drawLine(color, Offset(cx * 0.35f, cy * 1.3f), Offset(cx * 1.65f, cy * 1.3f), s.width, StrokeCap.Square)
            }
            GlyphId.Unknown -> {
                drawCircle(color, radius = r, center = Offset(cx, cy), style = s)
                drawLine(color, Offset(cx, cy * 0.7f), Offset(cx, cy * 1.15f), s.width, StrokeCap.Square)
                drawCircle(color, radius = r * 0.08f, center = Offset(cx, cy * 1.4f))
            }
        }
    }
}
