package com.calimuun.tasky.recyclercomponents

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.calimuun.tasky.database.TaskEntity
import com.calimuun.tasky.databinding.TaskLayoutBinding

class TaskAdapter: ListAdapter<TaskEntity, ViewHolders>(TaskComparator()) {

	var onCheckedChange: ((TaskEntity) -> Unit)? = null
	var onClick: ((TaskEntity) -> Unit)? = null

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolders {
		val binding = TaskLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return ViewHolders(binding)
	}

	override fun onBindViewHolder(viewHolder: ViewHolders, position: Int) {
		viewHolder.bindTask(getItem(position), onCheckedChange!!, onClick!!)
	}

	class TaskComparator : DiffUtil.ItemCallback<TaskEntity>() {
		override fun areItemsTheSame(old: TaskEntity, new: TaskEntity) =
			old.id == new.id

		override fun areContentsTheSame(old: TaskEntity, new: TaskEntity) =
			old == new
	}
}