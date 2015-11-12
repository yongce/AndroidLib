package me.ycdev.android.lib.common.apps;

public abstract class AppsLoadListener {
    /**
     * This method can be used to cancel the apps loading.
     * @return false will be returned by default.
     */
    public boolean isCancelled() {
        return false;
    }

    /**
     * You can override this method to listen the loading progress and loaded app info.
     * Nothing to do in the default implementation.
     * @param percent Value range [1, 2, ..., 100]
     * @param appInfo May be null
     */
    public void onProgressUpdated(int percent, AppInfo appInfo) {
        // nothing to do
    }
}
