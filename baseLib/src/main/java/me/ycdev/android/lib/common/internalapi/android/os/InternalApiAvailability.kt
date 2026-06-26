package me.ycdev.android.lib.common.internalapi.android.os

import android.os.Build
import java.lang.reflect.Method

internal data class InternalApiAvailability(
    val apiName: String,
    val available: Boolean,
    val reason: String,
    val blockedByPlatform: Boolean = false
) {
    companion object {
        fun available(apiName: String): InternalApiAvailability = InternalApiAvailability(
            apiName = apiName,
            available = true,
            reason = "$apiName is available"
        )

        fun unavailable(
            apiName: String,
            reason: String,
            blockedByPlatform: Boolean = false
        ): InternalApiAvailability = InternalApiAvailability(
            apiName = apiName,
            available = false,
            reason = reason,
            blockedByPlatform = blockedByPlatform
        )
    }
}

internal object InternalApiAccess {
    fun hiddenApiBlockedOnOrAfter(
        apiName: String,
        sdkInt: Int
    ): InternalApiAvailability? {
        if (Build.VERSION.SDK_INT < sdkInt) {
            return null
        }
        return InternalApiAvailability.unavailable(
            apiName = apiName,
            reason = "$apiName hidden API reflection is blocked on Android ${Build.VERSION.SDK_INT}",
            blockedByPlatform = true
        )
    }

    fun reflectedMethodAvailability(
        apiName: String,
        method: Method?
    ): InternalApiAvailability = if (method != null) {
        InternalApiAvailability.available(apiName)
    } else {
        InternalApiAvailability.unavailable(
            apiName = apiName,
            reason = "$apiName method is not available"
        )
    }
}
