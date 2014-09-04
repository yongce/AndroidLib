package me.ycdev.androidlib.root;

import android.content.Context;
import android.os.IBinder;

import java.util.Arrays;

import me.ycdev.androidlib.LibConfigs;
import me.ycdev.androidlib.internalapi.android.os.PowerManagerIA;
import me.ycdev.androidlib.internalapi.android.os.ServiceManagerIA;
import me.ycdev.androidlib.utils.LibLogger;

public class RootJarExecutor {
    private static final String TAG = "RootJarExecutor";
    private static final boolean DEBUG = LibConfigs.DEBUG_LOG;

    private static final String CMD_REBOOT = "reboot";

    private String[] mArgs;

    public static void main(String[] args) {
        if (DEBUG) LibLogger.d(TAG, "Received params: " + Arrays.toString(args));
        if (args.length < 1) {
            if (DEBUG) LibLogger.e(TAG, "Usage: RootJarExecutor <command> [command parameters]");
            return;
        }

        new RootJarExecutor(args).execute();
    }

    private RootJarExecutor(String[] args) {
        mArgs = args;
    }

    private void execute() {
        String cmd = mArgs[0];
        if (cmd.equals(CMD_REBOOT)) {
            String reason = "requested";
            if (mArgs.length > 1) {
                reason = mArgs[1];
            }
            reboot(reason);
        }
    }

    private static void reboot(String reason) {
        IBinder binder = ServiceManagerIA.getService(Context.POWER_SERVICE);
        if (binder != null) {
            Object service = PowerManagerIA.asInterface(binder);
            if (service != null) {
                PowerManagerIA.reboot(service, reason);
            }
        }
    }
}
