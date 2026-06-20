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
import java.time.LocalTime

class MainActivity : AppCompatActivity() {
	private lateinit var binding: ActivityMainBinding

	lateinit var greeting: String

	override fun onCreate(savedInstanceState: Bundle?) {
		installSplashScreen()
		super.onCreate(savedInstanceState)
		DynamicColors.applyToActivityIfAvailable(this)
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)

		val dataStore = AppDataStore(this)
		val now = LocalTime.now()

		greeting =
			if (now.hour >= 18) getString(R.string.good_night)
			else if (now.hour >= 12) getString(R.string.good_afternoon)
			else getString(R.string.good_morning)

		if (savedInstanceState == null) {
			lifecycleScope.launch {
				val isFirstTime = dataStore.isFirstTime().first()

				if (isFirstTime) {
					changeFragment(OnboardingScreenFragment())
				} else {
					changeFragment(TaskFragment())

					val username = dataStore.getUsername().first()
					greeting += ", " + username.ifBlank { getString(R.string.user_placeholder) } + "!"
					binding.textHeader.text = greeting
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

	fun setUIVisibility(isVisible: Boolean) {
		binding.navBar.visibility = if (isVisible) View.VISIBLE else View.GONE
		binding.textHeader.visibility = if (isVisible) View.VISIBLE else View.GONE
	}

	fun setGreetingText(text: String) {
		greeting += text
		binding.textHeader.text = greeting
	}

	fun changeUsernameFromGreeting(username: String) {
		val commaIndex = greeting.indexOfFirst { it == ',' } + 1
		greeting = greeting.substring(0, commaIndex)
		greeting += " ${username.ifBlank { getString(R.string.user_placeholder) }}!"

		greeting = greeting.replace(getString(R.string.user_placeholder), username)
		binding.textHeader.text = greeting
	}

	private fun changeFragment(newFragment: Fragment) {
		supportFragmentManager.beginTransaction()
			.replace(R.id.fragment_container, newFragment)
			.commit()
	}
}