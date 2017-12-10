package me.ycdev.android.lib.common.utils;

import android.support.annotation.NonNull;

import com.google.gson.JsonObject;

@SuppressWarnings({"unused", "WeakerAccess"})
public class GsonHelper {
    public static String optString(@NonNull JsonObject json, @NonNull String key, String defValue) {
        if (json.has(key)) {
            return json.get(key).getAsString();
        }
        return defValue;
    }

    public static boolean optBoolean(@NonNull JsonObject json, @NonNull String key, boolean defValue) {
        if (json.has(key)) {
            return json.get(key).getAsBoolean();
        }
        return defValue;
    }

    public static int optInt(@NonNull JsonObject json, @NonNull String key, int defValue) {
        if (json.has(key)) {
            return json.get(key).getAsInt();
        }
        return defValue;
    }

    public static long optLong(@NonNull JsonObject json, @NonNull String key, long defValue) {
        if (json.has(key)) {
            return json.get(key).getAsLong();
        }
        return defValue;
    }

    public static float optFloat(@NonNull JsonObject json, @NonNull String key, float defValue) {
        if (json.has(key)) {
            return json.get(key).getAsFloat();
        }
        return defValue;
    }

    public static double optDouble(@NonNull JsonObject json, @NonNull String key, double defValue) {
        if (json.has(key)) {
            return json.get(key).getAsDouble();
        }
        return defValue;
    }
}
