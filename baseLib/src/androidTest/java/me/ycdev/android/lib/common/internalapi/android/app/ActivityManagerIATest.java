package me.ycdev.android.lib.common.internalapi.android.app;

import android.content.Context;
import android.os.IBinder;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import me.ycdev.android.lib.common.internalapi.android.os.ServiceManagerIA;
import me.ycdev.android.lib.common.utils.LibLogger;

import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ActivityManagerIATest {
    private static final String TAG = "ActivityManagerIATest";

    @Before
    public void setUp() throws Exception {
        LibLogger.i(TAG, "setup");
    }

    @After
    public void tearDown() throws Exception {
        LibLogger.i(TAG, "tearDown");
    }

    @Test
    public void test_asInterface() {
        IBinder binder = ServiceManagerIA.getService(Context.ACTIVITY_SERVICE);
        assertTrue(binder != null);

        Object service = ActivityManagerIA.asInterface(binder);
        assertTrue(service != null);
    }

    @Test
    public void test_getIActivityManager() {
        assertTrue(ActivityManagerIA.getIActivityManager() != null);
    }

    @Test
    public void test_forceStopPackage() {
        assertTrue(ActivityManagerIA.checkReflect_forceStopPackage());
    }

}
