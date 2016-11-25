package me.ycdev.android.lib.common.internalapi.android.os;

import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import me.ycdev.android.lib.common.utils.LibLogger;

import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ServiceManagerIATest {
    private static final String TAG = "ServiceManagerIATest";

    @Before
    public void setUp() throws Exception {
        LibLogger.i(TAG, "setup");
    }

    @After
    public void tearDown() throws Exception {
        LibLogger.i(TAG, "tearDown");
    }

    @Test
    public void test_getService() {
        assertTrue(ServiceManagerIA.checkReflect_getService());
    }

    @Test
    public void test_checkService() {
        assertTrue(ServiceManagerIA.checkReflect_checkService());
    }

    @Test
    public void test_addService() {
        assertTrue(ServiceManagerIA.checkReflect_addService());
    }

    @Test
    public void test_listServices() {
        assertTrue(ServiceManagerIA.checkReflect_listServices());
    }
}
