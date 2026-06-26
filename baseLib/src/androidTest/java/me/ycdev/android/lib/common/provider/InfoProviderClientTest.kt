package me.ycdev.android.lib.common.provider

import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InfoProviderClientTest {
    private var mInfoClient: InfoProviderClient? = null

    @Before
    fun setup() {
        mInfoClient =
            InfoProviderClient(
                ApplicationProvider.getApplicationContext(),
                "me.ycdev.android.lib.common.provider.InfoProvider"
            )

        // clear dirty data
        mInfoClient!!.remove(TABLE_TEST, KEY_STR_1)
        mInfoClient!!.remove(TABLE_TEST, KEY_BOOL_1)
        mInfoClient!!.remove(TABLE_TEST, KEY_INT_1)
        mInfoClient!!.remove(null, KEY_DEFAULT_STR)
        mInfoClient!!.remove(TABLE_OTHER, KEY_STR_1)
        mInfoClient!!.remove(TABLE_TEST, KEY_INVALID_INT)
        mInfoClient!!.remove(TABLE_TEST, KEY_MISSING)
    }

    @Test
    fun getAndPutString() {
        var value = mInfoClient!!.getString(TABLE_TEST, KEY_STR_1, "")
        assertThat(value).isEqualTo("")

        value = mInfoClient!!.getString(TABLE_TEST, KEY_STR_1, null)
        assertThat(value).isNull()

        val result = mInfoClient!!.putString(TABLE_TEST, KEY_STR_1, "value_str1")
        assertThat(result).isTrue()

        value = mInfoClient!!.getString(TABLE_TEST, KEY_STR_1, null)
        assertThat(value).isEqualTo("value_str1")
    }

    @Test
    fun getAndPutBoolean() {
        var value = mInfoClient!!.getBoolean(TABLE_TEST, KEY_BOOL_1, true)
        assertThat(value).isTrue()

        value = mInfoClient!!.getBoolean(TABLE_TEST, KEY_BOOL_1, false)
        assertThat(value).isFalse()

        val result = mInfoClient!!.putBoolean(TABLE_TEST, KEY_BOOL_1, true)
        assertThat(result).isTrue()

        value = mInfoClient!!.getBoolean(TABLE_TEST, KEY_BOOL_1, false)
        assertThat(value).isTrue()
    }

    @Test
    fun getAndPutInt() {
        var value = mInfoClient!!.getInt(TABLE_TEST, KEY_INT_1, 1)
        assertThat(value).isEqualTo(1)

        value = mInfoClient!!.getInt(TABLE_TEST, KEY_INT_1, 2)
        assertThat(value).isEqualTo(2)

        val result = mInfoClient!!.putInt(TABLE_TEST, KEY_INT_1, 3)
        assertThat(result).isTrue()

        value = mInfoClient!!.getInt(TABLE_TEST, KEY_INT_1, 1)
        assertThat(value).isEqualTo(3)
    }

    @Test
    fun getAndPutString_defaultTable() {
        var value = mInfoClient!!.getString(null, KEY_DEFAULT_STR, "fallback")
        assertThat(value).isEqualTo("fallback")

        val result = mInfoClient!!.putString(null, KEY_DEFAULT_STR, "default-value")
        assertThat(result).isTrue()

        value = mInfoClient!!.getString(null, KEY_DEFAULT_STR, null)
        assertThat(value).isEqualTo("default-value")
    }

    @Test
    fun getAndPutString_isolatedAcrossTables() {
        assertThat(mInfoClient!!.putString(TABLE_TEST, KEY_STR_1, "value-test")).isTrue()
        assertThat(mInfoClient!!.putString(TABLE_OTHER, KEY_STR_1, "value-other")).isTrue()

        assertThat(mInfoClient!!.getString(TABLE_TEST, KEY_STR_1, null)).isEqualTo("value-test")
        assertThat(mInfoClient!!.getString(TABLE_OTHER, KEY_STR_1, null)).isEqualTo("value-other")
    }

    @Test
    fun getInt_invalidValueReturnsDefault() {
        assertThat(mInfoClient!!.putString(TABLE_TEST, KEY_INVALID_INT, "not-an-int")).isTrue()

        val value = mInfoClient!!.getInt(TABLE_TEST, KEY_INVALID_INT, 42)

        assertThat(value).isEqualTo(42)
    }

    @Test
    @Throws(InterruptedException::class)
    fun putAndAddContentObserver() {
        var value = mInfoClient!!.getString(TABLE_TEST, KEY_STR_1, null)
        assertThat(value).isNull()

        val latch = CountDownLatch(1)
        val observer =
            object : ContentObserver(Handler(Looper.getMainLooper())) {
                override fun onChange(selfChange: Boolean) {
                    val curValue = mInfoClient!!.getString(TABLE_TEST, KEY_STR_1, null)
                    assertThat(curValue).isEqualTo("value_str1")
                    latch.countDown()
                }
            }
        mInfoClient!!.registerObserver(TABLE_TEST, KEY_STR_1, observer)

        val result = mInfoClient!!.putString(TABLE_TEST, KEY_STR_1, "value_str1")
        assertThat(result).isTrue()

        value = mInfoClient!!.getString(TABLE_TEST, KEY_STR_1, null)
        assertThat(value).isEqualTo("value_str1")

        assertThat(latch.await(5, TimeUnit.SECONDS)).isTrue()
        mInfoClient!!.unregisterObserver(observer)
    }

    @Test
    @Throws(InterruptedException::class)
    fun removeAndAddContentObserver() {
        var result = mInfoClient!!.putString(TABLE_TEST, KEY_STR_1, "value_str1")
        assertThat(result).isTrue()

        var value = mInfoClient!!.getString(TABLE_TEST, KEY_STR_1, null)
        assertThat(value).isEqualTo("value_str1")

        val latch = CountDownLatch(1)
        val observer =
            object : ContentObserver(Handler(Looper.getMainLooper())) {
                override fun onChange(selfChange: Boolean) {
                    val curValue = mInfoClient!!.getString(TABLE_TEST, KEY_STR_1, null)
                    assertThat(curValue).isNull()
                    latch.countDown()
                }
            }
        mInfoClient!!.registerObserver(TABLE_TEST, KEY_STR_1, observer)

        result = mInfoClient!!.remove(TABLE_TEST, KEY_STR_1)
        assertThat(result).isTrue()

        value = mInfoClient!!.getString(TABLE_TEST, KEY_STR_1, null)
        assertThat(value).isNull()

        assertThat(latch.await(5, TimeUnit.SECONDS)).isTrue()
        mInfoClient!!.unregisterObserver(observer)
    }

    @Test
    @Throws(InterruptedException::class)
    fun putButNoChange() {
        var value = mInfoClient!!.getString(TABLE_TEST, KEY_STR_1, null)
        assertThat(value).isNull()

        val latch = CountDownLatch(2)
        val observer =
            object : ContentObserver(Handler(Looper.getMainLooper())) {
                override fun onChange(selfChange: Boolean) {
                    val curValue = mInfoClient!!.getString(TABLE_TEST, KEY_STR_1, null)
                    assertThat(curValue).isEqualTo("value_str1")
                    latch.countDown()
                }
            }
        mInfoClient!!.registerObserver(TABLE_TEST, KEY_STR_1, observer)

        // put 1
        var result = mInfoClient!!.putString(TABLE_TEST, KEY_STR_1, "value_str1")
        assertThat(result).isTrue()

        value = mInfoClient!!.getString(TABLE_TEST, KEY_STR_1, null)
        assertThat(value).isEqualTo("value_str1")

        // put 2 (but no value change)
        result = mInfoClient!!.putString(TABLE_TEST, KEY_STR_1, "value_str1")
        assertThat(result).isTrue()

        value = mInfoClient!!.getString(TABLE_TEST, KEY_STR_1, null)
        assertThat(value).isEqualTo("value_str1")

        assertThat(latch.await(5, TimeUnit.SECONDS)).isFalse()
        assertThat(latch.count).isEqualTo(1)

        mInfoClient!!.unregisterObserver(observer)
    }

    @Test
    @Throws(InterruptedException::class)
    fun removeMissingValueDoesNotNotifyObserver() {
        val latch = CountDownLatch(1)
        val observer =
            object : ContentObserver(Handler(Looper.getMainLooper())) {
                override fun onChange(selfChange: Boolean) {
                    latch.countDown()
                }
            }
        mInfoClient!!.registerObserver(TABLE_TEST, KEY_MISSING, observer)

        val result = mInfoClient!!.remove(TABLE_TEST, KEY_MISSING)

        assertThat(result).isTrue()
        assertThat(latch.await(NO_CHANGE_TIMEOUT_MS, TimeUnit.MILLISECONDS)).isFalse()
        mInfoClient!!.unregisterObserver(observer)
    }

    @Test
    @Throws(InterruptedException::class)
    fun unregisterObserverStopsNotifications() {
        val latch = CountDownLatch(1)
        val observer =
            object : ContentObserver(Handler(Looper.getMainLooper())) {
                override fun onChange(selfChange: Boolean) {
                    latch.countDown()
                }
            }
        mInfoClient!!.registerObserver(TABLE_TEST, KEY_STR_1, observer)
        mInfoClient!!.unregisterObserver(observer)

        val result = mInfoClient!!.putString(TABLE_TEST, KEY_STR_1, "value_str1")

        assertThat(result).isTrue()
        assertThat(latch.await(NO_CHANGE_TIMEOUT_MS, TimeUnit.MILLISECONDS)).isFalse()
    }

    companion object {
        private const val NO_CHANGE_TIMEOUT_MS = 300L
        private const val TABLE_TEST = "test"
        private const val TABLE_OTHER = "other"
        private const val KEY_STR_1 = "key_str1"
        private const val KEY_DEFAULT_STR = "key_default_str"
        private const val KEY_BOOL_1 = "key_bool1"
        private const val KEY_INT_1 = "key_int1"
        private const val KEY_INVALID_INT = "key_invalid_int"
        private const val KEY_MISSING = "key_missing"
    }
}
