package me.ycdev.android.lib.common.tracker;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import androidx.annotation.NonNull;

import me.ycdev.android.lib.common.utils.LibLogger;
import me.ycdev.android.lib.common.wrapper.BroadcastHelper;
import me.ycdev.android.lib.common.wrapper.IntentHelper;

@SuppressWarnings({"unused", "WeakerAccess"})
public class BatteryInfoTracker extends WeakTracker<BatteryInfoTracker.BatteryInfoListener> {
    private static final String TAG = "BatteryInfoTracker";

    @SuppressLint("StaticFieldLeak")
    private static BatteryInfoTracker sInstance = null;

    public static class BatteryInfo {
        private int level;
        private int scale;
        public int percent;  // percent corrected by us
        public double temperature;
    }

    public interface BatteryInfoListener {
        /**
         * @param newData Read-only, cannot be modified.
         */
        void onBatteryInfoUpdated(BatteryInfo newData);
    }

    private Context mContext;
    private BatteryInfo mBatteryInfo;
    private int mBatteryScale = 100;

    private BroadcastReceiver mBatteryInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LibLogger.i(TAG, "Received: " + intent.getAction());
            updateBatteryInfo(intent);
        }
    };

    public static synchronized BatteryInfoTracker getInsance(Context cxt) {
        if (sInstance == null) {
            sInstance = new BatteryInfoTracker(cxt);
        }
        return sInstance;
    }

    private BatteryInfoTracker(Context cxt) {
        mContext = cxt.getApplicationContext();
    }

    @Override
    protected void startTracker() {
        LibLogger.i(TAG, "BatteryInfo tracker is running");
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        Intent intent = BroadcastHelper.registerForExternal(mContext, mBatteryInfoReceiver, filter);
        if (intent != null) {
            updateBatteryInfo(intent);
        }
    }

    @Override
    protected void stopTracker() {
        LibLogger.i(TAG, "BatteryInfo tracker is stopped");
        mContext.unregisterReceiver(mBatteryInfoReceiver);
    }

    @Override
    protected void onListenerAdded(@NonNull BatteryInfoListener listener) {
        if (mBatteryInfo != null) {
            listener.onBatteryInfoUpdated(mBatteryInfo);
        }
    }

    private void updateBatteryInfo(Intent intent) {
        final BatteryInfo data = new BatteryInfo();
        data.level = IntentHelper.getIntExtra(intent, BatteryManager.EXTRA_LEVEL, 0);
        data.scale = IntentHelper.getIntExtra(intent, BatteryManager.EXTRA_SCALE, 100);
        data.temperature = IntentHelper.getIntExtra(intent, BatteryManager.EXTRA_TEMPERATURE, 0) * 0.1;

        fixData(data);

        int reportedPercent = data.scale < 1 ? data.level : (data.level * 100 / data.scale);
        if (reportedPercent >= 0 && reportedPercent <= 100)
            data.percent = reportedPercent;
        else if (reportedPercent < 0) {
            data.percent = 0;
        } else if (reportedPercent > 100) {
            data.percent = 100;
        }

        LibLogger.d(TAG, "battery info updated, " + dump(data));
        mBatteryInfo = data;
        notifyListeners(listener -> listener.onBatteryInfoUpdated(data));
    }

    private void fixData(BatteryInfo data) {
        // We may need to update 'mBatteryScale'
        if (data.level > data.scale) {
            LibLogger.e(TAG, "Bad battery data! level: %d, scale: %d, mBatteryScale: %d",
                    data.level, data.scale, mBatteryScale);
            if (data.level % 100 == 0) {
                mBatteryScale = data.level;
            }
        }

        // We may need to correct the 'data.scale'
        if (data.scale < mBatteryScale) {
            data.scale = mBatteryScale;
        }
    }

    private static String dump(BatteryInfo data) {
        return "level:" + data.level + ", scale:" + data.scale
                + ", percent: " + data.percent;
    }

}
