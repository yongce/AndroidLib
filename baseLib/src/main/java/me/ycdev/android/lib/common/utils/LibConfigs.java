package me.ycdev.android.lib.common.utils;

import android.support.annotation.RestrictTo;

import me.ycdev.android.lib.common.BuildConfig;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class LibConfigs {
    public static final boolean DISABLE_LOG = BuildConfig.DISABLE_LOG;
    public static final boolean DEBUG_LOG = BuildConfig.DEBUG_LOG;
}
