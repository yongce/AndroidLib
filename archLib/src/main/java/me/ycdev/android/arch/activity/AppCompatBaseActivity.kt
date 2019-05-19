package me.ycdev.android.arch.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Base class for Activity which wants to inherit
 * [androidx.appcompat.app.AppCompatActivity].
 */
abstract class AppCompatBaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (shouldSetDisplayHomeAsUpEnabled()) {
            val actionBar = supportActionBar
            actionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    protected open fun shouldSetDisplayHomeAsUpEnabled(): Boolean {
        return true
    }
}
