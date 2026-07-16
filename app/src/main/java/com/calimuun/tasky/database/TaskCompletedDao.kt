package com.calimuun.tasky.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/*
* This is a DAO (Data Access Object)
*
* Their function is to perform operations on the database
*
* As you can see, there is some annotations that define default operations (@Insert, @Delete) or a @Query that defines a custom operations
* */
@Dao
interface TaskCompletedDao {
	@Insert
	suspend fun insert(task: TaskCompletedEntity)

	@Delete
	suspend fun delete(task: TaskCompletedEntity)

	@Query("DELETE FROM sqlite_sequence WHERE name = 'TaskCompleted'")
	suspend fun resetId()

	@Query("SELECT * FROM TaskCompleted")
	fun getAllCompletedTasks(): Flow<List<TaskCompletedEntity>>
	@Query("DELETE FROM TaskCompleted")
	suspend fun deleteAllCompletedTasks()
}