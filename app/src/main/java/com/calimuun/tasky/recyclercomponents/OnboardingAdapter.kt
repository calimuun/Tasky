package com.calimuun.tasky.recyclercomponents

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.calimuun.tasky.databinding.OnboardScreenLastPageLayoutBinding
import com.calimuun.tasky.databinding.OnboardScreenPageLayoutBinding
import com.calimuun.tasky.databinding.OnboardScreenPermissionPageLayoutBinding
import com.calimuun.tasky.utils.OnboardPageItem

/*
* This is a RecyclerView component named "Adapter"
* An Adapter is responsible for providing data to the RecyclerView and creating views for the items
* And after creating views for the items, the Adapter binds the data to the views via an ViewHolder
* */
class OnboardingAdapter(
	private val items: List<OnboardPageItem>,
	private val onOnboardingFinish: (String) -> Unit,
	private val onRequestNotificationPermission: () -> Unit,
	private val onRequestAlarmPermission: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

	enum class PageType { NORMAL, PERMISSION , LAST }

	override fun getItemViewType(position: Int): Int {
		return when (items[position].title) {
			"" -> PageType.LAST.ordinal
			"Permission" -> PageType.PERMISSION.ordinal
			else -> PageType.NORMAL.ordinal
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		val inflater = LayoutInflater.from(parent.context)

		return when (viewType) {
			PageType.LAST.ordinal -> {
				val binding = OnboardScreenLastPageLayoutBinding.inflate(inflater, parent, false)
				LastPageViewHolder(binding, onOnboardingFinish)
			}

			PageType.PERMISSION.ordinal -> {
				val binding = OnboardScreenPermissionPageLayoutBinding.inflate(inflater, parent, false)
				PermissionPageViewHolder(binding, onRequestNotificationPermission, onRequestAlarmPermission)
			}

			else -> {
				val binding = OnboardScreenPageLayoutBinding.inflate(inflater, parent, false)
				NormalPageViewHolder(binding)
			}
		}
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		val item = items[position]

		when (holder) {
			is NormalPageViewHolder -> {
				holder.bind(item)
			}

			is PermissionPageViewHolder -> {
				holder.bind()
			}

			is LastPageViewHolder -> {
				holder.bind()
			}
		}

	}

	override fun getItemCount(): Int = items.size

	class NormalPageViewHolder(private val binding: OnboardScreenPageLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
		fun bind(item: OnboardPageItem) {
			binding.title.text = item.title
			binding.description.text = item.description
			binding.image.setImageResource(item.imageResId)
		}
	}

	class PermissionPageViewHolder(
		private val binding: OnboardScreenPermissionPageLayoutBinding,
		private val onRequestNotificationPermission: () -> Unit,
		private val onRequestAlarmPermission: () -> Unit
	) : RecyclerView.ViewHolder(binding.root) {
		fun bind() {
			binding.notificationPermissionButton.setOnClickListener	{
				onRequestNotificationPermission()
			}

			binding.alarmPermissionButton.setOnClickListener {
				onRequestAlarmPermission()
			}
		}
	}

	class LastPageViewHolder(
		private val binding: OnboardScreenLastPageLayoutBinding,
		private val onOnboardingFinish: (String) -> Unit
	) : RecyclerView.ViewHolder(binding.root) {
		fun bind() {
			binding.continueButton.setOnClickListener {
				val username = binding.usernameInput.text.toString().trim()
				onOnboardingFinish(username)
			}
		}
	}
}