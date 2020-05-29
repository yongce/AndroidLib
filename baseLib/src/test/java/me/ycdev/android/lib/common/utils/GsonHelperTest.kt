package me.ycdev.android.lib.common.utils

import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.annotations.SerializedName
import org.junit.Test

@SmallTest
class GsonHelperTest {
    private class Foo {
        @SerializedName("name")
        internal var mName: String? = null
        @Transient
        internal var mCache: String? = null
        @SerializedName("done")
        internal var mDone: Boolean = false
        @SerializedName("count")
        internal var mCount: Int = 0
        @SerializedName("time_stamp")
        internal var mTimeStamp: Long = 0
        @SerializedName("radius")
        internal var mRadius: Float = 0.toFloat()
        @SerializedName("distance")
        internal var mDistance: Double = 0.toDouble()
    }

    @Test
    fun testFooDemo() {
        val gson = Gson()
        run {
            val foo = gson.fromJson(FOO_DEMO, Foo::class.java)
            assertThat(foo.mName).isEqualTo("Task1")
            assertThat(foo.mCache).isNull()
            assertThat(foo.mDone).isTrue()
            assertThat(foo.mCount).isEqualTo(11)
            assertThat(foo.mTimeStamp).isEqualTo(1512881633817L)
            assertThat(foo.mRadius).isEqualTo(4.5f)
            assertThat(foo.mDistance).isEqualTo(12345.67)
        }
        run {
            val foo2 = createFooDemo()
            assertThat(foo2.mCache).isEqualTo("cache value")
            assertThat(gson.toJson(foo2)).isEqualTo(FOO_DEMO)
        }
    }

    @Test
    fun optString() {
        val json = JsonParser.parseString(FOO_DEMO).asJsonObject
        assertThat(GsonHelper.optString(json, "name", null)).isEqualTo("Task1")
        assertThat(GsonHelper.optString(json, "not-exist", null)).isNull()
        assertThat(GsonHelper.optString(json, "not-exist", "def")).isEqualTo("def")
    }

    @Test
    fun optBoolean() {
        val json = JsonParser.parseString(FOO_DEMO).asJsonObject
        assertThat(GsonHelper.optBoolean(json, "done", false)).isTrue()
        assertThat(GsonHelper.optBoolean(json, "not-exist", true)).isTrue()
        assertThat(GsonHelper.optBoolean(json, "not-exist", false)).isFalse()
    }

    @Test
    fun optInt() {
        val json = JsonParser.parseString(FOO_DEMO).asJsonObject
        assertThat(GsonHelper.optInt(json, "count", 0)).isEqualTo(11)
        assertThat(GsonHelper.optInt(json, "not-exist", 0)).isEqualTo(0)
        assertThat(GsonHelper.optInt(json, "not-exist", 3)).isEqualTo(3)
    }

    @Test
    fun optLong() {
        val json = JsonParser.parseString(FOO_DEMO).asJsonObject
        assertThat(GsonHelper.optLong(json, "time_stamp", 0L)).isEqualTo(1512881633817L)
        assertThat(GsonHelper.optLong(json, "not-exist", 0L)).isEqualTo(0L)
        assertThat(GsonHelper.optLong(json, "not-exist", 7L)).isEqualTo(7)
    }

    @Test
    fun optFloat() {
        val json = JsonParser.parseString(FOO_DEMO).asJsonObject
        assertThat(GsonHelper.optFloat(json, "radius", 0f)).isEqualTo(4.5f)
        assertThat(GsonHelper.optFloat(json, "not-exist", 0f)).isEqualTo(0f)
        assertThat(GsonHelper.optFloat(json, "not-exist", 3.5f)).isEqualTo(3.5f)
    }

    @Test
    fun optDouble() {
        val json = JsonParser.parseString(FOO_DEMO).asJsonObject
        assertThat(GsonHelper.optDouble(json, "distance", 0.1)).isEqualTo(12345.67)
        assertThat(GsonHelper.optDouble(json, "not-exist", 0.0)).isEqualTo(0.0)
        assertThat(GsonHelper.optDouble(json, "not-exist", 3.7)).isEqualTo(3.7)
    }

    companion object {

        private const val FOO_DEMO =
            "{\"name\":\"Task1\",\"done\":true,\"count\":11," + "\"time_stamp\":1512881633817,\"radius\":4.5,\"distance\":12345.67}"

        private fun createFooDemo(): Foo {
            val foo = Foo()
            foo.mName = "Task1"
            foo.mCache = "cache value"
            foo.mDone = true
            foo.mCount = 11
            foo.mTimeStamp = 1512881633817L
            foo.mRadius = 4.5f
            foo.mDistance = 12345.67
            return foo
        }
    }
}
