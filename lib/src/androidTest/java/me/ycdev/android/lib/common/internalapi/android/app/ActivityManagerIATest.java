package me.ycdev.android.lib.common.internalapi.android.app;

import android.content.Context;
import android.os.IBinder;
import android.test.AndroidTestCase;

import me.ycdev.android.lib.common.internalapi.android.app.ActivityManagerIA;
import me.ycdev.android.lib.common.internalapi.android.os.ServiceManagerIA;
import me.ycdev.android.lib.common.utils.Logger;

public class ActivityManagerIATest extends AndroidTestCase {
    private static final String TAG = "ActivityManagerIATest";

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

    public void test_asInterface() {
        IBinder binder = ServiceManagerIA.getService(Context.ACTIVITY_SERVICE);
        assertTrue(binder != null);

        Object service = ActivityManagerIA.asInterface(binder);
        assertTrue(service != null);
    }

    public void test_getIActivityManager() {
        assertTrue(ActivityManagerIA.getIActivityManager() != null);
    }

    public void test_forceStopPackage() {
        assertTrue(ActivityManagerIA.checkReflect_forceStopPackage());
    }

}
