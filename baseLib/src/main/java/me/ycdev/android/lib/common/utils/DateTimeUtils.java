package me.ycdev.android.lib.common.utils;

import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtils {
    /**
     * Generate file name from system time in the format "yyyyMMdd-HHmmss-SSS",
     * @param sysTime System time in milliseconds
     */
    @NonNull
    public static String generateFileName(long sysTime) {
        return new SimpleDateFormat("yyyyMMdd-HHmmss-SSS", Locale.US).format(new Date(sysTime));
    }

    /**
     * Parse system time from string in the format "yyyyMMdd-HHmmss-SSS",
     * @param timeStr Time string in the format "yyyyMMdd-HHmmss-SSS"
     */
    public static long parseFileName(@NonNull String timeStr) throws ParseException {
        return new SimpleDateFormat("yyyyMMdd-HHmmss-SSS", Locale.US).parse(timeStr).getTime();
    }

    /**
     * Generate file name from system time in the format "yyyy-MM-dd HH:mm:ss:SSS",
     * @param timeStamp System time in milliseconds
     */
    @NonNull
    public static String getReadableTimeStamp(long timeStamp) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.US).format(new Date(timeStamp));
    }

    /**
     * Format the time usage to string like "1d17h37m3s728ms"
     */
    @NonNull
    public static String getReadableTimeUsage(long timeUsageMs) {
        long millisecondsLeft = timeUsageMs % 1000;
        if (timeUsageMs == millisecondsLeft) {
            return millisecondsLeft + "ms";
        }

        long seconds = timeUsageMs / 1000;
        long secondsLeft = seconds % 60;
        if (secondsLeft == seconds) {
            return secondsLeft + "s" + millisecondsLeft + "ms";
        }

        long minutes = seconds / 60;
        long minutesLeft = minutes % 60;
        if (minutesLeft == minutes) {
            return minutesLeft + "m" + secondsLeft + "s" + millisecondsLeft + "ms";
        }

        long hours = minutes / 60;
        long hoursLeft = hours % 24;
        if (hoursLeft == hours) {
            return hoursLeft + "h" + minutesLeft + "m" + secondsLeft + "s" + millisecondsLeft + "ms";
        }

        long days = hours / 24;
        return days + "d" + hoursLeft + "h" + minutesLeft + "m" + secondsLeft + "s" + millisecondsLeft + "ms";
    }
}
