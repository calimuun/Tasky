package com.calielian.tasky.recyclercomponents

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.calielian.tasky.database.TaskCompletedEntity
import com.calielian.tasky.databinding.TaskLayoutBinding

class TaskCompletedAdapter: ListAdapter<TaskCompletedEntity, ViewHolders>(TaskCompletedComparator()) {

	var onCheckedChange: ((TaskCompletedEntity) -> Unit)? = null

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolders {
		val binding = TaskLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return ViewHolders(binding)
	}

	override fun onBindViewHolder(viewHolder: ViewHolders, position: Int) {
		viewHolder.bindCompletedTask(getItem(position), onCheckedChange!!)
	}

	class TaskCompletedComparator : DiffUtil.ItemCallback<TaskCompletedEntity>() {
		override fun areItemsTheSame(old: TaskCompletedEntity, new: TaskCompletedEntity) =
			old.id == new.id

		override fun areContentsTheSame(old: TaskCompletedEntity, new: TaskCompletedEntity) =
			old == new
	}
}