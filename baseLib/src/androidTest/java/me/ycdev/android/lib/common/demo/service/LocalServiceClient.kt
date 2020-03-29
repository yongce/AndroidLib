package me.ycdev.android.lib.common.demo.service

import android.content.Context
import androidx.annotation.NonNull
import me.ycdev.android.lib.common.ipc.ServiceClientBase
import me.ycdev.android.lib.common.utils.ThreadManager

class LocalServiceClient(@NonNull context: Context) : ServiceClientBase<IDemoService>(
    context,
    SERVICE_NAME,
    ThreadManager.instance.localServiceRequestIpcLooper(),
    LocalServiceConnector(context)
) {
    companion object {
        private const val SERVICE_NAME = "LocalService"
    }
}
