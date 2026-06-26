package me.ycdev.android.lib.common.dbmgr

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.google.common.truth.Truth.assertThat
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class SQLiteDbMgrTest {
    @Test
    fun acquireDatabase_reusesOpenDatabaseUntilAllReferencesReleased() {
        CountingCreator.createCount = 0
        val context = RuntimeEnvironment.getApplication()

        val first = SQLiteDbMgr.acquireDatabase(context, CountingCreator::class.java)
        val second = SQLiteDbMgr.acquireDatabase(context, CountingCreator::class.java)

        assertThat(first).isSameInstanceAs(second)
        assertThat(CountingCreator.createCount).isEqualTo(1)

        SQLiteDbMgr.releaseDatabase(context, CountingCreator::class.java)
        assertThat(first!!.isOpen).isTrue()

        SQLiteDbMgr.releaseDatabase(context, CountingCreator::class.java)
        assertThat(first.isOpen).isFalse()
    }

    @Test
    fun releaseDatabase_ignoresMissingOrAlreadyClosedCreator() {
        val context = RuntimeEnvironment.getApplication()

        SQLiteDbMgr.releaseDatabase(context, UnusedCreator::class.java)
        SQLiteDbMgr.releaseDatabase(context, UnusedCreator::class.java)
    }

    @Test
    fun acquireDatabase_afterFinalReleaseCreatesANewDatabase() {
        CountingCreator.createCount = 0
        val context = RuntimeEnvironment.getApplication()

        val first = SQLiteDbMgr.acquireDatabase(context, CountingCreator::class.java)
        SQLiteDbMgr.releaseDatabase(context, CountingCreator::class.java)

        val second = SQLiteDbMgr.acquireDatabase(context, CountingCreator::class.java)

        assertThat(first).isNotSameInstanceAs(second)
        assertThat(CountingCreator.createCount).isEqualTo(2)

        SQLiteDbMgr.releaseDatabase(context, CountingCreator::class.java)
    }

    @Test
    fun acquireDatabase_wrapsCreatorFailure() {
        val context = RuntimeEnvironment.getApplication()

        val error =
            assertThrows(RuntimeException::class.java) {
                SQLiteDbMgr.acquireDatabase(context, FailingCreator::class.java)
            }

        assertThat(error).hasMessageThat().isEqualTo("failed to create SQLiteOpenHelper instance")
        assertThat(error).hasCauseThat().isInstanceOf(IllegalStateException::class.java)
    }

    class CountingCreator : SQLiteDbCreator {
        override fun createDb(cxt: Context): SQLiteDatabase {
            createCount++
            return SQLiteDatabase.create(null)
        }

        companion object {
            var createCount = 0
        }
    }

    class UnusedCreator : SQLiteDbCreator {
        override fun createDb(cxt: Context): SQLiteDatabase = SQLiteDatabase.create(null)
    }

    class FailingCreator : SQLiteDbCreator {
        override fun createDb(cxt: Context): SQLiteDatabase = throw IllegalStateException("boom")
    }
}
