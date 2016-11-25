package me.ycdev.android.arch.utils;

import me.ycdev.android.lib.common.utils.LibLogger;

/**
 * A wrapper class as logger.
 * <p>TODO To write custom lint rules to enforce only AppLogger used instead of android.util.Log.</p>
 */
public class AppLogger extends LibLogger {
    private AppLogger() {
        // nothing to do
    }
}
