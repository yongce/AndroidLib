package me.ycdev.android.lib.common.manager

interface NotifyAction<IListener> {
    fun notify(listener: IListener)
}
