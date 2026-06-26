package me.ycdev.android.lib.common.provider

import android.os.Bundle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InfoProviderTest {
    @Test
    fun call_rejectsInvalidRequests() {
        val provider = MemoryInfoProvider()
        val validArgs = Bundle().apply {
            putString(InfoProvider.KEY_NAME, "name")
            putString(InfoProvider.KEY_VALUE, "value")
        }

        assertThat(provider.call(InfoProvider.METHOD_GET, null, null)).isNull()
        assertThat(provider.call("", null, validArgs)).isNull()
        assertThat(provider.call("unknown", null, validArgs)).isNull()
        assertThat(
            provider.call(
                InfoProvider.METHOD_GET,
                null,
                Bundle().apply { putString(InfoProvider.KEY_TABLE, "table") }
            )
        ).isNull()
        assertThat(
            provider.call(
                InfoProvider.METHOD_PUT,
                null,
                Bundle().apply { putString(InfoProvider.KEY_NAME, "name") }
            )
        ).isNull()
    }

    @Test
    fun call_usesDefaultTableWhenTableIsMissing() {
        val provider = MemoryInfoProvider()

        val putResult = provider.call(
            InfoProvider.METHOD_PUT,
            null,
            Bundle().apply {
                putString(InfoProvider.KEY_NAME, "name")
                putString(InfoProvider.KEY_VALUE, "value")
            }
        )

        assertThat(putResult!!.getBoolean(InfoProvider.KEY_STATUS)).isTrue()
        assertThat(provider.getValue(InfoProvider.TABLE_DEFAULT, "name")).isEqualTo("value")
    }

    @Test
    fun call_isolatesValuesAcrossTables() {
        val provider = MemoryInfoProvider()

        provider.call(
            InfoProvider.METHOD_PUT,
            null,
            Bundle().apply {
                putString(InfoProvider.KEY_TABLE, "tableA")
                putString(InfoProvider.KEY_NAME, "name")
                putString(InfoProvider.KEY_VALUE, "valueA")
            }
        )
        provider.call(
            InfoProvider.METHOD_PUT,
            null,
            Bundle().apply {
                putString(InfoProvider.KEY_TABLE, "tableB")
                putString(InfoProvider.KEY_NAME, "name")
                putString(InfoProvider.KEY_VALUE, "valueB")
            }
        )

        val tableAResult = provider.call(
            InfoProvider.METHOD_GET,
            null,
            Bundle().apply {
                putString(InfoProvider.KEY_TABLE, "tableA")
                putString(InfoProvider.KEY_NAME, "name")
            }
        )
        val tableBResult = provider.call(
            InfoProvider.METHOD_GET,
            null,
            Bundle().apply {
                putString(InfoProvider.KEY_TABLE, "tableB")
                putString(InfoProvider.KEY_NAME, "name")
            }
        )

        assertThat(tableAResult!!.getString(InfoProvider.KEY_VALUE)).isEqualTo("valueA")
        assertThat(tableBResult!!.getString(InfoProvider.KEY_VALUE)).isEqualTo("valueB")
    }

    @Test
    fun call_removeReturnsOldValueAndStatus() {
        val provider = MemoryInfoProvider()
        val args = Bundle().apply {
            putString(InfoProvider.KEY_TABLE, "table")
            putString(InfoProvider.KEY_NAME, "name")
            putString(InfoProvider.KEY_VALUE, "value")
        }
        provider.call(InfoProvider.METHOD_PUT, null, args)

        val removeResult = provider.call(
            InfoProvider.METHOD_REMOVE,
            null,
            Bundle().apply {
                putString(InfoProvider.KEY_TABLE, "table")
                putString(InfoProvider.KEY_NAME, "name")
            }
        )
        val secondRemoveResult = provider.call(
            InfoProvider.METHOD_REMOVE,
            null,
            Bundle().apply {
                putString(InfoProvider.KEY_TABLE, "table")
                putString(InfoProvider.KEY_NAME, "name")
            }
        )

        assertThat(removeResult!!.getString(InfoProvider.KEY_VALUE)).isEqualTo("value")
        assertThat(removeResult.getBoolean(InfoProvider.KEY_STATUS)).isTrue()
        assertThat(secondRemoveResult!!.getString(InfoProvider.KEY_VALUE)).isNull()
        assertThat(secondRemoveResult.getBoolean(InfoProvider.KEY_STATUS)).isFalse()
    }

    private class MemoryInfoProvider : InfoProvider() {
        private val values = mutableMapOf<String, MutableMap<String, String>>()

        override fun remove(table: String, name: String): Boolean = values[table]?.remove(name) != null

        override fun get(table: String, name: String): String? = values[table]?.get(name)

        override fun put(table: String, name: String, value: String): Boolean {
            values.getOrPut(table) { mutableMapOf() }[name] = value
            return true
        }

        fun getValue(table: String, name: String): String? = get(table, name)
    }
}
