package org.radilabs.weather.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.sp
import org.radilabs.weather.persist.ApiKeyStore
import org.radilabs.weather.ui.theme.Wx

@Composable
fun SettingsScreen(store: ApiKeyStore) {
    var draft by remember { mutableStateOf("") }
    var configured by remember { mutableStateOf(store.isConfigured()) }
    var status by remember { mutableStateOf(store.maskedLabel()) }

    fun refresh() {
        configured = store.isConfigured()
        status = store.maskedLabel()
        draft = ""
    }

    Column(Modifier.fillMaxSize().padding(Wx.space4)) {
        Text("SETTINGS", color = Wx.text, fontSize = 18.sp, letterSpacing = 3.sp)
        Spacer(Modifier.height(Wx.space5))
        Text("OPENWEATHER KEY", color = Wx.textMuted, fontSize = Wx.heading, letterSpacing = 2.sp)
        Spacer(Modifier.height(Wx.space2))
        Text(status, color = if (configured) Wx.accent else Wx.amber, fontSize = Wx.meta)
        Spacer(Modifier.height(Wx.space3))
        Text(
            "Stored on this device only. Not bundled. Not a vault against the device owner.",
            color = Wx.textMuted,
            fontSize = Wx.meta,
        )
        Spacer(Modifier.height(Wx.space4))
        BasicTextField(
            value = draft,
            onValueChange = { draft = it },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            cursorBrush = SolidColor(Wx.focus),
            textStyle = TextStyle(color = Wx.text, fontSize = Wx.body),
            modifier = Modifier
                .fillMaxWidth()
                .border(Wx.hairline, Wx.border)
                .background(Wx.surface)
                .padding(Wx.space3),
            decorationBox = { inner ->
                Box {
                    if (draft.isEmpty()) {
                        Text("Paste key, then save", color = Wx.disabled, fontSize = Wx.body)
                    }
                    inner()
                }
            },
        )
        Spacer(Modifier.height(Wx.space3))
        Row(horizontalArrangement = Arrangement.spacedBy(Wx.space3), verticalAlignment = Alignment.CenterVertically) {
            InstrumentButton("SAVE") {
                if (draft.isNotBlank()) {
                    store.save(draft)
                    refresh()
                }
            }
            InstrumentButton("REMOVE", danger = true) {
                store.remove()
                refresh()
            }
        }
        Spacer(Modifier.height(Wx.space6))
        Text("Weather 0.3.0 · org.radilabs.weather", color = Wx.disabled, fontSize = Wx.meta)
        Spacer(Modifier.height(Wx.space2))
        Text("Units and provider UI belong to later phases.", color = Wx.textMuted, fontSize = Wx.body)
    }
}

@Composable
private fun InstrumentButton(label: String, danger: Boolean = false, onClick: () -> Unit) {
    val color = if (danger) Wx.warning else Wx.accent
    Box(
        Modifier
            .height(Wx.touchMin)
            .border(Wx.hairline, color)
            .clickable(onClick = onClick)
            .padding(horizontal = Wx.space4),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = color, fontSize = Wx.nav, letterSpacing = 1.5.sp)
    }
}
