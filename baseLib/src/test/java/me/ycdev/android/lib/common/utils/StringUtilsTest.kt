package me.ycdev.android.lib.common.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class StringUtilsTest {
    @Test
    fun trimPrefixSpaces_trimsAsciiControlSpacesAndNoBreakSpace() {
        val text = "\t \u00a0 hello "

        assertThat(StringUtils.trimPrefixSpaces(text)).isEqualTo("hello ")
    }

    @Test
    fun trimPrefixSpaces_keepsNonPrefixSpaces() {
        val text = "hello \u00a0"

        assertThat(StringUtils.trimPrefixSpaces(text)).isSameInstanceAs(text)
    }

    @Test
    fun parseInt_returnsDefaultForInvalidInput() {
        assertThat(StringUtils.parseInt("42", -1)).isEqualTo(42)
        assertThat(StringUtils.parseInt("4.2", -1)).isEqualTo(-1)
        assertThat(StringUtils.parseInt("", -1)).isEqualTo(-1)
    }

    @Test
    fun parseLong_returnsDefaultForInvalidInput() {
        assertThat(StringUtils.parseLong("9223372036854775807", -1L))
            .isEqualTo(Long.MAX_VALUE)
        assertThat(StringUtils.parseLong("9223372036854775808", -1L)).isEqualTo(-1L)
        assertThat(StringUtils.parseLong("abc", -1L)).isEqualTo(-1L)
    }
}
