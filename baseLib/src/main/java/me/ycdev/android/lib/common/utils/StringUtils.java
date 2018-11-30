package me.ycdev.android.lib.common.utils;

import androidx.annotation.NonNull;

@SuppressWarnings({"unused", "WeakerAccess"})
public class StringUtils {
    public static String trimPrefixSpaces(String str) {
        final int N = str.length();
        int index = 0;
        while (index < N && (str.charAt(index) <= '\u0020' || str.charAt(index) == '\u00a0')) {
            index++;
        }
        if (index > 0) {
            return str.substring(index);
        }
        return str;
    }

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
