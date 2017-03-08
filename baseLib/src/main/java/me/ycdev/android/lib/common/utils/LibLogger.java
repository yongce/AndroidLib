package me.ycdev.android.lib.common.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.util.Log;

import java.util.Locale;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class LibLogger {
    private static final String TAG = "AndroidLib";
    private static boolean sJvmLogger = false;

    @RestrictTo(RestrictTo.Scope.SUBCLASSES)
    protected LibLogger() {
        // nothing to do
    }

    public static void enableJvmLogger() {
        sJvmLogger = true;
    }

    public static void setFileLogger(FileLogger fileLogger) {
        if (!sJvmLogger) {
            AndroidLogger.setFileLogger(fileLogger);
        }
    }

    public static void setDebug(boolean debug) {
        if (!sJvmLogger) {
            AndroidLogger.setDebug(debug);
        }
    }

    public static boolean isDebug() {
        return AndroidLogger.isDebug();
    }

    public static void v(@NonNull String tag, @NonNull String msg, Object... args) {
        if (!LibConfigs.DISABLE_LOG) {
            log(Log.VERBOSE, tag, msg, null, args);
        }
    }

    public static void d(@NonNull String tag, @NonNull String msg, Object... args) {
        if (!LibConfigs.DISABLE_LOG) {
            log(Log.DEBUG, tag, msg, null, args);
        }
    }

    public static void i(@NonNull String tag, @NonNull String msg, Object... args) {
        if (!LibConfigs.DISABLE_LOG) {
            log(Log.INFO, tag, msg, null, args);
        }
    }

    public static void w(@NonNull String tag, @NonNull String msg, Object... args) {
        if (!LibConfigs.DISABLE_LOG) {
            log(Log.WARN, tag, msg, null, args);
        }
    }

    public static void w(@NonNull String tag, @NonNull String msg, @NonNull Throwable e,
            Object... args) {
        if (!LibConfigs.DISABLE_LOG) {
            log(Log.WARN, tag, msg, e, args);
        }
    }

    public static void w(@NonNull String tag, @NonNull Throwable e, Object... args) {
        if (!LibConfigs.DISABLE_LOG) {
            log(Log.WARN, tag, null, e, args);
        }
    }

    public static void e(@NonNull String tag, @NonNull String msg, Object... args) {
        if (!LibConfigs.DISABLE_LOG) {
            log(Log.ERROR, tag, msg, null, args);
        }
    }

    public static void e(@NonNull String tag, @NonNull String msg, @NonNull Throwable e,
            Object... args) {
        if (!LibConfigs.DISABLE_LOG) {
            log(Log.ERROR, tag, msg, e, args);
        }
    }

    public static void log(int level, @NonNull String tag, @Nullable String msg,
            @Nullable Throwable tr, Object... args) {
        if (sJvmLogger) {
            if (msg != null && args != null && args.length > 0) {
                msg = String.format(Locale.US, msg, args);
            }
            System.out.println("[" + tag + "] " + msg);
            if (tr != null) {
                tr.printStackTrace();
            }
        } else {
            AndroidLogger.log(level, tag, msg, tr, args);
        }
    }

    private static class AndroidLogger {
        private static boolean sDebug = LibConfigs.DEBUG_LOG;
        private static FileLogger sFileLogger;

        static void setFileLogger(FileLogger fileLogger) {
            sFileLogger = fileLogger;
        }

        static void setDebug(boolean debug) {
            sDebug = debug;
            if (!debug && sFileLogger != null) {
                sFileLogger.close();
            }
        }

        static boolean isDebug() {
            return sDebug;
        }

        static void log(int level, @NonNull String tag, @Nullable String msg,
                @Nullable Throwable tr, Object... args) {
            if (showLog(level, tag)) {
                if (msg != null && args != null && args.length > 0) {
                    msg = String.format(Locale.US, msg, args);
                }
                if (tr == null) {
                    Log.println(level, tag, msg);
                } else {
                    Log.println(level, tag, msg + '\n' + Log.getStackTraceString(tr));
                }
                logToFile(tag, msg, tr);
            }
        }

        private static boolean showLog(int level, String tag) {
            return isLoggable(tag, level) || sDebug;
        }

        private static boolean isLoggable(String tag, int level) {
            try {
                return Log.isLoggable(tag, level);
            } catch (Exception e) {
                if (sDebug) {
                    throw e;
                } else {
                    Log.e(TAG, "please check the tag length?", e);
                }
            }
            return false;
        }

        private static void logToFile(String tag, String msg, Throwable tr) {
            if (sDebug && sFileLogger != null) {
                sFileLogger.logToFile(tag, msg, tr);
            }
        }

    }
}
