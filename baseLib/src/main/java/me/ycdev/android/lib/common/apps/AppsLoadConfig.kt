package me.ycdev.android.lib.common.apps

data class AppsLoadConfig(
    /**
     * Load the app name (true by default).
     */
    var loadLabel: Boolean = true,
    /**
     * Load the app icon (true by default).
     */
    var loadIcon: Boolean = true
)
