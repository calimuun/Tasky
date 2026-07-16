package com.calimuun.tasky.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calimuun.tasky.database.TaskDao
import com.calimuun.tasky.database.TaskEntity
import com.calimuun.tasky.database.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/*
* This is a ViewModel
* This will execute the DAO functions and has a flow list to observe the changes to the database
* Also, it's attached to the lifecycle of the activity or fragment
* */
class TaskViewModel(
	private val dao: TaskDao,
	private val repository: TaskRepository
) : ViewModel() {

	val allTasks: StateFlow<List<TaskEntity>> = dao.getAllTasks()
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5000),
			initialValue = emptyList()
		)

	fun insertTask(task: TaskEntity) {
		viewModelScope.launch {
			dao.insert(task)
		}
	}

	fun deleteAllTasks() {
		viewModelScope.launch {
			repository.deleteAllTasks()
		}
	}

	fun deleteTaskById(id: Int) {
		viewModelScope.launch {
			dao.deleteTask(id)
		}
	}

	fun updateTask(task: TaskEntity) {
		viewModelScope.launch {
			dao.update(task)
		}
	}

	fun completeTask(task: TaskEntity) {
		viewModelScope.launch {
			repository.completeTask(task)
		}
	}
}