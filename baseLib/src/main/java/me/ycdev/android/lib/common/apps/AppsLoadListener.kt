package me.ycdev.android.lib.common.apps

interface AppsLoadListener {
    /**
     * This method can be used to cancel the apps loading.
     * @return false will be returned by default.
     */
    fun isCancelled(): Boolean = false

    /**
     * You can override this method to listen the loading progress and loaded app info.
     * Nothing to do in the default implementation.
     * @param percent Value range [1, 2, ..., 100]
     * @param appInfo May be null
     */
    fun onProgressUpdated(percent: Int, appInfo: AppInfo)
}
