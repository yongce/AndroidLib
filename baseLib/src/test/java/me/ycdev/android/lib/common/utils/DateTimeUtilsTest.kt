package me.ycdev.android.lib.common.utils

import com.google.common.truth.Truth.assertThat
import java.text.ParseException
import java.util.TimeZone
import org.junit.After
import org.junit.Assert.assertThrows
import org.junit.Test

class DateTimeUtilsTest {
    @After
    fun tearDown() {
        DateTimeUtils.setTimeZoneForTestCases(TimeZone.getDefault())
    }

    @Test
    fun timestamp_roundTripsWithConfiguredTimezone() {
        DateTimeUtils.setTimeZoneForTestCases(TimeZone.getTimeZone("UTC"))

        val timestamp = DateTimeUtils.parseTimestamp("1970-01-02 03:04:05:006")

        assertThat(timestamp).isEqualTo(97_445_006L)
        assertThat(DateTimeUtils.getReadableTimeStamp(timestamp))
            .isEqualTo("1970-01-02 03:04:05:006")
    }

    @Test
    fun fileName_roundTripsSystemTime() {
        val timestamp = 97_445_006L

        val fileName = DateTimeUtils.generateFileName(timestamp)

        assertThat(DateTimeUtils.parseFileName(fileName)).isEqualTo(timestamp)
    }

    @Test
    fun parseFileName_rejectsBadInput() {
        assertThrows(ParseException::class.java) {
            DateTimeUtils.parseFileName("not-a-date")
        }
    }

    @Test
    fun readableTimeUsage_formatsEveryUnit() {
        assertThat(DateTimeUtils.getReadableTimeUsage(7L)).isEqualTo("7ms")
        assertThat(DateTimeUtils.getReadableTimeUsage(1_234L)).isEqualTo("1s234ms")
        assertThat(DateTimeUtils.getReadableTimeUsage(62_345L)).isEqualTo("1m2s345ms")
        assertThat(DateTimeUtils.getReadableTimeUsage(3_723_456L)).isEqualTo("1h2m3s456ms")
        assertThat(DateTimeUtils.getReadableTimeUsage(93_723_456L)).isEqualTo("1d2h2m3s456ms")
    }
}
