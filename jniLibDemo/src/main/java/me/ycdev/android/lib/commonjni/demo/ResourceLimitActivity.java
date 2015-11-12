package me.ycdev.android.lib.commonjni.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import me.ycdev.android.lib.commonjni.SysResourceLimitHelper;

public class ResourceLimitActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView mOflimitStatusView;
    private Button mIncreaseOflimitBtn;
    private Button mDecreaseOflimitBtn;

    private SysResourceLimitHelper.LimitInfo mCurOflimit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource_limit);

        mOflimitStatusView = (TextView) findViewById(R.id.oflimit_status);
        mIncreaseOflimitBtn = (Button) findViewById(R.id.oflimit_increase);
        mIncreaseOflimitBtn.setOnClickListener(this);
        mDecreaseOflimitBtn = (Button) findViewById(R.id.oflimit_decrease);
        mDecreaseOflimitBtn.setOnClickListener(this);

        refreshOflimitStatus();
    }

    private void refreshOflimitStatus() {
        mCurOflimit = SysResourceLimitHelper.getOpenFilesLimit();
        String status = getString(R.string.oflimit_status, mCurOflimit.curLimit, mCurOflimit.maxLimit);
        mOflimitStatusView.setText(status);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_resource_limit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == mIncreaseOflimitBtn) {
            if (mCurOflimit.curLimit == 0) {
                mCurOflimit.curLimit = 1;
            }
            if (!SysResourceLimitHelper.setOpenFilesLimit(mCurOflimit.curLimit * 2)) {
                Toast.makeText(this, "failed to set limit", Toast.LENGTH_SHORT).show();
            }
            refreshOflimitStatus();
        } else if (v == mDecreaseOflimitBtn) {
            if (!SysResourceLimitHelper.setOpenFilesLimit(mCurOflimit.curLimit / 2)) {
                Toast.makeText(this, "failed to set limit", Toast.LENGTH_SHORT).show();
            }
            refreshOflimitStatus();
        }
    }
}
