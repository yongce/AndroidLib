package me.ycdev.android.lib.common.type

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LongHolderTest {
    @Test
    fun basic() {
        run {
            val holder = LongHolder(0L)
            assertThat(holder.value).isEqualTo(0)
        }

        run {
            val holder = LongHolder(-10L)
            assertThat(holder.value).isEqualTo(-10L)
        }

        run {
            val holder = LongHolder(100L)
            assertThat(holder.value).isEqualTo(100L)
        }
    }
}
