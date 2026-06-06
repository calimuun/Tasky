package com.calielian.tasky

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.calielian.tasky.database.TaskRepository
import com.calielian.tasky.databinding.FragmentMoreBinding
import com.calielian.tasky.viewmodel.RoutineViewModel
import com.calielian.tasky.viewmodel.RoutineViewModelFactory
import com.calielian.tasky.viewmodel.TaskViewModel
import com.calielian.tasky.viewmodel.TaskViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis

class MoreFragment : Fragment() {
	private var _binding: FragmentMoreBinding? = null
	private val binding get() = _binding!!

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

		exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
		reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = FragmentMoreBinding.inflate(layoutInflater, container, false)
		return binding.root
	}

	@SuppressLint("SetTextI18n")
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.appVersion.text = getString(R.string.version) + BuildConfig.VERSION_NAME

		binding.seeCompletedTasks.setOnClickListener {
			parentFragmentManager.beginTransaction()
				.setReorderingAllowed(true)
				.replace(R.id.fragment_container, TaskCompletedFragment())
				.addToBackStack(null)
				.commit()
		}

		binding.deleteTasks.setOnClickListener {
			showConfirmationDialog(
				getString(R.string.delete_all_tasks_title),
				getString(R.string.delete_all_tasks_message)
			) { taskViewModel.deleteAllTasks() }
		}

		binding.deleteRoutines.setOnClickListener {
			showConfirmationDialog(
				getString(R.string.delete_all_routines_title),
				getString(R.string.delete_all_routines_message)
			) { routineViewModel.deleteAllRoutines() }
		}
	}

	private fun showConfirmationDialog(title: String, message: String, onConfirm: () -> Unit) {
		MaterialAlertDialogBuilder(requireContext())
			.setTitle(title)
			.setMessage(message)
			.setNegativeButton(getString(R.string.cancel), null)
			.setPositiveButton(getString(R.string.delete)) { _, _ ->
				onConfirm()
				Snackbar.make(
					binding.root,
					getString(R.string.deletion_completed),
					Snackbar.LENGTH_SHORT
				).show()
			}
			.show()
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}