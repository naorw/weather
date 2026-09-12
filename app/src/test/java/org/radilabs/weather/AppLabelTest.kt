package org.radilabs.weather

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AppLabelTest {
    @Test
    fun launcherLabelIsWxWeather() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        assertEquals("WX Weather", context.getString(R.string.app_name))
        val appInfo = context.packageManager.getApplicationInfo(context.packageName, 0)
        assertEquals("WX Weather", context.packageManager.getApplicationLabel(appInfo).toString())
        assertEquals("org.radilabs.weather", context.packageName)
    }
}
