package com.calielian.tasky

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.calielian.tasky.databinding.FragmentOnboardingScreenBinding
import com.calielian.tasky.recyclercomponents.OnboardingAdapter
import com.calielian.tasky.utils.AppDataStore
import com.calielian.tasky.utils.OnboardPageItem
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OnboardingScreenFragment : Fragment() {

	private var _binding: FragmentOnboardingScreenBinding? = null
	private val binding get() = _binding!!

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = FragmentOnboardingScreenBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val adapter = OnboardingAdapter(getOnboardPages()) { username ->
			viewLifecycleOwner.lifecycleScope.launch {
				val dataStore = AppDataStore(requireContext())

				dataStore.setUsername(username)
				dataStore.setFirstTime(false)

				(activity as? MainActivity)?.setGreetingText(", $username!")

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
		binding.viewPager.adapter = adapter

		TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ -> }.attach()
	}

	private fun getOnboardPages(): List<OnboardPageItem> {
		return listOf(
			OnboardPageItem("Teste 1", "Teste 1 desc", R.drawable.app_icon_scaled),
			OnboardPageItem("Teste 2", "Teste 2 desc", R.drawable.ic_done),
			OnboardPageItem("Teste 3", "Teste 3 desc", R.drawable.ic_routine),
			OnboardPageItem("", "", 0)
		)
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