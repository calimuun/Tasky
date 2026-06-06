package com.calielian.tasky.recyclercomponents

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.calielian.tasky.databinding.OnboardScreenLastPageLayoutBinding
import com.calielian.tasky.databinding.OnboardScreenPageLayoutBinding
import com.calielian.tasky.utils.OnboardPageItem

class OnboardingAdapter(
	private val items: List<OnboardPageItem>,
	private val onOnboardingFinish: (String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

	companion object {
		private const val TYPE_NORMAL = 0
		private const val TYPE_LAST = 1
	}

	override fun getItemViewType(position: Int): Int {
		return if (items[position].title.isEmpty()) TYPE_LAST else TYPE_NORMAL
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		val inflater = LayoutInflater.from(parent.context)

		return if (viewType == TYPE_LAST) {
			val binding = OnboardScreenLastPageLayoutBinding.inflate(inflater, parent, false)
			LastPageViewHolder(binding, onOnboardingFinish)
		} else {
			val binding = OnboardScreenPageLayoutBinding.inflate(inflater, parent, false)
			NormalPageViewHolder(binding)
		}
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		val item = items[position]
		if (holder is NormalPageViewHolder) {
			holder.bind(item)
		} else if (holder is LastPageViewHolder) {
			holder.bind()
		}
	}

	override fun getItemCount(): Int = items.size

	class NormalPageViewHolder(private val binding: OnboardScreenPageLayoutBinding) :
		RecyclerView.ViewHolder(binding.root) {
		fun bind(item: OnboardPageItem) {
			binding.title.text = item.title
			binding.description.text = item.description
			binding.image.setImageResource(item.imageResId)
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