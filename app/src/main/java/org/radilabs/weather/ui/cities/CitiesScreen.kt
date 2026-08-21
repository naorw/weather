package org.radilabs.weather.ui.cities

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import org.radilabs.weather.places.Place
import org.radilabs.weather.places.PlaceSource
import org.radilabs.weather.ui.instrument.Hairline
import org.radilabs.weather.ui.instrument.SectionLabel
import org.radilabs.weather.ui.theme.Wx

@Composable
fun CitiesScreen(
    saved: List<Place>,
    active: Place,
    results: List<Place>,
    status: String,
    permissionDenied: Boolean,
    onSearch: (String) -> Unit,
    onSelectResult: (Place) -> Unit,
    onActivate: (Place) -> Unit,
    onRemove: (Place) -> Unit,
    onSaveActive: () -> Unit,
    onUseDevice: () -> Unit,
) {
    var query by remember { mutableStateOf("") }
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Wx.space4),
    ) {
        Text("CITIES", color = Wx.text, fontSize = 18.sp, letterSpacing = 3.sp)
        Spacer(Modifier.height(Wx.space3))
        SectionLabel("SEARCH")
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = Wx.space2)
                .height(Wx.touchMin)
                .border(Wx.hairline, Wx.border)
                .padding(horizontal = Wx.space3),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BasicTextField(
                value = query,
                onValueChange = { query = it },
                singleLine = true,
                textStyle = TextStyle(color = Wx.text, fontSize = Wx.body),
                cursorBrush = SolidColor(Wx.accent),
                modifier = Modifier.weight(1f),
                decorationBox = { inner ->
                    if (query.isEmpty()) {
                        Text("City name", color = Wx.disabled, fontSize = Wx.body)
                    }
                    inner()
                },
            )
            Text(
                "GO",
                color = Wx.accent,
                fontSize = Wx.nav,
                letterSpacing = 1.5.sp,
                modifier = Modifier
                    .clickable { onSearch(query) }
                    .padding(Wx.space2),
            )
        }
        if (status.isNotBlank()) {
            Text(status, color = Wx.amber, fontSize = Wx.meta, modifier = Modifier.padding(top = Wx.space2))
        }
        results.forEach { place ->
            PlaceRow(
                place = place,
                active = false,
                action = "SAVE",
                onAction = { onSelectResult(place) },
            )
        }
        Hairline(Modifier.padding(vertical = Wx.space4))
        SectionLabel("DEVICE")
        val deviceLabel = if (permissionDenied) {
            "PERMISSION DENIED · SEARCH STILL WORKS"
        } else {
            "USE DEVICE LOCATION"
        }
        Text(
            deviceLabel,
            color = if (permissionDenied) Wx.disabled else Wx.accent,
            fontSize = Wx.nav,
            letterSpacing = 1.5.sp,
            modifier = Modifier
                .fillMaxWidth()
                .height(Wx.touchMin)
                .clickable(enabled = !permissionDenied, onClick = onUseDevice)
                .padding(top = Wx.space2),
        )
        if (active.source == PlaceSource.Device && saved.none { it.cacheKey == active.cacheKey }) {
            Text(
                "SAVE ACTIVE DEVICE PLACE",
                color = Wx.accent,
                fontSize = Wx.nav,
                letterSpacing = 1.5.sp,
                modifier = Modifier
                    .height(Wx.touchMin)
                    .clickable(onClick = onSaveActive)
                    .padding(top = Wx.space1),
            )
        }
        Hairline(Modifier.padding(vertical = Wx.space4))
        SectionLabel("SAVED")
        if (saved.isEmpty()) {
            Text(
                "None. Stockholm remains the default until you save a city.",
                color = Wx.textMuted,
                fontSize = Wx.body,
                modifier = Modifier.padding(top = Wx.space2),
            )
        }
        saved.forEach { place ->
            PlaceRow(
                place = place,
                active = place.cacheKey == active.cacheKey,
                action = "REMOVE",
                onAction = { onRemove(place) },
                onBody = { onActivate(place) },
            )
        }
        Spacer(Modifier.height(Wx.space4))
        Text(
            "ACTIVE ${active.displayName.uppercase()}",
            color = Wx.textMuted,
            fontSize = Wx.meta,
            letterSpacing = 1.5.sp,
        )
    }
}

@Composable
private fun PlaceRow(
    place: Place,
    active: Boolean,
    action: String,
    onAction: () -> Unit,
    onBody: (() -> Unit)? = null,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(Wx.touchMin)
            .then(if (onBody != null) Modifier.clickable(onClick = onBody) else Modifier)
            .then(if (active) Modifier.border(Wx.hairline, Wx.accent) else Modifier)
            .padding(horizontal = Wx.space1),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(Modifier.weight(1f)) {
            Text(place.displayName.uppercase(), color = Wx.text, fontSize = Wx.body, letterSpacing = 1.sp)
            Text(place.contextLabel(), color = Wx.textMuted, fontSize = Wx.meta)
        }
        Text(
            action,
            color = Wx.accent,
            fontSize = Wx.nav,
            letterSpacing = 1.5.sp,
            modifier = Modifier
                .clickable(onClick = onAction)
                .padding(Wx.space2),
        )
    }
}
