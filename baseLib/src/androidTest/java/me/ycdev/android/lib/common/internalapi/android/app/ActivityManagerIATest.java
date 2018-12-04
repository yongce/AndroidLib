package me.ycdev.android.lib.common.internalapi.android.app;

import android.content.Context;
import android.os.IBinder;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import me.ycdev.android.lib.common.internalapi.android.os.ServiceManagerIA;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ActivityManagerIATest {
    @Test
    public void test_asInterface() {
        IBinder binder = ServiceManagerIA.getService(Context.ACTIVITY_SERVICE);
        assertNotNull(binder);

        Object service = ActivityManagerIA.asInterface(binder);
        assertNotNull(service);
    }

    @Test
    public void test_getIActivityManager() {
        assertNotNull(ActivityManagerIA.getIActivityManager());
    }

    @Test
    public void test_forceStopPackage() {
        assertTrue(ActivityManagerIA.checkReflect_forceStopPackage());
    }

}
