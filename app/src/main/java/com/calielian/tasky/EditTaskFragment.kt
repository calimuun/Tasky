package com.calielian.tasky

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.calielian.tasky.database.RoutineEntity
import com.calielian.tasky.database.TaskEntity
import com.calielian.tasky.database.TaskRepository
import com.calielian.tasky.databinding.FragmentEditTaskBinding
import com.calielian.tasky.utils.AlarmScheduler
import com.calielian.tasky.viewmodel.RoutineViewModel
import com.calielian.tasky.viewmodel.RoutineViewModelFactory
import com.calielian.tasky.viewmodel.TaskViewModel
import com.calielian.tasky.viewmodel.TaskViewModelFactory
import com.google.android.material.transition.MaterialSharedAxis
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class EditTaskFragment : Fragment() {

	private var fragmentType: String? = null
	private var taskId: Int? = null
	private var taskTitle: String? = null
	private var taskDescription: String? = null
	private var taskDate: LocalDate? = null
	private var taskTime: LocalTime? = null

	private var routineChecked = false

	private var _binding: FragmentEditTaskBinding? = null
	private val binding get() = _binding!!

	private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM")
	private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

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

		enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
		returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)

		arguments?.let {
			fragmentType = it.getString(ARG_FRAGMENT_TYPE)
			taskId = it.getInt(ARG_ID)
			taskTitle = it.getString(ARG_TITLE)

			it.getString(ARG_DESCRIPTION)?.let { description ->
				taskDescription = description
			}

			it.getString(ARG_DATE)?.let { date ->
				taskDate = LocalDate.parse(date)
			}

			it.getString(ARG_TIME)?.let { date ->
				taskTime = LocalTime.parse(date)
			}

			if (fragmentType == "routine") {
				routineChecked = it.getBoolean(ARG_CHECKED)
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

		binding.taskTitleInput.setText(taskTitle)

		taskDescription?.let {
			binding.taskDescriptionInput.setText(it)
		}

		taskDate?.let {
			binding.dateChip.text = it.format(dateFormatter)
			binding.dateChip.visibility = View.VISIBLE
		}

		taskTime?.let {
			binding.timeChip.text = it.format(timeFormatter)
			binding.timeChip.visibility = View.VISIBLE
		}

		binding.closeButton.setOnClickListener {
			parentFragmentManager.popBackStack()
		}

		binding.dateButton.setOnClickListener {
			Pickers.showDatePicker(this) { date ->
				binding.dateChip.text = date.format(dateFormatter)
				binding.dateChip.visibility = View.VISIBLE
				taskDate = date
			}
		}

		binding.timeButton.setOnClickListener {
			Pickers.showTimePicker(this) { time ->
				val now = LocalDateTime.now()
				val selectedDateTime = LocalDateTime.of(taskDate ?: LocalDate.now(), time)

				if (selectedDateTime.isAfter(now)) {
					binding.timeChip.text = time.format(timeFormatter)
					binding.timeChip.visibility = View.VISIBLE
					taskTime = time
				} else {
					Toast.makeText(requireContext(), getString(R.string.time_past_error), Toast.LENGTH_SHORT).show()
				}
			}
		}

		if (fragmentType == "task") {
			binding.dateChip.setOnCloseIconClickListener {
				binding.dateChip.visibility = View.GONE
				taskDate = null
			}

			binding.timeChip.setOnCloseIconClickListener {
				binding.timeChip.visibility = View.GONE
				taskTime = null
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

			if (fragmentType == "task") {
				if (taskTime != null && taskDate == null) {
					taskDate = LocalDate.now()
				}

				val updatedTask = TaskEntity(
					id = taskId!!,
					title = title,
					description = description,
					date = taskDate,
					time = taskTime
				)

				taskViewModel.updateTask(updatedTask)
			} else {
				val updatedRoutine = RoutineEntity(
					id = taskId!!,
					title = title,
					description = description,
					date = taskDate!!,
					time = taskTime!!,
					routineChecked
				)

				routineViewModel.updateRoutine(updatedRoutine)
			}

			parentFragmentManager.popBackStack()
		}

		binding.deleteButton.setOnClickListener {
			if (fragmentType == "task") {
				taskViewModel.deleteTaskById(taskId!!)
				AlarmScheduler.cancelTask(requireContext(), taskId!!)
			} else {
				routineViewModel.deleteRoutineById(taskId!!)
				AlarmScheduler.cancelRoutine(requireContext(), taskId!!)
			}

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

			if (taskDate == null) taskDate = LocalDate.now()
			if (taskTime == null) taskTime = LocalTime.now()
		}
	}

	companion object {
		private const val ARG_FRAGMENT_TYPE = "fragment_type"
		private const val ARG_ID = "id"
		private const val ARG_TITLE = "title"
		private const val ARG_DESCRIPTION = "description"
		private const val ARG_DATE = "date"
		private const val ARG_TIME = "time"
		private const val ARG_CHECKED = "checked"

		@JvmStatic
		fun newInstance(fragmentType: String, id: Int, taskTitle: String, taskDescription: String?, taskDate: String?, taskTime: String?, checked: Boolean = false) =
			EditTaskFragment().apply {
				arguments = Bundle().apply {
					putString(ARG_FRAGMENT_TYPE, fragmentType)
					putInt(ARG_ID, id)
					putString(ARG_TITLE, taskTitle)
					putString(ARG_DESCRIPTION, taskDescription)
					putString(ARG_DATE, taskDate)
					putString(ARG_TIME, taskTime)
					putBoolean(ARG_CHECKED, checked)
				}
			}
	}
}