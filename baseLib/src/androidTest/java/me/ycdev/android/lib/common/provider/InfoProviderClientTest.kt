package me.ycdev.android.lib.common.provider

import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4

import com.google.common.truth.Truth.assertThat

@RunWith(AndroidJUnit4::class)
class InfoProviderClientTest {

    private var mInfoClient: InfoProviderClient? = null

    @Before
    fun setup() {
        mInfoClient = InfoProviderClient(
            ApplicationProvider.getApplicationContext<Context>(),
            "me.ycdev.android.lib.common.provider.InfoProvider"
        )

        // clear dirty data
        mInfoClient!!.remove(TABLE_TEST, KEY_STR_1)
        mInfoClient!!.remove(TABLE_TEST, KEY_BOOL_1)
        mInfoClient!!.remove(TABLE_TEST, KEY_INT_1)
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
    @Throws(InterruptedException::class)
    fun putAndAddContentObserver() {
        var value = mInfoClient!!.getString(TABLE_TEST, KEY_STR_1, null)
        assertThat(value).isNull()

        val latch = CountDownLatch(1)
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
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

        latch.await()
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
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
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

        latch.await()
        mInfoClient!!.unregisterObserver(observer)
    }

    @Test
    @Throws(InterruptedException::class)
    fun putButNoChange() {
        var value = mInfoClient!!.getString(TABLE_TEST, KEY_STR_1, null)
        assertThat(value).isNull()

        val latch = CountDownLatch(2)
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
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

        latch.await(5, TimeUnit.SECONDS)
        assertThat(latch.count).isEqualTo(1)

        mInfoClient!!.unregisterObserver(observer)
    }

    companion object {
        private const val TABLE_TEST = "test"
        private const val KEY_STR_1 = "key_str1"
        private const val KEY_BOOL_1 = "key_bool1"
        private const val KEY_INT_1 = "key_int1"
    }
}
