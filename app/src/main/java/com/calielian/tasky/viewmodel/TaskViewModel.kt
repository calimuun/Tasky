package com.calielian.tasky.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calielian.tasky.database.TaskDao
import com.calielian.tasky.database.TaskEntity
import com.calielian.tasky.database.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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

	fun deleteTask(task: TaskEntity) {
		viewModelScope.launch {
			dao.delete(task)
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

	fun updateTitle(id: Int, title: String) {
		viewModelScope.launch {
			dao.updateTaskTitle(id, title)
		}
	}

	fun updateDescription(id: Int, description: String) {
		viewModelScope.launch {
			dao.updateTaskDescription(id, description)
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