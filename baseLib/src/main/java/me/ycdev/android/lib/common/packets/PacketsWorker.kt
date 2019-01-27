package me.ycdev.android.lib.common.packets

import androidx.annotation.VisibleForTesting
import timber.log.Timber
import java.nio.ByteBuffer
import java.nio.ByteOrder

abstract class PacketsWorker(
    private val ownerTag: String,
    protected val callback: ParserCallback
) {
    var maxPacketSize: Int = 0
        set(value) {
            if (value < MAX_PACKET_SIZE_MIN) {
                throw PacketsException("The value ($value) for maxPacketSize is too small.")
            }
            if (debugLog) {
                Timber.tag(ownerTag).d("setMaxPacketSize: %d", value)
            }
            field = value
        }
    var debugLog = false

    @VisibleForTesting
    internal var parserState = ParserState.HEADER_MAGIC
    protected var readBuffer: ByteBuffer

    init {
        readBuffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE).order(ByteOrder.LITTLE_ENDIAN)
    }

    fun reset() {
        parserState = ParserState.HEADER_MAGIC
        readBuffer.clear()
    }

    abstract fun packetData(data: ByteArray): List<ByteArray>

    abstract fun parsePackets(data: ByteArray)

    interface ParserCallback {
        fun onDataParsed(data: ByteArray)
    }

    @VisibleForTesting
    internal enum class ParserState {
        HEADER_MAGIC,
        VERSION,
        NUMBER,
        DATA_CRC,
        DATA_SIZE,
        DATA
    }

    object Version {
        const val UNKNOWN: Byte = 0
        const val V1: Byte = 1
        const val V2: Byte = 2
        const val V3: Byte = 3
    }

    companion object {
        const val MAX_PACKET_SIZE_MIN = 20
        private const val DEFAULT_BUFFER_SIZE = 1024
    }
}