package me.ycdev.android.lib.common.packets

import androidx.annotation.VisibleForTesting
import timber.log.Timber
import java.lang.Math.min
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.ArrayList
import java.util.Arrays
import kotlin.experimental.xor

/**
 * Supported package formats:
 *
 * - Package format V1:
 * ```
 * <header magic, 4 bytes> + <version, 1 byte> + <data number[0~255], 1 byte>
 *     + <data DATA_CRC, 1 byte> + <data size, 1 bytes> + <data, N bytes>
 * ```
 *
 * - Package format V2:
 * ```
 * <header magic, 4 bytes> + <version, 1 byte> + <data number[0~255], 1 byte>
 *     + <data DATA_CRC, 1 byte> + <data size, 2 bytes> + <data, N bytes>
 * ```
 *
 * - Package format V3:
 * ```
 * <header magic, 4 bytes> + <version, 1 byte> + <data number[0~255], 1 byte>
 *     + <data DATA_CRC, 1 byte> + <data size, 4 bytes> + <data, N bytes>
 * ```
 */
class TinyPacketsWorker(callback: ParserCallback) : PacketsWorker(TAG, callback) {
    // for packet
    @VisibleForTesting
    internal var dataNumberHolder: Byte = 0
        private set

    // for parser
    private var totalParsedBytes: Long = 0L
    private var curVersion: Byte = Version.UNKNOWN
    private var curNumber: Byte = 0
    private var curDataCrc: Byte = 0
    private var curDataSize: Int = 0

    private fun getDataNumber(): Byte {
        if (dataNumberHolder < Byte.MAX_VALUE) {
            dataNumberHolder++
        } else {
            dataNumberHolder = 1
        }
        return dataNumberHolder
    }

    override fun packetData(data: ByteArray): List<ByteArray> {
        if (maxPacketSize < MAX_PACKET_SIZE_MIN) {
            throw PacketsException("Not supported maxPacketSize ($maxPacketSize)")
        }

        val version = calculateVersion(data.size)
        val metaInfoSize = calculateMetaInfoSize(version)
        val packetSize = min(maxPacketSize, data.size + metaInfoSize)
        val dataNumber = getDataNumber()
        val dataCrc = calculateDataCrc(data)
        val packets = ArrayList<ByteArray>()

        // first packet
        val buffer = ByteBuffer.allocate(packetSize).order(ByteOrder.LITTLE_ENDIAN)
        buffer.put(HEADER_MAGIC)
        buffer.put(version)
        buffer.put(dataNumber)
        buffer.put(dataCrc)
        fillDataSize(buffer, version, data.size)
        buffer.put(data, 0, packetSize - metaInfoSize)
        buffer.flip()
        packets.add(buffer.array())

        var offset = packetSize - metaInfoSize
        while (offset < data.size) {
            val packet: ByteArray
            if (offset + maxPacketSize < data.size) {
                packet = Arrays.copyOfRange(data, offset, offset + maxPacketSize)
                offset += maxPacketSize
            } else {
                packet = Arrays.copyOfRange(data, offset, data.size)
                offset = data.size
            }
            packets.add(packet)
        }

        return packets
    }

    override fun parsePackets(data: ByteArray) {
        totalParsedBytes += data.size
        if (debugLog) {
            Timber.tag(TAG).d(
                "parsePackets, size=%d, totalBytes=%d",
                data.size, totalParsedBytes
            )
        }

        // the buffer is in 'write' state
        if (readBuffer.remaining() >= data.size) {
            readBuffer.put(data)
        } else {
            // change the buffer to 'read' state
            readBuffer.flip()

            var newCapacity = readBuffer.capacity() * 2
            while (newCapacity < data.size + readBuffer.remaining()) {
                newCapacity *= 2
            }

            if (debugLog) {
                Timber.tag(TAG).d("enlarge buffer capability to [%d]", newCapacity)
            }
            val newBuffer = ByteBuffer.allocate(newCapacity).order(ByteOrder.LITTLE_ENDIAN)
            newBuffer.put(readBuffer)
            newBuffer.put(data)
            readBuffer = newBuffer
        }

        // change the buffer to 'read' state
        readBuffer.flip()

        while (readBuffer.remaining() > 0) {
            if (parserState == ParserState.HEADER_MAGIC) {
                if (readBuffer.remaining() < 4) {
                    break // waiting for new data
                }

                var headerMagicFound = false
                val searchPos = readBuffer.position()
                while (readBuffer.remaining() >= 4) {
                    if (readBuffer.get() == HEADER_MAGIC[0] &&
                        readBuffer.get() == HEADER_MAGIC[1] &&
                        readBuffer.get() == HEADER_MAGIC[2] &&
                        readBuffer.get() == HEADER_MAGIC[3]
                    ) {
                        headerMagicFound = true
                        break
                    }
                }
                var bytesDiscarded = readBuffer.position() - searchPos
                if (headerMagicFound) {
                    bytesDiscarded -= 4
                }
                if (bytesDiscarded > 0) {
                    Timber.tag(TAG).e(
                        "%d bytes discarded. header found? %s",
                        bytesDiscarded, headerMagicFound
                    )
                }

                if (!headerMagicFound) {
                    Timber.tag(TAG).e("No magic header found.")
                    break // waiting for new data
                }

                if (debugLog) {
                    Timber.tag(TAG).d("header found")
                }
                parserState = ParserState.VERSION
            } else if (parserState == ParserState.VERSION) {
                curVersion = readBuffer.get()
                parserState = if (isValidVersion(curVersion)) {
                    ParserState.NUMBER
                } else {
                    Timber.tag(TAG).e("Unknown version (%d)", curVersion)
                    // new version (unknown) or data corrupted
                    ParserState.HEADER_MAGIC
                }
            } else if (parserState == ParserState.NUMBER) {
                curNumber = readBuffer.get()
                parserState = ParserState.DATA_CRC
            } else if (parserState == ParserState.DATA_CRC) {
                curDataCrc = readBuffer.get()
                parserState = ParserState.DATA_SIZE
            } else if (parserState == ParserState.DATA_SIZE) {
                val dataSizeBytes = getDataSizeBytes(curVersion)
                if (readBuffer.remaining() < dataSizeBytes) {
                    break // waiting for new data
                }

                curDataSize = readDataSize(readBuffer, curVersion)
                parserState = if (curDataSize == 0) {
                    // empty data is legal
                    Timber.tag(TAG).i("Empty data received, ignore")
                    ParserState.HEADER_MAGIC
                } else {
                    if (curDataSize > DATA_SIZE_WARNING) {
                        Timber.tag(TAG).w("big data found (size=%d), maybe corrupted", curDataSize)
                    }
                    ParserState.DATA
                }
            } else if (parserState == ParserState.DATA) {
                if (readBuffer.remaining() >= curDataSize) {
                    val parsedData = ByteArray(curDataSize)
                    readBuffer.get(parsedData)
                    val crc = calculateDataCrc(parsedData)
                    if (debugLog) {
                        Timber.tag(TAG).d(
                            "data parsed (number=%d, dataSize=%d)",
                            curNumber, parsedData.size
                        )
                    }
                    if (crc == curDataCrc) {
                        callback.onDataParsed(parsedData)
                    } else {
                        Timber.tag(TAG).e("Mismatch CRC: %d (expected: %d)", crc, curDataCrc)
                    }
                    parserState = ParserState.HEADER_MAGIC
                } else {
                    break // waiting for new data
                }
            } else {
                Timber.tag(TAG).e("unknown parserState [%s]", parserState)
                break
            }
        }

        // change the buffer to 'write' state
        readBuffer.compact()
    }

    companion object {
        private const val TAG = "TinyPacketsWorker"

        private const val DATA_SIZE_WARNING = 1024 * 100 // 100KB

        @VisibleForTesting
        internal val HEADER_MAGIC = byteArrayOf(-117, 79, -103, 59)

        @VisibleForTesting
        internal fun calculateVersion(dataSize: Int): Byte {
            return when (dataSize) {
                in 0..0xFF -> Version.V1
                in 0x100..0xFFFF -> Version.V2
                else -> Version.V3
            }
        }

        private fun isValidVersion(version: Byte): Boolean {
            return when (version) {
                Version.V1, Version.V2, Version.V3 -> true
                else -> false
            }
        }

        @VisibleForTesting
        internal fun calculateMetaInfoSize(version: Byte): Int {
            // <header magic, 4 bytes> + <version, 1 byte> + <data number[0~255], 1 byte>
            //     + <data DATA_CRC, 1 byte> + <data size, 1~4 bytes> + <data, N bytes>
            return 7 + getDataSizeBytes(version)
        }

        @VisibleForTesting
        internal fun getDataSizeBytes(version: Byte): Int {
            return when (version) {
                Version.V1 -> 1
                Version.V2 -> 2
                Version.V3 -> 4
                else -> throw PacketsException("Unknown data version ($version)")
            }
        }

        @VisibleForTesting
        internal fun calculateDataCrc(data: ByteArray): Byte {
            var crc: Byte = 0
            data.forEach { crc = crc.xor(it) }
            return crc
        }

        @VisibleForTesting
        internal fun fillDataSize(buffer: ByteBuffer, version: Byte, dataSize: Int) {
            when (version) {
                Version.V1 -> buffer.put(dataSize.toByte())
                Version.V2 -> buffer.putShort(dataSize.toShort())
                Version.V3 -> buffer.putInt(dataSize)
                else -> throw PacketsException("Unknown data version ($version")
            }
        }

        @VisibleForTesting
        internal fun readDataSize(buffer: ByteBuffer, version: Byte): Int {
            return when (version) {
                Version.V1 -> 0xFF and buffer.get().toInt()
                Version.V2 -> 0xFFFF and buffer.short.toInt()
                Version.V3 -> buffer.int
                else -> throw PacketsException("Unknown data version ($version")
            }
        }
    }
}
