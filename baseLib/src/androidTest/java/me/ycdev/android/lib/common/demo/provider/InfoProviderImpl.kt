package me.ycdev.android.lib.common.demo.provider

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.NonNull
import me.ycdev.android.lib.common.provider.InfoProvider

class InfoProviderImpl : InfoProvider() {
    override fun remove(@NonNull table: String, @NonNull name: String): Boolean {
        getStoragePrefs(table).edit().remove(name).apply()
        return true
    }

    override fun get(@NonNull table: String, @NonNull name: String): String? {
        return getStoragePrefs(table).getString(name, null)
    }

    override fun put(@NonNull table: String, @NonNull name: String, @NonNull value: String): Boolean {
        getStoragePrefs(table).edit().putString(name, value).apply()
        return true
    }

    private fun getStoragePrefs(table: String): SharedPreferences {
        return context!!.getSharedPreferences("info_provider_$table", Context.MODE_PRIVATE)
    }
}
