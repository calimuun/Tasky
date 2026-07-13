package com.calimuun.tasky

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.calimuun.tasky.database.TaskRepository
import com.calimuun.tasky.databinding.FragmentTaskCompletedBinding
import com.calimuun.tasky.recyclercomponents.TaskCompletedAdapter
import com.calimuun.tasky.viewmodel.TaskCompletedViewModel
import com.calimuun.tasky.viewmodel.TaskCompletedViewModelFactory
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.launch

class TaskCompletedFragment : Fragment() {

	private var _binding: FragmentTaskCompletedBinding? = null
	private val binding get() = _binding!!

	private val viewModel: TaskCompletedViewModel by lazy {
		val app = requireActivity().application as App
		val repository = TaskRepository(app.database, app.database.taskDao(), app.database.taskCompletedDao())
		val factory = TaskCompletedViewModelFactory(app.database.taskCompletedDao(), repository)

		ViewModelProvider(this, factory)[TaskCompletedViewModel::class.java]
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
		returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = FragmentTaskCompletedBinding.inflate(layoutInflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val adapter = TaskCompletedAdapter().apply {
			onCheckedChange = { task ->
				viewModel.uncompleteTask(task)
			}
		}

		binding.taskCompletedRecyclerview.layoutManager = LinearLayoutManager(context)
		binding.taskCompletedRecyclerview.adapter = adapter

		lifecycleScope.launch {
			viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
				viewModel.allCompletedTasks.collect { list ->
					adapter.submitList(list)

					if (list.isEmpty()) {
						binding.emptyStateContainer.visibility = View.VISIBLE
						binding.taskCompletedRecyclerview.visibility = View.GONE
					} else {
						binding.emptyStateContainer.visibility = View.GONE
						binding.taskCompletedRecyclerview.visibility = View.VISIBLE
					}
				}
			}
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		_binding = null
	}
}