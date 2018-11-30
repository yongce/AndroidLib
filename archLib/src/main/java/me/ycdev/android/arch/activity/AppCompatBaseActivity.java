package me.ycdev.android.arch.activity;

import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Base class for Activity which wants to inherit
 * {@link androidx.appcompat.app.AppCompatActivity}.
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
