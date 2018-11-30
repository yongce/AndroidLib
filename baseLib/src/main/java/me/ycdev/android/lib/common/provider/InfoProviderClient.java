package me.ycdev.android.lib.common.provider;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import me.ycdev.android.lib.common.utils.LibLogger;

@SuppressWarnings({"unused", "WeakerAccess"})
public class InfoProviderClient {
    private static final String TAG = "InfoProviderClient";

    private ContentResolver mResolver;
    private String mAuthority;

    public InfoProviderClient(@NonNull Context cxt, @NonNull String authority) {
        mResolver = cxt.getApplicationContext().getContentResolver();
        mAuthority = authority;
    }

    private Uri getUriFor(@Nullable String table, @NonNull String name) {
        if (TextUtils.isEmpty(table)) {
            table = InfoProvider.TABLE_DEFAULT;
        }
        return new Uri.Builder().scheme("content").authority(mAuthority)
                .appendPath(table).appendPath(name)
                .build();
    }

    public void registerObserver(@Nullable String table, @NonNull String name,
            @NonNull ContentObserver observer) {
        Uri uri = getUriFor(table, name);
        mResolver.registerContentObserver(uri, true, observer);
    }

    public void unregisterObserver(@NonNull ContentObserver observer) {
        mResolver.unregisterContentObserver(observer);
    }

    public boolean remove(@Nullable String table, @NonNull String name) {
        try {
            Uri uri = getUriFor(table, name);
            Bundle args = new Bundle();
            args.putString(InfoProvider.KEY_TABLE, table);
            args.putString(InfoProvider.KEY_NAME, name);
            Bundle result = mResolver.call(uri, InfoProvider.METHOD_REMOVE, null, args);
            if (result == null) {
                LibLogger.e(TAG, "Cannot call method [%s]", InfoProvider.METHOD_REMOVE);
                return false;
            }

            boolean success = result.getBoolean(InfoProvider.KEY_STATUS);
            String oldValue = result.getString(InfoProvider.KEY_VALUE);
            if (success && !TextUtils.isEmpty(oldValue)) {
                mResolver.notifyChange(uri, null);
            }

            return success;
        } catch (Exception e) {
            LibLogger.w(TAG, "Failed to remove [%s] in table [%s]", name, table);
            return false;
        }
    }

    public String getString(@Nullable String table, @NonNull String name, @Nullable String defValue) {
        try {
            Uri uri = getUriFor(table, name);
            Bundle args = new Bundle();
            args.putString(InfoProvider.KEY_TABLE, table);
            args.putString(InfoProvider.KEY_NAME, name);
            Bundle result = mResolver.call(uri, InfoProvider.METHOD_GET, null, args);
            if (result == null) {
                LibLogger.e(TAG, "Cannot call method [%s]", InfoProvider.METHOD_GET);
                return defValue;
            }

            String value = result.getString(InfoProvider.KEY_VALUE);
            if (value == null) {
                value = defValue;
            }
            return value;
        } catch (Exception e) {
            LibLogger.w(TAG, "Failed to get value for [%s] in table [%s]", name, table);
        }
        return defValue;
    }

    public boolean putString(@Nullable String table, @NonNull String name, @NonNull String value) {
        try {
            Uri uri = getUriFor(table, name);
            Bundle args = new Bundle();
            args.putString(InfoProvider.KEY_TABLE, table);
            args.putString(InfoProvider.KEY_NAME, name);
            args.putString(InfoProvider.KEY_VALUE, value);
            Bundle result = mResolver.call(uri, InfoProvider.METHOD_PUT, null, args);
            if (result == null) {
                LibLogger.e(TAG, "Cannot call method [%s]", InfoProvider.METHOD_PUT);
                return false;
            }

            boolean success = result.getBoolean(InfoProvider.KEY_STATUS);
            String oldValue = result.getString(InfoProvider.KEY_VALUE);
            if (success && !TextUtils.equals(oldValue, value)) {
                mResolver.notifyChange(uri, null);
            }

            return success;
        } catch (Exception e) {
            LibLogger.w(TAG, "Failed to put value for [%s] in table [%s]", name, table);
        }
        return false;
    }

    public boolean getBoolean(@Nullable String table, @NonNull String name, boolean defValue) {
        try {
            String result = getString(table, name, null);
            if (!TextUtils.isEmpty(result)) {
                return Boolean.parseBoolean(result);
            }
        } catch (Exception e) {
            LibLogger.w(TAG, "Failed to get boolean value for [%s] in table [%s]", name, table);
        }
        return defValue;
    }

    public boolean putBoolean(@Nullable String table, @NonNull String name, boolean value) {
        return putString(table, name, Boolean.toString(value));
    }

    public int getInt(@Nullable String table, @NonNull String name, int defValue) {
        try {
            String result = getString(table, name, null);
            if (!TextUtils.isEmpty(result)) {
                return Integer.parseInt(result);
            }
        } catch (Exception e) {
            LibLogger.w(TAG, "Failed to get int value for [%s] in table [%s]", name, table);
        }
        return defValue;
    }

    public boolean putInt(@Nullable String table, @NonNull String name, int value) {
        return putString(table, name, Integer.toString(value));
    }
}
