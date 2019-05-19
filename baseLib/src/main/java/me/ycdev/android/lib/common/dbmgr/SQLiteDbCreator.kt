package me.ycdev.android.lib.common.dbmgr

import android.content.Context
import android.database.sqlite.SQLiteDatabase

interface SQLiteDbCreator {
    fun createDb(cxt: Context): SQLiteDatabase
}
