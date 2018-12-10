package me.ycdev.android.lib.common.net

import android.net.ConnectivityManager
import android.telephony.TelephonyManager
import com.google.common.truth.Truth.assertThat
import me.ycdev.android.lib.common.net.NetworkUtils.NetworkType.NETWORK_TYPE_COMPANION_PROXY
import me.ycdev.android.lib.common.net.NetworkUtils.NetworkType.NETWORK_TYPE_MOBILE
import me.ycdev.android.lib.common.net.NetworkUtils.NetworkType.NETWORK_TYPE_WIFI
import me.ycdev.android.lib.common.net.NetworkUtils.WEAR_OS_COMPANION_PROXY
import org.junit.Test

class NetworkUtilsTestBasic {
    @Test
    fun getNetworkType_common() {
        // phone Wi-Fi
        assertThat(NetworkUtils.getNetworkType(ConnectivityManager.TYPE_WIFI, 0))
            .isEqualTo(NETWORK_TYPE_WIFI)
        // faked subTypes
        for (i in 1..19) {
            assertThat(NetworkUtils.getNetworkType(ConnectivityManager.TYPE_WIFI, i))
                .isEqualTo(NETWORK_TYPE_WIFI)
        }

        // phone 4G
        assertThat(
            NetworkUtils.getNetworkType(
                ConnectivityManager.TYPE_MOBILE,
                TelephonyManager.NETWORK_TYPE_LTE
            )
        ).isEqualTo(NETWORK_TYPE_MOBILE)
        // phone 3G
        assertThat(
            NetworkUtils.getNetworkType(
                ConnectivityManager.TYPE_MOBILE,
                TelephonyManager.NETWORK_TYPE_UMTS
            )
        ).isEqualTo(NETWORK_TYPE_MOBILE)
        assertThat(
            NetworkUtils.getNetworkType(
                ConnectivityManager.TYPE_MOBILE,
                TelephonyManager.NETWORK_TYPE_HSPAP
            )
        ).isEqualTo(NETWORK_TYPE_MOBILE)
        // real or faked subTypes
        for (i in 1..19) {
            assertThat(
                NetworkUtils.getNetworkType(ConnectivityManager.TYPE_MOBILE, i)
            ).isEqualTo(NETWORK_TYPE_MOBILE)
        }
    }

    @Test
    fun getNetworkType_wearOs() {
        // companion proxy: phone Wi-Fi or mobile
        assertThat(
            NetworkUtils.getNetworkType(WEAR_OS_COMPANION_PROXY, 0)
        ).isEqualTo(NETWORK_TYPE_COMPANION_PROXY)
        // faked subTypes
        for (i in 1..19) {
            assertThat(
                NetworkUtils.getNetworkType(WEAR_OS_COMPANION_PROXY, i)
            ).isEqualTo(NETWORK_TYPE_COMPANION_PROXY)
        }
    }
}
