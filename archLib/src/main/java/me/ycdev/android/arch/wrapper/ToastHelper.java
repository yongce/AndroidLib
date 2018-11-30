package me.ycdev.android.arch.wrapper;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import android.widget.Toast;

import static me.ycdev.android.arch.ArchConstants.ToastDuration;

/**
 * A wrapper class for Toast so that we can customize and unify the UI in future.
 */
@SuppressWarnings("unused")
public class ToastHelper {
    private ToastHelper() {
        // nothing to do
    }

    public static void show(@NonNull Context cxt, @StringRes int msgResId,
            @ToastDuration int duration) {
        Toast.makeText(cxt, msgResId, duration).show();
    }

    public static void show(@NonNull Context cxt, @NonNull CharSequence msg,
            @ToastDuration int duration) {
        Toast.makeText(cxt, msg, duration).show();
    }

}
