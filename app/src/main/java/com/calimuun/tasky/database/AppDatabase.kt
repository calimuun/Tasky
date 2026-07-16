package com.calimuun.tasky.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/*
* This annotation defines a Room database file.
* here, you define the list of entities classes of your database and the version
*
* After defining your DAO, you need to define abstracts functions that returns the DAO
* */
@Database(
	entities = [TaskEntity::class, RoutineEntity::class, TaskCompletedEntity::class],
	version = 1
)
@TypeConverters(Converters::class) // this defines what class is responsible for converting unsupported data types
abstract class AppDatabase : RoomDatabase() {
	abstract fun taskDao(): TaskDao
	abstract fun routineDao(): RoutineDao
	abstract fun taskCompletedDao(): TaskCompletedDao

	/*
	* This companion object defines a INSTANCE variable that's volatile (the variable is shared between threads)
	*
	* getDatabase static function returns the database instance if not null or builds a new instance if null
	* */
	companion object {
		@Volatile
		private var INSTANCE : AppDatabase? = null

		fun getDatabase(context: Context): AppDatabase {
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