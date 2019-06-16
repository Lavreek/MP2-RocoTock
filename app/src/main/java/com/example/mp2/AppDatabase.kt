package com.example.mp2

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.example.mp2.Daos.GoalDao
import com.example.mp2.Daos.NoteDao
import com.example.mp2.Daos.TaskDao
import com.example.mp2.Entityes.EntityGoal
import com.example.mp2.Entityes.EntityNotes
import com.example.mp2.Entityes.EntityTasks


@Database(entities = arrayOf(EntityTasks::class, EntityGoal::class, EntityNotes::class), version = 1)

abstract class AppDatabase : RoomDatabase() {

    abstract fun employeeDao(): TaskDao
    abstract fun goalDao() : GoalDao
    abstract fun notesDao() : NoteDao

    companion object {
        var INSTANCE: AppDatabase? = null

        fun getAppDataBase(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class)
                {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        AppDatabase::class.java,
                        "myDB30").build() //myDB
                }
            }
            return INSTANCE
        }

        fun destroyDataBase() {
            INSTANCE = null
        }
    }
}