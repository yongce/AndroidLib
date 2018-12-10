package me.ycdev.android.lib.common.internalapi.android.os

import android.os.Build

import org.junit.Test
import org.junit.runner.RunWith

import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Assert.assertEquals

@RunWith(AndroidJUnit4::class)
class SystemPropertiesIATest {

    @Test
    fun test_get() {
        val defValue = "test.defValue"
        var actual = SystemPropertiesIA.get(TEST_KEY_NONE, defValue)
        assertEquals(defValue, actual)

        actual = SystemPropertiesIA.get("ro.product.model", defValue)
        assertEquals(Build.MODEL, actual)

        actual = SystemPropertiesIA.get("ro.build.fingerprint", defValue)
        assertEquals(Build.FINGERPRINT, actual)
    }

    @Test
    fun test_getInt() {
        val defValue = 123
        var actual = SystemPropertiesIA.getInt(TEST_KEY_NONE, defValue)
        assertEquals(defValue.toLong(), actual.toLong())

        actual = SystemPropertiesIA.getInt("ro.build.version.sdk", defValue)
        assertEquals(Build.VERSION.SDK_INT.toLong(), actual.toLong())
    }

    @Test
    fun test_getLong() {
        val defValue: Long = 123
        var actual = SystemPropertiesIA.getLong(TEST_KEY_NONE, defValue)
        assertEquals(defValue, actual)

        // Build.getLong("ro.build.date.utc") * 1000
        actual = SystemPropertiesIA.getLong("ro.build.date.utc", defValue) * 1000
        assertEquals(Build.TIME, actual)
    }

    @Test
    fun test_getBoolean() {
        val defValue = true
        var actual = SystemPropertiesIA.getBoolean(TEST_KEY_NONE, defValue)
        assertEquals(defValue, actual)

        actual = SystemPropertiesIA.getBoolean("ro.debuggable", true)
        val actual2 = SystemPropertiesIA.getBoolean("ro.debuggable", false)
        assertEquals(actual, actual2)
    }

    companion object {
        private const val TEST_KEY_NONE = "test.internalapis.none"
    }
}
