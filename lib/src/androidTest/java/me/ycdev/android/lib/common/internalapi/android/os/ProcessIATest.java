package me.ycdev.android.lib.common.internalapi.android.os;

import android.test.AndroidTestCase;

import me.ycdev.android.lib.common.utils.Logger;

public class ProcessIATest extends AndroidTestCase {
    private static final String TAG = "ServiceManagerIATest";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Logger.i(TAG, "setup");
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        Logger.i(TAG, "tearDown");
    }

    public void test_setArgV0() {
        assertTrue(ProcessIA.checkReflect_setArgV0());
    }

}
