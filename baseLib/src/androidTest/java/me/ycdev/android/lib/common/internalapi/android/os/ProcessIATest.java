package me.ycdev.android.lib.common.internalapi.android.os;

import android.support.test.filters.RequiresDevice;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@RequiresDevice
public class ProcessIATest {
    @Test
    public void test_setArgV0() {
        assertTrue("failed to reflect #setArgV0", ProcessIA.checkReflect_setArgV0());
    }

    @Test
    public void test_readProcLines() {
        assertTrue("failed to reflect #readProcLines", ProcessIA.checkReflect_readProcLines());
    }

    @Test
    public void test_getParentPid() {
        assertTrue("failed to reflect #getParentPid", ProcessIA.checkReflect_getParentPid());
        // app process --> zygote --> init (pid: 1)
        int pid = android.os.Process.myPid();
        int zygotePid = ProcessIA.getParentPid(pid);
        assertTrue("failed to get pid of zygote", zygotePid != pid);
        int initPid = ProcessIA.getParentPid(zygotePid);
        assertTrue("failed to get pid of init", initPid != zygotePid);
        assertTrue("pid of init is not 1", initPid == 1);
    }

    @Test
    public void test_myPpid() {
        assertTrue("failed to reflect #myPpid", ProcessIA.checkReflect_myPpid());
        // app process --> zygote --> init (pid: 1)
        int pid = android.os.Process.myPid();
        int zygotePid = ProcessIA.myPpid();
        assertTrue("failed to get pid of zygote", zygotePid != pid);
        int initPid = ProcessIA.getParentPid(zygotePid);
        assertTrue("failed to get pid of init", initPid != zygotePid);
        assertTrue("unexpected pid of init: " + initPid, initPid == 1);
    }

    @Test
    public void test_getProcessName() {
        // "/init", pid: 1
        String initProcName = ProcessIA.getProcessName(1);
        assertEquals("failed to validate /init process", "/init", initProcName);

        // this test app's process name is the package name
        String myProcName = ProcessIA.getProcessName(android.os.Process.myPid());
        assertEquals("failed to validate the test app", "me.ycdev.android.lib.common.test", myProcName);
    }

    @Test
    public void test_getProcessPid() {
        // "/init", pid: 1
        int initPid = ProcessIA.getProcessPid("/init");
        assertEquals("failed to validate /init process", 1, initPid);

        // this test app's process name is the package name
        int myPid = ProcessIA.getProcessPid("me.ycdev.android.lib.common.test");
        assertEquals("failed to validate the test app", android.os.Process.myPid(), myPid);
    }
}
