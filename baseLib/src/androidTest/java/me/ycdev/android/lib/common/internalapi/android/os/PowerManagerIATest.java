package me.ycdev.android.lib.common.internalapi.android.os;

import android.content.Context;
import android.os.IBinder;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class PowerManagerIATest {
    @Test
    public void test_asInterface() {
        IBinder binder = ServiceManagerIA.getService(Context.POWER_SERVICE);
        assertNotNull(binder);

        Object service = PowerManagerIA.asInterface(binder);
        assertNotNull(service);
    }

    @Test
    public void test_getIPowerManager() {
        assertNotNull(PowerManagerIA.getIPowerManager());
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
