package me.ycdev.android.lib.common.provider;

import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class InfoProviderClientTest {
    private static final String TABLE_TEST = "test";
    private static final String KEY_STR_1 = "key_str1";
    private static final String KEY_BOOL_1 = "key_bool1";
    private static final String KEY_INT_1 = "key_int1";

    private InfoProviderClient mInfoClient;

    @Before
    public void setup() {
        mInfoClient = new InfoProviderClient(ApplicationProvider.getApplicationContext(),
                "me.ycdev.android.lib.common.provider.InfoProvider");

        // clear dirty data
        mInfoClient.remove(TABLE_TEST, KEY_STR_1);
        mInfoClient.remove(TABLE_TEST, KEY_BOOL_1);
        mInfoClient.remove(TABLE_TEST, KEY_INT_1);
    }

    @Test
    public void getAndPutString() {
        String value = mInfoClient.getString(TABLE_TEST, KEY_STR_1, "");
        assertThat(value).isEqualTo("");

        value = mInfoClient.getString(TABLE_TEST, KEY_STR_1, null);
        assertThat(value).isNull();

        boolean result = mInfoClient.putString(TABLE_TEST, KEY_STR_1, "value_str1");
        assertThat(result).isTrue();

        value = mInfoClient.getString(TABLE_TEST, KEY_STR_1, null);
        assertThat(value).isEqualTo("value_str1");
    }

    @Test
    public void getAndPutBoolean() {
        boolean value = mInfoClient.getBoolean(TABLE_TEST, KEY_BOOL_1, true);
        assertThat(value).isTrue();

        value = mInfoClient.getBoolean(TABLE_TEST, KEY_BOOL_1, false);
        assertThat(value).isFalse();

        boolean result = mInfoClient.putBoolean(TABLE_TEST, KEY_BOOL_1, true);
        assertThat(result).isTrue();

        value = mInfoClient.getBoolean(TABLE_TEST, KEY_BOOL_1, false);
        assertThat(value).isTrue();
    }

    @Test
    public void getAndPutInt() {
        int value = mInfoClient.getInt(TABLE_TEST, KEY_INT_1, 1);
        assertThat(value).isEqualTo(1);

        value = mInfoClient.getInt(TABLE_TEST, KEY_INT_1, 2);
        assertThat(value).isEqualTo(2);

        boolean result = mInfoClient.putInt(TABLE_TEST, KEY_INT_1, 3);
        assertThat(result).isTrue();

        value = mInfoClient.getInt(TABLE_TEST, KEY_INT_1, 1);
        assertThat(value).isEqualTo(3);
    }

    @Test
    public void putAndAddContentObserver() throws InterruptedException {
        String value = mInfoClient.getString(TABLE_TEST, KEY_STR_1, null);
        assertThat(value).isNull();

        final CountDownLatch latch = new CountDownLatch(1);
        ContentObserver observer = new ContentObserver(new Handler(Looper.getMainLooper())) {
            @Override
            public void onChange(boolean selfChange) {
                String value = mInfoClient.getString(TABLE_TEST, KEY_STR_1, null);
                assertThat(value).isEqualTo("value_str1");
                latch.countDown();
            }
        };
        mInfoClient.registerObserver(TABLE_TEST, KEY_STR_1, observer);

        boolean result = mInfoClient.putString(TABLE_TEST, KEY_STR_1, "value_str1");
        assertThat(result).isTrue();

        value = mInfoClient.getString(TABLE_TEST, KEY_STR_1, null);
        assertThat(value).isEqualTo("value_str1");

        latch.await();
        mInfoClient.unregisterObserver(observer);
    }

    @Test
    public void removeAndAddContentObserver() throws InterruptedException {
        boolean result = mInfoClient.putString(TABLE_TEST, KEY_STR_1, "value_str1");
        assertThat(result).isTrue();

        String value = mInfoClient.getString(TABLE_TEST, KEY_STR_1, null);
        assertThat(value).isEqualTo("value_str1");

        final CountDownLatch latch = new CountDownLatch(1);
        ContentObserver observer = new ContentObserver(new Handler(Looper.getMainLooper())) {
            @Override
            public void onChange(boolean selfChange) {
                String value = mInfoClient.getString(TABLE_TEST, KEY_STR_1, null);
                assertThat(value).isNull();
                latch.countDown();
            }
        };
        mInfoClient.registerObserver(TABLE_TEST, KEY_STR_1, observer);

        result = mInfoClient.remove(TABLE_TEST, KEY_STR_1);
        assertThat(result).isTrue();

        value = mInfoClient.getString(TABLE_TEST, KEY_STR_1, null);
        assertThat(value).isNull();

        latch.await();
        mInfoClient.unregisterObserver(observer);
    }

    @Test
    public void putButNoChange() throws InterruptedException {
        String value = mInfoClient.getString(TABLE_TEST, KEY_STR_1, null);
        assertThat(value).isNull();

        final CountDownLatch latch = new CountDownLatch(2);
        ContentObserver observer = new ContentObserver(new Handler(Looper.getMainLooper())) {
            @Override
            public void onChange(boolean selfChange) {
                String value = mInfoClient.getString(TABLE_TEST, KEY_STR_1, null);
                assertThat(value).isEqualTo("value_str1");
                latch.countDown();
            }
        };
        mInfoClient.registerObserver(TABLE_TEST, KEY_STR_1, observer);

        // put 1
        boolean result = mInfoClient.putString(TABLE_TEST, KEY_STR_1, "value_str1");
        assertThat(result).isTrue();

        value = mInfoClient.getString(TABLE_TEST, KEY_STR_1, null);
        assertThat(value).isEqualTo("value_str1");

        // put 2 (but no value change)
        result = mInfoClient.putString(TABLE_TEST, KEY_STR_1, "value_str1");
        assertThat(result).isTrue();

        value = mInfoClient.getString(TABLE_TEST, KEY_STR_1, null);
        assertThat(value).isEqualTo("value_str1");

        latch.await(5, TimeUnit.SECONDS);
        assertThat(latch.getCount()).isEqualTo(1);

        mInfoClient.unregisterObserver(observer);
    }
}
