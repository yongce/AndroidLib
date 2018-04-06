package me.ycdev.android.lib.common;

import android.app.Application;

import me.ycdev.android.lib.common.utils.DebugUtils;
import me.ycdev.android.lib.common.utils.LibLogger;
import timber.log.Timber;

public class AndroidLibTestApplication extends Application {
    private static final String TAG = "BaseLibTestApp";

    @Override
    public void onCreate() {
        super.onCreate();
        LibLogger.d(TAG, "onCreate");
        DebugUtils.enableStrictMode();
        Timber.plant(new Timber.DebugTree());
    }
}
