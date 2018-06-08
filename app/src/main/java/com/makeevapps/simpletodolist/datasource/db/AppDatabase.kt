package com.makeevapps.simpletodolist.datasource.db

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.arch.persistence.room.migration.Migration
import android.content.Context
import com.makeevapps.simpletodolist.datasource.db.converter.DateConverter
import com.makeevapps.simpletodolist.datasource.db.converter.PriorityConverter
import com.makeevapps.simpletodolist.datasource.db.table.Task

@TypeConverters(DateConverter::class, PriorityConverter::class)
@Database(entities = arrayOf(Task::class), version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext,
                        AppDatabase::class.java, "simple_todo_list.db")
                        .fallbackToDestructiveMigration()
                        //.addMigrations(MIGRATION_1_2)
                        .build()

        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE task ADD COLUMN statusId INTEGER")
            }
        }
    }

}