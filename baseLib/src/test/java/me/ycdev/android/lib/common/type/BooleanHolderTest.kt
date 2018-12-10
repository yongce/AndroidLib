package me.ycdev.android.lib.common.type

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class BooleanHolderTest {
    @Test
    fun basic() {
        run {
            val holder = BooleanHolder(true)
            assertThat(holder.value).isTrue()
        }

        run {
            val holder = BooleanHolder(false)
            assertThat(holder.value).isFalse()
        }
    }
}
