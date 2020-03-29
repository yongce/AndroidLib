package me.ycdev.android.lib.common.internalapi.android.os

import android.annotation.SuppressLint
import android.text.TextUtils
import androidx.annotation.RestrictTo
import java.io.File
import java.io.IOException
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import me.ycdev.android.lib.common.utils.IoUtils
import me.ycdev.android.lib.common.utils.StringUtils
import timber.log.Timber

@Suppress("MemberVisibilityCanBePrivate", "unused")
@SuppressLint("PrivateApi")
object ProcessIA {
    private const val TAG = "ProcessIA"

    private var sMtd_setArgV0: Method? = null
    private var sMtd_readProcLines: Method? = null
    private var sMtd_getParentPid: Method? = null
    private var sMtd_myPpid: Method? = null

    private fun reflectSetArgV0() {
        if (sMtd_setArgV0 != null) {
            return
        }

        try {
            // Android 1.6: public static final native void setArgV0(String text);
            sMtd_setArgV0 = android.os.Process::class.java.getMethod("setArgV0", String::class.java)
        } catch (e: NoSuchMethodException) {
            Timber.tag(TAG).w(e, "method not found")
        }
    }

    fun setArgV0(processName: String) {
        reflectSetArgV0()
        if (sMtd_setArgV0 != null) {
            try {
                sMtd_setArgV0!!.invoke(null, processName)
            } catch (e: IllegalAccessException) {
                Timber.tag(TAG).w(e, "Failed to invoke #setArgV0()")
            } catch (e: InvocationTargetException) {
                Timber.tag(TAG).w(e, "Failed to invoke #setArgV0() ag")
            }
        } else {
            Timber.tag(TAG).w("#setArgV0() not available")
        }
    }

    /**
     * Just for unit test.
     */
    @RestrictTo(RestrictTo.Scope.TESTS)
    internal fun checkReflectSetArgV0(): Boolean {
        reflectSetArgV0()
        return sMtd_setArgV0 != null
    }

    private fun reflectReadProcLines() {
        if (sMtd_readProcLines != null) {
            return
        }

        try {
            // Android 1.6: public static final native void readProcLines(String path,
            //                  String[] reqFields, long[] outSizes);
            sMtd_readProcLines = android.os.Process::class.java.getMethod(
                "readProcLines",
                String::class.java, Array<String>::class.java, LongArray::class.java
            )
        } catch (e: NoSuchMethodException) {
            Timber.tag(TAG).w(e, "method not found")
        }
    }

    fun readProcLines(
        path: String,
        reqFields: Array<String>,
        outSizes: LongArray
    ) {
        reflectReadProcLines()
        if (sMtd_readProcLines != null) {
            try {
                sMtd_readProcLines!!.invoke(null, path, reqFields, outSizes)
            } catch (e: IllegalAccessException) {
                Timber.tag(TAG).w(e, "Failed to invoke #readProcLines()")
            } catch (e: InvocationTargetException) {
                Timber.tag(TAG).w(e, "Failed to invoke #readProcLines() ag")
            }
        } else {
            Timber.tag(TAG).w("#readProcLines() not available")
        }
    }

    fun getProcessName(pid: Int): String? {
        val cmdlineFile = "/proc/$pid/cmdline"
        try {
            return IoUtils.readAllLines(cmdlineFile).trim { it <= ' ' }
        } catch (e: IOException) {
            Timber.tag(TAG).w(e, "cannot read cmdline file")
        }
        return null
    }

    /**
     * Return the pid of the specified process name. If there are multiple processes
     * which have same process name, then just return the first one.
     * @param procName The process name
     * @return -1 if the specified process not found
     */
    fun getProcessPid(procName: String): Int {
        val procList = File("/proc").listFiles()
        if (procList != null && procList.isNotEmpty()) {
            for (procFile in procList) {
                if (!procFile.isDirectory) {
                    continue
                }
                if (!TextUtils.isDigitsOnly(procFile.name)) {
                    continue
                }
                val pid = StringUtils.parseInt(procFile.name, -1)
                if (pid > -1) {
                    val curProcName = getProcessName(pid)
                    if (procName == curProcName) {
                        return pid
                    }
                }
            }
        }
        return -1
    }

    /**
     * Just for unit test.
     */
    @RestrictTo(RestrictTo.Scope.TESTS)
    internal fun checkReflectReadProcLines(): Boolean {
        reflectReadProcLines()
        return sMtd_readProcLines != null
    }

    private fun reflectGetParentPid() {
        if (sMtd_getParentPid != null) {
            return
        }

        try {
            // Android 4.0: public static final int getParentPid(int pid)
            sMtd_getParentPid = android.os.Process::class.java.getMethod(
                "getParentPid",
                Int::class.javaPrimitiveType!!
            )
        } catch (e: NoSuchMethodException) {
            Timber.tag(TAG).w(e, "method not found")
        }
    }

    fun getParentPid(pid: Int): Int {
        reflectGetParentPid()
        if (sMtd_getParentPid != null) {
            try {
                return sMtd_getParentPid!!.invoke(null, pid) as Int
            } catch (e: IllegalAccessException) {
                Timber.tag(TAG).w(e, "Failed to invoke #getParentPid()")
            } catch (e: InvocationTargetException) {
                Timber.tag(TAG).w(e, "Failed to invoke #getParentPid() ag")
            }
        } else {
            val procStatusLabels = arrayOf("PPid:")
            val procStatusValues = LongArray(1)
            procStatusValues[0] = -1
            readProcLines("/proc/$pid/status", procStatusLabels, procStatusValues)
            return procStatusValues[0].toInt()
        }
        return -1
    }

    /**
     * Just for unit test.
     */
    @RestrictTo(RestrictTo.Scope.TESTS)
    internal fun checkReflectGetParentPid(): Boolean {
        reflectGetParentPid()
        return sMtd_getParentPid != null
    }

    private fun reflectMyPpid() {
        if (sMtd_myPpid != null) {
            return
        }

        try {
            // Android 4.4: public static final int myPpid()
            sMtd_myPpid = android.os.Process::class.java.getMethod("myPpid")
        } catch (e: NoSuchMethodException) {
            Timber.tag(TAG).w(e, "method not found")
        }
    }

    fun myPpid(): Int {
        reflectMyPpid()
        if (sMtd_myPpid != null) {
            try {
                return sMtd_myPpid!!.invoke(null) as Int
            } catch (e: IllegalAccessException) {
                Timber.tag(TAG).w(e, "Failed to invoke #myPpid()")
            } catch (e: InvocationTargetException) {
                Timber.tag(TAG).w(e, "Failed to invoke #myPpid() ag")
            }
        } else {
            return getParentPid(android.os.Process.myPid())
        }
        return -1
    }

    /**
     * Just for unit test.
     */
    @RestrictTo(RestrictTo.Scope.TESTS)
    internal fun checkReflectMyPpid(): Boolean {
        reflectMyPpid()
        return sMtd_myPpid != null
    }
}
