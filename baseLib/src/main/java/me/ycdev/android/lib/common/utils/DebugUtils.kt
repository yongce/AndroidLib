package me.ycdev.android.lib.common.utils

import android.os.StrictMode

object DebugUtils {
    /**
     * Should only be invoked in debug version. Never invoke this method in release version!
     */
    fun enableStrictMode() {
        // thread policy
        val threadPolicyBuilder = StrictMode.ThreadPolicy.Builder()
            .detectAll()
            .penaltyLog()
        threadPolicyBuilder.penaltyFlashScreen()
        threadPolicyBuilder.penaltyDeathOnNetwork()
        StrictMode.setThreadPolicy(threadPolicyBuilder.build())

        // VM policy
        val vmPolicyBuilder = StrictMode.VmPolicy.Builder()
            .detectAll()
            .penaltyLog()
            .penaltyDeath()
        StrictMode.setVmPolicy(vmPolicyBuilder.build())
    }
}
