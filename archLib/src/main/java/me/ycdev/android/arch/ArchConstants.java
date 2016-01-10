package me.ycdev.android.arch;

import android.support.annotation.IntDef;
import android.widget.Toast;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ArchConstants {
    /*
     * Intent types
     */
    public static final int INTENT_TYPE_ACTIVITY = 1;
    public static final int INTENT_TYPE_BROADCAST = 2;
    public static final int INTENT_TYPE_SERVICE = 3;

    @IntDef({INTENT_TYPE_ACTIVITY, INTENT_TYPE_BROADCAST, INTENT_TYPE_SERVICE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface IntentType {}

    /*
     * Durations for toast
     */
    @IntDef({Toast.LENGTH_SHORT, Toast.LENGTH_LONG})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ToastDuration {}
}
