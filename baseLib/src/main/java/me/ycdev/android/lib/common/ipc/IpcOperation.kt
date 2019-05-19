package me.ycdev.android.lib.common.ipc

import android.os.RemoteException

interface IpcOperation<IService> {
    @Throws(RemoteException::class)
    fun execute(service: IService)
}
