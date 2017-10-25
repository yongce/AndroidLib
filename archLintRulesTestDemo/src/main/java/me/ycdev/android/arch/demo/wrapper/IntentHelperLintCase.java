package me.ycdev.android.arch.demo.wrapper;

import android.content.Intent;
import android.os.Bundle;

import me.ycdev.android.lib.common.wrapper.IntentHelper;

public class IntentHelperLintCase {
    private static class Foo {
        public void hasExtra() { // lint good
        }

        public void getBundleExtra() { // lint good
        }
    }

    public static void hasExtra() { // lint good
        new Foo().hasExtra();
    }

    public static void getBundleExtra() { // lint good
        new Foo().getBundleExtra();
    }

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
