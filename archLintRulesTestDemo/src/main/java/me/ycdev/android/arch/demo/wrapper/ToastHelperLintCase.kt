package me.ycdev.android.arch.demo.wrapper

import android.content.Context
import android.widget.Toast
import me.ycdev.android.arch.wrapper.ToastHelper

object ToastHelperLintCase {
    private class Foo {
        fun show() { // lint good
        }

        fun makeText() { // lint good
        }
    }

    fun show() { // lint good
        Foo().show()
    }

    fun makeText() { // lint good
        Foo().makeText()
    }

    fun showGood(cxt: Context, msgResId: Int, duration: Int) {
        ToastHelper.show(cxt, msgResId, duration) // lint good
    }

    fun showGood(cxt: Context, msg: CharSequence, duration: Int) {
        ToastHelper.show(cxt, msg, duration) // lint good
    }

    fun showViolation(cxt: Context, msgResId: Int, duration: Int) {
        Toast.makeText(cxt, msgResId, duration).show() // lint violation
    }

    fun showViolation(cxt: Context, msg: CharSequence, duration: Int) {
        Toast.makeText(cxt, msg, duration).show() // lint violation
    }
}
