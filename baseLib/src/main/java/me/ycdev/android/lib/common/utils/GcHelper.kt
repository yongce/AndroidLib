package me.ycdev.android.lib.common.utils

import me.ycdev.android.lib.common.type.BooleanHolder
import timber.log.Timber

@Suppress("unused")
object GcHelper {
    private const val TAG = "GcHelper"

    fun forceGc(gcState: BooleanHolder) {
        // Now, 'objPartner' can be collected by GC!
        val timeStart = System.currentTimeMillis()

        // create a lot of objects to force GC
        val memAllocSize = 1024 * 1024 // 1MB
        var memAllocCount: Long = 0
        while (true) {
            Runtime.getRuntime().gc()
            ThreadUtils.sleep(100) // wait for GC
            if (gcState.value) {
                break // GC happened
            }
            Timber.tag(TAG).d("Allocating mem...")
            ByteArray(memAllocSize)
            memAllocCount++
        }

        val timeUsed = System.currentTimeMillis() - timeStart
        Timber.tag(TAG).d("Force GC, time used: %d, memAlloc: %dMB", timeUsed, memAllocCount)
    }

    fun forceGc() {
        val gcState = BooleanHolder(false)
        createGcWatcherObject(gcState)
        forceGc(gcState)
    }

    private fun createGcWatcherObject(gcState: BooleanHolder) {
        object : Any() {
            @Throws(Throwable::class)
            protected fun finalize() {
                Timber.tag(TAG).d("GC Partner object was collected")
                gcState.value = true
            }
        }
    }
}
