package me.ycdev.android.arch.lint.utils;

import com.android.tools.lint.checks.infrastructure.TestFile;
import com.android.tools.lint.checks.infrastructure.TestFiles;

public class TestFileStubs {
    public static TestFile getNonNull() {
        return TestFiles.java("" +
                "package android.support.annotation;\n" +
                "\n" +
                "import static java.lang.annotation.ElementType.ANNOTATION_TYPE;\n" +
                "import static java.lang.annotation.ElementType.FIELD;\n" +
                "import static java.lang.annotation.ElementType.LOCAL_VARIABLE;\n" +
                "import static java.lang.annotation.ElementType.METHOD;\n" +
                "import static java.lang.annotation.ElementType.PACKAGE;\n" +
                "import static java.lang.annotation.ElementType.PARAMETER;\n" +
                "import static java.lang.annotation.RetentionPolicy.CLASS;\n" +
                "\n" +
                "import java.lang.annotation.Documented;\n" +
                "import java.lang.annotation.Retention;\n" +
                "import java.lang.annotation.Target;\n" +
                "\n" +
                "/**\n" +
                " * Denotes that a parameter, field or method return value can never be null.\n" +
                " * <p>\n" +
                " * This is a marker annotation and it has no specific attributes.\n" +
                " */\n" +
                "@Documented\n" +
                "@Retention(CLASS)\n" +
                "@Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})\n" +
                "public @interface NonNull {\n" +
                "}\n");
    }

    public static TestFile getNullable() {
        return TestFiles.java("" +
                "package android.support.annotation;\n" +
                "\n" +
                "import static java.lang.annotation.ElementType.ANNOTATION_TYPE;\n" +
                "import static java.lang.annotation.ElementType.FIELD;\n" +
                "import static java.lang.annotation.ElementType.LOCAL_VARIABLE;\n" +
                "import static java.lang.annotation.ElementType.METHOD;\n" +
                "import static java.lang.annotation.ElementType.PACKAGE;\n" +
                "import static java.lang.annotation.ElementType.PARAMETER;\n" +
                "import static java.lang.annotation.RetentionPolicy.CLASS;\n" +
                "\n" +
                "import java.lang.annotation.Documented;\n" +
                "import java.lang.annotation.Retention;\n" +
                "import java.lang.annotation.Target;\n" +
                "\n" +
                "/**\n" +
                " * Denotes that a parameter, field or method return value can never be null.\n" +
                " * <p>\n" +
                " * This is a marker annotation and it has no specific attributes.\n" +
                " */\n" +
                "@Documented\n" +
                "@Retention(CLASS)\n" +
                "@Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})\n" +
                "public @interface Nullable {\n" +
                "}\n\n");
    }

    public static TestFile getStringRes() {
        return TestFiles.java("" +
                "package android.support.annotation;\n" +
                "\n" +
                "import static java.lang.annotation.ElementType.FIELD;\n" +
                "import static java.lang.annotation.ElementType.LOCAL_VARIABLE;\n" +
                "import static java.lang.annotation.ElementType.METHOD;\n" +
                "import static java.lang.annotation.ElementType.PARAMETER;\n" +
                "import static java.lang.annotation.RetentionPolicy.CLASS;\n" +
                "\n" +
                "import java.lang.annotation.Documented;\n" +
                "import java.lang.annotation.Retention;\n" +
                "import java.lang.annotation.Target;\n" +
                "\n" +
                "/**\n" +
                " * Denotes that an integer parameter, field or method return value is expected\n" +
                " * to be a String resource reference (e.g. {@code android.R.string.ok}).\n" +
                " */\n" +
                "@Documented\n" +
                "@Retention(CLASS)\n" +
                "@Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE})\n" +
                "public @interface StringRes {\n" +
                "}\n");
    }

    public static TestFile getAppCompatActivity() {
        return TestFiles.java("" +
                "package android.support.v7.app;\n" +
                "\n" +
                "import android.annotation.SuppressLint;\n" +
                "import android.app.Activity;\n" +
                "\n" +
                "@SuppressLint(\"MyBaseActivity\")" +
                "public class AppCompatActivity extends Activity {" +
                "}\n");
    }

    public static TestFile getLibLogger() {
        return TestFiles.java("" +
                "package me.ycdev.android.lib.common.utils;\n" +
                "\n" +
                "import android.support.annotation.NonNull;\n" +
                "import android.support.annotation.Nullable;\n" +
                "import android.util.Log;\n" +
                "\n" +
                "import java.util.Locale;\n" +
                "\n" +
                "public class LibLogger {\n" +
                "    private static final String TAG = \"AndroidLib\";\n" +
                "    private static boolean sJvmLogger = true;\n" +
                "\n" +
                "    protected LibLogger() {\n" +
                "        // nothing to do\n" +
                "    }\n" +
                "\n" +
                "    public static void enableJvmLogger() {\n" +
                "        sJvmLogger = true;\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * Log enabled by default\n" +
                "     */\n" +
                "    public static void setLogEnabled(boolean enabled) {\n" +
                "    }\n" +
                "\n" +
                "    public static boolean isLogEnabled() {\n" +
                "        return true;\n" +
                "    }\n" +
                "\n" +
                "    public static void v(@NonNull String tag, @NonNull String msg, Object... args) {\n" +
                "        log(Log.VERBOSE, tag, msg, null, args);\n" +
                "    }\n" +
                "\n" +
                "    public static void d(@NonNull String tag, @NonNull String msg, Object... args) {\n" +
                "        log(Log.DEBUG, tag, msg, null, args);\n" +
                "    }\n" +
                "\n" +
                "    public static void i(@NonNull String tag, @NonNull String msg, Object... args) {\n" +
                "        log(Log.INFO, tag, msg, null, args);\n" +
                "    }\n" +
                "\n" +
                "    public static void w(@NonNull String tag, @NonNull String msg, Object... args) {\n" +
                "        log(Log.WARN, tag, msg, null, args);\n" +
                "    }\n" +
                "\n" +
                "    public static void w(@NonNull String tag, @NonNull String msg, @NonNull Throwable e,\n" +
                "            Object... args) {\n" +
                "        log(Log.WARN, tag, msg, e, args);\n" +
                "    }\n" +
                "\n" +
                "    public static void w(@NonNull String tag, @NonNull Throwable e, Object... args) {\n" +
                "        log(Log.WARN, tag, null, e, args);\n" +
                "    }\n" +
                "\n" +
                "    public static void e(@NonNull String tag, @NonNull String msg, Object... args) {\n" +
                "        log(Log.ERROR, tag, msg, null, args);\n" +
                "    }\n" +
                "\n" +
                "    public static void e(@NonNull String tag, @NonNull String msg, @NonNull Throwable e,\n" +
                "            Object... args) {\n" +
                "        log(Log.ERROR, tag, msg, e, args);\n" +
                "    }\n" +
                "\n" +
                "    public static void log(int level, @NonNull String tag, @Nullable String msg,\n" +
                "            @Nullable Throwable tr, Object... args) {\n" +
                "    }\n" +
                "\n" +
                "}\n");
    }

    public static TestFile getBaseActivity() {
        return TestFiles.java("" +
                "package me.ycdev.android.arch.activity;\n" +
                "\n" +
                "import android.app.Activity;\n" +
                "\n" +
                "/**\n" +
                " * Base class for Activity which wants to inherit {@link android.app.Activity}.\n" +
                " */\n" +
                "public abstract class BaseActivity extends Activity {\n" +
                "    // nothing to do right now\n" +
                "}\n");

    }

    public static TestFile getAppCompatBaseActivity() {
        return TestFiles.java("package me.ycdev.android.arch.activity;\n" +
                "\n" +
                "import android.app.Activity;\n" +
                "\n" +
                "public abstract class AppCompatBaseActivity extends Activity {\n" +
                "}\n");
    }

    public static TestFile getBroadcastHelper() {
        return TestFiles.java("package me.ycdev.android.lib.common.wrapper;\n" +
                "\n" +
                "import android.content.BroadcastReceiver;\n" +
                "import android.content.Context;\n" +
                "import android.content.Intent;\n" +
                "import android.content.IntentFilter;\n" +
                "import android.support.annotation.NonNull;\n" +
                "\n" +
                "/**\n" +
                " * A wrapper class to avoid security issues when sending/receiving broadcast.\n" +
                " */\n" +
                "@SuppressWarnings(\"unused\")\n" +
                "public class BroadcastHelper {\n" +
                "    private static final String PERM_INTERNAL_BROADCAST_SUFFIX = \".permission.INTERNAL\";\n" +
                "\n" +
                "    private BroadcastHelper() {\n" +
                "        // nothing to do\n" +
                "    }\n" +
                "\n" +
                "    private static String getInternalBroadcastPerm(Context cxt) {\n" +
                "        return cxt.getPackageName() + PERM_INTERNAL_BROADCAST_SUFFIX;\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * Register a receiver for internal broadcast.\n" +
                "     */\n" +
                "    public static Intent registerForInternal(@NonNull Context cxt,\n" +
                "            @NonNull BroadcastReceiver receiver, @NonNull IntentFilter filter) {\n" +
                "        String perm = cxt.getPackageName() + PERM_INTERNAL_BROADCAST_SUFFIX;\n" +
                "        return cxt.registerReceiver(receiver, filter, perm, null);\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * Register a receiver for external broadcast (includes system broadcast).\n" +
                "     */\n" +
                "    public static Intent registerForExternal(@NonNull Context cxt,\n" +
                "            @NonNull BroadcastReceiver receiver, @NonNull IntentFilter filter) {\n" +
                "        return cxt.registerReceiver(receiver, filter);\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * Send a broadcast to internal receivers.\n" +
                "     */\n" +
                "    public static void sendToInternal(@NonNull Context cxt, @NonNull Intent intent) {\n" +
                "        String perm = cxt.getPackageName() + PERM_INTERNAL_BROADCAST_SUFFIX;\n" +
                "        intent.setPackage(cxt.getPackageName()); // only works on Android 4.0 and higher versions\n" +
                "        cxt.sendBroadcast(intent, perm);\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * Send a broadcast to external receivers.\n" +
                "     */\n" +
                "    public static void sendToExternal(@NonNull Context cxt, @NonNull Intent intent,\n" +
                "            @NonNull String perm) {\n" +
                "        cxt.sendBroadcast(intent, perm);\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * Send a broadcast to external receivers.\n" +
                "     */\n" +
                "    public static void sendToExternal(@NonNull Context cxt, @NonNull Intent intent) {\n" +
                "        cxt.sendBroadcast(intent);\n" +
                "    }\n" +
                "\n" +
                "}\n");
    }

    public static TestFile getIntentHelper() {
        return TestFiles.java("package me.ycdev.android.lib.common.wrapper;\n" +
                "\n" +
                "import android.content.Intent;\n" +
                "import android.os.Bundle;\n" +
                "import android.os.Parcelable;\n" +
                "import android.support.annotation.NonNull;\n" +
                "import android.support.annotation.Nullable;\n" +
                "\n" +
                "import java.io.Serializable;\n" +
                "import java.util.ArrayList;\n" +
                "\n" +
                "import me.ycdev.android.lib.common.utils.LibLogger;\n" +
                "\n" +
                "/**\n" +
                " * A wrapper class to avoid security issues when parsing Intent extras.\n" +
                " * <p>See details of the issue: http://code.google.com/p/android/issues/detail?id=177223.</p>\n" +
                " */\n" +
                "@SuppressWarnings(\"unused\")\n" +
                "public class IntentHelper {\n" +
                "    private static final String TAG = \"IntentUtils\";\n" +
                "\n" +
                "    private IntentHelper() {\n" +
                "        // nothing to do\n" +
                "    }\n" +
                "\n" +
                "    private static void onIntentAttacked(@NonNull Intent intent, Throwable e) {\n" +
                "        // prevent OOM for Android 5.0~?\n" +
                "        intent.replaceExtras((Bundle) null);\n" +
                "        LibLogger.w(TAG, \"attacked?\", e);\n" +
                "    }\n" +
                "\n" +
                "    public static boolean hasExtra(@Nullable Intent intent, @NonNull String key) {\n" +
                "        if (intent == null) {\n" +
                "            return false;\n" +
                "        }\n" +
                "\n" +
                "        try {\n" +
                "            return intent.hasExtra(key);\n" +
                "        } catch (Exception e) {\n" +
                "            onIntentAttacked(intent, e);\n" +
                "        }\n" +
                "        return false;\n" +
                "    }\n" +
                "\n" +
                "    public static boolean getBooleanExtra(@Nullable Intent intent, @NonNull String key,\n" +
                "            boolean defValue) {\n" +
                "        if (intent == null) {\n" +
                "            return defValue;\n" +
                "        }\n" +
                "\n" +
                "        try {\n" +
                "            return intent.getBooleanExtra(key, defValue);\n" +
                "        } catch (Exception e) {\n" +
                "            onIntentAttacked(intent, e);\n" +
                "        }\n" +
                "        return defValue;\n" +
                "    }\n" +
                "\n" +
                "    public static byte getByteExtra(@Nullable Intent intent, @NonNull String key,\n" +
                "            byte defValue) {\n" +
                "        if (intent == null) {\n" +
                "            return defValue;\n" +
                "        }\n" +
                "\n" +
                "        try {\n" +
                "            return intent.getByteExtra(key, defValue);\n" +
                "        } catch (Exception e) {\n" +
                "            onIntentAttacked(intent, e);\n" +
                "        }\n" +
                "        return defValue;\n" +
                "    }\n" +
                "\n" +
                "    public static short getShortExtra(@Nullable Intent intent, @NonNull String key,\n" +
                "            short defValue) {\n" +
                "        if (intent == null) {\n" +
                "            return defValue;\n" +
                "        }\n" +
                "\n" +
                "        try {\n" +
                "            return intent.getShortExtra(key, defValue);\n" +
                "        } catch (Exception e) {\n" +
                "            onIntentAttacked(intent, e);\n" +
                "        }\n" +
                "        return defValue;\n" +
                "    }\n" +
                "\n" +
                "    public static int getIntExtra(@Nullable Intent intent, @NonNull String key,\n" +
                "            int defValue) {\n" +
                "        if (intent == null) {\n" +
                "            return defValue;\n" +
                "        }\n" +
                "\n" +
                "        try {\n" +
                "            return intent.getIntExtra(key, defValue);\n" +
                "        } catch (Exception e) {\n" +
                "            onIntentAttacked(intent, e);\n" +
                "        }\n" +
                "        return defValue;\n" +
                "    }\n" +
                "\n" +
                "    public static long getLongExtra(@Nullable Intent intent, @NonNull String key,\n" +
                "            long defValue) {\n" +
                "        if (intent == null) {\n" +
                "            return defValue;\n" +
                "        }\n" +
                "\n" +
                "        try {\n" +
                "            return intent.getLongExtra(key, defValue);\n" +
                "        } catch (Exception e) {\n" +
                "            onIntentAttacked(intent, e);\n" +
                "        }\n" +
                "        return defValue;\n" +
                "    }\n" +
                "\n" +
                "    public static float getFloatExtra(@Nullable Intent intent, @NonNull String key,\n" +
                "            float defValue) {\n" +
                "        if (intent == null) {\n" +
                "            return defValue;\n" +
                "        }\n" +
                "\n" +
                "        try {\n" +
                "            return intent.getFloatExtra(key, defValue);\n" +
                "        } catch (Exception e) {\n" +
                "            onIntentAttacked(intent, e);\n" +
                "        }\n" +
                "        return defValue;\n" +
                "    }\n" +
                "\n" +
                "    public static double getDoubleExtra(@Nullable Intent intent, @NonNull String key,\n" +
                "            double defValue) {\n" +
                "        if (intent == null) {\n" +
                "            return defValue;\n" +
                "        }\n" +
                "\n" +
                "        try {\n" +
                "            return intent.getDoubleExtra(key, defValue);\n" +
                "        } catch (Exception e) {\n" +
                "            onIntentAttacked(intent, e);\n" +
                "        }\n" +
                "        return defValue;\n" +
                "    }\n" +
                "\n" +
                "    public static char getCharExtra(@Nullable Intent intent, @NonNull String key,\n" +
                "            char defValue) {\n" +
                "        if (intent == null) {\n" +
                "            return defValue;\n" +
                "        }\n" +
                "\n" +
                "        try {\n" +
                "            return intent.getCharExtra(key, defValue);\n" +
                "        } catch (Exception e) {\n" +
                "            onIntentAttacked(intent, e);\n" +
                "        }\n" +
                "        return defValue;\n" +
                "    }\n" +
                "\n" +
                "    @Nullable\n" +
                "    public static String getStringExtra(@Nullable Intent intent, @NonNull String key) {\n" +
                "        if (intent == null) {\n" +
                "            return null;\n" +
                "        }\n" +
                "\n" +
                "        try {\n" +
                "            return intent.getStringExtra(key);\n" +
                "        } catch (Exception e) {\n" +
                "            onIntentAttacked(intent, e);\n" +
                "        }\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    @Nullable\n" +
                "    public static CharSequence getCharSequenceExtra(@Nullable Intent intent, @NonNull String key) {\n" +
                "        if (intent == null) {\n" +
                "            return null;\n" +
                "        }\n" +
                "\n" +
                "        try {\n" +
                "            return intent.getCharSequenceExtra(key);\n" +
                "        } catch (Exception e) {\n" +
                "            onIntentAttacked(intent, e);\n" +
                "        }\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    @Nullable\n" +
                "    public static Serializable getSerializableExtra(@Nullable Intent intent,\n" +
                "            @NonNull String key) {\n" +
                "        if (intent == null) {\n" +
                "            return null;\n" +
                "        }\n" +
                "\n" +
                "        try {\n" +
                "            return intent.getSerializableExtra(key);\n" +
                "        } catch (Exception e) {\n" +
                "            onIntentAttacked(intent, e);\n" +
                "        }\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    @Nullable\n" +
                "    public static <T extends Parcelable> T getParcelableExtra(@Nullable Intent intent,\n" +
                "            @NonNull String key) {\n" +
                "        if (intent == null) {\n" +
                "            return null;\n" +
                "        }\n" +
                "\n" +
                "        try {\n" +
                "            return intent.getParcelableExtra(key);\n" +
                "        } catch (Exception e) {\n" +
                "            onIntentAttacked(intent, e);\n" +
                "        }\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    @Nullable\n" +
                "    public static boolean[] getBooleanArrayExtra(@Nullable Intent intent, @NonNull String key) {\n" +
                "        if (intent == null) {\n" +
                "            return null;\n" +
                "        }\n" +
                "\n" +
                "        try {\n" +
                "            return intent.getBooleanArrayExtra(key);\n" +
                "        } catch (Exception e) {\n" +
                "            onIntentAttacked(intent, e);\n" +
                "        }\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    @Nullable\n" +
                "    public static int[] getIntArrayExtra(@Nullable Intent intent, @NonNull String key) {\n" +
                "        if (intent == null) {\n" +
                "            return null;\n" +
                "        }\n" +
                "\n" +
                "        try {\n" +
                "            return intent.getIntArrayExtra(key);\n" +
                "        } catch (Exception e) {\n" +
                "            onIntentAttacked(intent, e);\n" +
                "        }\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    @Nullable\n" +
                "    public static long[] getLongArrayExtra(@Nullable Intent intent, @NonNull String key) {\n" +
                "        if (intent == null) {\n" +
                "            return null;\n" +
                "        }\n" +
                "\n" +
                "        try {\n" +
                "            return intent.getLongArrayExtra(key);\n" +
                "        } catch (Exception e) {\n" +
                "            onIntentAttacked(intent, e);\n" +
                "        }\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    @Nullable\n" +
                "    public static String[] getStringArrayExtra(@Nullable Intent intent, @NonNull String key) {\n" +
                "        if (intent == null) {\n" +
                "            return null;\n" +
                "        }\n" +
                "\n" +
                "        try {\n" +
                "            return intent.getStringArrayExtra(key);\n" +
                "        } catch (Exception e) {\n" +
                "            onIntentAttacked(intent, e);\n" +
                "        }\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    @Nullable\n" +
                "    public static Parcelable[] getParcelableArrayExtra(@Nullable Intent intent,\n" +
                "            @NonNull String key) {\n" +
                "        if (intent == null) {\n" +
                "            return null;\n" +
                "        }\n" +
                "\n" +
                "        try {\n" +
                "            return intent.getParcelableArrayExtra(key);\n" +
                "        } catch (Exception e) {\n" +
                "            onIntentAttacked(intent, e);\n" +
                "        }\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    @Nullable\n" +
                "    public static ArrayList<String> getStringArrayListExtra(@Nullable Intent intent,\n" +
                "            @NonNull String key) {\n" +
                "        if (intent == null) {\n" +
                "            return null;\n" +
                "        }\n" +
                "\n" +
                "        try {\n" +
                "            return intent.getStringArrayListExtra(key);\n" +
                "        } catch (Exception e) {\n" +
                "            onIntentAttacked(intent, e);\n" +
                "        }\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    @Nullable\n" +
                "    public static <T extends Parcelable> ArrayList<T> getParcelableArrayListExtra(\n" +
                "            @Nullable Intent intent, @NonNull String key) {\n" +
                "        if (intent == null) {\n" +
                "            return null;\n" +
                "        }\n" +
                "\n" +
                "        try {\n" +
                "            return intent.getParcelableArrayListExtra(key);\n" +
                "        } catch (Exception e) {\n" +
                "            onIntentAttacked(intent, e);\n" +
                "        }\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    @Nullable\n" +
                "    public static Bundle getBundleExtra(@Nullable Intent intent, @NonNull String key) {\n" +
                "        if (intent == null) {\n" +
                "            return null;\n" +
                "        }\n" +
                "\n" +
                "        try {\n" +
                "            return intent.getBundleExtra(key);\n" +
                "        } catch (Exception e) {\n" +
                "            onIntentAttacked(intent, e);\n" +
                "        }\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "}\n");
    }

    public static TestFile getToastHelper() {
        return TestFiles.java("package me.ycdev.android.arch.wrapper;\n" +
                "\n" +
                "import android.content.Context;\n" +
                "import android.support.annotation.NonNull;\n" +
                "import android.support.annotation.StringRes;\n" +
                "import android.widget.Toast;\n" +
                "\n" +
                "/**\n" +
                " * A wrapper class for Toast so that we can customize and unify the UI in future.\n" +
                " */\n" +
                "@SuppressWarnings(\"unused\")\n" +
                "public class ToastHelper {\n" +
                "    private ToastHelper() {\n" +
                "        // nothing to do\n" +
                "    }\n" +
                "\n" +
                "    public static void show(@NonNull Context cxt, @StringRes int msgResId,\n" +
                "            int duration) {\n" +
                "        Toast.makeText(cxt, msgResId, duration).show();\n" +
                "    }\n" +
                "\n" +
                "    public static void show(@NonNull Context cxt, @NonNull CharSequence msg,\n" +
                "            int duration) {\n" +
                "        Toast.makeText(cxt, msg, duration).show();\n" +
                "    }\n" +
                "\n" +
                "}\n");
    }
}
