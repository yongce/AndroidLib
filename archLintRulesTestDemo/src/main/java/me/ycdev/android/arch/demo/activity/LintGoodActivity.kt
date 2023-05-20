package me.ycdev.android.arch.demo.activity

import android.view.Menu
import android.view.MenuItem
import me.ycdev.android.arch.activity.AppCompatBaseActivity

class LintGoodActivity : AppCompatBaseActivity() { // lint good

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        item.itemId

        return super.onOptionsItemSelected(item)
    }
}
