package me.ycdev.android.lib.common.internalapi.android.os

import android.annotation.SuppressLint
import android.os.UserHandle
import androidx.annotation.RestrictTo
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import timber.log.Timber

@SuppressLint("PrivateApi")
object UserHandleIA {
    private const val TAG = "UserHandleIA"

    private var sMtd_myUserId: Method? = null

    init {
        try {
            sMtd_myUserId = UserHandle::class.java.getMethod("myUserId")
        } catch (e: NoSuchMethodException) {
            Timber.tag(TAG).w(e, "method not found")
        }
    }

    fun myUserId(): Int {
        if (sMtd_myUserId != null) {
            try {
                return sMtd_myUserId!!.invoke(null) as Int
            } catch (e: IllegalAccessException) {
                Timber.tag(TAG).w(e, "Failed to invoke #myUserId()")
            } catch (e: InvocationTargetException) {
                Timber.tag(TAG).w(e, "Failed to invoke #myUserId() ag")
            }
        }
        return 0
    }

    /**
     * Just for unit test.
     */
    @RestrictTo(RestrictTo.Scope.TESTS)
    internal fun checkReflectMyUserId(): Boolean {
        return sMtd_myUserId != null
    }
}
