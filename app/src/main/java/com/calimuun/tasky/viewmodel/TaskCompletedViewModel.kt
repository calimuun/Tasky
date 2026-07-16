package com.calimuun.tasky.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calimuun.tasky.database.TaskCompletedDao
import com.calimuun.tasky.database.TaskCompletedEntity
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
class TaskCompletedViewModel(
	dao: TaskCompletedDao,
	private val repository: TaskRepository
) : ViewModel() {

	val allCompletedTasks: StateFlow<List<TaskCompletedEntity>> = dao.getAllCompletedTasks()
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5000),
			initialValue = emptyList()
		)
	fun uncompleteTask(task: TaskCompletedEntity) {
		viewModelScope.launch {
			repository.uncompleteTask(task)
		}
	}
}