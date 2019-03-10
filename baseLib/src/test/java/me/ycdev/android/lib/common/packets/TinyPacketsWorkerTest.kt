package me.ycdev.android.lib.common.packets

import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import me.ycdev.android.lib.common.packets.PacketsWorker.ParserState
import me.ycdev.android.lib.common.packets.PacketsWorker.Version
import me.ycdev.android.lib.common.utils.EncodingUtils.encodeWithHex
import me.ycdev.android.lib.test.log.TimberJvmTree
import org.junit.Test
import timber.log.Timber
import java.nio.ByteBuffer
import java.nio.ByteOrder

class TinyPacketsWorkerTest : PacketsWorkerTestBase() {
    @Test
    fun calculateVersion() {
        assertThat(TinyPacketsWorker.calculateVersion(0)).isEqualTo(Version.V1)
        assertThat(TinyPacketsWorker.calculateVersion(1)).isEqualTo(Version.V1)
        assertThat(TinyPacketsWorker.calculateVersion(255)).isEqualTo(Version.V1)

        assertThat(TinyPacketsWorker.calculateVersion(256)).isEqualTo(Version.V2)
        assertThat(TinyPacketsWorker.calculateVersion(23535)).isEqualTo(Version.V2)
        assertThat(TinyPacketsWorker.calculateVersion(0xFFFF)).isEqualTo(Version.V2)

        assertThat(TinyPacketsWorker.calculateVersion(0x1_0000)).isEqualTo(Version.V3)
        assertThat(TinyPacketsWorker.calculateVersion(0x1_0001)).isEqualTo(Version.V3)
        assertThat(TinyPacketsWorker.calculateVersion(0xFF_FFFF)).isEqualTo(Version.V3)
        assertThat(TinyPacketsWorker.calculateVersion(0x7FFF_FFFF)).isEqualTo(Version.V3)
    }

    @Test
    fun calculateDataCrc() {
        assertThat(TinyPacketsWorker.calculateDataCrc(ByteArray(0))).isEqualTo(0)

        var data = byteArrayOf(0x2F.toByte(), 0x12, 0xa3.toByte())
        assertThat(TinyPacketsWorker.calculateDataCrc(data)).isEqualTo(0x9E.toByte())

        data = byteArrayOf(
            0xa3.toByte(), 0x3d, 0x26, 0xf3.toByte(), 0x3d, 0x6b, 0xc8.toByte(), 0x2e
        )
        assertThat(TinyPacketsWorker.calculateDataCrc(data)).isEqualTo(0xFB.toByte())
    }

    @Test
    fun fillDataSize_V1() {
        val buffer: ByteBuffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)

        buffer.clear()
        TinyPacketsWorker.fillDataSize(buffer, Version.V1, 0)
        assertThat(buffer.position()).isEqualTo(1)
        buffer.flip()
        assertThat(buffer.get()).isEqualTo(0)

        buffer.clear()
        TinyPacketsWorker.fillDataSize(buffer, Version.V1, 1)
        assertThat(buffer.position()).isEqualTo(1)
        buffer.flip()
        assertThat(buffer.get()).isEqualTo(1)

        buffer.clear()
        TinyPacketsWorker.fillDataSize(buffer, Version.V1, 0x7F)
        assertThat(buffer.position()).isEqualTo(1)
        buffer.flip()
        assertThat(buffer.get()).isEqualTo(127)

        buffer.clear()
        TinyPacketsWorker.fillDataSize(buffer, Version.V1, 0xFF)
        assertThat(buffer.position()).isEqualTo(1)
        buffer.flip()
        assertThat(buffer.get()).isEqualTo(255.toByte())

        // overflow
        buffer.clear()
        TinyPacketsWorker.fillDataSize(buffer, Version.V1, 0x101)
        assertThat(buffer.position()).isEqualTo(1)
        buffer.flip()
        assertThat(buffer.get()).isEqualTo(1)
    }

    @Test
    fun fillDataSize_V2() {
        val buffer: ByteBuffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)

        buffer.clear()
        TinyPacketsWorker.fillDataSize(buffer, Version.V2, 0x100)
        assertThat(buffer.position()).isEqualTo(2)
        buffer.flip()
        assertThat(buffer.get()).isEqualTo(0)
        assertThat(buffer.get()).isEqualTo(1)

        buffer.clear()
        TinyPacketsWorker.fillDataSize(buffer, Version.V2, 0x102)
        assertThat(buffer.position()).isEqualTo(2)
        buffer.flip()
        assertThat(buffer.get()).isEqualTo(2)
        assertThat(buffer.get()).isEqualTo(1)

        buffer.clear()
        TinyPacketsWorker.fillDataSize(buffer, Version.V2, 0xFFFF)
        assertThat(buffer.position()).isEqualTo(2)
        buffer.flip()
        assertThat(buffer.get()).isEqualTo(0xFF.toByte())
        assertThat(buffer.get()).isEqualTo(0xFF.toByte())

        // overflow
        buffer.clear()
        TinyPacketsWorker.fillDataSize(buffer, Version.V2, 0x1_2345)
        assertThat(buffer.position()).isEqualTo(2)
        buffer.flip()
        assertThat(buffer.get()).isEqualTo(0x45)
        assertThat(buffer.get()).isEqualTo(0x23)

        // underflow
        buffer.clear()
        TinyPacketsWorker.fillDataSize(buffer, Version.V2, 0x89)
        assertThat(buffer.position()).isEqualTo(2)
        buffer.flip()
        assertThat(buffer.get()).isEqualTo(0x89.toByte())
        assertThat(buffer.get()).isEqualTo(0)
    }

    @Test
    fun fillDataSize_V3() {
        val buffer: ByteBuffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)

        buffer.clear()
        TinyPacketsWorker.fillDataSize(buffer, Version.V3, 0x1_0000)
        assertThat(buffer.position()).isEqualTo(4)
        buffer.flip()
        assertThat(buffer.get()).isEqualTo(0)
        assertThat(buffer.get()).isEqualTo(0)
        assertThat(buffer.get()).isEqualTo(1)
        assertThat(buffer.get()).isEqualTo(0)

        buffer.clear()
        TinyPacketsWorker.fillDataSize(buffer, Version.V3, 0x1234_5678)
        assertThat(buffer.position()).isEqualTo(4)
        buffer.flip()
        assertThat(buffer.get()).isEqualTo(0x78)
        assertThat(buffer.get()).isEqualTo(0x56)
        assertThat(buffer.get()).isEqualTo(0x34)
        assertThat(buffer.get()).isEqualTo(0x12)

        // underflow
        buffer.clear()
        TinyPacketsWorker.fillDataSize(buffer, Version.V3, 0xABCD)
        assertThat(buffer.position()).isEqualTo(4)
        buffer.flip()
        assertThat(buffer.get()).isEqualTo(0xCD.toByte())
        assertThat(buffer.get()).isEqualTo(0xAB.toByte())
        assertThat(buffer.get()).isEqualTo(0)
        assertThat(buffer.get()).isEqualTo(0)
    }

    @Test
    fun readDataSize() {
        val buffer: ByteBuffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
        for (dataSize in 0..500) {
            buffer.clear()
            val version = TinyPacketsWorker.calculateVersion(dataSize)
            TinyPacketsWorker.fillDataSize(buffer, version, dataSize)
            buffer.flip()
            assertThat(TinyPacketsWorker.readDataSize(buffer, version)).isEqualTo(dataSize)
        }
    }

    @Test
    fun packetData_maxPackageSize() {
        val packetsWorker = TinyPacketsWorker(parserCallback)
        val data = generateData(50)

        try {
            packetsWorker.packetData(data)
        } catch (e: PacketsException) {
            assertThat(e).hasMessageThat().isEqualTo("Not supported maxPacketSize (0)")
        }

        try {
            packetsWorker.maxPacketSize = 10
        } catch (e: PacketsException) {
            assertThat(e).hasMessageThat().isEqualTo("The value (10) for maxPacketSize is too small.")
        }

        packetsWorker.maxPacketSize = 20
        packetsWorker.packetData(data)
    }

    @Test
    fun packetData_dataNumber() {
        val packetsWorker = TinyPacketsWorker(parserCallback)
        packetsWorker.maxPacketSize = 100

        for (number in 0..500) {
            val data = generateData(number)
            packetsWorker.packetData(data)

            val expectedNumber = number % 127 + 1
            assertWithMessage("Test number: $number")
                .that(packetsWorker.dataNumberHolder)
                .isEqualTo(expectedNumber)
        }
    }

    @Test
    fun packetData() {
        val maxPackageSize = 100
        val packetsWorker = TinyPacketsWorker(parserCallback)
        packetsWorker.maxPacketSize = maxPackageSize

        // V1
        for (dataSize in 0..255) {
            packetData_once(packetsWorker, maxPackageSize, dataSize)
        }

        // V2
        for (dataSize in 0x100..0x200) {
            packetData_once(packetsWorker, maxPackageSize, dataSize)
        }

        // V3
        for (dataSize in 0x10000..0x10100) {
            packetData_once(packetsWorker, maxPackageSize, dataSize)
        }
    }

    private fun packetData_once(packetsWorker: TinyPacketsWorker, maxPackageSize: Int, dataSize: Int) {
        val data = generateData(dataSize)
        val version = TinyPacketsWorker.calculateVersion(dataSize)
        val crc = TinyPacketsWorker.calculateDataCrc(data)
        val metaInfoSize = TinyPacketsWorker.calculateMetaInfoSize(version)
        val packetsCount = (dataSize + metaInfoSize + maxPackageSize - 1) / maxPackageSize

        val packets = packetsWorker.packetData(data)
        assertThat(packets.size).isEqualTo(packetsCount)

        // check meta info
        val firstPacket = packets[0]
        // header magic
        assertThat(firstPacket[0]).isEqualTo(TinyPacketsWorker.HEADER_MAGIC[0])
        assertThat(firstPacket[1]).isEqualTo(TinyPacketsWorker.HEADER_MAGIC[1])
        assertThat(firstPacket[2]).isEqualTo(TinyPacketsWorker.HEADER_MAGIC[2])
        assertThat(firstPacket[3]).isEqualTo(TinyPacketsWorker.HEADER_MAGIC[3])
        // version
        assertThat(firstPacket[4]).isEqualTo(version)
        // firstPacket[5]: data number, ignore
        // data CRC
        assertThat(firstPacket[6]).isEqualTo(crc)
        // data size
        var buffer: ByteBuffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
        when (version) {
            Version.V1 -> buffer.put(firstPacket[7])
            Version.V2 -> buffer.put(firstPacket, 7, 2)
            Version.V3 -> buffer.put(firstPacket, 7, 4)
        }
        buffer.flip()
        assertThat(TinyPacketsWorker.readDataSize(buffer, version)).isEqualTo(dataSize)

        // check data
        if (packets.size == 1) {
            assertThat(firstPacket.copyOfRange(metaInfoSize, metaInfoSize + dataSize)).isEqualTo(data)
        } else {
            buffer = ByteBuffer.allocate(dataSize).order(ByteOrder.LITTLE_ENDIAN)
            buffer.put(firstPacket, metaInfoSize, maxPackageSize - metaInfoSize)
            for (i in 1 until packets.size) {
                buffer.put(packets[i])
            }
            assertThat(buffer.array()).isEqualTo(data)
        }
    }

    @Test
    fun packetData_case1() {
        val packetsWorker = TinyPacketsWorker(parserCallback)
        packetsWorker.maxPacketSize = 20

        val data = "ACK{This is a Ping message#1 from MagicPingClient}".toByteArray()
        val packets = packetsWorker.packetData(data)
        assertThat(packets.size).isEqualTo(3)
        assertThat(encodeWithHex(packets[0])).isEqualTo("8B4F993B0101053241434B7B5468697320697320")
        assertThat(encodeWithHex(packets[1])).isEqualTo("612050696E67206D65737361676523312066726F")
        assertThat(encodeWithHex(packets[2])).isEqualTo("6D204D6167696350696E67436C69656E747D")

        packetsWorker.parsePackets(packets[0])
        packetsWorker.parsePackets(packets[1])
        packetsWorker.parsePackets(packets[2])
        assertThat(parserCallback.getData()).isEqualTo(data)
    }

    @Test
    fun parsePackets_empty() {
        val packetsWorker = TinyPacketsWorker(parserCallback)
        packetsWorker.maxPacketSize = 100

        // one side
        val packets = packetsWorker.packetData(generateData(0))
        assertThat(packets.size).isEqualTo(1)

        packetsWorker.parsePackets(packets[0])
        assertThat(parserCallback.getData()).isNull()

        // the other side
        packetsWorker.parsePackets(TinyPacketsWorker.HEADER_MAGIC)
        assertThat(packetsWorker.parserState).isEqualTo(ParserState.VERSION)
        packetsWorker.parsePackets(byteArrayOf(Version.V1))
        assertThat(packetsWorker.parserState).isEqualTo(ParserState.NUMBER)
        packetsWorker.parsePackets(byteArrayOf(5)) // number
        assertThat(packetsWorker.parserState).isEqualTo(ParserState.DATA_CRC)
        packetsWorker.parsePackets(byteArrayOf(0)) // CRC
        assertThat(packetsWorker.parserState).isEqualTo(ParserState.DATA_SIZE)
        packetsWorker.parsePackets(byteArrayOf(0)) // data size
        assertThat(packetsWorker.parserState).isEqualTo(ParserState.HEADER_MAGIC)
    }

    @Test
    fun parsePackets_normal() {
        val packetsWorker = TinyPacketsWorker(parserCallback)
        packetsWorker.maxPacketSize = 100

        for (length in 1..1024) {
            val data = generateData(length)
            val packets = packetsWorker.packetData(data)
            packets.forEach { packetsWorker.parsePackets(it) }
            assertThat(parserCallback.getData()).isEqualTo(data)
        }
    }

    @Test
    fun parsePackets_corruptHeaderMagic() {
        val packetsWorker = TinyPacketsWorker(parserCallback)
        packetsWorker.maxPacketSize = 100

        // corrupt
        val buffer = ByteBuffer.allocate(5).order(ByteOrder.LITTLE_ENDIAN)
        buffer.put(TinyPacketsWorker.HEADER_MAGIC[0])
        buffer.put(TinyPacketsWorker.HEADER_MAGIC[1])
        buffer.put(1) // invalid HEADER_MAGIC
        buffer.put(2) // invalid HEADER_MAGIC
        buffer.flip()

        packetsWorker.parsePackets(buffer.array())
        assertThat(packetsWorker.parserState).isEqualTo(ParserState.HEADER_MAGIC)

        // restore
        buffer.clear()
        buffer.put(TinyPacketsWorker.HEADER_MAGIC)
        buffer.put(Version.V1)
        buffer.flip()
        packetsWorker.parsePackets(buffer.array())
        assertThat(packetsWorker.parserState).isEqualTo(ParserState.NUMBER)
    }

    @Test
    fun parsePackets_corruptVersion() {
        val packetsWorker = TinyPacketsWorker(parserCallback)
        packetsWorker.maxPacketSize = 100

        // one side
        packetsWorker.parsePackets(TinyPacketsWorker.HEADER_MAGIC)
        assertThat(packetsWorker.parserState).isEqualTo(ParserState.VERSION)
        packetsWorker.parsePackets(byteArrayOf(-1)) // unknown version
        assertThat(packetsWorker.parserState).isEqualTo(ParserState.HEADER_MAGIC)

        // the other side
        val buffer = ByteBuffer.allocate(5).order(ByteOrder.LITTLE_ENDIAN)
        buffer.put(TinyPacketsWorker.HEADER_MAGIC)
        buffer.put(-1) // unknown version
        buffer.flip()
        packetsWorker.parsePackets(buffer.array())
        assertThat(packetsWorker.parserState).isEqualTo(ParserState.HEADER_MAGIC)
    }

    @Test
    fun parsePackets_corruptCrc() {
        val packetsWorker = TinyPacketsWorker(parserCallback)
        packetsWorker.maxPacketSize = 100

        val data = generateData(1000)
        val packets = packetsWorker.packetData(data)

        // normal first
        packets.forEach { packetsWorker.parsePackets(it) }
        assertThat(parserCallback.getData()).isEqualTo(data)

        // emulate data corrupt
        packets.last()[0] = packets.last()[0].inc()
        packets.forEach { packetsWorker.parsePackets(it) }
        assertThat(parserCallback.getData()).isNull()
    }

    @Test
    fun testReset() {
        val packetsWorker = TinyPacketsWorker(parserCallback)
        packetsWorker.maxPacketSize = 100

        val buffer = ByteBuffer.allocate(5).order(ByteOrder.LITTLE_ENDIAN)
        buffer.put(TinyPacketsWorker.HEADER_MAGIC).put(Version.V1)
        buffer.flip()

        packetsWorker.parsePackets(buffer.array())
        assertThat(packetsWorker.parserState).isEqualTo(ParserState.NUMBER)

        packetsWorker.reset()
        assertThat(packetsWorker.parserState).isEqualTo(ParserState.HEADER_MAGIC)
    }

    @Test
    fun setDebug_false() {
        val tree = TimberJvmTree()
        Timber.plant(tree)

        val packetsWorker = TinyPacketsWorker(parserCallback)
        packetsWorker.maxPacketSize = 100

        for (length in 1..500) {
            val data = generateData(length)
            val packets = packetsWorker.packetData(data)
            packets.forEach { packetsWorker.parsePackets(it) }
            assertThat(parserCallback.getData()).isEqualTo(data)
        }

        assertThat(tree.hasLogs()).isFalse()
    }

    @Test
    fun setDebug_true() {
        val tree = TimberJvmTree()
        Timber.plant(tree)

        val packetsWorker = TinyPacketsWorker(parserCallback)
        packetsWorker.maxPacketSize = 100
        packetsWorker.debugLog = true

        val data = generateData(300)
        val packets = packetsWorker.packetData(data)
        packets.forEach { packetsWorker.parsePackets(it) }
        assertThat(parserCallback.getData()).isEqualTo(data)

        assertThat(tree.hasLogs()).isTrue()
    }
}