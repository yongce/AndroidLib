package me.ycdev.android.arch.demo.wrapper

import android.content.Intent
import android.os.Bundle
import me.ycdev.android.lib.common.wrapper.IntentHelper

object IntentHelperLintCase {
    private class Foo {
        fun hasExtra() { // lint good
        }

        fun getBundleExtra() { // lint good
        }
    }

    fun hasExtra() { // lint good
        Foo().hasExtra()
    }

    fun getBundleExtra() { // lint good
        Foo().getBundleExtra()
    }

    fun hasExtraGood(intent: Intent, key: String): Boolean {
        return IntentHelper.hasExtra(intent, key) // lint good
    }

    fun getBooleanExtraGood(intent: Intent, key: String, defValue: Boolean): Boolean {
        return IntentHelper.getBooleanExtra(intent, key, defValue) // lint good
    }

    fun getBundleExtraGood(intent: Intent, key: String): Bundle? {
        return IntentHelper.getBundleExtra(intent, key) // lint good
    }

    fun hasExtraBad(intent: Intent, key: String): Boolean {
        return intent.hasExtra(key) // lint violation
    }

    fun getBooleanExtraBad(intent: Intent, key: String, defValue: Boolean): Boolean {
        return intent.getBooleanExtra(key, defValue) // lint violation
    }

    fun getBundleExtraBad(intent: Intent, key: String): Bundle {
        return intent.getBundleExtra(key) // lint violation
    }
}
