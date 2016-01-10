package me.ycdev.android.arch.demo.activity;

import android.os.Bundle;

import me.ycdev.android.arch.activity.BaseActivity;
import me.ycdev.android.arch.demo.R;


public class LintGood2Activity extends BaseActivity { // lint good

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lint_good2);
    }
}
