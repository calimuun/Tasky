package com.calielian.tasky

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.calielian.tasky.database.TaskEntity
import com.calielian.tasky.database.TaskRepository
import com.calielian.tasky.databinding.FragmentTaskBinding
import com.calielian.tasky.databinding.NewTaskLayoutBinding
import com.calielian.tasky.recyclercomponents.TaskAdapter
import com.calielian.tasky.utils.AlarmScheduler
import com.calielian.tasky.utils.AppDataStore
import com.calielian.tasky.viewmodel.TaskViewModel
import com.calielian.tasky.viewmodel.TaskViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class TaskFragment : Fragment() {

	private var _binding: FragmentTaskBinding? = null
	private val binding get() = _binding!!

	private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM")
	private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

	var isEmptyAfterElement = false
	lateinit var party: Party

	private val viewModel: TaskViewModel by lazy {
		val app = requireActivity().application as App
		val repository = TaskRepository(app.database, app.database.taskDao(), app.database.taskCompletedDao())
		val factory = TaskViewModelFactory(app.database.taskDao(), repository)

		ViewModelProvider(this, factory)[TaskViewModel::class.java]
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
		reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = FragmentTaskBinding.inflate(layoutInflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val dataStore = AppDataStore(requireContext())

		party = Party(
			speed = 0f,
			maxSpeed = 30f,
			damping = 0.9f,
			spread = 360,
			colors = listOf(0x5248cf, 0x1b155b, 0x03002d, 0x958dfd),
			emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100)
		)

		binding.fabAddTask.setOnClickListener {
			val bottomSheet = BottomSheetDialog(requireContext())
			val menuBinding = NewTaskLayoutBinding.inflate(layoutInflater)
			bottomSheet.setContentView(menuBinding.root)

			var newTaskDate: LocalDate? = null
			var newTaskTime: LocalTime? = null

			menuBinding.taskTitleInput.doOnTextChanged { _, _, _, _ ->
				if (menuBinding.taskTitleInput.text.isNullOrEmpty()) {
					menuBinding.taskTitleInputLayout.error = getString(R.string.required_input_error)
					menuBinding.saveButton.isEnabled = false
				} else {
					menuBinding.taskTitleInputLayout.error = null
					menuBinding.saveButton.isEnabled = true
				}
			}

			menuBinding.dateButton.setOnClickListener {
				Pickers.showDatePicker(this) { date ->
					menuBinding.dateChip.text = date.format(dateFormatter)
					menuBinding.dateChip.visibility = View.VISIBLE
					newTaskDate = date
				}
			}

			menuBinding.timeButton.setOnClickListener {
				Pickers.showTimePicker(this) { time ->
					val now = LocalDateTime.now()
					val selectedDateTime = LocalDateTime.of(newTaskDate ?: LocalDate.now(), time)

					if (selectedDateTime.isAfter(now)) {
						menuBinding.timeChip.text = time.format(timeFormatter)
						menuBinding.timeChip.visibility = View.VISIBLE
						newTaskTime = time
					} else {
						Toast.makeText(context, getString(R.string.time_past_error), Toast.LENGTH_SHORT).show()
					}
				}
			}

			menuBinding.dateChip.setOnCloseIconClickListener {
				menuBinding.dateChip.visibility = View.GONE
				newTaskDate = null
			}

			menuBinding.timeChip.setOnCloseIconClickListener {
				menuBinding.timeChip.visibility = View.GONE
				newTaskTime = null
			}

			menuBinding.saveButton.setOnClickListener {
				if (menuBinding.taskTitleInput.text.isNullOrEmpty()) {
					menuBinding.taskTitleInputLayout.error = getString(R.string.required_input_error)
					menuBinding.saveButton.isEnabled = false
					return@setOnClickListener
				}

				val title = menuBinding.taskTitleInput.text.toString().trim()

				val description = menuBinding.taskDescriptionInput.text.toString().trim()

				val newTask = TaskEntity(
					title = title,
					description = description,
					date = newTaskDate,
					time = newTaskTime
				)

				viewModel.insertTask(newTask)

				if (newTaskDate != null || newTaskTime != null) {
					lifecycleScope.launch {
						val taskToSchedule = if (newTaskDate != null && newTaskTime == null) {
							val defaultTime = dataStore.getDefaultAlarmTime().first()

							if (LocalDateTime.now().isAfter(LocalDateTime.of(newTaskDate, LocalTime.parse(defaultTime)))) return@launch

							newTask.copy(
								time = LocalTime.parse(defaultTime)
							)
						} else if (newTaskDate == null) {
							newTask.copy(date = LocalDate.now())
						} else {
							newTask.copy()
						}

						AlarmScheduler.scheduleTask(requireContext(), taskToSchedule)
					}
				}

				bottomSheet.dismiss()
			}

			bottomSheet.show()
		}

		val adapter = TaskAdapter().apply {
			onCheckedChange = { task ->
				viewModel.completeTask(task)
				AlarmScheduler.cancelTask(requireContext(), task.id)
			}

			onClick = { task ->
				parentFragmentManager.beginTransaction()
					.setReorderingAllowed(true)
					.replace(
						R.id.fragment_container,
						EditTaskFragment.newInstance(
							"task",
							task.id,
							task.title,
							task.description,
							task.date?.toString(),
							task.time?.toString()
						)
					)
					.addToBackStack(null)
					.commit()
			}
		}

		binding.taskRecyclerview.layoutManager = LinearLayoutManager(context)
		binding.taskRecyclerview.adapter = adapter

		lifecycleScope.launch {
			viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
				viewModel.allTasks.collect { list ->
					adapter.submitList(list)

					if (list.isEmpty()) {
						binding.emptyStateContainer.visibility = View.VISIBLE
						binding.taskRecyclerview.visibility = View.GONE

						if (isEmptyAfterElement) {
							binding.konfetti.start(party)
							isEmptyAfterElement = false
						}
					} else {
						isEmptyAfterElement = true
						binding.emptyStateContainer.visibility = View.GONE
						binding.taskRecyclerview.visibility = View.VISIBLE
					}
				}
			}
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}