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
import com.calielian.tasky.database.RoutineEntity
import com.calielian.tasky.databinding.FragmentRoutineBinding
import com.calielian.tasky.databinding.NewTaskLayoutBinding
import com.calielian.tasky.recyclercomponents.RoutineAdapter
import com.calielian.tasky.utils.AlarmScheduler
import com.calielian.tasky.viewmodel.RoutineViewModel
import com.calielian.tasky.viewmodel.RoutineViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class RoutineFragment : Fragment() {
	private var _binding: FragmentRoutineBinding? = null
	private val binding get() = _binding!!

	private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM")
	private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

	var isNotEmpty = false
	lateinit var party: Party

	private val viewModel: RoutineViewModel by lazy {
		val app = requireActivity().application as App
		val factory = RoutineViewModelFactory(app.database.routineDao())

		ViewModelProvider(this, factory)[RoutineViewModel::class.java]
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = FragmentRoutineBinding.inflate(layoutInflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

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

			var newRoutineDate: LocalDate = LocalDate.now().plusDays(1)
			var newRoutineTime: LocalTime = LocalTime.now()

			menuBinding.taskTitleInputLayout.hint = getString(R.string.routine_title_input_hint)
			menuBinding.taskDescriptionInputLayout.hint = getString(R.string.routine_description_input_hint)

			menuBinding.dateChip.text = newRoutineDate.format(dateFormatter)
			menuBinding.dateChip.visibility = View.VISIBLE
			menuBinding.dateChip.closeIcon = null

			menuBinding.timeChip.text = newRoutineTime.format(timeFormatter)
			menuBinding.timeChip.visibility = View.VISIBLE
			menuBinding.timeChip.closeIcon = null

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
					newRoutineDate = date
				}
			}

			menuBinding.timeButton.setOnClickListener {
				Pickers.showTimePicker(this) { time ->
					val now = LocalDateTime.now()
					val selectedDateTime = LocalDateTime.of(newRoutineDate, time)

					if (selectedDateTime.isAfter(now)) {
						menuBinding.timeChip.text = time.format(timeFormatter)
						menuBinding.timeChip.visibility = View.VISIBLE
						newRoutineTime = time
					} else {
						Toast.makeText(context, getString(R.string.time_past_error), Toast.LENGTH_SHORT).show()
					}
				}
			}

			menuBinding.saveButton.setOnClickListener {
				if (menuBinding.taskTitleInput.text.isNullOrEmpty()) {
					menuBinding.taskTitleInputLayout.error = getString(R.string.required_input_error)
					menuBinding.saveButton.isEnabled = false
					return@setOnClickListener
				}

				val title = menuBinding.taskTitleInput.text.toString().trim()

				val description = menuBinding.taskDescriptionInput.text.toString().trim()

				viewModel.insertRoutine(RoutineEntity(
					title = title,
					description = description,
					date = newRoutineDate,
					time = newRoutineTime
				))

				bottomSheet.dismiss()
			}

			bottomSheet.show()
		}

		val adapter = RoutineAdapter().apply {
			onCheckedChange = { routine ->
				val routineUpdated = routine.copy(checked = !routine.checked, date = routine.date.plusDays(1))
				viewModel.updateRoutineCheck(routineUpdated)
				AlarmScheduler.cancelRoutine(requireContext(), routineUpdated.id)
				AlarmScheduler.scheduleRoutine(requireContext(), routineUpdated)
			}

			onClick = { routine ->
				parentFragmentManager.beginTransaction()
					.setReorderingAllowed(true)
					.replace(
						R.id.fragment_container,
						EditTaskFragment.newInstance(
							"routine",
							routine.id,
							routine.title,
							routine.description,
							routine.date.toString(),
							routine.time.toString()
						)
					)
					.addToBackStack(null)
					.commit()
			}
		}

		binding.routineRecyclerview.layoutManager = LinearLayoutManager(context)
		binding.routineRecyclerview.adapter = adapter

		lifecycleScope.launch {
			viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

				delay(100)

				viewModel.allRoutines.collect { list ->
					adapter.submitList(list)

					if (list.isEmpty()) {
						isNotEmpty = false
						binding.emptyStateContainer.visibility = View.VISIBLE
						binding.routineRecyclerview.visibility = View.GONE
					} else {
						isNotEmpty = true
						binding.emptyStateContainer.visibility = View.GONE
						binding.routineRecyclerview.visibility = View.VISIBLE
					}

					for (routine: RoutineEntity in list) {
						if (!routine.checked) return@collect
					}
					if (isNotEmpty) {
						binding.konfetti.start(party)
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