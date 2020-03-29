package me.ycdev.android.lib.common.net

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.telephony.TelephonyManager
import android.text.TextUtils
import androidx.annotation.IntDef
import androidx.annotation.RequiresPermission
import androidx.annotation.VisibleForTesting
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import me.ycdev.android.lib.common.utils.LibLogger

@Suppress("unused")
object NetworkUtils {
    private const val TAG = "NetworkUtils"

    const val WEAR_OS_COMPANION_PROXY = 16

    const val NETWORK_TYPE_NONE = -1
    const val NETWORK_TYPE_WIFI = 1
    const val NETWORK_TYPE_MOBILE = 2
    const val NETWORK_TYPE_2G = 10
    const val NETWORK_TYPE_3G = 11
    const val NETWORK_TYPE_4G = 12
    const val NETWORK_TYPE_COMPANION_PROXY = 20

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(
        NETWORK_TYPE_NONE,
        NETWORK_TYPE_WIFI,
        NETWORK_TYPE_MOBILE,
        NETWORK_TYPE_2G,
        NETWORK_TYPE_3G,
        NETWORK_TYPE_4G,
        NETWORK_TYPE_COMPANION_PROXY
    )
    annotation class NetworkType

    @Suppress("DEPRECATION")
    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    fun dumpActiveNetworkInfo(cxt: Context): String {
        val info = getActiveNetworkInfo(cxt) ?: return "No active network"

        val sb = StringBuilder()
        sb.append("type=").append(info.type)
            .append(", subType=").append(info.subtype)
            .append(", infoDump=").append(info)
        return sb.toString()
    }

    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    fun getActiveNetworkInfo(cxt: Context): NetworkInfo? {
        val cm = cxt.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager?
        if (cm == null) {
            LibLogger.w(TAG, "failed to get connectivity service")
            return null
        }

        var netInfo: NetworkInfo? = null
        try {
            netInfo = cm.activeNetworkInfo
        } catch (e: Exception) {
            LibLogger.w(TAG, "failed to get active network info", e)
        }

        return netInfo
    }

    /**
     * @return One of the values [NETWORK_TYPE_NONE],
     * [NETWORK_TYPE_WIFI] or [NETWORK_TYPE_MOBILE]
     */
    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    @NetworkType
    fun getNetworkType(cxt: Context): Int {
        val netInfo = getActiveNetworkInfo(cxt) ?: return NETWORK_TYPE_NONE

        @Suppress("DEPRECATION")
        return getNetworkType(netInfo.type, netInfo.subtype)
    }

    @Suppress("DEPRECATION", "UNUSED_PARAMETER")
    @NetworkType
    @VisibleForTesting
    internal fun getNetworkType(type: Int, subType: Int): Int {
        if (type == ConnectivityManager.TYPE_WIFI ||
            type == ConnectivityManager.TYPE_WIMAX ||
            type == ConnectivityManager.TYPE_ETHERNET
        ) {
            return NETWORK_TYPE_WIFI
        } else if (type == ConnectivityManager.TYPE_MOBILE || type == ConnectivityManager.TYPE_MOBILE_MMS) {
            return NETWORK_TYPE_MOBILE
        } else if (type == WEAR_OS_COMPANION_PROXY) {
            // Wear OS
            return NETWORK_TYPE_COMPANION_PROXY
        }
        return NETWORK_TYPE_NONE // Take unknown networks as none
    }

    /**
     * @return One of values [NETWORK_TYPE_2G], [NETWORK_TYPE_3G],
     * [NETWORK_TYPE_4G] or [NETWORK_TYPE_NONE]
     */
    @NetworkType
    fun getMobileNetworkType(cxt: Context): Int {
        // Code from android-5.1.1_r4:
        // frameworks/base/packages/SystemUI/src/com/android/systemui/statusbar/policy/NetworkControllerImpl.java
        // in NetworkControllerImpl#mapIconSets()
        val tm = cxt.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
        if (tm == null) {
            LibLogger.w(TAG, "failed to get telephony service")
            return NETWORK_TYPE_NONE
        }

        val tmType: Int
        try {
            tmType = tm.networkType
        } catch (e: Exception) {
            LibLogger.w(TAG, "failed to get telephony network type", e)
            return NETWORK_TYPE_NONE
        }

        when (tmType) {
            TelephonyManager.NETWORK_TYPE_UNKNOWN -> return NETWORK_TYPE_NONE

            TelephonyManager.NETWORK_TYPE_LTE -> return NETWORK_TYPE_4G

            TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_UMTS ->
                //            case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                return NETWORK_TYPE_3G

            TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_HSPAP -> return NETWORK_TYPE_3G // H

            TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_1xRTT ->
                //            case TelephonyManager.NETWORK_TYPE_GSM:
                return NETWORK_TYPE_2G
        }
        return NETWORK_TYPE_2G
    }

    /**
     * @return One of values [NETWORK_TYPE_WIFI], [NETWORK_TYPE_2G],
     * [NETWORK_TYPE_3G], [NETWORK_TYPE_4G] or [NETWORK_TYPE_NONE]
     */
    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    @NetworkType
    fun getMixedNetworkType(cxt: Context): Int {
        var type = getNetworkType(cxt)
        if (type == NETWORK_TYPE_MOBILE) {
            type = getMobileNetworkType(cxt)
        }
        return type
    }

    /**
     * Check if there is an active network connection
     */
    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    fun isNetworkAvailable(cxt: Context): Boolean {
        val network = getActiveNetworkInfo(cxt)
        return network != null && network.isConnected
    }

    /**
     * Check if the current active network may cause monetary cost
     * @see ConnectivityManager.isActiveNetworkMetered
     */
    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    fun isActiveNetworkMetered(cxt: Context): Boolean {
        val cm = cxt.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (cm == null) {
            LibLogger.w(TAG, "failed to get connectivity service")
            return true
        }
        return cm.isActiveNetworkMetered
    }

    /**
     * Open a HTTP connection to the specified URL. Use proxy automatically if needed.
     */
    @Throws(IOException::class)
    fun openHttpURLConnection(url: String): HttpURLConnection {
        // check if url can be parsed successfully to prevent host == null crash
        // https://code.google.com/p/android/issues/detail?id=16895
        val linkUrl = URL(url)
        val host = linkUrl.host
        if (TextUtils.isEmpty(host)) {
            throw MalformedURLException("Malformed URL: $url")
        }
        // TODO if needed to support proxy
        return linkUrl.openConnection() as HttpURLConnection
    }
}
