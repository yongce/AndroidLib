package me.ycdev.android.lib.common.ipc;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

public class IpcHandler extends Handler {
    private static volatile IpcHandler sInstance;

    private IpcHandler() {
        super(createLooper());
    }

    private static Looper createLooper() {
        HandlerThread thread = new HandlerThread("IpcHandler");
        thread.start();
        return thread.getLooper();
    }

    public static IpcHandler getInstance() {
        if (sInstance == null) {
            synchronized (IpcHandler.class) {
                if (sInstance == null) {
                    sInstance = new IpcHandler();
                }
            }
        }
        return sInstance;
    }
}
