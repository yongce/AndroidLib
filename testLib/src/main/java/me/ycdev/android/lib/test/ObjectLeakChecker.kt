package me.ycdev.android.lib.test

import android.os.SystemClock
import java.lang.ref.WeakReference
import java.util.concurrent.CountDownLatch

class ObjectLeakChecker<T>(private val targetOperator: ObjectOperator<T>) {

    private val latch = CountDownLatch(2)
    private var targetObjHolder: WeakReference<T>? = null

    private var isPrepared = false
    private var isGcDone = false

    val leakedObjectCount: Long
        get() {
            if (!isGcDone) {
                throw RuntimeException("please call #waitGcDone() first")
            }
            return latch.count
        }

    interface ObjectOperator<T> {
        fun createObject(): T
        fun operate(obj: T)
    }

    fun prepareForGc() {
        // partner object
        object : Any() {
            @Throws(Throwable::class)
            protected fun finalize() {
                latch.countDown()
            }
        }

        val targetObj = targetOperator.createObject()
        targetObjHolder = WeakReference(targetObj)
        targetOperator.operate(targetObj)
        isPrepared = true
    }

    fun waitGcDone() {
        if (!isPrepared) {
            throw RuntimeException("please call #prepareForGc() first")
        }

        // create a lot of objects to force GC
        while (true) {
            ByteArray(1024 * 1024) // 1MB
            SystemClock.sleep(50) // wait for GC
            if (latch.count < 2) {
                break // GC happened
            }
        }
        if (targetObjHolder!!.get() == null) {
            latch.countDown()
        }
        isGcDone = true
    }
}
