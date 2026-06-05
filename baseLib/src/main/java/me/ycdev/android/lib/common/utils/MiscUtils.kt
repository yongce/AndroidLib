package me.ycdev.android.lib.common.utils

object MiscUtils {
    fun calcProgressPercent(
        percentStart: Int,
        percentEnd: Int,
        i: Int,
        n: Int
    ): Int = percentStart + i * (percentEnd - percentStart) / n
}
