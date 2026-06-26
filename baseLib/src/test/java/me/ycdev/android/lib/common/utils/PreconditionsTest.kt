package me.ycdev.android.lib.common.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Assert.assertThrows
import org.junit.Test

class PreconditionsTest {
    @Test
    fun checkArgument_allowsTrueAndRejectsFalse() {
        Preconditions.checkArgument(true)

        assertThrows(IllegalArgumentException::class.java) {
            Preconditions.checkArgument(false)
        }
    }

    @Test
    fun checkNotNull_returnsValueAndRejectsNull() {
        val value = Any()

        assertThat(Preconditions.checkNotNull(value)).isSameInstanceAs(value)
        assertThrows(NullPointerException::class.java) {
            Preconditions.checkNotNull(null)
        }
    }
}
