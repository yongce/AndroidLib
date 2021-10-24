package me.ycdev.android.lib.common.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class TypeUtilsTest {
    @Test
    fun getRawType() {
        assertThat(TypeUtils.getRawType(TypeUtils::class.java)).isEqualTo(TypeUtils::class.java)
        assertThat(TypeUtils.getRawType(dummyArrayList().javaClass)).isEqualTo(ArrayList::class.java)
        assertThat(TypeUtils.getRawType(Array<String>::class.java)).isEqualTo(Array<String>::class.java)
    }

    private fun dummyArrayList(): ArrayList<String> = arrayListOf()

    private fun dummyArray(): Array<String> = arrayOf()
}
