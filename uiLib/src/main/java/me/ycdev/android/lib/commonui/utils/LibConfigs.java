package me.ycdev.android.lib.commonui.utils;

import android.support.annotation.RestrictTo;

import me.ycdev.android.lib.commonui.BuildConfig;

@RestrictTo(RestrictTo.Scope.GROUP_ID)
public class LibConfigs {
    public static final boolean DISABLE_LOG = BuildConfig.DISABLE_LOG;
    public static final boolean DEBUG_LOG = BuildConfig.DEBUG_LOG;
}
