package com.calimuun.tasky.database

import androidx.room.withTransaction

/*
* This is a "repository"
*
* This connects two DAO's and performs operations on them
*
* This is necessary in Tasky's context because when you complete a task, you need to add it to the completed tasks table and remove from tasks table or vice versa
* */
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

	suspend fun deleteAllTasks() {
		database.withTransaction {
			taskDao.deleteAllTasks()
			taskCompletedDao.deleteAllCompletedTasks()

			taskDao.resetId()
			taskCompletedDao.resetId()
		}
	}
}