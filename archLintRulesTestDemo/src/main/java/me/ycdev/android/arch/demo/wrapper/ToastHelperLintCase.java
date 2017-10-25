package me.ycdev.android.arch.demo.wrapper;

import android.content.Context;
import android.widget.Toast;

import me.ycdev.android.arch.wrapper.ToastHelper;

public class ToastHelperLintCase {
    private static class Foo {
        public void show() { // lint good
        }

        public void makeText() { // lint good
        }
    }

    public static void show() { // lint good
        new Foo().show();
    }

    public static void makeText() { // lint good
        new Foo().makeText();
    }

    public static void showGood(Context cxt, int msgResId, int duration) {
        ToastHelper.show(cxt, msgResId, duration); // lint good
    }

    public static void showGood(Context cxt, CharSequence msg, int duration) {
        ToastHelper.show(cxt, msg, duration); // lint good
    }

    public static void showViolation(Context cxt, int msgResId, int duration) {
        Toast.makeText(cxt, msgResId, duration).show(); // lint violation
    }

    public static void showViolation(Context cxt, CharSequence msg, int duration) {
        Toast.makeText(cxt, msg, duration).show(); // lint violation
    }
}
