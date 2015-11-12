package me.ycdev.android.lib.commonui.utils;

import android.app.Activity;

import me.ycdev.android.lib.commonui.base.WaitingAsyncTaskBase;

public class WaitingAsyncTask extends WaitingAsyncTaskBase<Void, Void, Void> {
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
        mTask.run();
        return null;
    }
}
