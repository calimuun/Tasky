package com.calimuun.tasky.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/*
* This is a DAO (Data Access Object)
*
* Their function is to perform operations on the database
*
* As you can see, there is some annotations that define default operations (@Insert, @Delete, @Update) or a @Query that defines a custom operations
* */
@Dao
interface TaskDao {
	@Insert
	suspend fun insert(task: TaskEntity)

	@Delete
	suspend fun delete(task: TaskEntity)

	@Update
	suspend fun update(task: TaskEntity)

	@Query("DELETE FROM sqlite_sequence WHERE name = 'Task'")
	suspend fun resetId()

	@Query("SELECT * FROM Task")
	fun getAllTasks(): Flow<List<TaskEntity>>

	@Query("DELETE FROM Task WHERE id = :id")
	suspend fun deleteTask(id: Int)

	@Query("DELETE FROM Task")
	suspend fun deleteAllTasks()
}