package me.ycdev.android.lib.common.provider

import android.content.ContentResolver
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils

import me.ycdev.android.lib.common.utils.LibLogger

class InfoProviderClient(cxt: Context, private val authority: String) {
    private val resolver: ContentResolver = cxt.applicationContext.contentResolver

    private fun getUriFor(table: String?, name: String): Uri {
        var tableTmp = table
        if (TextUtils.isEmpty(tableTmp)) {
            tableTmp = InfoProvider.TABLE_DEFAULT
        }
        return Uri.Builder().scheme("content").authority(authority)
            .appendPath(tableTmp).appendPath(name)
            .build()
    }

    fun registerObserver(
        table: String?,
        name: String,
        observer: ContentObserver
    ) {
        val uri = getUriFor(table, name)
        resolver.registerContentObserver(uri, true, observer)
    }

    fun unregisterObserver(observer: ContentObserver) {
        resolver.unregisterContentObserver(observer)
    }

    fun remove(table: String?, name: String): Boolean {
        try {
            val uri = getUriFor(table, name)
            val args = Bundle()
            args.putString(InfoProvider.KEY_TABLE, table)
            args.putString(InfoProvider.KEY_NAME, name)
            val result = resolver.call(uri, InfoProvider.METHOD_REMOVE, null, args)
            if (result == null) {
                LibLogger.e(TAG, "Cannot call method [%s]", InfoProvider.METHOD_REMOVE)
                return false
            }

            val success = result.getBoolean(InfoProvider.KEY_STATUS)
            val oldValue = result.getString(InfoProvider.KEY_VALUE)
            if (success && !TextUtils.isEmpty(oldValue)) {
                resolver.notifyChange(uri, null)
            }

            return success
        } catch (e: Exception) {
            LibLogger.w(TAG, "Failed to remove [%s] in table [%s]", name, table)
            return false
        }
    }

    fun getString(table: String?, name: String, defValue: String?): String? {
        try {
            val uri = getUriFor(table, name)
            val args = Bundle()
            args.putString(InfoProvider.KEY_TABLE, table)
            args.putString(InfoProvider.KEY_NAME, name)
            val result = resolver.call(uri, InfoProvider.METHOD_GET, null, args)
            if (result == null) {
                LibLogger.e(TAG, "Cannot call method [%s]", InfoProvider.METHOD_GET)
                return defValue
            }

            var value = result.getString(InfoProvider.KEY_VALUE)
            if (value == null) {
                value = defValue
            }
            return value
        } catch (e: Exception) {
            LibLogger.w(TAG, "Failed to get value for [%s] in table [%s]", name, table)
        }

        return defValue
    }

    fun putString(table: String?, name: String, value: String): Boolean {
        try {
            val uri = getUriFor(table, name)
            val args = Bundle()
            args.putString(InfoProvider.KEY_TABLE, table)
            args.putString(InfoProvider.KEY_NAME, name)
            args.putString(InfoProvider.KEY_VALUE, value)
            val result = resolver.call(uri, InfoProvider.METHOD_PUT, null, args)
            if (result == null) {
                LibLogger.e(TAG, "Cannot call method [%s]", InfoProvider.METHOD_PUT)
                return false
            }

            val success = result.getBoolean(InfoProvider.KEY_STATUS)
            val oldValue = result.getString(InfoProvider.KEY_VALUE)
            if (success && !TextUtils.equals(oldValue, value)) {
                resolver.notifyChange(uri, null)
            }

            return success
        } catch (e: Exception) {
            LibLogger.w(TAG, "Failed to put value for [%s] in table [%s]", name, table)
        }

        return false
    }

    fun getBoolean(table: String?, name: String, defValue: Boolean): Boolean {
        try {
            val result = getString(table, name, null)
            if (!TextUtils.isEmpty(result)) {
                return java.lang.Boolean.parseBoolean(result)
            }
        } catch (e: Exception) {
            LibLogger.w(TAG, "Failed to get boolean value for [%s] in table [%s]", name, table)
        }

        return defValue
    }

    fun putBoolean(table: String?, name: String, value: Boolean): Boolean {
        return putString(table, name, java.lang.Boolean.toString(value))
    }

    fun getInt(table: String?, name: String, defValue: Int): Int {
        try {
            val result = getString(table, name, null)
            if (!TextUtils.isEmpty(result)) {
                return Integer.parseInt(result!!)
            }
        } catch (e: Exception) {
            LibLogger.w(TAG, "Failed to get int value for [%s] in table [%s]", name, table)
        }

        return defValue
    }

    fun putInt(table: String?, name: String, value: Int): Boolean {
        return putString(table, name, Integer.toString(value))
    }

    companion object {
        private const val TAG = "InfoProviderClient"
    }
}
