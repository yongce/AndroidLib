package me.ycdev.android.lib.common.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiManager;

@SuppressLint("MissingPermission")
public class SystemSwitchUtils {
    public static boolean isWifiEnabled(Context cxt) {
        WifiManager wifiMgr = (WifiManager) cxt.getSystemService(Context.WIFI_SERVICE);
        int wifiState = wifiMgr.getWifiState();
        return (wifiState == WifiManager.WIFI_STATE_ENABLED
                || wifiState == WifiManager.WIFI_STATE_ENABLING);
    }

    public static void setWifiEnabled(Context cxt, boolean enable) {
        WifiManager wifiMgr = (WifiManager) cxt.getSystemService(Context.WIFI_SERVICE);
        wifiMgr.setWifiEnabled(enable);
    }
}
