package me.ycdev.android.arch.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

/**
 * Base class for Activity which wants to inherit
 * {@link android.support.v7.app.AppCompatActivity}.
 */
public abstract class AppCompatBaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (shouldSetDisplayHomeAsUpEnabled()) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    protected boolean shouldSetDisplayHomeAsUpEnabled() {
        return true;
    }
}
