package me.ycdev.android.lib.common.utils;

import android.os.HandlerThread;
import android.os.Looper;

public class ThreadManager {
    private static ThreadManager sInstance = new ThreadManager();

    private HandlerThread mLocalServiceRequestIpcThread;
    private HandlerThread mRemoteServiceRequestIpcThread;

    public static ThreadManager getInstance() {
        return sInstance;
    }

    public synchronized Looper localServiceRequestIpcLooper() {
        if (mLocalServiceRequestIpcThread == null) {
            mLocalServiceRequestIpcThread = new HandlerThread("LocalService.client");
            mLocalServiceRequestIpcThread.start();
        }
        return mLocalServiceRequestIpcThread.getLooper();
    }

    public synchronized Looper remoteServiceRequestIpcLooper() {
        if (mRemoteServiceRequestIpcThread == null) {
            mRemoteServiceRequestIpcThread = new HandlerThread("RemoteService.client");
            mRemoteServiceRequestIpcThread.start();
        }
        return mRemoteServiceRequestIpcThread.getLooper();
    }
}
