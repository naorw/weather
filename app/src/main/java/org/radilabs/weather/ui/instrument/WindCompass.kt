package org.radilabs.weather.ui.instrument

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import org.radilabs.weather.ui.theme.Wx

@Composable
fun WindCompass(directionDeg: Double?, modifier: Modifier = Modifier) {
    val description = directionDeg?.let { "Wind from ${it.toInt()} degrees" } ?: "Wind direction unknown"
    Canvas(
        modifier
            .size(36.dp)
            .semantics { contentDescription = description },
    ) {
        val color = Wx.accent
        val muted = Wx.border
        val stroke = Stroke(width = size.minDimension * 0.06f, cap = StrokeCap.Square)
        val c = Offset(size.width / 2f, size.height / 2f)
        val r = size.minDimension * 0.42f
        drawCircle(muted, radius = r, center = c, style = stroke)
        drawLine(muted, Offset(c.x, c.y - r), Offset(c.x, c.y - r * 0.7f), stroke.width, StrokeCap.Square)
        if (directionDeg != null) {
            rotate(degrees = directionDeg.toFloat(), pivot = c) {
                drawLine(
                    color,
                    Offset(c.x, c.y + r * 0.15f),
                    Offset(c.x, c.y - r * 0.85f),
                    stroke.width * 1.4f,
                    StrokeCap.Square,
                )
            }
        }
    }
}
