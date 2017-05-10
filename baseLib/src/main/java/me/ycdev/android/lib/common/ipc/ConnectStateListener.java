package me.ycdev.android.lib.common.ipc;

public interface ConnectStateListener {
    void onStateChanged(@ServiceClient.ConnectState int newState);
}
