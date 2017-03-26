package me.ycdev.android.lib.common.tracker;

import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import me.ycdev.android.lib.common.utils.LibLogger;

public abstract class WeakTracker<IListener> {
    private static final String TAG = "WeakTracker";

    protected abstract class NotifyAction {
        protected abstract void notify(IListener listener);
    }

    private class ListenerInfo {
        String className;
        WeakReference<IListener> holder;

        ListenerInfo(IListener listener) {
            className = listener.getClass().getName();
            holder = new WeakReference<>(listener);
        }
    }

    private final List<ListenerInfo> mListeners = new ArrayList<>();

    protected abstract void startTracker();

    protected abstract void stopTracker();

    /**
     * Override this method to notify the listener when registered.
     */
    protected void onListenerRegistered(@NonNull IListener listener) {
        // nothing to do
    }

    public void registerListener(@NonNull IListener listener) {
        synchronized (mListeners) {
            if (mListeners.size() == 0) {
                startTracker();
            }

            for (ListenerInfo l : mListeners) {
                if (l.holder.get() == listener) return; // skip duplicate listeners
            }
            mListeners.add(new ListenerInfo(listener));
        }

        // Notify the listener to get initialized
        onListenerRegistered(listener);
    }

    public void unregisterListener(@NonNull IListener listener) {
        synchronized (mListeners) {
            final int N = mListeners.size();
            boolean removed = false;
            for (int i = 0; i < N; i++) {
                ListenerInfo listenerInfo = mListeners.get(i);
                if (listenerInfo.holder.get() == listener) {
                    mListeners.remove(i);
                    removed = true;
                    break;
                }
            }
            if (mListeners.size() == 0 && removed) {
                stopTracker();
            }
        }
    }

    protected void notifyListeners(@NonNull NotifyAction action) {
        synchronized (mListeners) {
            for (int i = 0; i < mListeners.size();) {
                ListenerInfo listenerInfo = mListeners.get(i);
                IListener l = listenerInfo.holder.get();
                if (l == null) {
                    LibLogger.e(TAG, "listener leak found: " + listenerInfo.className);
                    mListeners.remove(i);
                } else {
                    LibLogger.d(TAG, "notify: " + listenerInfo.className);
                    action.notify(l);
                    i++;
                }
            }
            LibLogger.d(TAG, "notify done, cur size: " + mListeners.size());
        }
    }

}
