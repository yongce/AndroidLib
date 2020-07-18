package me.ycdev.android.lib.common.net

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
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
    const val NETWORK_TYPE_5G = 13
    const val NETWORK_TYPE_COMPANION_PROXY = 20

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(
        NETWORK_TYPE_NONE,
        NETWORK_TYPE_WIFI,
        NETWORK_TYPE_MOBILE,
        NETWORK_TYPE_2G,
        NETWORK_TYPE_3G,
        NETWORK_TYPE_4G,
        NETWORK_TYPE_5G,
        NETWORK_TYPE_COMPANION_PROXY
    )
    annotation class NetworkType

    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    fun dumpActiveNetworkInfo(cxt: Context): String {
        val capabilities = getActiveNetworkCapabilities(cxt) ?: return "No active network"
        return capabilities.toString()
    }

    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    fun getActiveNetwork(cxt: Context): Network? {
        val cm = cxt.getSystemService(ConnectivityManager::class.java)
        if (cm == null) {
            LibLogger.w(TAG, "failed to get connectivity service")
            return null
        }

        try {
            return cm.activeNetwork
        } catch (e: Exception) {
            LibLogger.w(TAG, "failed to get active network", e)
        }
        return null
    }

    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    fun getActiveNetworkCapabilities(cxt: Context): NetworkCapabilities? {
        val cm = cxt.getSystemService(ConnectivityManager::class.java)
        if (cm == null) {
            LibLogger.w(TAG, "failed to get connectivity service")
            return null
        }

        try {
            val network = cm.activeNetwork ?: return null
            return cm.getNetworkCapabilities(network)
        } catch (e: Exception) {
            LibLogger.w(TAG, "failed to get active network", e)
        }
        return null
    }

    /**
     * @return One of the values [NETWORK_TYPE_NONE],
     * [NETWORK_TYPE_WIFI] or [NETWORK_TYPE_MOBILE]
     */
    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    @NetworkType
    fun getNetworkType(cxt: Context): Int {
        val capabilities = getActiveNetworkCapabilities(cxt) ?: return NETWORK_TYPE_NONE
        return getNetworkType(capabilities)
    }

    @NetworkType
    @VisibleForTesting
    internal fun getNetworkType(capabilities: NetworkCapabilities): Int {
        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        ) {
            return NETWORK_TYPE_WIFI
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            return NETWORK_TYPE_MOBILE
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)) {
            // Wear OS
            return NETWORK_TYPE_COMPANION_PROXY
        }
        return NETWORK_TYPE_NONE // Take unknown networks as none
    }

    /**
     * @return One of values [NETWORK_TYPE_2G], [NETWORK_TYPE_3G],
     * [NETWORK_TYPE_4G], [NETWORK_TYPE_5G] or [NETWORK_TYPE_NONE]
     */
    @NetworkType
    @RequiresPermission(android.Manifest.permission.READ_PHONE_STATE)
    fun getMobileNetworkType(cxt: Context): Int {
        // #1 Code from android-5.1.1_r4:
        //    frameworks/base/packages/SystemUI/src/com/android/systemui/statusbar/policy/NetworkControllerImpl.java
        //    in NetworkControllerImpl#mapIconSets()
        // #2 Code from master (Android R):
        //    frameworks/base/packages/SystemUI/src/com/android/systemui/statusbar/policy/MobileSignalController.java
        //    in MobileSignalController#mapIconSets()
        val tm = cxt.getSystemService(TelephonyManager::class.java)
        if (tm == null) {
            LibLogger.w(TAG, "failed to get telephony service")
            return NETWORK_TYPE_NONE
        }

        val tmType: Int
        try {
            @Suppress("DEPRECATION")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tmType = tm.dataNetworkType
            } else {
                tmType = tm.networkType
            }
        } catch (e: Exception) {
            LibLogger.w(TAG, "failed to get telephony network type", e)
            return NETWORK_TYPE_NONE
        }

        return when (tmType) {
            TelephonyManager.NETWORK_TYPE_UNKNOWN -> NETWORK_TYPE_NONE
            TelephonyManager.NETWORK_TYPE_LTE -> NETWORK_TYPE_4G
            TelephonyManager.NETWORK_TYPE_NR -> NETWORK_TYPE_5G

            TelephonyManager.NETWORK_TYPE_EVDO_0,
            TelephonyManager.NETWORK_TYPE_EVDO_A,
            TelephonyManager.NETWORK_TYPE_EVDO_B,
            TelephonyManager.NETWORK_TYPE_EHRPD,
            TelephonyManager.NETWORK_TYPE_TD_SCDMA,
            TelephonyManager.NETWORK_TYPE_UMTS -> NETWORK_TYPE_3G

            TelephonyManager.NETWORK_TYPE_HSDPA,
            TelephonyManager.NETWORK_TYPE_HSUPA,
            TelephonyManager.NETWORK_TYPE_HSPA,
            TelephonyManager.NETWORK_TYPE_HSPAP -> NETWORK_TYPE_3G // H

            TelephonyManager.NETWORK_TYPE_GPRS,
            TelephonyManager.NETWORK_TYPE_EDGE,
            TelephonyManager.NETWORK_TYPE_CDMA,
            TelephonyManager.NETWORK_TYPE_GSM,
            TelephonyManager.NETWORK_TYPE_1xRTT -> NETWORK_TYPE_2G

            else -> NETWORK_TYPE_2G
        }
    }

    /**
     * @return One of values [NETWORK_TYPE_WIFI], [NETWORK_TYPE_2G],
     * [NETWORK_TYPE_3G], [NETWORK_TYPE_4G], [NETWORK_TYPE_5G] or [NETWORK_TYPE_NONE]
     */
    @RequiresPermission(
        allOf = [
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.READ_PHONE_STATE
        ]
    )
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
        val capabilities = getActiveNetworkCapabilities(cxt) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
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
