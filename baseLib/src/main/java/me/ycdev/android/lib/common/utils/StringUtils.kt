package me.ycdev.android.lib.common.utils

@Suppress("unused")
object StringUtils {
    fun trimPrefixSpaces(str: String): String {
        val size = str.length
        var index = 0
        while (index < size && (str[index] <= '\u0020' || str[index] == '\u00a0')) {
            index++
        }
        return if (index > 0) {
            str.substring(index)
        } else {
            str
        }
    }

    fun parseInt(value: String, defValue: Int): Int {
        return try {
            value.toInt()
        } catch (e: Exception) {
            defValue
        }
    }

    fun parseLong(value: String, defValue: Long): Long {
        return try {
            value.toLong()
        } catch (e: Exception) {
            defValue
        }
    }
}
