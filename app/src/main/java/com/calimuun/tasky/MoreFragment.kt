package com.calimuun.tasky

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.calimuun.tasky.database.TaskRepository
import com.calimuun.tasky.databinding.ChangeUsernameDialogLayoutBinding
import com.calimuun.tasky.databinding.FragmentMoreBinding
import com.calimuun.tasky.utils.AppDataStore
import com.calimuun.tasky.utils.Pickers
import com.calimuun.tasky.viewmodel.RoutineViewModel
import com.calimuun.tasky.viewmodel.RoutineViewModelFactory
import com.calimuun.tasky.viewmodel.TaskViewModel
import com.calimuun.tasky.viewmodel.TaskViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalTime

/*
* This defines the fragment "More" logic
* Layout: fragment_more.xml
* */
class MoreFragment : Fragment() {
	private var _binding: FragmentMoreBinding? = null
	private val binding get() = _binding!!

	private var requestAlarm: Boolean = false

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

	// launches the permission request and receives the result of the request
	private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
		if (isGranted) {
			if (requestAlarm) requestAlarmPermission()
			else binding.receiveNotifications.visibility = View.GONE
		}
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

		checkAlarmPermissionGranted()

		if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED || requestAlarm) {
			binding.receiveNotifications.visibility = View.VISIBLE
		}

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

		binding.receiveNotifications.setOnClickListener {
			val hasNotificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
				ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
			} else {
				true
			}

			checkAlarmPermissionGranted()

			when {
				!hasNotificationPermission -> {
					if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.POST_NOTIFICATIONS)) {
						showPermissionRequestExplanationDialog()
					} else {
						requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
					}
				}

				requestAlarm -> {
					requestAlarmPermission()
				}

				else -> {
					binding.receiveNotifications.visibility = View.GONE
				}
			}
		}
	}

	private fun checkAlarmPermissionGranted() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
			val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
			requestAlarm = !alarmManager.canScheduleExactAlarms()
		} else {
			 requestAlarm = false
		}
	}

	private fun showPermissionRequestExplanationDialog() {
		AlertDialog.Builder(requireActivity())
			.setTitle(getString(R.string.notifications_permission))
			.setMessage(getString(R.string.notifications_permission_rationale))
			.setPositiveButton(getString(R.string.notifications_permission_rationale_positive)) { _, _ ->
				requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
			}
			.setNegativeButton(getString(R.string.notifications_permission_rationale_negative)) { _, _ -> }
			.show()
	}

	// "Schedule Exact Alarm" it's not request by an overlay like other permissions, so we need to redirect the user to settings to manually allow
	private fun requestAlarmPermission() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
			val intent = Intent().apply {
				action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
			}
			startActivity(intent)
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