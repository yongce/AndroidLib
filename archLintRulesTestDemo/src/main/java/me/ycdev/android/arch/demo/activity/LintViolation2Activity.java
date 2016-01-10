package me.ycdev.android.arch.demo.activity;

import android.app.Activity;
import android.os.Bundle;

import me.ycdev.android.arch.demo.R;

// class comment for test
public class LintViolation2Activity extends Activity { // lint violation
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lint_violation2);
    }
}
