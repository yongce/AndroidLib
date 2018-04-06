package me.ycdev.android.lib.test.log;

import android.support.annotation.NonNull;

import timber.log.Timber;

public class TimberJvmTree extends Timber.Tree {
    @Override
    protected void log(int priority, String tag, @NonNull String message, Throwable t) {
        System.out.println(AndroidLogHelper.getPriorityName(priority) + "/" + tag + ": " + message);
        if (t != null) {
            t.printStackTrace(System.out);
        }
    }
}
