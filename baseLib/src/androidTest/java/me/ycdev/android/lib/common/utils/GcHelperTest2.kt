package me.ycdev.android.lib.common.utils

import com.google.common.truth.Truth.assertThat
import me.ycdev.android.lib.common.type.BooleanHolder
import org.junit.Test
import timber.log.Timber
import java.lang.ref.ReferenceQueue
import java.lang.ref.SoftReference
import java.lang.ref.WeakReference

class GcHelperTest2 {

    @Test
    fun forceGc_default() {
        GcHelper.forceGc()
        // GC happened
    }

    @Test
    fun forceGc_holder() {
        val gcState = BooleanHolder(false)
        createGcWatcherObject(gcState)
        GcHelper.forceGc(gcState)
    }

    private fun createGcWatcherObject(gcState: BooleanHolder) {
        object : Any() {
            @Throws(Throwable::class)
            protected fun finalize() {
                Timber.tag(TAG).d("forceGc_holder, GC Partner object was collected")
                gcState.value = true
            }
        }
    }

    @Test
    fun checkWeakReference_demo1() {
        val objHolder = createWeakReferenceObject()
        GcHelper.forceGc()
        assertThat(objHolder.get()).isNull()
    }

    private fun createWeakReferenceObject(): WeakReference<Dummy> {
        val obj = Dummy()
        return WeakReference(obj)
    }

    @Test
    fun checkWeakReference_demo2() {
        val refQueue = ReferenceQueue<Dummy>()
        val objHolder = createWeakReferenceObject(refQueue)
        GcHelper.forceGc()
        assertThat(objHolder.get()).isNull()
        assertThat(refQueue.poll()).isSameInstanceAs(objHolder)
    }

    private fun createWeakReferenceObject(refQueue: ReferenceQueue<Dummy>): WeakReference<Dummy> {
        val obj = Dummy()
        return WeakReference(obj, refQueue)
    }

    @Test
    fun checkSoftReference() {
        val objHolder = createSoftReferenceObject()
        GcHelper.forceGc()
        assertThat(objHolder.get()).isNotNull()
    }

    private fun createSoftReferenceObject(): SoftReference<Dummy> {
        val obj = Dummy()
        return SoftReference(obj)
    }

    private class Dummy

    companion object {
        private const val TAG = "GcHelperTest2"
    }
}
