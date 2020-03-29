package me.ycdev.android.lib.common.net

import android.content.Context
import android.os.SystemClock
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.google.common.truth.Truth.assertWithMessage
import me.ycdev.android.lib.common.net.NetworkUtils.NETWORK_TYPE_2G
import me.ycdev.android.lib.common.net.NetworkUtils.NETWORK_TYPE_3G
import me.ycdev.android.lib.common.net.NetworkUtils.NETWORK_TYPE_4G
import me.ycdev.android.lib.common.net.NetworkUtils.NETWORK_TYPE_COMPANION_PROXY
import me.ycdev.android.lib.common.net.NetworkUtils.NETWORK_TYPE_MOBILE
import me.ycdev.android.lib.common.net.NetworkUtils.NETWORK_TYPE_NONE
import me.ycdev.android.lib.common.net.NetworkUtils.NETWORK_TYPE_WIFI
import me.ycdev.android.lib.common.net.NetworkUtils.NetworkType
import me.ycdev.android.lib.common.utils.SystemSwitchUtils
import org.junit.Rule
import org.junit.Test
import org.junit.rules.Timeout
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class NetworkUtilsTest {
    @Rule @JvmField
    var timeout: Timeout = Timeout.seconds(60)

    @Test
    fun test_getNetworkType() {
        // for any network
        val context = ApplicationProvider.getApplicationContext<Context>()
        @NetworkType var networkType = NetworkUtils.getNetworkType(context)
        assertWithMessage("check all return values")
            .that(networkType)
            .isAnyOf(
                NETWORK_TYPE_MOBILE,
                NETWORK_TYPE_WIFI,
                NETWORK_TYPE_COMPANION_PROXY,
                NETWORK_TYPE_NONE
            )

        val oldNetworkType = NetworkUtils.getNetworkType(context)
        if (SystemSwitchUtils.isWifiEnabled(context)) {
            // disable WiFi
            SystemSwitchUtils.setWifiEnabled(context, false)
            waitForWiFiConnected(context, false)
            if (!SystemSwitchUtils.isWifiEnabled(context)) {
                networkType = NetworkUtils.getNetworkType(context)
                assertWithMessage("wifi disabled")
                    .that(networkType)
                    .isAnyOf(NETWORK_TYPE_MOBILE, NETWORK_TYPE_COMPANION_PROXY, NETWORK_TYPE_NONE)

                if (oldNetworkType == NETWORK_TYPE_WIFI) {
                    // enable WiFi
                    SystemSwitchUtils.setWifiEnabled(context, true)
                    waitForWiFiConnected(context, true)
                    networkType = NetworkUtils.getNetworkType(context)
                    assertWithMessage("wifi enabled")
                        .that(networkType).isEqualTo(NETWORK_TYPE_WIFI)
                }
            }
        } else {
            // enable WiFi
            SystemSwitchUtils.setWifiEnabled(context, true)
            waitForWiFiConnected(context, true)
            if (SystemSwitchUtils.isWifiEnabled(context)) {
                networkType = NetworkUtils.getNetworkType(context)
                assertWithMessage("wifi enabled 2")
                    .that(networkType).isAnyOf(NETWORK_TYPE_WIFI, oldNetworkType)

                // disable WiFi
                SystemSwitchUtils.setWifiEnabled(context, false)
                waitForWiFiConnected(context, false)
                networkType = NetworkUtils.getNetworkType(context)
                assertWithMessage("wifi disabled 2")
                    .that(networkType)
                    .isAnyOf(NETWORK_TYPE_MOBILE, NETWORK_TYPE_COMPANION_PROXY, NETWORK_TYPE_NONE)
            }
        }
    }

    @Test
    fun test_getMobileNetworkType() {
        // for any network
        val context = ApplicationProvider.getApplicationContext<Context>()
        @NetworkType val networkType = NetworkUtils.getMobileNetworkType(context)
        assertWithMessage("check all return values")
            .that(networkType)
            .isAnyOf(NETWORK_TYPE_2G, NETWORK_TYPE_3G, NETWORK_TYPE_4G, NETWORK_TYPE_NONE)

        // disable WiFi
        SystemSwitchUtils.setWifiEnabled(context, false)
        waitForWiFiConnected(context, false)
        assertWithMessage("check all return values")
            .that(networkType)
            .isAnyOf(NETWORK_TYPE_2G, NETWORK_TYPE_3G, NETWORK_TYPE_4G, NETWORK_TYPE_NONE)
    }

    @Test
    fun test_getMixedNetworkType() {
        // for any network
        val context = ApplicationProvider.getApplicationContext<Context>()
        @NetworkType val networkType = NetworkUtils.getMixedNetworkType(context)
        assertWithMessage("check all return values").that(networkType)
            .isAnyOf(
                NETWORK_TYPE_WIFI,
                NETWORK_TYPE_2G,
                NETWORK_TYPE_3G,
                NETWORK_TYPE_4G,
                NETWORK_TYPE_COMPANION_PROXY,
                NETWORK_TYPE_NONE
            )
    }

    @Test
    fun test_isActiveNetworkMetered() {
        // TODO
    }

    @Test
    fun test_openHttpURLConnection() {
        // TODO
    }

    private fun waitForWiFiConnected(cxt: Context, connected: Boolean) {
        val timeStart = SystemClock.elapsedRealtime()
        while (true) {
            if (SystemClock.elapsedRealtime() - timeStart >= 1000 * 15) {
                break // timeout
            }

            if (connected && NetworkUtils.getNetworkType(cxt) == NETWORK_TYPE_WIFI) {
                break
            } else if (!connected && NetworkUtils.getNetworkType(cxt) != NETWORK_TYPE_WIFI) {
                break
            }
            SystemClock.sleep(100)
        }
    }
}
