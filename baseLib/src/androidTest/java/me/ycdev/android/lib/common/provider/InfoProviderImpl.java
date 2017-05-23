package me.ycdev.android.lib.common.provider;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

public class InfoProviderImpl extends InfoProvider {
    @Override
    protected boolean remove(@NonNull String table, @NonNull String name) {
        getStoragePrefs(table).edit().remove(name).apply();
        return true;
    }

    @Override
    protected String get(@NonNull String table, @NonNull String name) {
        return getStoragePrefs(table).getString(name, null);
    }

    @Override
    protected boolean put(@NonNull String table, @NonNull String name, @NonNull String value) {
        getStoragePrefs(table).edit().putString(name, value).apply();
        return true;
    }

    @SuppressWarnings("ConstantConditions")
    private SharedPreferences getStoragePrefs(String table) {
        return getContext().getSharedPreferences("info_provider_" + table, Context.MODE_PRIVATE);
    }
}
