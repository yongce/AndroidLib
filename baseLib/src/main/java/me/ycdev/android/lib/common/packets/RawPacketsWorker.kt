package me.ycdev.android.lib.common.packets

class RawPacketsWorker(callback: ParserCallback) : PacketsWorker(TAG, callback) {
    override fun packetData(data: ByteArray): List<ByteArray> {
        return arrayListOf(data)
    }

    override fun parsePackets(data: ByteArray) {
        // support empty data & ignore it
        if (data.isNotEmpty()) {
            callback.onDataParsed(data)
        }
    }

    companion object {
        const val TAG = "RawPacketsWorker"
    }
}
