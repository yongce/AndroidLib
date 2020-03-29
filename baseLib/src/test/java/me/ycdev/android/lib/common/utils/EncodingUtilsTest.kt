package me.ycdev.android.lib.common.utils

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.startsWith
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

class EncodingUtilsTest {
    @get:Rule
    var thrownRule = ExpectedException.none()

    @Test
    fun encodeWithHex() {
        val data = byteArrayOf(0x1a, 0x2b.toByte(), 0x3c, 0x4d, 0x5c.toByte(), 0x6d, 0x7e.toByte())
        var result = EncodingUtils.encodeWithHex(data, 0, data.size)
        assertThat(result, equalTo("1A2B3C4D5C6D7E"))
        result = EncodingUtils.encodeWithHex(data, 1, 4, true)
        assertThat(result, equalTo("2B3C4D"))
        result = EncodingUtils.encodeWithHex(data, 3, 20)
        assertThat(result, equalTo("4D5C6D7E"))

        // lowercase
        result = EncodingUtils.encodeWithHex(data, 0, data.size, false)
        assertThat(result, equalTo("1a2b3c4d5c6d7e"))
        result = EncodingUtils.encodeWithHex(data, 1, 4, false)
        assertThat(result, equalTo("2b3c4d"))
        result = EncodingUtils.encodeWithHex(data, 3, 20, false)
        assertThat(result, equalTo("4d5c6d7e"))
    }

    @Test
    fun test_fromHexString() {
        val hexStr = "01020304050607"
        val hexStr2 = " 010 20 30 405 060 7 "
        val hexStr3 = "010 203 040 506 07"
        val data = byteArrayOf(0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07)
        assertThat(EncodingUtils.fromHexString(hexStr), equalTo(data))
        assertThat(EncodingUtils.fromHexString(hexStr2), equalTo(data))
        assertThat(EncodingUtils.fromHexString(hexStr3), equalTo(data))
    }

    @Test
    fun test_illegalLength() {
        thrownRule.expect(IllegalArgumentException::class.java)
        thrownRule.expectMessage(startsWith("Bad length: 10101"))

        val hexStr = "10101"
        EncodingUtils.fromHexString(hexStr)
    }

    @Test
    fun test_illegalCharacter() {
        thrownRule.expect(IllegalArgumentException::class.java)
        thrownRule.expectMessage(startsWith("Not hex string: 10101X"))

        val hexStr = "10101X"
        EncodingUtils.fromHexString(hexStr)
    }
}
