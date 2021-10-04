package me.ycdev.android.lib.common.manager

@Suppress("unused")
open class ListenerManager<IListener : Any>(override val weakReference: Boolean) :
    ObjectManager<IListener>(weakReference) {

    /**
     * Only invoked when invoke [addListener]
     */
    protected open fun onFirstListenerAdd() {
        // nothing to do
    }

    /**
     * Only invoked when invoke [removeListener]
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

    final override fun onFirstObjectAdd() {
        onFirstListenerAdd()
    }

    final override fun onLastObjectRemoved() {
        onLastListenerRemoved()
    }

    final override fun onObjectAdded(obj: IListener) {
        onListenerAdded(obj)
    }

    /**
     * Get the listeners count right now.
     * But the returned value may be NOT accurate if [weakReference] is true.
     * Some of the listeners may be already collected by GC.
     */
    val listenersCount: Int by ::objectsCount

    fun addListener(listener: IListener) = super.addObject(listener)

    fun addListener(listener: IListener, tag: String) = super.addObject(listener, tag)

    fun removeListener(listener: IListener) = super.removeObject(listener)

    fun notifyListeners(action: NotifyAction<IListener>) = super.notifyObjects(action)

    fun notifyListeners(action: (IListener) -> Unit) = super.notifyObjects(action)

    companion object {
        private const val TAG = "ListenerManager"
    }
}
