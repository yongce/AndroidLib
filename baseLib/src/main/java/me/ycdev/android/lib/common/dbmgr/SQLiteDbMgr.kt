package me.ycdev.android.lib.common.dbmgr

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import me.ycdev.android.lib.common.pattern.SingletonHolderP1
import timber.log.Timber
import java.util.HashMap

@Suppress("unused")
class SQLiteDbMgr private constructor(cxt: Context) {

    private val mAppContext: Context = cxt.applicationContext
    private val mOpenHelpers = HashMap<Class<out SQLiteDbCreator>, DbInfo>()

    private class DbInfo {
        internal var db: SQLiteDatabase? = null
        internal var referenceCount: Int = 0
    }

    private fun acquireDatabase(dbInfoClass: Class<out SQLiteDbCreator>): SQLiteDatabase? {
        Timber.tag(TAG).d("acquire DB: %s", dbInfoClass.name)
        val db: SQLiteDatabase?
        synchronized(SQLiteDbMgr::class.java) {
            var info = mOpenHelpers[dbInfoClass]
            if (info == null) {
                try {
                    Timber.tag(TAG).d("create DB: %s", dbInfoClass.name)
                    val helper = dbInfoClass.newInstance()
                    info = DbInfo()
                    info.db = helper.createDb(mAppContext)
                    info.referenceCount = 0
                    mOpenHelpers[dbInfoClass] = info
                } catch (e: Exception) {
                    throw RuntimeException("failed to create SQLiteOpenHelper instance", e)
                }
            }
            info.referenceCount++
            db = info.db
        }
        return db
    }

    private fun releaseDatabase(dbInfoClass: Class<out SQLiteDbCreator>) {
        Timber.tag(TAG).d("release DB: %s", dbInfoClass.name)
        synchronized(SQLiteDbMgr::class.java) {
            val info = mOpenHelpers[dbInfoClass]
            if (info != null) {
                info.referenceCount--
                if (info.referenceCount == 0) {
                    Timber.tag(TAG).d("close DB: %s", dbInfoClass.name)
                    info.db!!.close()
                    info.db = null
                    mOpenHelpers.remove(dbInfoClass)
                }
            }
        }
    }

    companion object : SingletonHolderP1<SQLiteDbMgr, Context>(::SQLiteDbMgr) {
        private const val TAG = "SQLiteDbMgr"

        fun acquireDatabase(
            cxt: Context,
            dbInfoClass: Class<out SQLiteDbCreator>
        ): SQLiteDatabase? {
            return getInstance(cxt).acquireDatabase(dbInfoClass)
        }

        fun releaseDatabase(
            cxt: Context,
            dbInfoClass: Class<out SQLiteDbCreator>
        ) {
            getInstance(cxt).releaseDatabase(dbInfoClass)
        }
    }
}
