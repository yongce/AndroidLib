package me.ycdev.android.lib.common;

import android.app.Application;

import me.ycdev.android.lib.common.utils.DebugUtils;
import me.ycdev.android.lib.common.utils.LibLogger;

public class AndroidLibTestApplication extends Application {
    private static final String TAG = "AndroidLibTestApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        LibLogger.d(TAG, "onCreate");
        DebugUtils.enableStrictMode();
    }
}
