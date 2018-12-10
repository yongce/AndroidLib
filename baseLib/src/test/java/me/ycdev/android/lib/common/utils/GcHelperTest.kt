package me.ycdev.android.lib.common.utils

import org.junit.Test

import me.ycdev.android.lib.common.type.BooleanHolder
import me.ycdev.android.lib.test.base.NormalJUnitBase
import timber.log.Timber

class GcHelperTest : NormalJUnitBase() {

    @Test
    fun forceGc_default() {
        GcHelper.forceGc()
        // GC happened
    }

    @Test
    fun forceGc_holder() {
        val gcState = BooleanHolder(false)
        run {
            object : Any() {
                @Throws(Throwable::class)
                protected fun finalize() {
                    Timber.tag(TAG).d("forceGc_holder, GC Partner object was collected")
                    gcState.value = true
                }
            }
        }
        GcHelper.forceGc(gcState)
    }

    companion object {
        private const val TAG = "GcHelperTest"
    }
}
