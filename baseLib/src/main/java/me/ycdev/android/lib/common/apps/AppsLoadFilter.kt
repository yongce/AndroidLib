package me.ycdev.android.lib.common.apps

class AppsLoadFilter {
    /**
     * Get mounted apps only (true by default).
     */
    var onlyMounted = true

    /**
     * Get enabled apps only (true by default).
     */
    var onlyEnabled = true

    /**
     * Include all system apps (true by default).
     * Note: if this config is true, [.includeUpdatedSysApp] will be ignored;
     * otherwise, [.includeUpdatedSysApp] will be checked.
     */
    var includeSysApp = true

    /**
     * Include updated system apps (true by default).
     * Note: this config will be ignored if [.includeSysApp] is true.
     */
    var includeUpdatedSysApp = true

    /**
     * Include myself (true by default).
     */
    var includeMyself = true
}
