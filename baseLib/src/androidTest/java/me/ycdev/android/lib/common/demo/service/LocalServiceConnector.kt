package me.ycdev.android.lib.common.demo.service

import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.annotation.NonNull

import me.ycdev.android.lib.common.ipc.ServiceConnector

class LocalServiceConnector(cxt: Context) : ServiceConnector<IDemoService>(cxt, SERVICE_NAME) {

    @NonNull
    override fun getServiceIntent(): Intent {
        return Intent(mAppContext, LocalService::class.java)
    }

    override fun asInterface(service: IBinder): IDemoService? {
        return IDemoService.Stub.asInterface(service)
    }

    companion object {
        private const val SERVICE_NAME = "LocalService"
    }
}
