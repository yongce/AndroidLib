package me.ycdev.android.lib.common.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("unused")
object DateTimeUtils {
    /**
     * Generate file name from system time in the format "yyyyMMdd-HHmmss-SSS",
     * @param sysTime System time in milliseconds
     */
    fun generateFileName(sysTime: Long): String {
        return SimpleDateFormat("yyyyMMdd-HHmmss-SSS", Locale.US).format(Date(sysTime))
    }

    /**
     * Parse system time from string in the format "yyyyMMdd-HHmmss-SSS",
     * @param timeStr Time string in the format "yyyyMMdd-HHmmss-SSS"
     */
    @Throws(ParseException::class)
    fun parseFileName(timeStr: String): Long {
        return SimpleDateFormat("yyyyMMdd-HHmmss-SSS", Locale.US).parse(timeStr).time
    }

    /**
     * Generate file name from system time in the format "yyyy-MM-dd HH:mm:ss:SSS",
     * @param timeStamp System time in milliseconds
     */
    fun getReadableTimeStamp(timeStamp: Long): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.US).format(Date(timeStamp))
    }

    /**
     * Format the time usage to string like "1d17h37m3s728ms"
     */
    fun getReadableTimeUsage(timeUsageMs: Long): String {
        val millisecondsLeft = timeUsageMs % 1000
        if (timeUsageMs == millisecondsLeft) {
            return millisecondsLeft.toString() + "ms"
        }

        val seconds = timeUsageMs / 1000
        val secondsLeft = seconds % 60
        if (secondsLeft == seconds) {
            return secondsLeft.toString() + "s" + millisecondsLeft + "ms"
        }

        val minutes = seconds / 60
        val minutesLeft = minutes % 60
        if (minutesLeft == minutes) {
            return minutesLeft.toString() + "m" + secondsLeft + "s" + millisecondsLeft + "ms"
        }

        val hours = minutes / 60
        val hoursLeft = hours % 24
        if (hoursLeft == hours) {
            return hoursLeft.toString() + "h" + minutesLeft + "m" + secondsLeft + "s" + millisecondsLeft + "ms"
        }

        val days = hours / 24
        return days.toString() + "d" + hoursLeft + "h" + minutesLeft + "m" + secondsLeft + "s" + millisecondsLeft + "ms"
    }
}
