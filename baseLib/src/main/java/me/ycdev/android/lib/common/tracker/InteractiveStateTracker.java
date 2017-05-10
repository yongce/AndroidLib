package me.ycdev.android.lib.common.tracker;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.support.annotation.NonNull;

import me.ycdev.android.lib.common.compat.PowerManagerCompat;
import me.ycdev.android.lib.common.utils.LibLogger;
import me.ycdev.android.lib.common.wrapper.BroadcastHelper;

/**
 * A tracker to track the interactive state of the device.
 */
public class InteractiveStateTracker extends WeakTracker<InteractiveStateTracker.InteractiveStateListener> {
    private static final String TAG = "InteractiveStateTracker";

    public interface InteractiveStateListener {
        /**
         * Will be invoked when Intent.ACTION_SCREEN_ON or Intent.ACTION_SCREEN_OFF received.
         */
        void onInteractiveChanged(boolean interactive);

        /**
         * Will be invoked when Intent.ACTION_USER_PRESENT received.
         */
        void onUserPresent();
    }

    private Context mAppContext;
    private boolean mInteractive;
    private boolean mNeedRefreshInteractiveState;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LibLogger.i(TAG, "Received: " + action);
            if (action.equals(Intent.ACTION_USER_PRESENT)) {
                notifyUserPresent();
            } else {
                mInteractive = action.equals(Intent.ACTION_SCREEN_ON);
                notifyInteractiveChanged(mInteractive);
            }
        }
    };

    @SuppressLint("StaticFieldLeak")
    private static volatile InteractiveStateTracker sInstance;

    private InteractiveStateTracker(Context cxt) {
        mAppContext = cxt.getApplicationContext();
    }

    public static InteractiveStateTracker getInstance(Context cxt) {
        if (sInstance == null) {
            synchronized (InteractiveStateTracker.class) {
                if (sInstance == null) {
                    sInstance = new InteractiveStateTracker(cxt);
                }
            }
        }
        return sInstance;
    }

    public boolean isInteractive() {
        if (mNeedRefreshInteractiveState) {
            refreshInteractiveState();
        }
        return mInteractive;
    }

    private void refreshInteractiveState() {
        PowerManager pm = (PowerManager) mAppContext.getSystemService(Context.POWER_SERVICE);
        mInteractive = PowerManagerCompat.isScreenOn(pm);
    }

    @Override
    protected void startTracker() {
        LibLogger.i(TAG, "Screen on/off tracker is running");
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        BroadcastHelper.registerForExternal(mAppContext, mReceiver, filter);

        refreshInteractiveState();
        mNeedRefreshInteractiveState = false;
    }

    @Override
    protected void stopTracker() {
        LibLogger.i(TAG, "Screen on/off tracker is stopped");
        mAppContext.unregisterReceiver(mReceiver);
        mNeedRefreshInteractiveState = true;
    }

    @Override
    protected void onListenerAdded(@NonNull InteractiveStateListener listener) {
        listener.onInteractiveChanged(mInteractive);
    }

    private void notifyInteractiveChanged(final boolean interactive) {
        notifyListeners(new NotifyAction<InteractiveStateListener>() {
            @Override
            public void notify(InteractiveStateListener listener) {
                listener.onInteractiveChanged(interactive);
            }
        });
    }

    private void notifyUserPresent() {
        notifyListeners(new NotifyAction<InteractiveStateListener>() {
            @Override
            public void notify(InteractiveStateListener listener) {
                listener.onUserPresent();
            }
        });
    }
}
