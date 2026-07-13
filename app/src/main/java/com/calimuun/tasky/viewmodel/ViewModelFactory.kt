package com.calimuun.tasky.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.calimuun.tasky.database.RoutineDao
import com.calimuun.tasky.database.TaskCompletedDao
import com.calimuun.tasky.database.TaskDao
import com.calimuun.tasky.database.TaskRepository

class TaskViewModelFactory(
	private val dao: TaskDao,
	private val repository: TaskRepository
) : ViewModelProvider.Factory {

	@Suppress("UNCHECKED_CAST")
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
			return TaskViewModel(dao, repository) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
	}
}

class RoutineViewModelFactory(private val dao: RoutineDao) : ViewModelProvider.Factory {

	@Suppress("UNCHECKED_CAST")
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if (modelClass.isAssignableFrom(RoutineViewModel::class.java)) {
			return RoutineViewModel(dao) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
	}
}

class TaskCompletedViewModelFactory(
	private val dao: TaskCompletedDao,
	private val repository: TaskRepository
) : ViewModelProvider.Factory {

	@Suppress("UNCHECKED_CAST")
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if (modelClass.isAssignableFrom(TaskCompletedViewModel::class.java)) {
			return TaskCompletedViewModel(dao, repository) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
	}
}