package me.ycdev.android.lib.common.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import me.ycdev.android.lib.common.utils.LibLogger;

public abstract class InfoProvider extends ContentProvider {
    private static final String TAG = "InfoProvider";

    public static final String METHOD_REMOVE = "remove";
    public static final String METHOD_GET = "get";
    public static final String METHOD_PUT = "put";

    public static final String KEY_TABLE = "table";
    public static final String KEY_NAME = "name";
    public static final String KEY_VALUE = "value";
    public static final String KEY_STATUS = "status";

    public static final String TABLE_DEFAULT = "default";

    protected abstract boolean remove(@NonNull String table, @NonNull String name);
    protected abstract String get(@NonNull String table, @NonNull String name);
    protected abstract boolean put(@NonNull String table, @NonNull String name, @NonNull String value);

    @Override
    public boolean onCreate() {
        LibLogger.d(TAG, "onCreate");
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
            @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        LibLogger.d(TAG, "query: %s", uri);
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        LibLogger.d(TAG, "getType: %s", uri);
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        LibLogger.d(TAG, "insert: %s", uri);
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        LibLogger.d(TAG, "delete: %s", uri);
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
            @Nullable String[] selectionArgs) {
        LibLogger.d(TAG, "update: %s", uri);
        return 0;
    }

    @Nullable
    @Override
    public Bundle call(@NonNull String method, @Nullable String arg, @Nullable Bundle extras) {
        if (extras == null) {
            LibLogger.e(TAG, "no args for method [%s]", method);
            return null;
        }

        String table = extras.getString(KEY_TABLE);
        String name = extras.getString(KEY_NAME);
        String value = extras.getString(KEY_VALUE);
        LibLogger.d(TAG, "call [%s] for [%s] in table [%s]", method, name, table);
        if (TextUtils.isEmpty(method) || TextUtils.isEmpty(name)) {
            LibLogger.w(TAG, "no method or name for the request");
            return null;
        }
        if (TextUtils.isEmpty(table)) {
            table = TABLE_DEFAULT;
        }

        Bundle result = new Bundle();
        switch (method) {
            case METHOD_REMOVE: {
                result.putString(KEY_VALUE, get(table, name)); // old value
                result.putBoolean(KEY_STATUS, remove(table, name));
                break;
            }
            case METHOD_GET: {
                result.putString(KEY_VALUE, get(table, name));
                break;
            }
            case METHOD_PUT: {
                if (TextUtils.isEmpty(value)) {
                    LibLogger.w(TAG, "no value for the request");
                    return null;
                }
                result.putString(KEY_VALUE, get(table, name)); // old value
                result.putBoolean(KEY_STATUS, put(table, name, value));
                break;
            }
            default: {
                LibLogger.e(TAG, "unknown method [%s]", method);
                return null;
            }
        }
        return result;
    }
}
