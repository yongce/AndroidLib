package me.ycdev.android.lib.common.packets

import me.ycdev.android.lib.common.packets.PacketsWorker.ParserCallback
import me.ycdev.android.lib.test.log.TimberJvmTree
import org.junit.Before
import timber.log.Timber
import java.util.Random
import java.util.concurrent.ArrayBlockingQueue

open class PacketsWorkerTestBase {
    protected val parserCallback = CallbackImpl()
    private val random = Random(System.currentTimeMillis())

    @Before
    fun setup() {
        Timber.plant(TimberJvmTree())
    }

    fun generateData(length: Int): ByteArray {
        val data = ByteArray(length)
        random.nextBytes(data)
        return data
    }

    inner class CallbackImpl : ParserCallback {
        private val dataQueue = ArrayBlockingQueue<ByteArray>(5)

        internal fun getData(): ByteArray? {
            return dataQueue.poll()
        }

        override fun onDataParsed(data: ByteArray) {
            dataQueue.add(data)
        }
    }
}
