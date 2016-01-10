package me.ycdev.android.arch.demo.wrapper;

import android.content.Intent;
import android.os.Bundle;

import me.ycdev.android.arch.wrapper.IntentHelper;

public class IntentHelperLintCase {
    public static boolean hasExtraGood(Intent intent, String key) {
        return IntentHelper.hasExtra(intent, key); // lint good
    }

    public static boolean getBooleanExtraGood(Intent intent, String key, boolean defValue) {
        return IntentHelper.getBooleanExtra(intent, key, defValue); // lint good
    }

    public static Bundle getBundleExtraGood(Intent intent, String key) {
        return IntentHelper.getBundleExtra(intent, key); // lint good
    }

    public static boolean hasExtraBad(Intent intent, String key) {
        return intent.hasExtra(key); // lint violation
    }

    public static boolean getBooleanExtraBad(Intent intent, String key, boolean defValue) {
        return intent.getBooleanExtra(key, defValue); // lint violation
    }

    public static Bundle getBundleExtraBad(Intent intent, String key) {
        return intent.getBundleExtra(key); // lint violation
    }

}
