package com.calimuun.tasky.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

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

	@Query("DELETE FROM TaskCompleted WHERE id = :id")
	suspend fun deleteTaskCompleted(id: Int)

	@Query("DELETE FROM TaskCompleted")
	suspend fun deleteAllCompletedTasks()
}