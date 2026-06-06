package com.calielian.tasky

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.calielian.tasky.databinding.FragmentMoreBinding
import com.google.android.material.transition.MaterialSharedAxis

class MoreFragment : Fragment() {
	private var _binding: FragmentMoreBinding? = null
	private val binding get() = _binding!!

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
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}