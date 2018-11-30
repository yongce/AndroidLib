package me.ycdev.android.arch;

import androidx.annotation.IntDef;
import android.widget.Toast;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static me.ycdev.android.arch.ArchConstants.IntentType.INTENT_TYPE_ACTIVITY;
import static me.ycdev.android.arch.ArchConstants.IntentType.INTENT_TYPE_BROADCAST;
import static me.ycdev.android.arch.ArchConstants.IntentType.INTENT_TYPE_SERVICE;

public class ArchConstants {
    @IntDef({INTENT_TYPE_ACTIVITY, INTENT_TYPE_BROADCAST, INTENT_TYPE_SERVICE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface IntentType {
        int INTENT_TYPE_ACTIVITY = 1;
        int INTENT_TYPE_BROADCAST = 2;
        int INTENT_TYPE_SERVICE = 3;
    }

    /*
     * Durations for toast
     */
    @IntDef({Toast.LENGTH_SHORT, Toast.LENGTH_LONG})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ToastDuration {}
}
