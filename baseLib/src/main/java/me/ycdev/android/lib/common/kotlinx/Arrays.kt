@file:Suppress("unused")

package me.ycdev.android.lib.common.kotlinx

fun BooleanArray?.isNullOrEmpty(): Boolean {
    return this == null || this.isEmpty()
}

fun CharArray?.isNullOrEmpty(): Boolean {
    return this == null || this.isEmpty()
}

fun ByteArray?.isNullOrEmpty(): Boolean {
    return this == null || this.isEmpty()
}

fun ShortArray?.isNullOrEmpty(): Boolean {
    return this == null || this.isEmpty()
}

fun IntArray?.isNullOrEmpty(): Boolean {
    return this == null || this.isEmpty()
}

fun LongArray?.isNullOrEmpty(): Boolean {
    return this == null || this.isEmpty()
}

fun FloatArray?.isNullOrEmpty(): Boolean {
    return this == null || this.isEmpty()
}

fun DoubleArray?.isNullOrEmpty(): Boolean {
    return this == null || this.isEmpty()
}
