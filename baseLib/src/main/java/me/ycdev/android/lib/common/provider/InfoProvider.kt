package me.ycdev.android.lib.common.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils

import me.ycdev.android.lib.common.utils.LibLogger

abstract class InfoProvider : ContentProvider() {

    protected abstract fun remove(table: String, name: String): Boolean
    protected abstract fun get(table: String, name: String): String?
    protected abstract fun put(table: String, name: String, value: String): Boolean

    override fun onCreate(): Boolean {
        LibLogger.d(TAG, "onCreate")
        return false
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        LibLogger.d(TAG, "query: %s", uri)
        return null
    }

    override fun getType(uri: Uri): String? {
        LibLogger.d(TAG, "getType: %s", uri)
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        LibLogger.d(TAG, "insert: %s", uri)
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        LibLogger.d(TAG, "delete: %s", uri)
        return 0
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        LibLogger.d(TAG, "update: %s", uri)
        return 0
    }

    override fun call(method: String, arg: String?, extras: Bundle?): Bundle? {
        if (extras == null) {
            LibLogger.e(TAG, "no args for method [%s]", method)
            return null
        }

        var table = extras.getString(KEY_TABLE)
        val name = extras.getString(KEY_NAME)
        val value = extras.getString(KEY_VALUE)
        LibLogger.d(TAG, "call [%s] for [%s] in table [%s]", method, name, table)
        if (TextUtils.isEmpty(method) || TextUtils.isEmpty(name)) {
            LibLogger.w(TAG, "no method or name for the request")
            return null
        }
        if (TextUtils.isEmpty(table)) {
            table = TABLE_DEFAULT
        }

        val result = Bundle()
        when (method) {
            METHOD_REMOVE -> {
                result.putString(KEY_VALUE, get(table!!, name!!)) // old value
                result.putBoolean(KEY_STATUS, remove(table, name))
            }
            METHOD_GET -> {
                result.putString(KEY_VALUE, get(table!!, name!!))
            }
            METHOD_PUT -> {
                if (TextUtils.isEmpty(value)) {
                    LibLogger.w(TAG, "no value for the request")
                    return null
                }
                result.putString(KEY_VALUE, get(table!!, name!!)) // old value
                result.putBoolean(KEY_STATUS, put(table, name, value!!))
            }
            else -> {
                LibLogger.e(TAG, "unknown method [%s]", method)
                return null
            }
        }
        return result
    }

    companion object {
        private const val TAG = "InfoProvider"

        const val METHOD_REMOVE = "remove"
        const val METHOD_GET = "get"
        const val METHOD_PUT = "put"

        const val KEY_TABLE = "table"
        const val KEY_NAME = "name"
        const val KEY_VALUE = "value"
        const val KEY_STATUS = "status"

        const val TABLE_DEFAULT = "default"
    }
}
