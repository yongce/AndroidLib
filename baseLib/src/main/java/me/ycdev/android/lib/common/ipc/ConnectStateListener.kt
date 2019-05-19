package me.ycdev.android.lib.common.ipc

interface ConnectStateListener {
    fun onStateChanged(@ServiceConnector.ConnectState newState: Int)
}
