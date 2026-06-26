package me.ycdev.android.lib.common.kotlinx

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class IsNullOrEmptyTest {
    @Test
    fun primitiveArrays_returnTrueForNullOrEmpty() {
        val booleans: BooleanArray? = null
        val chars: CharArray? = null
        val bytes: ByteArray? = null
        val shorts: ShortArray? = null
        val ints: IntArray? = null
        val longs: LongArray? = null
        val floats: FloatArray? = null
        val doubles: DoubleArray? = null

        assertThat(booleans.isNullOrEmpty()).isTrue()
        assertThat(chars.isNullOrEmpty()).isTrue()
        assertThat(bytes.isNullOrEmpty()).isTrue()
        assertThat(shorts.isNullOrEmpty()).isTrue()
        assertThat(ints.isNullOrEmpty()).isTrue()
        assertThat(longs.isNullOrEmpty()).isTrue()
        assertThat(floats.isNullOrEmpty()).isTrue()
        assertThat(doubles.isNullOrEmpty()).isTrue()

        assertThat(booleanArrayOf().isNullOrEmpty()).isTrue()
        assertThat(charArrayOf().isNullOrEmpty()).isTrue()
        assertThat(byteArrayOf().isNullOrEmpty()).isTrue()
        assertThat(shortArrayOf().isNullOrEmpty()).isTrue()
        assertThat(intArrayOf().isNullOrEmpty()).isTrue()
        assertThat(longArrayOf().isNullOrEmpty()).isTrue()
        assertThat(floatArrayOf().isNullOrEmpty()).isTrue()
        assertThat(doubleArrayOf().isNullOrEmpty()).isTrue()
    }

    @Test
    fun primitiveArrays_returnFalseForNonEmpty() {
        assertThat(booleanArrayOf(false).isNullOrEmpty()).isFalse()
        assertThat(charArrayOf('a').isNullOrEmpty()).isFalse()
        assertThat(byteArrayOf(1).isNullOrEmpty()).isFalse()
        assertThat(shortArrayOf(1).isNullOrEmpty()).isFalse()
        assertThat(intArrayOf(1).isNullOrEmpty()).isFalse()
        assertThat(longArrayOf(1).isNullOrEmpty()).isFalse()
        assertThat(floatArrayOf(1f).isNullOrEmpty()).isFalse()
        assertThat(doubleArrayOf(1.0).isNullOrEmpty()).isFalse()
    }
}
