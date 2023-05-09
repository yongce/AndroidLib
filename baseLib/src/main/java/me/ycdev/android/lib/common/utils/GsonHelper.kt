package me.ycdev.android.lib.common.utils

import com.google.gson.JsonObject

object GsonHelper {
    fun optString(json: JsonObject, key: String, defValue: String?): String? {
        return if (json.has(key)) {
            json.get(key).asString
        } else {
            defValue
        }
    }

    fun optBoolean(json: JsonObject, key: String, defValue: Boolean): Boolean {
        return if (json.has(key)) {
            json.get(key).asBoolean
        } else {
            defValue
        }
    }

    fun optInt(json: JsonObject, key: String, defValue: Int): Int {
        return if (json.has(key)) {
            json.get(key).asInt
        } else {
            defValue
        }
    }

    fun optLong(json: JsonObject, key: String, defValue: Long): Long {
        return if (json.has(key)) {
            json.get(key).asLong
        } else {
            defValue
        }
    }

    fun optFloat(json: JsonObject, key: String, defValue: Float): Float {
        return if (json.has(key)) {
            json.get(key).asFloat
        } else {
            defValue
        }
    }

    fun optDouble(json: JsonObject, key: String, defValue: Double): Double {
        return if (json.has(key)) {
            json.get(key).asDouble
        } else {
            defValue
        }
    }
}
