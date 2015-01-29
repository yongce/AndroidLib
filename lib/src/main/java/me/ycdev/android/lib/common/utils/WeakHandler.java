package me.ycdev.android.lib.common.utils;

import java.lang.ref.WeakReference;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

public class WeakHandler extends Handler {
    public interface MessageHandler {
        void handleMessage(Message msg);
    }

    private WeakReference<MessageHandler> mTargetHandler;

    public WeakHandler(@NonNull MessageHandler msgHandler) {
        mTargetHandler = new WeakReference<MessageHandler>(msgHandler);
    }

    @Override
    public void handleMessage(Message msg) {
        MessageHandler realHandler = mTargetHandler.get();
        if (realHandler != null) {
            realHandler.handleMessage(msg);
        }
    }
}
