package me.ycdev.android.lib.common.packets

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class RawPacketsWorkerTest : PacketsWorkerTestBase() {
    @Test
    fun packetAndParse() {
        val packetsWorker = RawPacketsWorker(parserCallback)
        for (length in 1..1024) {
            val data = generateData(length)
            val packets = packetsWorker.packetData(data)
            assertThat(packets.size).isEqualTo(1)

            packetsWorker.parsePackets(packets[0])
            assertThat(parserCallback.getData()).isEqualTo(data)
        }
    }

    @Test
    fun parseEmptyData() {
        val packetsWorker = RawPacketsWorker(parserCallback)
        val packets = packetsWorker.packetData(byteArrayOf())
        assertThat(packets.size).isEqualTo(1)

        packetsWorker.parsePackets(packets[0])
        assertThat(parserCallback.getData()).isNull()
    }
}
