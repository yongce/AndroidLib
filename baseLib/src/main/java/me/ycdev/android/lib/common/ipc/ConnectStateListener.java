package me.ycdev.android.lib.common.ipc;

public interface ConnectStateListener {
    void onStateChanged(@ServiceConnector.ConnectState int newState);
}
