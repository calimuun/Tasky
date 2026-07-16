package com.calimuun.tasky.recyclercomponents

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.calimuun.tasky.database.TaskEntity
import com.calimuun.tasky.databinding.TaskLayoutBinding

/*
* This is a RecyclerView component named "Adapter"
* An Adapter is responsible for providing data to the RecyclerView and creating views for the items
* And after creating views for the items, the Adapter binds the data to the views via an ViewHolder
*
* This one have a new class called "Comparator" that sees what item is the same and what is different
* This allows for a smooth list update and adds animations instead of the item just popping on the screen
* E.g: If an item is added between two others, there will be an animation of one item scrolling down and the new item fading in
* */
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