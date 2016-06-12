package me.ycdev.android.lib.commonui.utils;

import android.app.Activity;
import android.os.SystemClock;

import me.ycdev.android.lib.commonui.base.WaitingAsyncTaskBase;

public class WaitingAsyncTask extends WaitingAsyncTaskBase<Void, Void, Void> {
    private static final long WAITING_TIME_MIN = 500; // ms

    private Runnable mTask;
    private String mMsg;

    public WaitingAsyncTask(Activity activity, String msg, Runnable task) {
        super(activity);
        mMsg = msg;
        mTask = task;
    }

    @Override
    protected String getInitMessage() {
        return mMsg;
    }

    @Override
    protected Void doInBackground(Void... params) {
        long timeStart = SystemClock.elapsedRealtime();
        mTask.run();
        long timeUsed = SystemClock.elapsedRealtime() - timeStart;
        if (timeUsed < WAITING_TIME_MIN) {
            SystemClock.sleep(WAITING_TIME_MIN - timeUsed);
        }
        return null;
    }
}
