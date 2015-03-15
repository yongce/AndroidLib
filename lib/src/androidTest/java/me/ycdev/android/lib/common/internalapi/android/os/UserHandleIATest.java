package me.ycdev.android.lib.common.internalapi.android.os;

import android.test.AndroidTestCase;

import me.ycdev.android.lib.common.utils.TestLogger;

public class UserHandleIATest extends AndroidTestCase {
    private static final String TAG = "UserHandleIATest";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TestLogger.i(TAG, "setup");
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        TestLogger.i(TAG, "tearDown");
    }

    public void test_myUserId() {
        assertTrue(UserHandleIA.checkReflect_myUserId());
    }

}
