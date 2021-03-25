package me.ycdev.android.lib.common.demo.service.operation

import android.os.RemoteException
import androidx.annotation.NonNull
import me.ycdev.android.lib.common.demo.service.IDemoService
import me.ycdev.android.lib.common.ipc.IpcOperation
import java.util.concurrent.CountDownLatch

class HelloOperation(private val mGift: String) : IpcOperation<IDemoService> {
    private var mLatch: CountDownLatch? = null

    fun setNotifier(latch: CountDownLatch): HelloOperation {
        mLatch = latch
        return this
    }

    @Throws(RemoteException::class)
    override fun execute(@NonNull service: IDemoService) {
        service.sayHello(mGift)
        if (mLatch != null) {
            mLatch!!.countDown()
        }
    }

    override fun toString(): String {
        return "Operation[Hello]"
    }
}
