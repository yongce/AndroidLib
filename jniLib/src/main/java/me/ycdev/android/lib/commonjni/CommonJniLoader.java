package me.ycdev.android.lib.commonjni;

class CommonJniLoader {
    static {
        System.loadLibrary("ycdev-commonjni");
    }

    static void load() {
        // nothing to do
    }
}
