package me.ycdev.android.lib.common.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiManager

@SuppressLint("MissingPermission")
object SystemSwitchUtils {
    fun isWifiEnabled(cxt: Context): Boolean {
        val wifiMgr = cxt.applicationContext.getSystemService(
            Context.WIFI_SERVICE
        ) as WifiManager
        val wifiState = wifiMgr.wifiState
        return wifiState == WifiManager.WIFI_STATE_ENABLED || wifiState == WifiManager.WIFI_STATE_ENABLING
    }

    fun setWifiEnabled(cxt: Context, enable: Boolean) {
        val wifiMgr = cxt.applicationContext.getSystemService(
            Context.WIFI_SERVICE
        ) as WifiManager
        wifiMgr.isWifiEnabled = enable
    }
}
