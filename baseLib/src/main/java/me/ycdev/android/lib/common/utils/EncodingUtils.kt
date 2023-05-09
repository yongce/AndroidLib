package me.ycdev.android.lib.common.utils

object EncodingUtils {
    private val HEX_ARRAY_UPPERCASE =
        charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')
    private val HEX_ARRAY_LOWERCASE =
        charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')

    /**
     * Encode the data with HEX (Base16) encoding
     */
    fun encodeWithHex(bytes: ByteArray?, uppercase: Boolean = true): String {
        return if (bytes == null) {
            "null"
        } else {
            encodeWithHex(bytes, 0, bytes.size, uppercase)
        }
    }

    /**
     * Encode the data with HEX (Base16) encoding
     */
    fun encodeWithHex(
        bytes: ByteArray,
        startPos: Int,
        endPos: Int,
        uppercase: Boolean = true
    ): String {
        var endPosTmp = endPos
        if (endPosTmp > bytes.size) {
            endPosTmp = bytes.size
        }
        val size = endPosTmp - startPos
        val charsArray = if (uppercase) HEX_ARRAY_UPPERCASE else HEX_ARRAY_LOWERCASE
        val hexChars = CharArray(size * 2)
        var i = startPos
        var j = 0
        while (i < endPosTmp) {
            val v = bytes[i].toInt() and 0xFF
            hexChars[j] = charsArray[v.ushr(4)]
            hexChars[j + 1] = charsArray[v and 0x0F]
            i++
            j += 2
        }
        return String(hexChars)
    }

    fun fromHexString(hexStr: String): ByteArray {
        val hexStrTmp = hexStr.replace(" ", "") // support spaces
        if (hexStrTmp.length % 2 != 0) {
            throw IllegalArgumentException("Bad length: $hexStrTmp")
        }

        val result = ByteArray(hexStrTmp.length / 2)
        for (i in result.indices) {
            val high = fromHexChar(hexStrTmp, i * 2) shl 4
            val low = fromHexChar(hexStrTmp, i * 2 + 1)
            result[i] = (high or low and 0xFF).toByte()
        }
        return result
    }

    private fun fromHexChar(hexStr: String, index: Int): Int {
        return when (val ch = hexStr[index]) {
            in '0'..'9' -> ch - '0'
            in 'a'..'f' -> 10 + (ch - 'a')
            in 'A'..'F' -> 10 + (ch - 'A')
            else -> throw IllegalArgumentException("Not hex string: $hexStr")
        }
    }
}
