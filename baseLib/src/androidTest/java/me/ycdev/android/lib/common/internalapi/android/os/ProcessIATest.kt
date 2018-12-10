package me.ycdev.android.lib.common.internalapi.android.os

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.RequiresDevice

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

@RunWith(AndroidJUnit4::class)
@RequiresDevice
class ProcessIATest {
    @Test
    fun test_setArgV0() {
        assertTrue("failed to reflect #setArgV0", ProcessIA.checkReflect_setArgV0())
    }

    @Test
    fun test_readProcLines() {
        assertTrue("failed to reflect #readProcLines", ProcessIA.checkReflect_readProcLines())
    }

    @Test
    fun test_getParentPid() {
        assertTrue("failed to reflect #getParentPid", ProcessIA.checkReflect_getParentPid())
        // app process --> zygote
        val pid = android.os.Process.myPid()
        val zygotePid = ProcessIA.getParentPid(pid)
        assertTrue("failed to get pid of zygote", zygotePid != pid && zygotePid > 0)
    }

    @Test
    fun test_myPpid() {
        assertTrue("failed to reflect #myPpid", ProcessIA.checkReflect_myPpid())
        // app process --> zygote
        val pid = android.os.Process.myPid()
        val zygotePid = ProcessIA.myPpid()
        assertTrue("failed to get pid of zygote", zygotePid != pid && zygotePid > 0)
    }

    @Test
    fun test_getProcessName() {
        // this test app's process name is the package name
        val myProcName = ProcessIA.getProcessName(android.os.Process.myPid())
        assertEquals(
            "failed to validate the test app",
            "me.ycdev.android.lib.common.test",
            myProcName
        )
    }

    @Test
    fun test_getProcessPid() {
        // this test app's process name is the package name
        val myPid = ProcessIA.getProcessPid("me.ycdev.android.lib.common.test")
        assertEquals(
            "failed to validate the test app",
            android.os.Process.myPid().toLong(),
            myPid.toLong()
        )
    }
}
