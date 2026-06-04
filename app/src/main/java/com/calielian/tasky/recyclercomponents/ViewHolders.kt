package com.calielian.tasky.recyclercomponents

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.calielian.tasky.database.RoutineEntity
import com.calielian.tasky.database.TaskCompletedEntity
import com.calielian.tasky.database.TaskEntity
import com.calielian.tasky.databinding.TaskLayoutBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ViewHolders(private val binding: TaskLayoutBinding): RecyclerView.ViewHolder(binding.root) {

	private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
	private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

	fun bindTask(task: TaskEntity, onCheckedChange: (TaskEntity) -> Unit, onClick: (TaskEntity) -> Unit) {
		var dateText = ""
		binding.itemName.text = task.title

		if (!task.description.isNullOrBlank()) {
			binding.itemDescription.text = task.description
		} else {
			binding.itemDescription.visibility = View.GONE
		}

		if (task.date != null) {
			dateText += task.date.format(dateFormatter)
		}

		if (task.time != null) {
			dateText += " " + task.time.format(timeFormatter)
			dateText = dateText.trim()
		}

		if (dateText.isNotBlank()) {
			binding.itemDate.text = dateText
		} else {
			binding.itemDate.visibility = View.GONE
		}

		binding.radioButton.setOnCheckedChangeListener { _, _ ->
			onCheckedChange(task)
		}

		binding.root.setOnClickListener {
			onClick(task)
		}
 	}

	fun bindRoutine(routine: RoutineEntity, onCheckedChange: (RoutineEntity) -> Unit, onClick: (RoutineEntity) -> Unit) {
		var dateText = ""
		binding.itemName.text = routine.title

		if (!routine.description.isNullOrBlank()) {
			binding.itemDescription.text = routine.description
		} else {
			binding.itemDescription.visibility = View.GONE
		}

		dateText += routine.date.format(dateFormatter)

		dateText += " " + routine.time.format(timeFormatter)
		dateText = dateText.trim()

		binding.itemDate.text = dateText

		val now = LocalDate.now()
		val isNow = routine.date.isEqual(now)

		binding.radioButton.setOnCheckedChangeListener(null)

		binding.radioButton.isChecked = routine.checked

		binding.radioButton.isEnabled = isNow

		binding.radioButton.setOnCheckedChangeListener { _, isChecked ->
			if (isChecked && isNow) {
				onCheckedChange(routine)
			}
		}

		binding.root.setOnClickListener {
			onClick(routine)
		}
	}

	fun bindCompletedTask(task: TaskCompletedEntity, onCheckedChange: (TaskCompletedEntity) -> Unit) {
		var dateText = ""
		binding.itemName.text = task.title

		if (!task.description.isNullOrBlank()) {
			binding.itemDescription.text = task.description
		} else {
			binding.itemDescription.visibility = View.GONE
		}

		if (task.date != null) {
			dateText += task.date.format(dateFormatter)
		}

		if (task.time != null) {
			dateText += " " + task.time.format(timeFormatter)
			dateText = dateText.trim()
		}

		if (dateText != "") {
			binding.itemDate.text = dateText
		} else {
			binding.itemDate.visibility = View.GONE
		}

		binding.radioButton.isChecked = true

		binding.radioButton.setOnCheckedChangeListener { _, _ ->
			onCheckedChange(task)
		}
	}
}