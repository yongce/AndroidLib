package me.ycdev.android.lib.common.manager

import timber.log.Timber
import java.lang.ref.WeakReference
import java.util.ArrayList

open class ObjectManager<IObject : Any>(open val weakReference: Boolean) {
    private class ObjectInfo<IObject>(obj: IObject, val tag: String, weakReference: Boolean) {
        private var obj: IObject? = null
        private var holder: WeakReference<IObject>? = null

        init {
            if (weakReference) {
                holder = WeakReference(obj)
            } else {
                this.obj = obj
            }
        }

        fun getObject(): IObject? {
            return obj ?: holder!!.get()
        }
    }

    private val allObjects: MutableList<ObjectInfo<IObject>> = ArrayList()

    /**
     * Get the objects count right now.
     * But the returned value may be NOT accurate if [weakReference] is true.
     * Some of the objects may be already collected by GC.
     */
    val objectsCount: Int get() = allObjects.size

    /**
     * Only invoked when invoke [addObject]
     */
    protected open fun onFirstObjectAdd() {
        // nothing to do
    }

    /**
     * Only invoked when invoke [removeObject]
     */
    protected open fun onLastObjectRemoved() {
        // nothing to do
    }

    /**
     * Override this method to notify the listener when registered.
     */
    protected open fun onObjectAdded(obj: IObject) {
        // nothing to do
    }

    fun addObject(obj: IObject) {
        addObject(obj, obj::class.java.name)
    }

    /**
     * @param tag Identity the object, for debug only
     */
    fun addObject(obj: IObject, tag: String) {
        synchronized(allObjects) {
            if (allObjects.size == 0) {
                onFirstObjectAdd()
            }
            for (objectInfo in allObjects) {
                if (obj == objectInfo.getObject()) return // skip duplicate object
            }
            allObjects.add(ObjectInfo(obj, tag, weakReference))
        }

        // Notify the listener to get initialized
        onObjectAdded(obj)
    }

    fun removeObject(obj: IObject) {
        synchronized(allObjects) {
            var removed = false
            for (i in 0 until allObjects.size) {
                val objectInfo = allObjects[i]
                if (obj == objectInfo.getObject()) {
                    allObjects.removeAt(i)
                    removed = true
                    break
                }
            }
            if (allObjects.size == 0 && removed) {
                onLastObjectRemoved()
            }
        }
    }

    fun notifyObjects(action: NotifyAction<IObject>) {
        notifyObjects { action.notify(it) }
    }

    fun notifyObjects(action: (IObject) -> Unit) {
        synchronized(allObjects) {
            // The object may remove itself!
            val objectsCopied: List<ObjectInfo<IObject>> = ArrayList(allObjects)
            for (i in objectsCopied.indices) {
                val objectInfo = objectsCopied[i]
                val obj: IObject? = objectInfo.getObject()
                if (obj == null) {
                    Timber.tag(TAG).e("object leak found: %s", objectInfo.tag)
                    allObjects.remove(objectInfo)
                } else {
                    if (DEV_LOG) {
                        Timber.tag(TAG).d("notify #%d: %s", i, objectInfo.tag)
                    }
                    action(obj)
                }
            }
            if (DEV_LOG) {
                Timber.tag(TAG).d("notify done, cur size: %d", allObjects.size)
            }
        }
    }

    companion object {
        private const val TAG = "ObjectManager"
        private const val DEV_LOG = false
    }
}
