package com.calielian.tasky.database

import androidx.room.withTransaction

class TaskRepository(
	private val database: AppDatabase,
	private val taskDao: TaskDao,
	private val taskCompletedDao: TaskCompletedDao
) {
	suspend fun completeTask(task: TaskEntity) {
		database.withTransaction {
			val completedTask = TaskCompletedEntity(
				title = task.title,
				description = task.description,
				date = task.date,
				time = task.time
			)
			taskCompletedDao.insert(completedTask)
			taskDao.delete(task)
		}

	}

	suspend fun uncompleteTask(completedTask: TaskCompletedEntity) {
		database.withTransaction {
			val task = TaskEntity(
				title = completedTask.title,
				description = completedTask.description,
				date = completedTask.date,
				time = completedTask.time
			)
			taskCompletedDao.delete(completedTask)
			taskDao.insert(task)
		}
	}
}