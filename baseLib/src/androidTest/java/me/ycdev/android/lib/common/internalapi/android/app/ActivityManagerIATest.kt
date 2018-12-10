package me.ycdev.android.lib.common.internalapi.android.app

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import me.ycdev.android.lib.common.internalapi.android.os.ServiceManagerIA
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ActivityManagerIATest {
    @Test
    fun test_asInterface() {
        val binder = ServiceManagerIA.getService(Context.ACTIVITY_SERVICE)
        assertNotNull(binder)

        val service = ActivityManagerIA.asInterface(binder!!)
        assertNotNull(service)
    }

    @Test
    fun test_getIActivityManager() {
        assertNotNull(ActivityManagerIA.getIActivityManager())
    }

    @Test
    fun test_forceStopPackage() {
        assertTrue(ActivityManagerIA.checkReflect_forceStopPackage())
    }
}
