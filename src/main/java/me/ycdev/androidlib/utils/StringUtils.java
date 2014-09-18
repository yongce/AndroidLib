package me.ycdev.androidlib.utils;

public class StringUtils {
    public static int parseInt(String value, int defValue) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return defValue;
        }
    }

    public static long parseLong(String value, long defValue) {
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            return defValue;
        }
    }
}
