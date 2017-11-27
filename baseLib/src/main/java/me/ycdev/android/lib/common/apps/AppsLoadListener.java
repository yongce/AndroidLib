package me.ycdev.android.lib.common.apps;

@SuppressWarnings("WeakerAccess")
public interface AppsLoadListener {
    /**
     * This method can be used to cancel the apps loading.
     * @return false will be returned by default.
     */
    default boolean isCancelled() {
        return false;
    }

    /**
     * You can override this method to listen the loading progress and loaded app info.
     * Nothing to do in the default implementation.
     * @param percent Value range [1, 2, ..., 100]
     * @param appInfo May be null
     */
    void onProgressUpdated(int percent, AppInfo appInfo);
}
