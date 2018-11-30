package me.ycdev.android.lib.common.internalapi.android.os;

import android.content.Context;
import android.os.IBinder;
import androidx.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import me.ycdev.android.lib.common.utils.LibLogger;

import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class PowerManagerIATest {
    private static final String TAG = "PowerManagerIATest";

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
        IBinder binder = ServiceManagerIA.getService(Context.POWER_SERVICE);
        assertTrue(binder != null);

        Object service = PowerManagerIA.asInterface(binder);
        assertTrue(service != null);
    }

    @Test
    public void test_getIPowerManager() {
        assertTrue(PowerManagerIA.getIPowerManager() != null);
    }

    @Test
    public void test_reboot() {
        assertTrue(PowerManagerIA.checkReflect_reboot());
    }

    @Test
    public void test_shutdown() {
        assertTrue(PowerManagerIA.checkReflect_shutdown());
    }

    @Test
    public void test_crash() {
        assertTrue(PowerManagerIA.checkReflect_crash());
    }

    @Test
    public void test_goToSleep() {
        assertTrue(PowerManagerIA.checkReflect_goToSleep());
    }
}
