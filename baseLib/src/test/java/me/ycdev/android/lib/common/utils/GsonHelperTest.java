package me.ycdev.android.lib.common.utils;

import androidx.test.filters.SmallTest;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@SmallTest
public class GsonHelperTest {
    private static class Foo {
        @SerializedName("name")
        String mName;
        transient String mCache;
        @SerializedName("done")
        boolean mDone;
        @SerializedName("count")
        int mCount;
        @SerializedName("time_stamp")
        long mTimeStamp;
        @SerializedName("radius")
        float mRadius;
        @SerializedName("distance")
        double mDistance;
    }

    private static final String FOO_DEMO = "{\"name\":\"Task1\",\"done\":true,\"count\":11," +
            "\"time_stamp\":1512881633817,\"radius\":4.5,\"distance\":12345.67}";

    private static Foo createFooDemo() {
        Foo foo = new Foo();
        foo.mName = "Task1";
        foo.mCache = "cache value";
        foo.mDone = true;
        foo.mCount = 11;
        foo.mTimeStamp = 1512881633817L;
        foo.mRadius = 4.5F;
        foo.mDistance = 12345.67;
        return foo;
    }

    @Test
    public void testFooDemo() {
        Gson gson = new Gson();
        {
            Foo foo = gson.fromJson(FOO_DEMO, Foo.class);
            assertThat(foo.mName, equalTo("Task1"));
            assertThat(foo.mCache, equalTo(null));
            assertThat(foo.mDone, equalTo(true));
            assertThat(foo.mCount, equalTo(11));
            assertThat(foo.mTimeStamp, equalTo(1512881633817L));
            assertThat(foo.mRadius, equalTo(4.5F));
            assertThat(foo.mDistance, equalTo(12345.67));
        }
        {
            Foo foo2 = createFooDemo();
            assertThat(foo2.mCache, equalTo("cache value"));
            assertThat(gson.toJson(foo2), equalTo(FOO_DEMO));
        }
    }

    @Test
    public void optString() {
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(FOO_DEMO).getAsJsonObject();
        assertThat(GsonHelper.optString(json, "name", null), equalTo("Task1"));
        assertThat(GsonHelper.optString(json, "not-exist", null), equalTo(null));
        assertThat(GsonHelper.optString(json, "not-exist", "def"), equalTo("def"));
    }

    @Test
    public void optBoolean() {
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(FOO_DEMO).getAsJsonObject();
        assertThat(GsonHelper.optBoolean(json, "done", false), equalTo(true));
        assertThat(GsonHelper.optBoolean(json, "not-exist", true), equalTo(true));
        assertThat(GsonHelper.optBoolean(json, "not-exist", false), equalTo(false));
    }

    @Test
    public void optInt() {
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(FOO_DEMO).getAsJsonObject();
        assertThat(GsonHelper.optInt(json, "count", 0), equalTo(11));
        assertThat(GsonHelper.optInt(json, "not-exist", 0), equalTo(0));
        assertThat(GsonHelper.optInt(json, "not-exist", 3), equalTo(3));
    }

    @Test
    public void optLong() {
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(FOO_DEMO).getAsJsonObject();
        assertThat(GsonHelper.optLong(json, "time_stamp", 0L), equalTo(1512881633817L));
        assertThat(GsonHelper.optLong(json, "not-exist", 0L), equalTo(0L));
        assertThat(GsonHelper.optLong(json, "not-exist", 7L), equalTo(7L));
    }

    @Test
    public void optFloat() {
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(FOO_DEMO).getAsJsonObject();
        assertThat(GsonHelper.optFloat(json, "radius", 0F), equalTo(4.5F));
        assertThat(GsonHelper.optFloat(json, "not-exist", 0F), equalTo(0F));
        assertThat(GsonHelper.optFloat(json, "not-exist", 3.5F), equalTo(3.5F));
    }

    @Test
    public void optDouble() {
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(FOO_DEMO).getAsJsonObject();
        assertThat(GsonHelper.optDouble(json, "distance", 0.1), equalTo(12345.67));
        assertThat(GsonHelper.optDouble(json, "not-exist", 0.0), equalTo(0.0));
        assertThat(GsonHelper.optDouble(json, "not-exist", 3.7), equalTo(3.7));
    }
}
