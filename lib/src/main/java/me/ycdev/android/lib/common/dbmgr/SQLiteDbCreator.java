package me.ycdev.android.lib.common.dbmgr;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

public interface SQLiteDbCreator {
    SQLiteDatabase createDb(@NonNull Context cxt);
}
