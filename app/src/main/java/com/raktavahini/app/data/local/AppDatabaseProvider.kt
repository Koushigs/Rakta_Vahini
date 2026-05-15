package com.raktavahini.app.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object AppDatabaseProvider {
    private const val DATABASE_NAME = "rakta_vahini.db"

    @Volatile
    private var instance: AppDatabase? = null

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `logged_in_users` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `first_name` TEXT NOT NULL,
                    `last_name` TEXT NOT NULL,
                    `blood_group` TEXT NOT NULL,
                    `date_of_birth` TEXT NOT NULL,
                    `current_location` TEXT NOT NULL,
                    `phone_number` TEXT NOT NULL,
                    `email` TEXT NOT NULL,
                    `is_current_session` INTEGER NOT NULL,
                    `updated_at_epoch_millis` INTEGER NOT NULL
                )
                """.trimIndent()
            )
        }
    }

    fun get(context: Context): AppDatabase {
        return instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                .addMigrations(MIGRATION_1_2)
                .build()
                .also { instance = it }
        }
    }
}