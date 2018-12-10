package me.ycdev.android.lib.common.type

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class IntegerHolderTest {
    @Test
    fun basic() {
        run {
            val holder = IntegerHolder(0)
            assertThat(holder.value).isEqualTo(0)
        }

        run {
            val holder = IntegerHolder(-10)
            assertThat(holder.value).isEqualTo(-10)
        }

        run {
            val holder = IntegerHolder(100)
            assertThat(holder.value).isEqualTo(100)
        }
    }
}
