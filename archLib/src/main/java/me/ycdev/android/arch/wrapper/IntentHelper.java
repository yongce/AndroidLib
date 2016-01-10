package me.ycdev.android.arch.wrapper;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;

import me.ycdev.android.arch.utils.AppConfigs;
import me.ycdev.android.arch.utils.AppLogger;

/**
 * A wrapper class to avoid security issues when parsing Intent extras.
 * <p>See details of the issue: http://code.google.com/p/android/issues/detail?id=177223.</p>
 */
@SuppressWarnings("unused")
public class IntentHelper {
    private static final String TAG = "IntentUtils";
    private static final boolean DEBUG = AppConfigs.DEBUG_LOG;

    private IntentHelper() {
        // nothing to do
    }

    private static void onIntentAttacked(@NonNull Intent intent, Throwable e) {
        // prevent OOM for Android 5.0~?
        intent.replaceExtras((Bundle) null);
        if (DEBUG) AppLogger.w(TAG, "attacked?", e);
    }

    public static boolean hasExtra(@Nullable Intent intent, @NonNull String key) {
        if (intent == null) {
            return false;
        }

        try {
            return intent.hasExtra(key);
        } catch (Exception e) {
            onIntentAttacked(intent, e);
        }
        return false;
    }

    public static boolean getBooleanExtra(@Nullable Intent intent, @NonNull String key,
            boolean defValue) {
        if (intent == null) {
            return defValue;
        }

        try {
            return intent.getBooleanExtra(key, defValue);
        } catch (Exception e) {
            onIntentAttacked(intent, e);
        }
        return defValue;
    }

    public static byte getByteExtra(@Nullable Intent intent, @NonNull String key,
            byte defValue) {
        if (intent == null) {
            return defValue;
        }

        try {
            return intent.getByteExtra(key, defValue);
        } catch (Exception e) {
            onIntentAttacked(intent, e);
        }
        return defValue;
    }

    public static short getShortExtra(@Nullable Intent intent, @NonNull String key,
            short defValue) {
        if (intent == null) {
            return defValue;
        }

        try {
            return intent.getShortExtra(key, defValue);
        } catch (Exception e) {
            onIntentAttacked(intent, e);
        }
        return defValue;
    }

    public static int getIntExtra(@Nullable Intent intent, @NonNull String key,
            int defValue) {
        if (intent == null) {
            return defValue;
        }

        try {
            return intent.getIntExtra(key, defValue);
        } catch (Exception e) {
            onIntentAttacked(intent, e);
        }
        return defValue;
    }

    public static long getLongExtra(@Nullable Intent intent, @NonNull String key,
            long defValue) {
        if (intent == null) {
            return defValue;
        }

        try {
            return intent.getLongExtra(key, defValue);
        } catch (Exception e) {
            onIntentAttacked(intent, e);
        }
        return defValue;
    }

    public static float getFloatExtra(@Nullable Intent intent, @NonNull String key,
            float defValue) {
        if (intent == null) {
            return defValue;
        }

        try {
            return intent.getFloatExtra(key, defValue);
        } catch (Exception e) {
            onIntentAttacked(intent, e);
        }
        return defValue;
    }

    public static double getDoubleExtra(@Nullable Intent intent, @NonNull String key,
            double defValue) {
        if (intent == null) {
            return defValue;
        }

        try {
            return intent.getDoubleExtra(key, defValue);
        } catch (Exception e) {
            onIntentAttacked(intent, e);
        }
        return defValue;
    }

    public static char getCharExtra(@Nullable Intent intent, @NonNull String key,
            char defValue) {
        if (intent == null) {
            return defValue;
        }

        try {
            return intent.getCharExtra(key, defValue);
        } catch (Exception e) {
            onIntentAttacked(intent, e);
        }
        return defValue;
    }

    @Nullable
    public static String getStringExtra(@Nullable Intent intent, @NonNull String key) {
        if (intent == null) {
            return null;
        }

        try {
            return intent.getStringExtra(key);
        } catch (Exception e) {
            onIntentAttacked(intent, e);
        }
        return null;
    }

    @Nullable
    public static CharSequence getCharSequenceExtra(@Nullable Intent intent, @NonNull String key) {
        if (intent == null) {
            return null;
        }

        try {
            return intent.getCharSequenceExtra(key);
        } catch (Exception e) {
            onIntentAttacked(intent, e);
        }
        return null;
    }

    @Nullable
    public static Serializable getSerializableExtra(@Nullable Intent intent,
            @NonNull String key) {
        if (intent == null) {
            return null;
        }

        try {
            return intent.getSerializableExtra(key);
        } catch (Exception e) {
            onIntentAttacked(intent, e);
        }
        return null;
    }

    @Nullable
    public static <T extends Parcelable> T getParcelableExtra(@Nullable Intent intent,
            @NonNull String key) {
        if (intent == null) {
            return null;
        }

        try {
            return intent.getParcelableExtra(key);
        } catch (Exception e) {
            onIntentAttacked(intent, e);
        }
        return null;
    }

    @Nullable
    public static boolean[] getBooleanArrayExtra(@Nullable Intent intent, @NonNull String key) {
        if (intent == null) {
            return null;
        }

        try {
            return intent.getBooleanArrayExtra(key);
        } catch (Exception e) {
            onIntentAttacked(intent, e);
        }
        return null;
    }

    @Nullable
    public static int[] getIntArrayExtra(@Nullable Intent intent, @NonNull String key) {
        if (intent == null) {
            return null;
        }

        try {
            return intent.getIntArrayExtra(key);
        } catch (Exception e) {
            onIntentAttacked(intent, e);
        }
        return null;
    }

    @Nullable
    public static long[] getLongArrayExtra(@Nullable Intent intent, @NonNull String key) {
        if (intent == null) {
            return null;
        }

        try {
            return intent.getLongArrayExtra(key);
        } catch (Exception e) {
            onIntentAttacked(intent, e);
        }
        return null;
    }

    @Nullable
    public static String[] getStringArrayExtra(@Nullable Intent intent, @NonNull String key) {
        if (intent == null) {
            return null;
        }

        try {
            return intent.getStringArrayExtra(key);
        } catch (Exception e) {
            onIntentAttacked(intent, e);
        }
        return null;
    }

    @Nullable
    public static Parcelable[] getParcelableArrayExtra(@Nullable Intent intent,
            @NonNull String key) {
        if (intent == null) {
            return null;
        }

        try {
            return intent.getParcelableArrayExtra(key);
        } catch (Exception e) {
            onIntentAttacked(intent, e);
        }
        return null;
    }

    @Nullable
    public static ArrayList<String> getStringArrayListExtra(@Nullable Intent intent,
            @NonNull String key) {
        if (intent == null) {
            return null;
        }

        try {
            return intent.getStringArrayListExtra(key);
        } catch (Exception e) {
            onIntentAttacked(intent, e);
        }
        return null;
    }

    @Nullable
    public static <T extends Parcelable> ArrayList<T> getParcelableArrayListExtra(
            @Nullable Intent intent, @NonNull String key) {
        if (intent == null) {
            return null;
        }

        try {
            return intent.getParcelableArrayListExtra(key);
        } catch (Exception e) {
            onIntentAttacked(intent, e);
        }
        return null;
    }

    @Nullable
    public static Bundle getBundleExtra(@Nullable Intent intent, @NonNull String key) {
        if (intent == null) {
            return null;
        }

        try {
            return intent.getBundleExtra(key);
        } catch (Exception e) {
            onIntentAttacked(intent, e);
        }
        return null;
    }

}
