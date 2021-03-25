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
        intent.replaceExtras((null as Bundle?)!!)
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
