@file:Suppress("unused")

package me.ycdev.android.lib.common.kotlinx

fun BooleanArray?.isNullOrEmpty(): Boolean = this == null || this.isEmpty()

fun CharArray?.isNullOrEmpty(): Boolean = this == null || this.isEmpty()

fun ByteArray?.isNullOrEmpty(): Boolean = this == null || this.isEmpty()

fun ShortArray?.isNullOrEmpty(): Boolean = this == null || this.isEmpty()

fun IntArray?.isNullOrEmpty(): Boolean = this == null || this.isEmpty()

fun LongArray?.isNullOrEmpty(): Boolean = this == null || this.isEmpty()

fun FloatArray?.isNullOrEmpty(): Boolean = this == null || this.isEmpty()

fun DoubleArray?.isNullOrEmpty(): Boolean = this == null || this.isEmpty()
