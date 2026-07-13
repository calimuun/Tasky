package com.calimuun.tasky.recyclercomponents

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.calimuun.tasky.database.RoutineEntity
import com.calimuun.tasky.databinding.TaskLayoutBinding

class RoutineAdapter: ListAdapter<RoutineEntity, ViewHolders>(RoutineComparator()) {

	var onCheckedChange: ((RoutineEntity) -> Unit)? = null
	var onClick: ((RoutineEntity) -> Unit)? = null

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolders {
		val binding = TaskLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return ViewHolders(binding)
	}

	override fun onBindViewHolder(viewHolder: ViewHolders, position: Int) {
		viewHolder.bindRoutine(getItem(position), onCheckedChange!!, onClick!!)
	}

	class RoutineComparator : DiffUtil.ItemCallback<RoutineEntity>() {
		override fun areItemsTheSame(old: RoutineEntity, new: RoutineEntity) =
			old.id == new.id

		override fun areContentsTheSame(old: RoutineEntity, new: RoutineEntity) =
			old == new
	}
}