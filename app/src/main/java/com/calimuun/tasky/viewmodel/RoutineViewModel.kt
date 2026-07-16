package com.calimuun.tasky.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calimuun.tasky.database.RoutineDao
import com.calimuun.tasky.database.RoutineEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/*
* This is a ViewModel
* This will execute the DAO functions and has a flow list to observe the changes to the database
* Also, it's attached to the lifecycle of the activity or fragment
* */
class RoutineViewModel(private val dao: RoutineDao) : ViewModel() {
	val allRoutines: StateFlow<List<RoutineEntity>> = dao.getAllRoutines()
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5000),
			initialValue = emptyList()
		)

	fun insertRoutine(routine: RoutineEntity) {
		viewModelScope.launch {
			dao.insert(routine)
		}
	}

	fun deleteAllRoutines() {
		viewModelScope.launch {
			dao.deleteAllRoutines()
			dao.resetId()
		}
	}

	fun updateRoutineCheck(routine: RoutineEntity) {
		viewModelScope.launch {
			dao.updateRoutineCheck(routine.id, routine.date, routine.checked)
		}
	}

	fun deleteRoutineById(id: Int) {
		viewModelScope.launch {
			dao.deleteRoutine(id)
		}
	}

	fun updateRoutine(routine: RoutineEntity) {
		viewModelScope.launch {
			dao.update(routine)
		}
	}
}
