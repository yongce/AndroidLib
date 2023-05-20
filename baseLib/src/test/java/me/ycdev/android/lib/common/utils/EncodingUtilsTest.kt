package me.ycdev.android.lib.common.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Assert
import org.junit.Test

class EncodingUtilsTest {
    @Test
    fun encodeWithHex() {
        val data = byteArrayOf(0x1a, 0x2b.toByte(), 0x3c, 0x4d, 0x5c.toByte(), 0x6d, 0x7e.toByte())
        var result = EncodingUtils.encodeWithHex(data, 0, data.size)
        assertThat(result).isEqualTo("1A2B3C4D5C6D7E")
        result = EncodingUtils.encodeWithHex(data, 1, 4, true)
        assertThat(result).isEqualTo("2B3C4D")
        result = EncodingUtils.encodeWithHex(data, 3, 20)
        assertThat(result).isEqualTo("4D5C6D7E")

        // lowercase
        result = EncodingUtils.encodeWithHex(data, 0, data.size, false)
        assertThat(result).isEqualTo("1a2b3c4d5c6d7e")
        result = EncodingUtils.encodeWithHex(data, 1, 4, false)
        assertThat(result).isEqualTo("2b3c4d")
        result = EncodingUtils.encodeWithHex(data, 3, 20, false)
        assertThat(result).isEqualTo("4d5c6d7e")
    }

    @Test
    fun test_fromHexString() {
        val hexStr = "01020304050607"
        val hexStr2 = " 010 20 30 405 060 7 "
        val hexStr3 = "010 203 040 506 07"
        val data = byteArrayOf(0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07)
        assertThat(EncodingUtils.fromHexString(hexStr)).isEqualTo(data)
        assertThat(EncodingUtils.fromHexString(hexStr2)).isEqualTo(data)
        assertThat(EncodingUtils.fromHexString(hexStr3)).isEqualTo(data)
    }

    @Test
    fun test_illegalLength() {
        val e = Assert.assertThrows(IllegalArgumentException::class.java) {
            EncodingUtils.fromHexString("10101")
        }
        assertThat(e).hasMessageThat().startsWith("Bad length: 10101")
    }

    @Test
    fun test_illegalCharacter() {
        val e = Assert.assertThrows(IllegalArgumentException::class.java) {
            EncodingUtils.fromHexString("10101X")
        }
        assertThat(e).hasMessageThat().startsWith("Not hex string: 10101X")
    }
}
