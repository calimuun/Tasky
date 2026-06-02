package com.calielian.tasky.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calielian.tasky.database.RoutineDao
import com.calielian.tasky.database.RoutineEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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

	fun deleteRoutine(routine: RoutineEntity) {
		viewModelScope.launch {
			dao.delete(routine)
		}
	}

	fun deleteAllRoutines() {
		viewModelScope.launch {
			dao.deleteAllRoutines()
			dao.resetId()
		}
	}

	fun deleteRoutineById(id: Int) {
		viewModelScope.launch {
			dao.deleteRoutine(id)
		}
	}

	fun updateTitle(id: Int, title: String) {
		viewModelScope.launch {
			dao.updateRoutineTitle(id, title)
		}
	}

	fun updateDescription(id: Int, description: String) {
		viewModelScope.launch {
			dao.updateRoutineDescription(id, description)
		}
	}

	fun updateRoutine(routine: RoutineEntity) {
		viewModelScope.launch {
			dao.update(routine)
		}
	}
}
