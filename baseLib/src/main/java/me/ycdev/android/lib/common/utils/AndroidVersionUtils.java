package me.ycdev.android.lib.common.utils;

import android.os.Build;

public class AndroidVersionUtils {
    /**
     * Ice Cream Sandwich MR1 (4.0.3) and higher version (API 15+)
     */
    public static boolean hasIceCreamSandwichMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1;
    }

    /**
     * Jelly Bean (4.1) and higher version (API 16+)
     */
    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    /**
     * Jelly Bean (4.2) and higher version (API 17+)
     */
    public static boolean hasJellyBeanMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }


    /**
     * Jelly Bean (4.3) and higher version (API 18+)
     */
    public static boolean hasJellyBeanMR2() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

}
