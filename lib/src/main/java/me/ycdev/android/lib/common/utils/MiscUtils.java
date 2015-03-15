package me.ycdev.android.lib.common.utils;

public class MiscUtils {
    public static int calcProgressPercent(int percentStart, int percentEnd, int i, int n) {
        return percentStart + i * (percentEnd - percentStart) / n;
    }
}
