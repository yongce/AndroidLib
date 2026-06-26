package me.ycdev.android.lib.common.net

import android.net.NetworkCapabilities
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import java.net.MalformedURLException
import me.ycdev.android.lib.common.net.NetworkUtils.NETWORK_TYPE_COMPANION_PROXY
import me.ycdev.android.lib.common.net.NetworkUtils.NETWORK_TYPE_MOBILE
import me.ycdev.android.lib.common.net.NetworkUtils.NETWORK_TYPE_NONE
import me.ycdev.android.lib.common.net.NetworkUtils.NETWORK_TYPE_WIFI
import org.junit.Assert.assertThrows
import org.junit.Test

class NetworkUtilsTestBasic {
    @Test
    fun getNetworkType_common() {
        val capabilities = mockk<NetworkCapabilities>()

        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) } returns false
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) } returns false
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) } returns false
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) } returns false
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_LOWPAN) } returns false
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) } returns false
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI_AWARE) } returns false

        // no network
        assertThat(NetworkUtils.getNetworkType(capabilities)).isEqualTo(NETWORK_TYPE_NONE)

        // Wi-Fi
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) } returns true
        assertThat(NetworkUtils.getNetworkType(capabilities)).isEqualTo(NETWORK_TYPE_WIFI)
        // reset
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) } returns false
        assertThat(NetworkUtils.getNetworkType(capabilities)).isEqualTo(NETWORK_TYPE_NONE)

        // mobile
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) } returns true
        assertThat(NetworkUtils.getNetworkType(capabilities)).isEqualTo(NETWORK_TYPE_MOBILE)
        // reset
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) } returns false
        assertThat(NetworkUtils.getNetworkType(capabilities)).isEqualTo(NETWORK_TYPE_NONE)

        // bluetooth proxy (Wear OS)
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) } returns true
        assertThat(NetworkUtils.getNetworkType(capabilities)).isEqualTo(NETWORK_TYPE_COMPANION_PROXY)
        // reset
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) } returns false
        assertThat(NetworkUtils.getNetworkType(capabilities)).isEqualTo(NETWORK_TYPE_NONE)
    }

    @Test
    fun getNetworkType_prefersWifiAndEthernetOverOtherTransports() {
        val capabilities = mockk<NetworkCapabilities>()

        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) } returns true
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) } returns false
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) } returns true
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) } returns true

        assertThat(NetworkUtils.getNetworkType(capabilities)).isEqualTo(NETWORK_TYPE_WIFI)

        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) } returns false
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) } returns true

        assertThat(NetworkUtils.getNetworkType(capabilities)).isEqualTo(NETWORK_TYPE_WIFI)
    }

    @Test
    fun openHttpURLConnection_rejectsUrlWithoutHost() {
        val e =
            assertThrows(MalformedURLException::class.java) {
                NetworkUtils.openHttpURLConnection("http:///path-only")
            }

        assertThat(e).hasMessageThat().isEqualTo("Malformed URL: http:///path-only")
    }
}
