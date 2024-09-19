package com.app.tensquare.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.tensquare.databinding.RowSubjectSelectionToEnrolBinding
import com.app.tensquare.ui.initialsetup.SubjectData
import java.util.*


private const val IS_CHECKED = "is.checked"

class SubjectSelectionAdapter(private val listener: (items: MutableList<SubjectData>, checked: Boolean, subject: SubjectData) -> Unit) :
    ListAdapter<SubjectData, SubjectSelectionAdapter.CustomerViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val binding =
            RowSubjectSelectionToEnrolBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return CustomerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, pos: Int) {
        onBindViewHolder(holder, pos, Collections.emptyList())
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int, payload: List<Any>) {
        val currentItem = getItem(position)

        if (payload.isEmpty() || payload[0] !is Bundle) {
            holder.bind(currentItem)
        } else {
            val bundle = payload[0] as Bundle
            holder.update(bundle)
        }

    }


    inner class CustomerViewHolder(private val binding: RowSubjectSelectionToEnrolBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SubjectData) {

            binding.apply {
                txtSubjectName.text = item.name

                imgSelection.isVisible = item.isSelected
                /*if (item.isSelected) {
                    rlCard.setBackgroundResource(R.drawable.bg_rect_black_stroke)
                } else {
                    rlCard.setBackgroundResource(R.drawable.bg_rect_gray_stroke)
                }*/
            }

            binding.rlCard.setOnClickListener {
                if (!binding.imgSelection.isVisible)
                    listener(currentList.toMutableList(), !binding.imgSelection.isVisible, item)
            }

        }

        fun update(bundle: Bundle) {
            if (bundle.containsKey(IS_CHECKED)) {
                val checked = bundle.getBoolean(IS_CHECKED)
                binding.imgSelection.isVisible = checked
                /*if (checked) {
                    binding.rlCard.setBackgroundResource(R.drawable.bg_rect_black_stroke)
                } else {
                    binding.rlCard.setBackgroundResource(R.drawable.bg_rect_gray_stroke)
                }*/
            }
        }

    }

    class DiffCallback : DiffUtil.ItemCallback<SubjectData>() {

        override fun areItemsTheSame(oldItem: SubjectData, newItem: SubjectData): Boolean {
            return oldItem._id == newItem._id
        }

        override fun areContentsTheSame(oldItem: SubjectData, newItem: SubjectData): Boolean {
            return oldItem == newItem
            /*return when {
                oldItem.isSelected != newItem.isSelected -> false
                oldItem.name != newItem.name -> false
                oldItem.id != newItem.id -> false
                else -> true
            }*/
        }

        override fun getChangePayload(oldItem: SubjectData, newItem: SubjectData): Any? {
            if (oldItem._id == newItem._id) {
                return if (oldItem.isSelected == newItem.isSelected) {
                    super.getChangePayload(oldItem, newItem)
                } else {
                    val diff = Bundle()
                    diff.putBoolean(IS_CHECKED, newItem.isSelected)
                    diff
                }
            }

            return super.getChangePayload(oldItem, newItem)
        }


    }


}