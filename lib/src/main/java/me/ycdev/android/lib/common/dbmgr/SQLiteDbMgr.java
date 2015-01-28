package me.ycdev.android.lib.common.dbmgr;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;

import me.ycdev.android.lib.common.utils.LibConfigs;
import me.ycdev.android.lib.common.utils.LibLogger;

public class SQLiteDbMgr {
    private static final String TAG = "SQLiteDbMgr";
    private static final boolean DEBUG = LibConfigs.DEBUG_LOG;

    private static class DbInfo {
        SQLiteDatabase db;
        int referenceCount;
    }

    private Context mAppContext;
    private HashMap<Class<? extends SQLiteDbCreator>, DbInfo> mOpenHelpers
            = new HashMap<Class<? extends SQLiteDbCreator>, DbInfo>();

    private static volatile SQLiteDbMgr sInstance;

    private SQLiteDbMgr(Context cxt) {
        mAppContext = cxt.getApplicationContext();
    }

    private static SQLiteDbMgr getInstance(Context cxt) {
        if (sInstance == null) {
            synchronized (SQLiteDbMgr.class) {
                if (sInstance == null) {
                    sInstance = new SQLiteDbMgr(cxt);
                }
            }
        }
        return sInstance;
    }

    private SQLiteDatabase acquireDatabase(Class<? extends SQLiteDbCreator> dbInfoClass) {
        if (DEBUG) LibLogger.d(TAG, "acquire DB: " + dbInfoClass.getName());
        SQLiteDatabase db;
        synchronized (SQLiteDbMgr.class) {
            DbInfo info = mOpenHelpers.get(dbInfoClass);
            if (info == null) {
                try {
                    if (DEBUG) LibLogger.d(TAG, "create DB: " + dbInfoClass.getName());
                    SQLiteDbCreator helper = dbInfoClass.newInstance();
                    info = new DbInfo();
                    info.db = helper.createDb(mAppContext);
                    info.referenceCount = 0;
                    mOpenHelpers.put(dbInfoClass, info);
                } catch (Exception e) {
                    throw new RuntimeException("failed to create SQLiteOpenHelper instance", e);
                }
            }
            info.referenceCount++;
            db = info.db;
        }
        return db;
    }

    private void releaseDatabase(Class<? extends SQLiteDbCreator> dbInfoClass) {
        if (DEBUG) LibLogger.d(TAG, "release DB: " + dbInfoClass.getName());
        synchronized (SQLiteDbMgr.class) {
            DbInfo info = mOpenHelpers.get(dbInfoClass);
            if (info != null) {
                info.referenceCount--;
                if (info.referenceCount == 0) {
                    if (DEBUG) LibLogger.d(TAG, "close DB: " + dbInfoClass.getName());
                    info.db.close();
                    info.db = null;
                    mOpenHelpers.remove(dbInfoClass);
                }
            }
        }
    }

    public static SQLiteDatabase acquireDatabase(Context cxt, Class<? extends SQLiteDbCreator> dbInfoClass) {
        return getInstance(cxt).acquireDatabase(dbInfoClass);
    }

    public static void releaseDatabase(Context cxt, Class<? extends SQLiteDbCreator> dbInfoClass) {
        getInstance(cxt).releaseDatabase(dbInfoClass);
    }
}
