package me.ycdev.androidlib.root;

import android.content.Context;

/**
 * Used to create commands to be executed in root shell.
 */
public class RootCommandBuilder {
    private RootCommandBuilder() {
        // nothing to do
    }

    public static String[] installPackage(Context cxt, String apkPath) {
        String[] cmds = new String[] {
                "pm install -r \"" + apkPath + "\""
        };
        return cmds;
    }

    public static String[] uninstallPackage(Context cxt, String pkgName) {
        String[] cmds = new String[] {
                "pm uninstall \"" + pkgName + "\""
        };
        return cmds;
    }

    public static String[] rebootDevice(Context cxt) {
        String apkFilepath = cxt.getPackageCodePath();
        String[] cmds = new String[] {
                "export CLASSPATH=" + apkFilepath,
                "/system/bin/app_process /system/bin " + RootJarExecutor.class.getName() + " reboot"
        };
        return cmds;
    }
}
