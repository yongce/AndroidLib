package me.ycdev.androidlib.utils;

import java.lang.ref.WeakReference;

import android.os.Handler;
import android.os.Message;

public class WeakHandler extends Handler {
    public interface MessageHandler {
        void handleMessage(Message msg);
    }

    private WeakReference<MessageHandler> mTargetHandler;

    public WeakHandler(MessageHandler msgHandler) {
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
