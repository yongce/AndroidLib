package me.ycdev.android.lib.common.utils

import androidx.annotation.VisibleForTesting
import timber.log.Timber
import java.lang.ref.WeakReference
import java.util.ArrayList

@Suppress("unused")
open class WeakListenerManager<IListener : Any> {
    private val listeners = ArrayList<ListenerInfo<IListener>>()

    interface NotifyAction<IListener> {
        fun notify(listener: IListener)
    }

    private class ListenerInfo<IListener : Any>(listener: IListener) {
        var className: String = listener::class.java.name
        var holder: WeakReference<IListener> = WeakReference(listener)
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
            // The listener may unregister itself!
            val listenersCopied: List<ListenerInfo<IListener>> = ArrayList(listeners)
            for ((i, listenerInfo) in listenersCopied.withIndex()) {
                val l = listenerInfo.holder.get()
                if (l == null) {
                    Timber.tag(TAG).w("listener leak found: %s", listenerInfo.className)
                    listeners.remove(listenerInfo)
                } else {
                    Timber.tag(TAG).d("notify #%d: %s", i, listenerInfo.className)
                    action(l)
                }
            }
            Timber.tag(TAG).d("notify done, cur size: %d in %s", listeners.size, this)
        }
    }

    @VisibleForTesting
    internal fun listenersCount(): Int {
        return listeners.size
    }

    companion object {
        private const val TAG = "WeakListenerManager"
    }
}
