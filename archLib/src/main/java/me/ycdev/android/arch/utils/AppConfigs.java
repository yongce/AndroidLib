package me.ycdev.android.arch.utils;

import me.ycdev.android.arch.BuildConfig;

public class AppConfigs {
    private AppConfigs() {
        // nothing to do
    }

    public static final boolean DISABLE_LOG = BuildConfig.DISABLE_LOG;
    public static final boolean DEBUG_LOG = BuildConfig.DEBUG_LOG;
}
