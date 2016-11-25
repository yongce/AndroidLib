package me.ycdev.android.lib.common.utils;

import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.ycdev.android.lib.common.annotation.GuardedBy;

public class FileLogger extends LibLogger {
    private static final String TAG = "FileLogger";

    private Writer mFileWriter = null;
    // Each log file every day.
    private String mCurrentDay;

    private SimpleDateFormat mDayFormat = new SimpleDateFormat("yyMMdd", Locale.US);
    private SimpleDateFormat mTimeFormat = new SimpleDateFormat("MM-dd HH:mm:ss:SSS", Locale.US);

    private String mLogDir;
    private String mLogFileNamePrefix;
    private String mProcessNameSuffix;

    public FileLogger(String logDir, String logFileNamePrefix) {
        this(logDir, logFileNamePrefix, null);
    }

    public FileLogger(@NonNull String logDir, @NonNull String logFileNamePrefix,
            @Nullable String processNameSuffix) {
        mLogDir = logDir;
        mLogFileNamePrefix = logFileNamePrefix;
        mProcessNameSuffix = processNameSuffix;
    }

    @GuardedBy("this")
    public synchronized void close() {
        IoUtils.closeQuietly(mFileWriter);
        mFileWriter = null;
    }

    @GuardedBy("this")
    public void logToFile(String tag, String msg, Throwable tr) {
        StringBuilder builder = new StringBuilder();
        builder.append(mTimeFormat.format(new Date()));
        builder.append(" ");
        builder.append(tag);
        builder.append("\t");
        builder.append(Process.myPid()).append(" ").append(Process.myTid()).append(" ");
        if (!TextUtils.isEmpty(msg)) {
            builder.append(msg);
        }
        if (tr != null) {
            builder.append("\n\t");
            builder.append(Log.getStackTraceString(tr));
        }
        builder.append("\n");

        writeLog(builder.toString());
    }

    @GuardedBy("this")
    private synchronized void writeLog(String logLine) {
        if (null == mFileWriter) {
            if (!openFile()) {
                return;
            }
        }

        try {
            String day = getCurrentDay();
            // If is another day, then create a new log file.
            if (!day.equals(mCurrentDay)) {
                mFileWriter.flush();
                mFileWriter.close();
                mFileWriter = null;

                boolean success = openFile();
                if (!success) {
                    return;
                }
            }

            mFileWriter.write(logLine);
            mFileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GuardedBy("this")
    private boolean openFile() {
        if (mLogDir == null) {
            return false;
        }

        File logDirFile = new File(mLogDir);
        if (!logDirFile.exists()) {
            if (!logDirFile.mkdirs()) {
                Log.w(TAG, "Cannot create dir: " + mLogDir);
                return false;
            }
        }

        mCurrentDay = getCurrentDay();
        try {
            File logFile = new File(mLogDir, composeFileName(mCurrentDay));
            mFileWriter = new FileWriter(logFile, true);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String composeFileName(String currentDay) {
        StringBuilder sb = new StringBuilder();
        sb.append(mLogFileNamePrefix).append("_log_").append(currentDay);
        if (!TextUtils.isEmpty(mProcessNameSuffix)) {
            sb.append("_").append(mProcessNameSuffix);
        }
        sb.append(".txt");
        return sb.toString();
    }

    private String getCurrentDay() {
        return mDayFormat.format(new Date());
    }

}
