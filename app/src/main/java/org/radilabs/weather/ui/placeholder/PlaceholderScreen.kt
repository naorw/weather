package org.radilabs.weather.ui.placeholder

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import org.radilabs.weather.ui.theme.Wx

@Composable
fun PlaceholderScreen(title: String, copy: String) {
    Column(Modifier.fillMaxSize().padding(Wx.space4)) {
        Text(title.uppercase(), color = Wx.text, fontSize = 18.sp, letterSpacing = 3.sp)
        Spacer(Modifier.height(Wx.space3))
        Text(copy, color = Wx.textMuted, fontSize = Wx.body)
    }
}
