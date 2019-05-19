package me.ycdev.android.lib.commonjni.demo

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import me.ycdev.android.lib.commonjni.SysResourceLimitHelper

class ResourceLimitActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var ofLimitStatusView: TextView
    private lateinit var increaseOflimitBtn: Button
    private lateinit var decreaseOflimitBtn: Button

    private lateinit var curOflimit: SysResourceLimitHelper.LimitInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resource_limit)

        ofLimitStatusView = findViewById<View>(R.id.oflimit_status) as TextView
        increaseOflimitBtn = findViewById<View>(R.id.oflimit_increase) as Button
        increaseOflimitBtn.setOnClickListener(this)
        decreaseOflimitBtn = findViewById<View>(R.id.oflimit_decrease) as Button
        decreaseOflimitBtn.setOnClickListener(this)

        refreshOflimitStatus()
    }

    private fun refreshOflimitStatus() {
        curOflimit = SysResourceLimitHelper.getOpenFilesLimit()
        val status =
            getString(R.string.oflimit_status, curOflimit.curLimit, curOflimit.maxLimit)
        ofLimitStatusView.text = status
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_resource_limit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View) {
        if (v === increaseOflimitBtn) {
            if (curOflimit.curLimit == 0) {
                curOflimit.curLimit = 1
            }
            if (!SysResourceLimitHelper.setOpenFilesLimit(curOflimit.curLimit * 2)) {
                Toast.makeText(this, "failed to set limit", Toast.LENGTH_SHORT).show()
            }
            refreshOflimitStatus()
        } else if (v === decreaseOflimitBtn) {
            if (!SysResourceLimitHelper.setOpenFilesLimit(curOflimit.curLimit / 2)) {
                Toast.makeText(this, "failed to set limit", Toast.LENGTH_SHORT).show()
            }
            refreshOflimitStatus()
        }
    }
}
