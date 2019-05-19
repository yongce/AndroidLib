package me.ycdev.android.lib.commonjni

internal object CommonJniLoader {
    init {
        System.loadLibrary("ycdev-commonjni")
    }

    fun load() {
        // nothing to do
    }
}
