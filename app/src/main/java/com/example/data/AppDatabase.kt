package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

@Database(
    entities = [Personnel::class, LeaveRecord::class],
    version = 9,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun personnelDao(): PersonnelDao
    abstract fun leaveRecordDao(): LeaveRecordDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private fun hasColumn(
            db: SupportSQLiteDatabase,
            tableName: String,
            columnName: String
        ): Boolean {
            db.query("PRAGMA table_info($tableName)").use { cursor ->
                val nameIndex = cursor.getColumnIndex("name")
                if (nameIndex < 0) return false
                while (cursor.moveToNext()) {
                    if (cursor.getString(nameIndex).equals(columnName, ignoreCase = true)) {
                        return true
                    }
                }
            }
            return false
        }

        private fun addColumnIfMissing(
            db: SupportSQLiteDatabase,
            tableName: String,
            columnName: String,
            columnDefinition: String
        ) {
            if (!hasColumn(db, tableName, columnName)) {
                db.execSQL("ALTER TABLE $tableName ADD COLUMN $columnName $columnDefinition")
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                addColumnIfMissing(db, "personnel", "addedAtMillis", "INTEGER NOT NULL DEFAULT 0")
                val now = System.currentTimeMillis()
                db.execSQL("UPDATE personnel SET addedAtMillis = $now WHERE addedAtMillis = 0")
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                addColumnIfMissing(db, "personnel", "isPinned", "INTEGER NOT NULL DEFAULT 0")
                addColumnIfMissing(db, "personnel", "pinnedAtMillis", "INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                addColumnIfMissing(db, "personnel", "isDeleted", "INTEGER NOT NULL DEFAULT 0")
                addColumnIfMissing(db, "personnel", "deletedAtMillis", "INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                addColumnIfMissing(db, "personnel", "fullNameLatin", "TEXT NOT NULL DEFAULT ''")
                addColumnIfMissing(db, "leave_records", "personnelNameLatin", "TEXT NOT NULL DEFAULT ''")
            }
        }

        val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                addColumnIfMissing(db, "personnel", "addedAtMillis", "INTEGER NOT NULL DEFAULT 0")
                addColumnIfMissing(db, "personnel", "isPinned", "INTEGER NOT NULL DEFAULT 0")
                addColumnIfMissing(db, "personnel", "pinnedAtMillis", "INTEGER NOT NULL DEFAULT 0")
                addColumnIfMissing(db, "personnel", "isDeleted", "INTEGER NOT NULL DEFAULT 0")
                addColumnIfMissing(db, "personnel", "deletedAtMillis", "INTEGER NOT NULL DEFAULT 0")
                addColumnIfMissing(db, "personnel", "fullNameLatin", "TEXT NOT NULL DEFAULT ''")
                addColumnIfMissing(db, "leave_records", "personnelNameLatin", "TEXT NOT NULL DEFAULT ''")
            }
        }

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "personnel_leaves_db"
                )
                    .addMigrations(MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
