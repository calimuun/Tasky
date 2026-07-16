package com.calimuun.tasky.recyclercomponents

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.calimuun.tasky.database.RoutineEntity
import com.calimuun.tasky.database.TaskCompletedEntity
import com.calimuun.tasky.database.TaskEntity
import com.calimuun.tasky.databinding.TaskLayoutBinding
import com.google.android.material.color.MaterialColors
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.google.android.material.R as MaterialR

/*
* This is a RecyclerView component named "Adapter"
* An Adapter is responsible for providing data to the RecyclerView and creating views for the items
* And after creating views for the items, the Adapter binds the data to the views via an ViewHolder
* */
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

		if (task.date != null || task.time != null) {
			binding.itemDate.visibility = View.VISIBLE
		} else {
			binding.itemDate.visibility = View.GONE
		}

		binding.radioButton.setOnCheckedChangeListener { _, _ ->
			onCheckedChange(task)
		}

		binding.root.setOnClickListener {
			onClick(task)
		}

		val colorError = MaterialColors.getColor(
			binding.root,
			MaterialR.attr.colorErrorContainer
		)

		val colorNormal = MaterialColors.getColor(
			binding.root,
			MaterialR.attr.colorSurfaceContainer
		)

		if (task.date != null && task.time != null) {
			if (LocalDateTime.of(task.date, task.time).isBefore(LocalDateTime.now())) {
				binding.background.setCardBackgroundColor(colorError)
			} else {
				binding.background.setCardBackgroundColor(colorNormal)
			}
		} else if (task.date != null) {
			if (task.date.isBefore(LocalDate.now())) {
				binding.background.setCardBackgroundColor(colorError)
			} else {
				binding.background.setCardBackgroundColor(colorNormal)
			}
		} else if (task.time != null) {
			if (task.time.isBefore(LocalDateTime.now().toLocalTime())) {
				binding.background.setCardBackgroundColor(colorError)
			} else {
				binding.background.setCardBackgroundColor(colorNormal)
			}
		} else {
			binding.background.setCardBackgroundColor(colorNormal)
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