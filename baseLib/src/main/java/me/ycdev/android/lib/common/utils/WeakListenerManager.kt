package me.ycdev.android.lib.common.utils

import java.lang.ref.WeakReference
import java.util.ArrayList

@Suppress("unused")
open class WeakListenerManager<IListener : Any> {
    private val listeners = ArrayList<ListenerInfo<IListener>>()

    interface NotifyAction<IListener> {
        fun notify(listener: IListener)
    }

    private class ListenerInfo<IListener : Any> internal constructor(listener: IListener) {
        internal var className: String = listener::class.java.name
        internal var holder: WeakReference<IListener> = WeakReference(listener)
    }

    /**
     * Only invoked when invoke [.addListener]
     */
    protected open fun onFirstListenerAdd() {
        // nothing to do
    }

    /**
     * Only invoked when invoke [.removeListener]
     */
    protected open fun onLastListenerRemoved() {
        // nothing to do
    }

    /**
     * Override this method to notify the listener when registered.
     */
    protected open fun onListenerAdded(listener: IListener) {
        // nothing to do
    }

    fun addListener(listener: IListener) {
        synchronized(listeners) {
            if (listeners.size == 0) {
                onFirstListenerAdd()
            }

            for (l in listeners) {
                if (l.holder.get() === listener) return // skip duplicate listeners
            }
            listeners.add(ListenerInfo(listener))
        }

        // Notify the listener to get initialized
        onListenerAdded(listener)
    }

    fun removeListener(listener: IListener) {
        synchronized(listeners) {
            var removed = false
            for (i in 0 until listeners.size) {
                val listenerInfo = listeners[i]
                if (listenerInfo.holder.get() === listener) {
                    listeners.removeAt(i)
                    removed = true
                    break
                }
            }
            if (listeners.size == 0 && removed) {
                onLastListenerRemoved()
            }
        }
    }

    fun notifyListeners(action: NotifyAction<IListener>) {
        notifyListeners { action.notify(it) }
    }

    fun notifyListeners(action: (IListener) -> Unit) {
        synchronized(listeners) {
            var i = 0
            while (i < listeners.size) {
                val listenerInfo = listeners[i]
                val l = listenerInfo.holder.get()
                if (l == null) {
                    LibLogger.e(TAG, "listener leak found: " + listenerInfo.className)
                    listeners.removeAt(i)
                } else {
                    LibLogger.d(TAG, "notify: " + listenerInfo.className)
                    action(l)
                    i++
                }
            }
            LibLogger.d(TAG, "notify done, cur size: " + listeners.size)
        }
    }

    companion object {
        private const val TAG = "WeakListenerManager"
    }
}
