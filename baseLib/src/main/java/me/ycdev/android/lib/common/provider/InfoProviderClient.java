package me.ycdev.android.lib.common.provider;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import me.ycdev.android.lib.common.utils.LibLogger;

public class InfoProviderClient {
    private static final String TAG = "InfoProviderClient";

    private ContentResolver mResolver;
    private String mAuthority;

    public InfoProviderClient(@NonNull Context cxt, @NonNull String authority) {
        mResolver = cxt.getApplicationContext().getContentResolver();
        mAuthority = authority;
    }

    private Uri getUriForCall() {
        Uri.Builder builder = new Uri.Builder();
        builder = builder.scheme("content");
        builder.authority(mAuthority);
        return builder.build();
    }

    public boolean remove(@Nullable String table, @NonNull String name) {
        try {
            Uri uri = getUriForCall();
            Bundle args = new Bundle();
            args.putString(InfoProvider.KEY_TABLE, table);
            args.putString(InfoProvider.KEY_NAME, name);
            Bundle result = mResolver.call(uri, InfoProvider.METHOD_REMOVE, null, args);
            if (result == null) {
                LibLogger.e(TAG, "Cannot call method [%s]", InfoProvider.METHOD_REMOVE);
                return false;
            }
            return result.getBoolean(InfoProvider.KEY_STATUS);
        } catch (Exception e) {
            LibLogger.w(TAG, "Failed to remove [%s] in table [%s]", name, table);
            return false;
        }
    }

    public String getString(@Nullable String table, @NonNull String name, @Nullable String defValue) {
        try {
            Uri uri = getUriForCall();
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
            Uri uri = getUriForCall();
            Bundle args = new Bundle();
            args.putString(InfoProvider.KEY_TABLE, table);
            args.putString(InfoProvider.KEY_NAME, name);
            args.putString(InfoProvider.KEY_VALUE, value);
            Bundle result = mResolver.call(uri, InfoProvider.METHOD_PUT, null, args);
            if (result == null) {
                LibLogger.e(TAG, "Cannot call method [%s]", InfoProvider.METHOD_PUT);
                return false;
            }
            return result.getBoolean(InfoProvider.KEY_STATUS);
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
