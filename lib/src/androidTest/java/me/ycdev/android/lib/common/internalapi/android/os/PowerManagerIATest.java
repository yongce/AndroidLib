package me.ycdev.android.lib.common.internalapi.android.os;

import android.content.Context;
import android.os.IBinder;
import android.test.AndroidTestCase;

import me.ycdev.android.lib.common.utils.TestLogger;

public class PowerManagerIATest extends AndroidTestCase {
    private static final String TAG = "PowerManagerIATest";

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

    public void test_asInterface() {
        IBinder binder = ServiceManagerIA.getService(Context.POWER_SERVICE);
        assertTrue(binder != null);

        Object service = PowerManagerIA.asInterface(binder);
        assertTrue(service != null);
    }

    public void test_getIPowerManager() {
        assertTrue(PowerManagerIA.getIPowerManager() != null);
    }

    public void test_reboot() {
        assertTrue(PowerManagerIA.checkReflect_reboot());
    }

    public void test_shutdown() {
        assertTrue(PowerManagerIA.checkReflect_shutdown());
    }

    public void test_crash() {
        assertTrue(PowerManagerIA.checkReflect_crash());
    }

    public void test_goToSleep() {
        assertTrue(PowerManagerIA.checkReflect_goToSleep());
    }
}
