package me.ycdev.androidlib.internalapi.android.os;

import android.content.Context;
import android.os.IBinder;
import android.test.AndroidTestCase;

import me.ycdev.androidlib.utils.Logger;

public class PowerManagerIATest extends AndroidTestCase {
    private static final String TAG = "PowerManagerIATest";

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

    public void testAsInterface() {
        IBinder binder = ServiceManagerIA.getService(Context.POWER_SERVICE);
        assertTrue(binder != null);

        Object service = PowerManagerIA.asInterface(binder);
        assertTrue(service != null);
    }

    public void testReboot() {
        assertTrue(PowerManagerIA.checkRebootReflect());
    }

    public void testShutdown() {
        assertTrue(PowerManagerIA.checkShutdownReflect());
    }

    public void testCrash() {
        assertTrue(PowerManagerIA.checkCrashReflect());
    }
}
