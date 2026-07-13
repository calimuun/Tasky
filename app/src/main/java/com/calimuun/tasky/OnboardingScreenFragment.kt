package com.calimuun.tasky

import android.Manifest
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
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.calimuun.tasky.databinding.FragmentOnboardingScreenBinding
import com.calimuun.tasky.databinding.OnboardScreenPermissionPageLayoutBinding
import com.calimuun.tasky.recyclercomponents.OnboardingAdapter
import com.calimuun.tasky.utils.AppDataStore
import com.calimuun.tasky.utils.OnboardPageItem
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OnboardingScreenFragment : Fragment() {

	private var _binding: FragmentOnboardingScreenBinding? = null
	private val binding get() = _binding!!
	private lateinit var dataStore: AppDataStore

	private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
		if (isGranted) {
			viewLifecycleOwner.lifecycleScope.launch {
				dataStore.setNotificationPermitted(true)
			}
		}
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = FragmentOnboardingScreenBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		dataStore = AppDataStore(requireContext())

		val onOnboardingFinish: (String) -> Unit = { username ->
			viewLifecycleOwner.lifecycleScope.launch {
				val realUsername = username.ifBlank { getString(R.string.user_placeholder) }

				dataStore.setUsername(realUsername)
				dataStore.setFirstTime(false)

				(activity as? MainActivity)?.setGreetingText(", $realUsername!")

				delay(300)

				parentFragmentManager.beginTransaction()
					.setCustomAnimations(
						android.R.anim.fade_in,
						android.R.anim.fade_out,
						android.R.anim.fade_in,
						android.R.anim.fade_out
					)
					.replace(R.id.fragment_container, TaskFragment())
					.commit()
			}
		}

		val onRequestNotificationPermission: (OnboardScreenPermissionPageLayoutBinding) -> Unit = { binding ->
			val disableButton: (Int) -> Unit = { resourceId ->
				binding.notificationPermissionButton.isClickable = false
				binding.notificationPermissionButtonChevron.visibility = View.GONE
				binding.notificationPermissionButtonIcon.setImageResource(resourceId)
			}

			when {
				// permissão já consentida
				ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED -> {
					disableButton(R.drawable.ic_notifications)
				}

				// permissão negada anteriormente, mostrar uma mensagem explicativa
				ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.POST_NOTIFICATIONS) -> {
					showPermissionRequestExplanationDialog(disableButton)
				}

				// permissão ainda não consentida e solicitada
				else -> {
					requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
				}
			}
		}

		val onRequestAlarmPermission: (OnboardScreenPermissionPageLayoutBinding) -> Unit = { binding ->
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
				val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
				if (!alarmManager.canScheduleExactAlarms()) {
					val intent = Intent().apply {
						action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
					}
					startActivity(intent)
				} else {
					binding.alarmPermissionButton.isClickable = false
					binding.alarmPermissionButtonChevron.visibility = View.GONE
					binding.alarmPermissionButtonIcon.setImageResource(R.drawable.ic_alarm_clock)
				}
			}
		}

		val adapter = OnboardingAdapter(getOnboardPages(), onOnboardingFinish, onRequestNotificationPermission, onRequestAlarmPermission)
		binding.viewPager.adapter = adapter

		TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ -> }.attach()
	}

	private fun getOnboardPages(): List<OnboardPageItem> {
		return listOf(
			OnboardPageItem("Teste 1", "Teste 1 desc", R.drawable.app_icon_scaled),
			OnboardPageItem("Teste 2", "Teste 2 desc", R.drawable.ic_done),
			OnboardPageItem("Teste 3", "Teste 3 desc", R.drawable.ic_routine),
			OnboardPageItem("Permission", "", 0),
			OnboardPageItem("", "", 0)
		)
	}

	private fun showPermissionRequestExplanationDialog(disableButton: (Int) -> Unit) {
		AlertDialog.Builder(requireActivity())
			.setTitle("Permissão de Notificações")
			.setMessage("Você precisa dar permissão para receber notificações.")
			.setPositiveButton("Ta bom") { _, _ ->
				requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
			}
			.setNegativeButton("Nao") { _, _ ->
				viewLifecycleOwner.lifecycleScope.launch {
					dataStore.setNotificationPermitted(false)
				}
				disableButton(R.drawable.ic_notifications_off)
			}
			.show()
	}

	override fun onResume() {
		super.onResume()
		(activity as? MainActivity)?.setUIVisibility(false)
	}

	override fun onStop() {
		super.onStop()
		(activity as? MainActivity)?.setUIVisibility(true)
	}

	override fun onDestroy() {
		super.onDestroy()
		_binding = null
	}
}