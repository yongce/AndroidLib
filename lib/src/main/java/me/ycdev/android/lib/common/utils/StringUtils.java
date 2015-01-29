package me.ycdev.android.lib.common.utils;

import android.support.annotation.NonNull;

public class StringUtils {
    public static int parseInt(@NonNull String value, int defValue) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return defValue;
        }
    }

    public static long parseLong(@NonNull String value, long defValue) {
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            return defValue;
        }
    }
}
