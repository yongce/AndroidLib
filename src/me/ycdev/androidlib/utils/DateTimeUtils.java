package me.ycdev.androidlib.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtils {
    /**
     * Generate file name from system time in the format "yyyyMMdd-HHmmss-SSS",
     * @param sysTime System time in milliseconds
     */
    public static String generateFileName(long sysTime) {
        return new SimpleDateFormat("yyyyMMdd-HHmmss-SSS", Locale.US).format(new Date(sysTime));
    }
}
