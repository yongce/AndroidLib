package me.ycdev.android.lib.common.net

import android.content.Context
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

        @NetworkType val networkType = NetworkUtils.getNetworkType(context)
        assertWithMessage("check all return values")
            .that(networkType)
            .isAnyOf(
                NETWORK_TYPE_MOBILE,
                NETWORK_TYPE_WIFI,
                NETWORK_TYPE_COMPANION_PROXY,
                NETWORK_TYPE_NONE
            )
    }

    @Test
    fun test_getMobileNetworkType() {
        // for any network
        val context = ApplicationProvider.getApplicationContext<Context>()

        @NetworkType val networkType = NetworkUtils.getMobileNetworkType(context)
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
}
