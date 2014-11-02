package me.ycdev.android.lib.common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StringUtils {
    private static SimpleDateFormat sDateTimeFormat = null;

    private static SimpleDateFormat getDefaultFormatInstance() {
        if (sDateTimeFormat == null) {
            sDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        }
        return sDateTimeFormat;
    }

    /**
     * @param time returned by System.currentTimeMillis(), in milliseconds.
     * @return In format "yyyy-MM-dd HH:mm:ss"
     */
    public static String formatDateTime(long time) {
        return getDefaultFormatInstance().format(new Date(time));
    }

    public static int parseInt(String value, int defValue) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return defValue;
        }
    }

    public static long parseLong(String value, long defValue) {
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            return defValue;
        }
    }
}
