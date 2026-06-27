package com.calielian.tasky

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.calielian.tasky.database.TaskRepository
import com.calielian.tasky.databinding.ChangeUsernameDialogLayoutBinding
import com.calielian.tasky.databinding.FragmentMoreBinding
import com.calielian.tasky.utils.AppDataStore
import com.calielian.tasky.viewmodel.RoutineViewModel
import com.calielian.tasky.viewmodel.RoutineViewModelFactory
import com.calielian.tasky.viewmodel.TaskViewModel
import com.calielian.tasky.viewmodel.TaskViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalTime

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

	private val exportDatabaseLauncher = registerForActivityResult(
		ActivityResultContracts.CreateDocument("application/octet-stream")
	) { uri ->
		uri?.let { copyDatabaseToUri(it) }
	}

	private val importDatabaseLauncher = registerForActivityResult(
		ActivityResultContracts.OpenDocument()
	) { uri ->
		uri?.let { importDatabaseFromUri(it) }
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
		val dataStore = AppDataStore(requireContext())

		binding.appVersion.text = getString(R.string.version) + BuildConfig.VERSION_NAME

		binding.seeCompletedTasks.setOnClickListener {
			parentFragmentManager.beginTransaction()
				.setReorderingAllowed(true)
				.replace(R.id.fragment_container, TaskCompletedFragment())
				.addToBackStack(null)
				.commit()
		}

		binding.changeUsername.setOnClickListener {
			lifecycleScope.launch {
				val dialogBinding = ChangeUsernameDialogLayoutBinding.inflate(layoutInflater)

				val dialog = MaterialAlertDialogBuilder(requireContext())
					.setView(dialogBinding.root)
					.create()

				dialogBinding.usernameInput.setText(dataStore.getUsername().first())

				dialogBinding.usernameInput.doOnTextChanged { _, _, _, _ ->
					if (dialogBinding.usernameInput.text.isNullOrEmpty()) {
						dialogBinding.usernameInputLayout.error =
							getString(R.string.required_input_error)
						dialogBinding.changeButton.isEnabled = false
					} else {
						dialogBinding.usernameInputLayout.error = null
						dialogBinding.changeButton.isEnabled = true
					}
				}

				dialogBinding.changeButton.setOnClickListener {
					if (dialogBinding.usernameInput.text.isNullOrEmpty()) {
						dialogBinding.usernameInputLayout.error =
							getString(R.string.required_input_error)
						dialogBinding.changeButton.isEnabled = false
						return@setOnClickListener
					}

					lifecycleScope.launch {
						dataStore.setUsername(dialogBinding.usernameInput.text.toString())
						(activity as MainActivity).changeUsernameFromGreeting(dialogBinding.usernameInput.text.toString())
					}
					dialog.dismiss()

				}

				dialogBinding.cancelButton.setOnClickListener {
					dialog.dismiss()
				}

				dialog.show()
			}
		}

		binding.changeNotificationTime.setOnClickListener {
			lifecycleScope.launch {
				Pickers.showTimePicker(this@MoreFragment, LocalTime.parse(dataStore.getDefaultAlarmTime().first())) { time ->
					lifecycleScope.launch {
						dataStore.setDefaultAlarmTime(time.toString())
					}
				}
			}
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

		binding.exportData.setOnClickListener {
			val app = requireActivity().application as App

			if (app.database.isOpen) {
				app.database.close()
			}

			val timestamp = System.currentTimeMillis()
			exportDatabaseLauncher.launch("tasky_backup_$timestamp.db")
		}

		binding.importData.setOnClickListener {
			val app = requireActivity().application as App

			if (app.database.isOpen) {
				app.database.close()
			}

			importDatabaseLauncher.launch(arrayOf("application/octet-stream"))
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

	private fun copyDatabaseToUri(uri: android.net.Uri) {
		try {
			val dbFile = requireContext().getDatabasePath("app_database")
			val inputStream = dbFile.inputStream()
			val outputStream = requireContext().contentResolver.openOutputStream(uri)

			inputStream.use { input ->
				outputStream?.use { output ->
					input.copyTo(output)
				}
			}
			Snackbar.make(binding.root, getString(R.string.export_completed), Snackbar.LENGTH_SHORT).show()
		} catch (e: Exception) {
			Snackbar.make(binding.root, getString(R.string.export_error), Snackbar.LENGTH_SHORT).show()
		}
	}

	private fun importDatabaseFromUri(uri: android.net.Uri) {
		try {
			val dbFile = requireContext().getDatabasePath("app_database")
			val inputStream = requireContext().contentResolver.openInputStream(uri)
			val outputStream = dbFile.outputStream()

			inputStream?.use { input ->
				outputStream.use { output ->
					input.copyTo(output)
				}
			}
			Snackbar.make(binding.root, getString(R.string.restarting_after_import), Snackbar.LENGTH_LONG).show()
			binding.root.postDelayed({
				val intent = requireContext().packageManager.getLaunchIntentForPackage(requireContext().packageName)
				intent?.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP)
				startActivity(intent!!)
				Runtime.getRuntime().exit(0)
			}, 2000)

		} catch (e: Exception) {
			Snackbar.make(binding.root, getString(R.string.import_error), Snackbar.LENGTH_SHORT).show()
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}