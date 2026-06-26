package me.ycdev.android.lib.test.log

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class AndroidLogHelperTest {
    @Test
    fun getPriorityName_mapsKnownPriorities() {
        assertThat(AndroidLogHelper.getPriorityName(AndroidLogHelper.VERBOSE)).isEqualTo("V")
        assertThat(AndroidLogHelper.getPriorityName(AndroidLogHelper.DEBUG)).isEqualTo("D")
        assertThat(AndroidLogHelper.getPriorityName(AndroidLogHelper.INFO)).isEqualTo("I")
        assertThat(AndroidLogHelper.getPriorityName(AndroidLogHelper.WARN)).isEqualTo("W")
        assertThat(AndroidLogHelper.getPriorityName(AndroidLogHelper.ERROR)).isEqualTo("E")
        assertThat(AndroidLogHelper.getPriorityName(AndroidLogHelper.ASSERT)).isEqualTo("A")
    }

    @Test
    fun getPriorityName_returnsUnknownForUnexpectedPriority() {
        assertThat(AndroidLogHelper.getPriorityName(Int.MIN_VALUE)).isEqualTo("U")
    }
}
