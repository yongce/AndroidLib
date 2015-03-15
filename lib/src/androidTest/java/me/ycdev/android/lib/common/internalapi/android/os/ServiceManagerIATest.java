package me.ycdev.android.lib.common.internalapi.android.os;

import android.test.AndroidTestCase;

import me.ycdev.android.lib.common.utils.TestLogger;

public class ServiceManagerIATest extends AndroidTestCase {
    private static final String TAG = "ServiceManagerIATest";

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

    public void test_getService() {
        assertTrue(ServiceManagerIA.checkReflect_getService());
    }

    public void test_checkService() {
        assertTrue(ServiceManagerIA.checkReflect_checkService());
    }

    public void test_addService() {
        assertTrue(ServiceManagerIA.checkReflect_addService());
    }

    public void test_listServices() {
        assertTrue(ServiceManagerIA.checkReflect_listServices());
    }
}
