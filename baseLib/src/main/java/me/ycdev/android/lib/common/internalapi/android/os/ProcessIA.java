package me.ycdev.android.lib.common.internalapi.android.os;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import me.ycdev.android.lib.common.utils.IoUtils;
import me.ycdev.android.lib.common.utils.LibConfigs;
import me.ycdev.android.lib.common.utils.LibLogger;
import me.ycdev.android.lib.common.utils.StringUtils;

@SuppressWarnings({"unused", "WeakerAccess"})
@SuppressLint("PrivateApi")
public class ProcessIA {
    private static final String TAG = "ProcessIA";
    private static final boolean DEBUG = LibConfigs.DEBUG_LOG;

    private static Method sMtd_setArgV0;
    private static Method sMtd_readProcLines;
    private static Method sMtd_getParentPid;
    private static Method sMtd_myPpid;

    private static void reflect_setArgV0() {
        if (sMtd_setArgV0 != null) {
            return;
        }

        try {
            // Android 1.6: public static final native void setArgV0(String text);
            sMtd_setArgV0 = android.os.Process.class.getMethod("setArgV0", String.class);
        } catch (NoSuchMethodException e) {
            if (DEBUG) LibLogger.w(TAG, "method not found", e);
        }
    }

    public static void setArgV0(@NonNull String processName) {
        reflect_setArgV0();
        if (sMtd_setArgV0 != null) {
            try {
                sMtd_setArgV0.invoke(null, processName);
            } catch (IllegalAccessException e) {
                if (DEBUG) LibLogger.w(TAG, "Failed to invoke #setArgV0()", e);
            } catch (InvocationTargetException e) {
                if (DEBUG) LibLogger.w(TAG, "Failed to invoke #setArgV0() more", e);
            }
        } else {
            if (DEBUG) LibLogger.w(TAG, "#setArgV0() not available");
        }
    }

    /**
     * Just for unit test.
     */
    @RestrictTo(RestrictTo.Scope.TESTS)
    static boolean checkReflect_setArgV0() {
        reflect_setArgV0();
        return sMtd_setArgV0 != null;
    }

    private static void reflect_readProcLines() {
        if (sMtd_readProcLines != null) {
            return;
        }

        try {
            // Android 1.6: public static final native void readProcLines(String path,
            //                  String[] reqFields, long[] outSizes);
            sMtd_readProcLines = android.os.Process.class.getMethod("readProcLines",
                    String.class, String[].class, long[].class);
        } catch (NoSuchMethodException e) {
            if (DEBUG) LibLogger.w(TAG, "method not found", e);
        }
    }

    public static void readProcLines(@NonNull String path, @NonNull String[] reqFields,
            @NonNull long[] outSizes) {
        reflect_readProcLines();
        if (sMtd_readProcLines != null) {
            try {
                sMtd_readProcLines.invoke(null, path, reqFields, outSizes);
            } catch (IllegalAccessException e) {
                if (DEBUG) LibLogger.w(TAG, "Failed to invoke #readProcLines()", e);
            } catch (InvocationTargetException e) {
                if (DEBUG) LibLogger.w(TAG, "Failed to invoke #readProcLines() more", e);
            }
        } else {
            if (DEBUG) LibLogger.w(TAG, "#readProcLines() not available");
        }
    }

    @Nullable
    public static String getProcessName(int pid) {
        String cmdlineFile = "/proc/" + pid + "/cmdline";
        try {
            return IoUtils.readAllLines(cmdlineFile).trim();
        } catch (IOException e) {
            if (DEBUG) LibLogger.w(TAG, "cannot read cmdline file", e);
        }
        return null;
    }

    /**
     * Return the pid of the specified process name. If there are multiple processes
     * which have same process name, then just return the first one.
     * @param procName The process name
     * @return -1 if the specified process not found
     */
    public static int getProcessPid(@NonNull String procName) {
        File[] procList = new File("/proc").listFiles();
        if (procList != null && procList.length > 0) {
            for (File procFile : procList) {
                if (!procFile.isDirectory()) {
                    continue;
                }
                if (!TextUtils.isDigitsOnly(procFile.getName())) {
                    continue;
                }
                int pid = StringUtils.parseInt(procFile.getName(), -1);
                if (pid > -1) {
                    String curProcName = getProcessName(pid);
                    if (procName.equals(curProcName)) {
                        return pid;
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Just for unit test.
     */
    @RestrictTo(RestrictTo.Scope.TESTS)
    static boolean checkReflect_readProcLines() {
        reflect_readProcLines();
        return sMtd_readProcLines != null;
    }

    private static void reflect_getParentPid() {
        if (sMtd_getParentPid != null) {
            return;
        }

        try {
            // Android 4.0: public static final int getParentPid(int pid)
            sMtd_getParentPid = android.os.Process.class.getMethod("getParentPid", int.class);
        } catch (NoSuchMethodException e) {
            if (DEBUG) LibLogger.w(TAG, "method not found", e);
        }
    }

    public static int getParentPid(int pid) {
        reflect_getParentPid();
        if (sMtd_getParentPid != null) {
            try {
                return (int) sMtd_getParentPid.invoke(null, pid);
            } catch (IllegalAccessException e) {
                if (DEBUG) LibLogger.w(TAG, "Failed to invoke #getParentPid()", e);
            } catch (InvocationTargetException e) {
                if (DEBUG) LibLogger.w(TAG, "Failed to invoke #getParentPid() more", e);
            }
        } else {
            String[] procStatusLabels = { "PPid:" };
            long[] procStatusValues = new long[1];
            procStatusValues[0] = -1;
            readProcLines("/proc/" + pid + "/status", procStatusLabels, procStatusValues);
            return (int) procStatusValues[0];
        }
        return -1;
    }

    /**
     * Just for unit test.
     */
    @RestrictTo(RestrictTo.Scope.TESTS)
    static boolean checkReflect_getParentPid() {
        reflect_getParentPid();
        return sMtd_getParentPid != null;
    }


    private static void reflect_myPpid() {
        if (sMtd_myPpid != null || Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }

        try {
            // Android 4.4: public static final int myPpid()
            sMtd_myPpid = android.os.Process.class.getMethod("myPpid");
        } catch (NoSuchMethodException e) {
            if (DEBUG) LibLogger.w(TAG, "method not found", e);
        }
    }

    public static int myPpid() {
        reflect_myPpid();
        if (sMtd_myPpid != null) {
            try {
                return (int) sMtd_myPpid.invoke(null);
            } catch (IllegalAccessException e) {
                if (DEBUG) LibLogger.w(TAG, "Failed to invoke #myPpid()", e);
            } catch (InvocationTargetException e) {
                if (DEBUG) LibLogger.w(TAG, "Failed to invoke #myPpid() more", e);
            }
        } else {
            return getParentPid(android.os.Process.myPid());
        }
        return -1;
    }

    /**
     * Just for unit test.
     */
    @RestrictTo(RestrictTo.Scope.TESTS)
    static boolean checkReflect_myPpid() {
        reflect_myPpid();
        return sMtd_myPpid != null || Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT;
    }

}
