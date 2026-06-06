package com.calielian.tasky

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.calielian.tasky.databinding.ActivityMainBinding
import com.calielian.tasky.utils.AppDataStore
import com.google.android.material.color.DynamicColors
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
	private lateinit var binding: ActivityMainBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		installSplashScreen()
		super.onCreate(savedInstanceState)
		DynamicColors.applyToActivityIfAvailable(this)
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)

		val dataStore = AppDataStore(this)

		if (savedInstanceState == null) {
			lifecycleScope.launch {
				val isFirstTime = dataStore.isFirstTime().first()
				if (isFirstTime) {
					changeFragment(OnboardingScreenFragment())
				} else {
					changeFragment(TaskFragment())
				}
			}
		}

		binding.navBar.setOnItemSelectedListener {
			when (it.itemId) {
				R.id.task_navbar -> changeFragment(TaskFragment())
				R.id.routine_navbar -> changeFragment(RoutineFragment())
				R.id.more_navbar -> changeFragment(MoreFragment())
			}
			true
		}
	}

	fun setNavBarVisibility(isVisible: Boolean) {
		binding.navBar.visibility = if (isVisible) View.VISIBLE else View.GONE
	}

	private fun changeFragment(newFragment: Fragment) {
		supportFragmentManager.beginTransaction()
			.replace(R.id.fragment_container, newFragment)
			.commit()
	}
}