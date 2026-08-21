package org.radilabs.weather.ui.instrument

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import org.radilabs.weather.ui.theme.Wx
import kotlin.math.max

data class RangeBarFractions(val start: Float, val span: Float)

fun rangeBarFractions(low: Int, high: Int, scaleMin: Int, scaleMax: Int): RangeBarFractions {
    val lo = minOf(low, high)
    val hi = maxOf(low, high)
    val span = max(1, scaleMax - scaleMin).toFloat()
    val start = ((lo - scaleMin) / span).coerceIn(0f, 1f)
    val end = ((hi - scaleMin) / span).coerceIn(0f, 1f)
    return RangeBarFractions(start = start, span = max(0.02f, end - start))
}

@Composable
fun RangeBar(low: Int, high: Int, scaleMin: Int, scaleMax: Int, modifier: Modifier = Modifier) {
    val fractions = rangeBarFractions(low, high, scaleMin, scaleMax)
    val rest = max(0.001f, 1f - fractions.start - fractions.span)
    Row(
        modifier
            .height(6.dp)
            .semantics {
                contentDescription = "Range $low to $high degrees"
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(Modifier.weight(max(0.001f, fractions.start)))
        Box(
            Modifier
                .weight(fractions.span)
                .height(6.dp)
                .background(Wx.accent),
        )
        Spacer(Modifier.weight(rest))
    }
}
