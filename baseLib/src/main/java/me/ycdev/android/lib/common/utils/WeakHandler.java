package me.ycdev.android.lib.common.utils;

import java.lang.ref.WeakReference;

import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;

@SuppressWarnings({"unused", "WeakerAccess"})
public class WeakHandler extends Handler {
    private WeakReference<Handler.Callback> mTargetHandler;

    public WeakHandler(@NonNull Handler.Callback msgHandler) {
        mTargetHandler = new WeakReference<>(msgHandler);
    }

    @Override
    public void handleMessage(Message msg) {
        Handler.Callback realHandler = mTargetHandler.get();
        if (realHandler != null) {
            realHandler.handleMessage(msg);
        }
    }
}
