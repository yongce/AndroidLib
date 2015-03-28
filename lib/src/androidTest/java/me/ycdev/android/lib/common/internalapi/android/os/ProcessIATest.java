package me.ycdev.android.lib.common.internalapi.android.os;

import android.test.AndroidTestCase;

public class ProcessIATest extends AndroidTestCase {
    public void test_setArgV0() {
        assertTrue("failed to reflect #setArgV0", ProcessIA.checkReflect_setArgV0());
    }

    public void test_readProcLines() {
        assertTrue("failed to reflect #readProcLines", ProcessIA.checkReflect_readProcLines());
    }

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
}
