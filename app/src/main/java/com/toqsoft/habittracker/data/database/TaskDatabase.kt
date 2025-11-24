package com.toqsoft.habittracker.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.toqsoft.habittracker.data.conveters.Converters
import com.toqsoft.habittracker.data.dao.TaskDao
import com.toqsoft.habittracker.data.model.TaskEntity

@TypeConverters(Converters::class)
@Database(entities = [TaskEntity::class], version = 1)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile private var INSTANCE: TaskDatabase? = null

        fun getDatabase(context: Context): TaskDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "my_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
