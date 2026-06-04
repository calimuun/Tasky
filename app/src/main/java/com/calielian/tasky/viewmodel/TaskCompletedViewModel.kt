package com.calielian.tasky.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calielian.tasky.database.TaskCompletedDao
import com.calielian.tasky.database.TaskCompletedEntity
import com.calielian.tasky.database.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskCompletedViewModel(
	private val dao: TaskCompletedDao,
	private val repository: TaskRepository
) : ViewModel() {

	val allCompletedTasks: StateFlow<List<TaskCompletedEntity>> = dao.getAllCompletedTasks()
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5000),
			initialValue = emptyList()
		)

	fun deleteTask(task: TaskCompletedEntity) {
		viewModelScope.launch {
			dao.delete(task)
		}
	}

	fun deleteAllTasks() {
		viewModelScope.launch {
			dao.deleteAllCompletedTasks()
			dao.resetId()
		}
	}

	fun deleteTaskById(id: Int) {
		viewModelScope.launch {
			dao.deleteTaskCompleted(id)
		}
	}

	fun uncompleteTask(task: TaskCompletedEntity) {
		viewModelScope.launch {
			repository.uncompleteTask(task)
		}
	}
}