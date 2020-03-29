package me.ycdev.android.lib.common.demo.service.operation

import android.os.RemoteException
import androidx.annotation.NonNull
import java.util.concurrent.CountDownLatch
import me.ycdev.android.lib.common.demo.service.IDemoService
import me.ycdev.android.lib.common.ipc.IpcOperation

class WhoOperation : IpcOperation<IDemoService> {
    private var mLatch: CountDownLatch? = null

    fun setNotifier(latch: CountDownLatch): WhoOperation {
        mLatch = latch
        return this
    }

    @Throws(RemoteException::class)
    override fun execute(@NonNull service: IDemoService) {
        service.who()
        if (mLatch != null) {
            mLatch!!.countDown()
        }
    }

    override fun toString(): String {
        return "Operation[who]"
    }
}
