package me.ycdev.android.lib.common.utils

import com.google.gson.JsonObject

object GsonHelper {
    fun optString(
        json: JsonObject,
        key: String,
        defValue: String?
    ): String? = if (json.has(key)) {
        json.get(key).asString
    } else {
        defValue
    }

    fun optBoolean(
        json: JsonObject,
        key: String,
        defValue: Boolean
    ): Boolean = if (json.has(key)) {
        json.get(key).asBoolean
    } else {
        defValue
    }

    fun optInt(
        json: JsonObject,
        key: String,
        defValue: Int
    ): Int = if (json.has(key)) {
        json.get(key).asInt
    } else {
        defValue
    }

    fun optLong(
        json: JsonObject,
        key: String,
        defValue: Long
    ): Long = if (json.has(key)) {
        json.get(key).asLong
    } else {
        defValue
    }

    fun optFloat(
        json: JsonObject,
        key: String,
        defValue: Float
    ): Float = if (json.has(key)) {
        json.get(key).asFloat
    } else {
        defValue
    }

    fun optDouble(
        json: JsonObject,
        key: String,
        defValue: Double
    ): Double = if (json.has(key)) {
        json.get(key).asDouble
    } else {
        defValue
    }
}
