package org.radilabs.weather.persist

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ApiKeyStoreTest {
    private fun store(): ApiKeyStore {
        val context = ApplicationProvider.getApplicationContext<Context>()
        return ApiKeyStore(
            context.getSharedPreferences("test_secrets", Context.MODE_PRIVATE).also { it.edit().clear().commit() },
        )
    }

    @Test
    fun saveReplaceRemoveAndReadBack() {
        val keys = store()
        assertFalse(keys.isConfigured())
        assertNull(keys.read())

        keys.save("  first-key  ")
        assertTrue(keys.isConfigured())
        assertEquals("first-key", keys.read())
        assertTrue(keys.maskedLabel().contains("key"))
        assertFalse(keys.maskedLabel().contains("first-key"))

        keys.save("second-key")
        assertEquals("second-key", keys.read())

        keys.remove()
        assertFalse(keys.isConfigured())
        assertNull(keys.read())
        assertEquals("Not configured", keys.maskedLabel())
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsBlankSave() {
        store().save("   ")
    }
}
