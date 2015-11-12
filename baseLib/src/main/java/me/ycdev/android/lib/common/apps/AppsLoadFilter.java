package me.ycdev.android.lib.common.apps;

public class AppsLoadFilter {
    /**
     * Get mounted apps only (true by default).
     */
    public boolean onlyMounted = true;

    /**
     * Get enabled apps only (true by default).
     */
    public boolean onlyEnabled = true;

    /**
     * Include all system apps (true by default).
     * Note: if this config is true, {@link #includeUpdatedSysApp} will be ignored;
     * otherwise, {@link #includeUpdatedSysApp} will be checked.
     */
    public boolean includeSysApp = true;

    /**
     * Include updated system apps (true by default).
     * Note: this config will be ignored if {@link #includeSysApp} is true.
     */
    public boolean includeUpdatedSysApp = true;

    /**
     * Include myself (true by default).
     */
    public boolean includeMyself = true;
}
