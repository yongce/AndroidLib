package me.ycdev.android.lib.common.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.IntDef;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import me.ycdev.android.lib.common.utils.LibConfigs;
import me.ycdev.android.lib.common.utils.LibLogger;

public class NetworkUtils {
    private static final String TAG = "NetworkUtils";
    private static final boolean DEBUG = LibConfigs.DEBUG_LOG;

    public static final int NETWORK_TYPE_NONE = -1;
    public static final int NETWORK_TYPE_WIFI = 1;
    public static final int NETWORK_TYPE_MOBILE = 2;
    public static final int NETWORK_TYPE_2G = 10;
    public static final int NETWORK_TYPE_3G = 11;
    public static final int NETWORK_TYPE_4G = 12;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            NETWORK_TYPE_NONE, NETWORK_TYPE_WIFI, NETWORK_TYPE_MOBILE,
            NETWORK_TYPE_2G, NETWORK_TYPE_3G, NETWORK_TYPE_4G
    })
    public @interface NetworkType {}

    public static NetworkInfo getActiveNetworkInfo(Context cxt) {
        ConnectivityManager cm = (ConnectivityManager) cxt.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            if (DEBUG) LibLogger.w(TAG, "failed to get connectivity service");
            return null;
        }

        NetworkInfo netInfo = null;
        try {
            netInfo = cm.getActiveNetworkInfo();
        } catch (Exception e) {
            if (DEBUG) LibLogger.w(TAG, "failed to get active network info", e);
        }
        return netInfo;
    }

    /**
     * @return One of the values {@link #NETWORK_TYPE_NONE},
     *         {@link #NETWORK_TYPE_WIFI} or {@link #NETWORK_TYPE_MOBILE}
     */
    @SuppressWarnings("deprecation")
    @NetworkType
    public static int getNetworkType(Context cxt) {
        NetworkInfo netInfo = getActiveNetworkInfo(cxt);
        if (netInfo == null) {
            return NETWORK_TYPE_NONE;
        }

        int type = netInfo.getType();

        if (type == ConnectivityManager.TYPE_WIFI
                || type == ConnectivityManager.TYPE_WIMAX
                || type == ConnectivityManager.TYPE_ETHERNET) {
            return NETWORK_TYPE_WIFI;
        } else if (type == ConnectivityManager.TYPE_MOBILE
                || type == ConnectivityManager.TYPE_MOBILE_MMS) {
            return NETWORK_TYPE_MOBILE;
        }
        return NETWORK_TYPE_NONE; // Take unknown networks as none
    }

    /**
     * @return One of values {@link #NETWORK_TYPE_2G}, {@link #NETWORK_TYPE_3G},
     *                       {@link #NETWORK_TYPE_4G} or {@link #NETWORK_TYPE_NONE}
     */
    @NetworkType
    public static int getMobileNetworkType(Context cxt) {
        // Code from android-5.1.1_r4:
        // frameworks/base/packages/SystemUI/src/com/android/systemui/statusbar/policy/NetworkControllerImpl.java
        // in NetworkControllerImpl#mapIconSets()
        TelephonyManager tm = (TelephonyManager) cxt.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm == null) {
            if (DEBUG) LibLogger.w(TAG, "failed to get telephony service");
            return NETWORK_TYPE_NONE;
        }

        int tmType;
        try {
            tmType = tm.getNetworkType();
        } catch (Exception e) {
            if (DEBUG) LibLogger.w(TAG, "failed to get telephony network type", e);
            return NETWORK_TYPE_NONE;
        }

        switch (tmType) {
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return NETWORK_TYPE_NONE;

            case TelephonyManager.NETWORK_TYPE_LTE:
                return NETWORK_TYPE_4G;

            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_UMTS:
//            case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                return NETWORK_TYPE_3G;

            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return NETWORK_TYPE_3G; // H

            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
//            case TelephonyManager.NETWORK_TYPE_GSM:
                return NETWORK_TYPE_2G;
        }
        return NETWORK_TYPE_2G;
    }

    /**
     * @return One of values {@link #NETWORK_TYPE_WIFI}, {@link #NETWORK_TYPE_2G},
     *         {@link #NETWORK_TYPE_3G}, {@link #NETWORK_TYPE_4G} or {@link #NETWORK_TYPE_NONE}
     */
    @NetworkType
    public static int getMixedNetworkType(Context cxt) {
        int type = getNetworkType(cxt);
        if (type == NETWORK_TYPE_MOBILE) {
            type = getMobileNetworkType(cxt);
        }
        return type;
    }

    /**
     * Check if there is an active network connection
     */
    public static boolean isNetworkAvailable(Context cxt) {
        NetworkInfo network = getActiveNetworkInfo(cxt);
        return network != null && network.isConnected();
    }

    /**
     * Check if the current active network may cause monetary cost
     * @see ConnectivityManager#isActiveNetworkMetered()
     */
    public static boolean isActiveNetworkMetered(Context cxt) {
        ConnectivityManager cm = (ConnectivityManager) cxt.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            if (DEBUG) LibLogger.w(TAG, "failed to get connectivity service");
            return true;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            return getNetworkType(cxt) == NETWORK_TYPE_MOBILE;
        }
        return cm.isActiveNetworkMetered();
    }

    /**
     * Open a HTTP connection to the specified URL. Use proxy automatically if needed.
     * @throws IOException
     */
    public static HttpURLConnection openHttpURLConnection(String url) throws IOException {
        // check if url can be parsed successfully to prevent host == null crash
        // https://code.google.com/p/android/issues/detail?id=16895
        URL linkUrl = new URL(url);
        String host = linkUrl.getHost();
        if (TextUtils.isEmpty(host)) {
            throw new MalformedURLException("Malformed URL: " + url);
        }
        // TODO if needed to support proxy
        return (HttpURLConnection) linkUrl.openConnection();
    }
}
