package me.ycdev.android.lib.common.internalapi.android.os

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PowerManagerIATest {
    @Test
    fun test_asInterface() {
        val binder = ServiceManagerIA.getService(Context.POWER_SERVICE)
        assertNotNull(binder)

        val service = PowerManagerIA.asInterface(binder!!)
        assertNotNull(service)
    }

    @Test
    fun test_getIPowerManager() {
        assertNotNull(PowerManagerIA.iPowerManager)
    }

    @Test
    fun test_reboot() {
        assertTrue(PowerManagerIA.checkReflectReboot())
    }

    @Test
    fun test_shutdown() {
        assertTrue(PowerManagerIA.checkReflectShutdown())
    }

    @Test
    fun test_crash() {
        assertTrue(PowerManagerIA.checkReflectCrash())
    }

    @Test
    fun test_goToSleep() {
        assertTrue(PowerManagerIA.checkReflectGoToSleep())
    }
}
