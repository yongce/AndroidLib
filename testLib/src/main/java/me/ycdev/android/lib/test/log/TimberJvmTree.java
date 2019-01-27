package me.ycdev.android.lib.test.log;

import java.util.ArrayList;

import androidx.annotation.NonNull;

import timber.log.Timber;

public class TimberJvmTree extends Timber.Tree {
    private ArrayList<String> mLogs = new ArrayList<>();

    public void clear() {
        mLogs.clear();
    }

    public boolean hasLogs() {
        return !mLogs.isEmpty();
    }

    @Override
    protected void log(int priority, String tag, @NonNull String message, Throwable t) {
        String log = AndroidLogHelper.getPriorityName(priority) + "/" + tag + ": " + message;
        mLogs.add(log);
        System.out.println(log);
        if (t != null) {
            t.printStackTrace(System.out);
        }
    }
}
