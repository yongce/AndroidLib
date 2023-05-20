package me.ycdev.android.arch.lint.utils

import com.android.tools.lint.checks.infrastructure.TestFile
import com.android.tools.lint.checks.infrastructure.TestFiles

object TestFileStubs {
    val nonNull: TestFile
        get() = TestFiles.java(
            "" +
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
                "}\n"
        )

    val nullable: TestFile
        get() = TestFiles.java(
            "" +
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
                "}\n\n"
        )

    val stringRes: TestFile
        get() = TestFiles.java(
            "" +
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
                "}\n"
        )

    val appCompatActivity: TestFile
        get() = TestFiles.java(
            "" +
                "package android.support.v7.app;\n" +
                "\n" +
                "import android.annotation.SuppressLint;\n" +
                "import android.app.Activity;\n" +
                "\n" +
                "@SuppressLint(\"MyBaseActivity\")" +
                "public class AppCompatActivity extends Activity {" +
                "}\n"
        )

    val appCompatActivityAndroidX: TestFile
        get() = TestFiles.java(
            "" +
                "package androidx.appcompat.app;\n" +
                "\n" +
                "import android.annotation.SuppressLint;\n" +
                "import android.app.Activity;\n" +
                "\n" +
                "@SuppressLint(\"MyBaseActivity\")" +
                "public class AppCompatActivity extends Activity {" +
                "}\n"
        )

    val libLogger: TestFile
        get() = TestFiles.java(
            "" +
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
                "}\n"
        )

    val baseActivity: TestFile
        get() = TestFiles.java(
            "" +
                "package me.ycdev.android.arch.activity;\n" +
                "\n" +
                "import android.app.Activity;\n" +
                "\n" +
                "/**\n" +
                " * Base class for Activity which wants to inherit android.app.Activity.\n" +
                " */\n" +
                "public abstract class BaseActivity extends Activity {\n" +
                "    // nothing to do right now\n" +
                "}\n"
        )

    val appCompatBaseActivity: TestFile
        get() = TestFiles.java(
            "package me.ycdev.android.arch.activity;\n" +
                "\n" +
                "import android.app.Activity;\n" +
                "\n" +
                "public abstract class AppCompatBaseActivity extends Activity {\n" +
                "}\n"
        )

    val broadcastHelper: TestFile
        get() = TestFiles.java(
            "package me.ycdev.android.lib.common.wrapper;\n" +
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
                "}\n"
        )

    val intentHelper: TestFile
        get() = TestFiles.kotlin(
            """
                package me.ycdev.android.lib.common.wrapper

                import android.content.Intent
                import android.os.Bundle
                import android.os.Parcelable
                import me.ycdev.android.lib.common.utils.LibLogger
                import java.io.Serializable
                import java.util.ArrayList

                /**
                 * A wrapper class to avoid security issues when parsing Intent extras.
                 *
                 * See details of the issue: http://code.google.com/p/android/issues/detail?id=177223.
                 */
                @Suppress("unused")
                object IntentHelper {
                    private const val TAG = "IntentUtils"

                    private fun onIntentAttacked(intent: Intent, e: Throwable) {
                        // prevent OOM for Android 5.0~?
                        intent.replaceExtras(null)
                        LibLogger.w(TAG, "attacked?", e)
                    }

                    fun hasExtra(intent: Intent?, key: String): Boolean {
                        if (intent == null) {
                            return false
                        }

                        try {
                            return intent.hasExtra(key)
                        } catch (e: Exception) {
                            onIntentAttacked(intent, e)
                        }

                        return false
                    }

                    fun getBooleanExtra(intent: Intent?, key: String, defValue: Boolean): Boolean {
                        if (intent == null) {
                            return defValue
                        }

                        try {
                            return intent.getBooleanExtra(key, defValue)
                        } catch (e: Exception) {
                            onIntentAttacked(intent, e)
                        }

                        return defValue
                    }

                    fun getByteExtra(intent: Intent?, key: String, defValue: Byte): Byte {
                        if (intent == null) {
                            return defValue
                        }

                        try {
                            return intent.getByteExtra(key, defValue)
                        } catch (e: Exception) {
                            onIntentAttacked(intent, e)
                        }

                        return defValue
                    }

                    fun getShortExtra(intent: Intent?, key: String, defValue: Short): Short {
                        if (intent == null) {
                            return defValue
                        }

                        try {
                            return intent.getShortExtra(key, defValue)
                        } catch (e: Exception) {
                            onIntentAttacked(intent, e)
                        }

                        return defValue
                    }

                    fun getIntExtra(intent: Intent?, key: String, defValue: Int): Int {
                        if (intent == null) {
                            return defValue
                        }

                        try {
                            return intent.getIntExtra(key, defValue)
                        } catch (e: Exception) {
                            onIntentAttacked(intent, e)
                        }

                        return defValue
                    }

                    fun getLongExtra(intent: Intent?, key: String, defValue: Long): Long {
                        if (intent == null) {
                            return defValue
                        }

                        try {
                            return intent.getLongExtra(key, defValue)
                        } catch (e: Exception) {
                            onIntentAttacked(intent, e)
                        }

                        return defValue
                    }

                    fun getFloatExtra(intent: Intent?, key: String, defValue: Float): Float {
                        if (intent == null) {
                            return defValue
                        }

                        try {
                            return intent.getFloatExtra(key, defValue)
                        } catch (e: Exception) {
                            onIntentAttacked(intent, e)
                        }

                        return defValue
                    }

                    fun getDoubleExtra(intent: Intent?, key: String, defValue: Double): Double {
                        if (intent == null) {
                            return defValue
                        }

                        try {
                            return intent.getDoubleExtra(key, defValue)
                        } catch (e: Exception) {
                            onIntentAttacked(intent, e)
                        }

                        return defValue
                    }

                    fun getCharExtra(intent: Intent?, key: String, defValue: Char): Char {
                        if (intent == null) {
                            return defValue
                        }

                        try {
                            return intent.getCharExtra(key, defValue)
                        } catch (e: Exception) {
                            onIntentAttacked(intent, e)
                        }

                        return defValue
                    }

                    fun getStringExtra(intent: Intent?, key: String): String? {
                        if (intent == null) {
                            return null
                        }

                        try {
                            return intent.getStringExtra(key)
                        } catch (e: Exception) {
                            onIntentAttacked(intent, e)
                        }

                        return null
                    }

                    fun getCharSequenceExtra(intent: Intent?, key: String): CharSequence? {
                        if (intent == null) {
                            return null
                        }

                        try {
                            return intent.getCharSequenceExtra(key)
                        } catch (e: Exception) {
                            onIntentAttacked(intent, e)
                        }

                        return null
                    }

                    fun getSerializableExtra(intent: Intent?, key: String): Serializable? {
                        if (intent == null) {
                            return null
                        }

                        try {
                            return intent.getSerializableExtra(key)
                        } catch (e: Exception) {
                            onIntentAttacked(intent, e)
                        }

                        return null
                    }

                    fun <T : Parcelable> getParcelableExtra(intent: Intent?, key: String): T? {
                        if (intent == null) {
                            return null
                        }

                        try {
                            return intent.getParcelableExtra(key)
                        } catch (e: Exception) {
                            onIntentAttacked(intent, e)
                        }

                        return null
                    }

                    fun getBooleanArrayExtra(intent: Intent?, key: String): BooleanArray? {
                        if (intent == null) {
                            return null
                        }

                        try {
                            return intent.getBooleanArrayExtra(key)
                        } catch (e: Exception) {
                            onIntentAttacked(intent, e)
                        }

                        return null
                    }

                    fun getIntArrayExtra(intent: Intent?, key: String): IntArray? {
                        if (intent == null) {
                            return null
                        }

                        try {
                            return intent.getIntArrayExtra(key)
                        } catch (e: Exception) {
                            onIntentAttacked(intent, e)
                        }

                        return null
                    }

                    fun getLongArrayExtra(intent: Intent?, key: String): LongArray? {
                        if (intent == null) {
                            return null
                        }

                        try {
                            return intent.getLongArrayExtra(key)
                        } catch (e: Exception) {
                            onIntentAttacked(intent, e)
                        }

                        return null
                    }

                    fun getStringArrayExtra(intent: Intent?, key: String): Array<String>? {
                        if (intent == null) {
                            return null
                        }

                        try {
                            return intent.getStringArrayExtra(key)
                        } catch (e: Exception) {
                            onIntentAttacked(intent, e)
                        }

                        return null
                    }

                    fun getParcelableArrayExtra(intent: Intent?, key: String): Array<Parcelable>? {
                        if (intent == null) {
                            return null
                        }

                        try {
                            return intent.getParcelableArrayExtra(key)
                        } catch (e: Exception) {
                            onIntentAttacked(intent, e)
                        }

                        return null
                    }

                    fun getStringArrayListExtra(intent: Intent?, key: String): ArrayList<String>? {
                        if (intent == null) {
                            return null
                        }

                        try {
                            return intent.getStringArrayListExtra(key)
                        } catch (e: Exception) {
                            onIntentAttacked(intent, e)
                        }

                        return null
                    }

                    fun <T : Parcelable> getParcelableArrayListExtra(intent: Intent?, key: String): ArrayList<T>? {
                        if (intent == null) {
                            return null
                        }

                        try {
                            return intent.getParcelableArrayListExtra(key)
                        } catch (e: Exception) {
                            onIntentAttacked(intent, e)
                        }

                        return null
                    }

                    fun getBundleExtra(intent: Intent?, key: String): Bundle? {
                        if (intent == null) {
                            return null
                        }

                        try {
                            return intent.getBundleExtra(key)
                        } catch (e: Exception) {
                            onIntentAttacked(intent, e)
                        }

                        return null
                    }
                }
            """.trimIndent()
        )

    val toastHelper: TestFile
        get() = TestFiles.java(
            "package me.ycdev.android.arch.wrapper;\n" +
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
                "}\n"
        )
}
