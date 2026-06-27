package com.calielian.tasky.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
	entities = [TaskEntity::class, RoutineEntity::class, TaskCompletedEntity::class],
	version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
	abstract fun taskDao(): TaskDao
	abstract fun routineDao(): RoutineDao
	abstract fun taskCompletedDao(): TaskCompletedDao

	companion object {
		@Volatile
		private var INSTANCE : AppDatabase? = null

		fun getDatabase(context: Context): AppDatabase {

			if (INSTANCE != null && !INSTANCE!!.isOpen) {
				INSTANCE = null
			}

			return INSTANCE ?: synchronized(this) {
				val instance = Room.databaseBuilder(
					context.applicationContext,
					AppDatabase::class.java,
					"app_database"
				).build()
				INSTANCE = instance
				instance
			}
		}
	}
}