package com.calimuun.tasky

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.calimuun.tasky.database.RoutineEntity
import com.calimuun.tasky.database.TaskEntity
import com.calimuun.tasky.database.TaskRepository
import com.calimuun.tasky.databinding.FragmentEditTaskBinding
import com.calimuun.tasky.utils.AlarmScheduler
import com.calimuun.tasky.utils.AppDataStore
import com.calimuun.tasky.utils.Pickers
import com.calimuun.tasky.viewmodel.RoutineViewModel
import com.calimuun.tasky.viewmodel.RoutineViewModelFactory
import com.calimuun.tasky.viewmodel.TaskViewModel
import com.calimuun.tasky.viewmodel.TaskViewModelFactory
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/*
* This defines the fragment "Edit Task" logic
* Layout: fragment_edit_task.xml
*
* "Routine" is a type of "recurrent task", so it is edited here too
* */
class EditTaskFragment : Fragment() {

	private var fragmentType: String? = null
	private var itemId: Int? = null
	private var title: String? = null
	private var description: String? = null
	private var date: LocalDate? = null
	private var time: LocalTime? = null

	private var _binding: FragmentEditTaskBinding? = null
	private val binding get() = _binding!!

	private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM")
	private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

	// creates the ViewModel
	// "by lazy" means that the ViewModel will be created only when it is needed (when the constant is called for the first time)
	// by -> delegate the initialization for something
	// lazy -> initialization method that is only called the first time the constant is called
	val taskViewModel: TaskViewModel by lazy {
		val app = requireActivity().application as App
		val repository = TaskRepository(app.database, app.database.taskDao(), app.database.taskCompletedDao())
		val factory = TaskViewModelFactory(app.database.taskDao(), repository)

		ViewModelProvider(this, factory)[TaskViewModel::class.java]
	}

	val routineViewModel: RoutineViewModel by lazy {
		val app = requireActivity().application as App
		val factory = RoutineViewModelFactory(app.database.routineDao())

		ViewModelProvider(this, factory)[RoutineViewModel::class.java]
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		// changes the transition. Predictive Back thing
		enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
		returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)

		// receives the arguments passed to the fragment
		arguments?.let {
			fragmentType = it.getString(ARG_FRAGMENT_TYPE)
			itemId = it.getInt(ARG_ID)
			title = it.getString(ARG_TITLE)

			it.getString(ARG_DESCRIPTION)?.let { itemDescription ->
				description = itemDescription
			}

			it.getString(ARG_DATE)?.let { itemDate ->
				date = LocalDate.parse(itemDate)
			}

			it.getString(ARG_TIME)?.let { itemTime ->
				time = LocalTime.parse(itemTime)
			}
		}
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = FragmentEditTaskBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		changeItemsToMatchFragmentType()

		val dataStore = AppDataStore(requireContext())

		binding.taskTitleInput.setText(title)

		description?.let {
			binding.taskDescriptionInput.setText(it)
		}

		date?.let {
			binding.dateChip.text = it.format(dateFormatter)
			binding.dateChip.visibility = View.VISIBLE
		}

		time?.let {
			binding.timeChip.text = it.format(timeFormatter)
			binding.timeChip.visibility = View.VISIBLE
		}

		binding.closeButton.setOnClickListener {
			parentFragmentManager.popBackStack()
		}

		binding.dateButton.setOnClickListener {
			Pickers.showDatePicker(this) { datePicked ->
				binding.dateChip.text = datePicked.format(dateFormatter)
				binding.dateChip.visibility = View.VISIBLE
				date = datePicked
			}
		}

		binding.timeButton.setOnClickListener {
			Pickers.showTimePicker(this) { timePicked ->
				val now = LocalDateTime.now()
				val selectedDateTime = LocalDateTime.of(date ?: LocalDate.now(), timePicked)

				if (selectedDateTime.isAfter(now)) {
					binding.timeChip.text = timePicked.format(timeFormatter)
					binding.timeChip.visibility = View.VISIBLE
					time = timePicked
				} else {
					Toast.makeText(requireContext(), getString(R.string.time_past_error), Toast.LENGTH_SHORT).show()
				}
			}
		}

		if (fragmentType == "task") {
			binding.dateChip.setOnCloseIconClickListener {
				binding.dateChip.visibility = View.GONE
				date = null
			}

			binding.timeChip.setOnCloseIconClickListener {
				binding.timeChip.visibility = View.GONE
				time = null
			}

		}

		binding.taskTitleInput.doOnTextChanged { _, _, _, _ ->
			if (binding.taskTitleInput.text.isNullOrEmpty()) {
				binding.taskTitleInputLayout.error = getString(R.string.required_input_error)
				binding.saveButton.isEnabled = false
			} else {
				binding.taskTitleInputLayout.error = null
				binding.saveButton.isEnabled = true
			}
		}

		binding.saveButton.setOnClickListener {
			val title = binding.taskTitleInput.text.toString().trim()
			val description = binding.taskDescriptionInput.text.toString().trim().ifEmpty { null }

			lifecycleScope.launch {
				if (fragmentType == "task") {
					if (time != null && date == null) {
						date = LocalDate.now()
					} else if(date != null && time == null) {
						val defaultAlarmTime = LocalTime.parse(dataStore.getDefaultAlarmTime().first())

						if (LocalDateTime.of(LocalDate.now(), defaultAlarmTime).isBefore(LocalDateTime.now())) {
							Toast.makeText(requireContext(), getString(R.string.time_past_error), Toast.LENGTH_SHORT).show()
							return@launch
						}

						time = defaultAlarmTime
					}

					val updatedTask = TaskEntity(
						id = itemId!!,
						title = title,
						description = description,
						date = date,
						time = time
					)

					taskViewModel.updateTask(updatedTask)
					AlarmScheduler.cancelTask(requireContext(), updatedTask.id)
				} else {
					val updatedRoutine = RoutineEntity(
						id = itemId!!,
						title = title,
						description = description,
						date = date!!,
						time = time!!,
					)

					routineViewModel.updateRoutine(updatedRoutine)
					AlarmScheduler.cancelRoutine(requireContext(), updatedRoutine.id)
				}

				lifecycleScope.launch(Dispatchers.IO) {
					val db = (requireActivity().application as App).database
					AlarmScheduler.rescheduleNext(requireContext(), db)
				}

				parentFragmentManager.popBackStack()
			}
		}

		binding.deleteButton.setOnClickListener {
			if (fragmentType == "task") {
				taskViewModel.deleteTaskById(itemId!!)
				AlarmScheduler.cancelTask(requireContext(), itemId!!)
			} else {
				routineViewModel.deleteRoutineById(itemId!!)
				AlarmScheduler.cancelRoutine(requireContext(), itemId!!)
			}

			val db = (requireActivity().application as App).database
			AlarmScheduler.rescheduleNext(requireContext(), db)

			parentFragmentManager.popBackStack()
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		_binding = null
	}

	fun changeItemsToMatchFragmentType() {
		if (fragmentType == "routine") {
			binding.fragmentTitle.text = getString(R.string.edit_routine_title)
			binding.taskTitleInputLayout.hint = getString(R.string.routine_title_input_hint)
			binding.taskDescriptionInputLayout.hint = getString(R.string.routine_description_input_hint)
			binding.dateChip.closeIcon = null
			binding.timeChip.closeIcon = null

			if (date == null) date = LocalDate.now()
			if (time == null) time = LocalTime.now()
		}
	}

	// defines what arguments can be passed to the fragment
	companion object {
		private const val ARG_FRAGMENT_TYPE = "fragment_type"
		private const val ARG_ID = "id"
		private const val ARG_TITLE = "title"
		private const val ARG_DESCRIPTION = "description"
		private const val ARG_DATE = "date"
		private const val ARG_TIME = "time"

		@JvmStatic
		fun newInstance(fragmentType: String, id: Int, title: String, description: String?, date: String?, time: String?) =
			EditTaskFragment().apply {
				arguments = Bundle().apply {
					putString(ARG_FRAGMENT_TYPE, fragmentType)
					putInt(ARG_ID, id)
					putString(ARG_TITLE, title)
					putString(ARG_DESCRIPTION, description)
					putString(ARG_DATE, date)
					putString(ARG_TIME, time)
				}
			}
	}
}