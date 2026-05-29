package com.calielian.tasky.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalTime

@Dao
interface TaskDao {
	@Insert
	suspend fun insert(task: TaskEntity)

	@Delete
	suspend fun delete(task: TaskEntity)

	@Query("DELETE FROM sqlite_sequence WHERE name = 'Task'")
	suspend fun resetId()

	@Query("SELECT * FROM Task")
	fun getAllTasks(): Flow<List<TaskEntity>>

	@Query("DELETE FROM Task WHERE id = :id")
	suspend fun deleteTask(id: Int)

	@Query("DELETE FROM Task")
	suspend fun deleteAllTasks()

	@Query("UPDATE Task SET description = :description WHERE id = :id")
	suspend fun updateTaskDescription(id: Int, description: String)

	@Query("UPDATE Task SET title = :title WHERE id = :id")
	suspend fun updateTaskTitle(id: Int, title: String)

	@Query("UPDATE Task SET date = :date, time = :time WHERE id = :id")
	suspend fun updateTaskDateTime(id: Int, date: LocalDate, time: LocalTime)

	@Query("UPDATE Task SET date = :date WHERE id = :id")
	suspend fun updateTaskDate(id: Int, date: LocalDate)

	@Query("UPDATE Task SET time = :time WHERE id = :id")
	suspend fun updateTaskTime(id: Int, time: LocalTime)
}