package com.app.tensquare.adapter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.tensquare.databinding.RowSubjectSelfAnalysisBinding
import com.app.tensquare.ui.initialsetup.SubjectData
import com.app.tensquare.utils.*
import java.util.*

private const val IS_SELECTED = "is.selected"

class SelfAnalysisSubjectAdapter(private val listener: (items: MutableList<SubjectData>, checked: Boolean, subject: SubjectData) -> Unit) :
    ListAdapter<SubjectData, SelfAnalysisSubjectAdapter.CustomerViewHolder>(DiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val binding =
            RowSubjectSelfAnalysisBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return CustomerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        onBindViewHolder(holder, position, Collections.emptyList())
        /*val currentItem = getItem(position)
        holder.bind(currentItem)*/
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    override fun onBindViewHolder(
        holder: SelfAnalysisSubjectAdapter.CustomerViewHolder,
        position: Int,
        payload: List<Any>
    ) {
        val currentItem = getItem(position)

        if (payload.isEmpty() || payload[0] !is Bundle) {
            holder.bind(currentItem)
        } else {
            val bundle = payload[0] as Bundle
            holder.update(bundle)
        }

    }

    inner class CustomerViewHolder(private val binding: RowSubjectSelfAnalysisBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(item: SubjectData) {

            binding.apply {
                txtName.text = item.name

                setSubjectImage(item.value, imgImage)
                viewSelection.isVisible = item.isSelected
            }

            binding.root.setOnClickListener {
                Log.e("setOnClickListener","setOnClickListener = ${binding.viewSelection.isVisible}")
                listener.invoke(currentList.toMutableList(), !binding.viewSelection.isVisible, item)
            }

        }

        fun update(bundle: Bundle) {
            if (bundle.containsKey(IS_SELECTED)) {
                val checked = bundle.getBoolean(IS_SELECTED)
                binding.viewSelection.isVisible = checked
                /*if (checked) {
                    binding.rlCard.setBackgroundResource(R.drawable.bg_rect_black_stroke)
                } else {
                    binding.rlCard.setBackgroundResource(R.drawable.bg_rect_gray_stroke)
                }*/
            }
        }

        private fun setSubjectImage(subjectValue: Int, imageView: ImageView) {
            when (subjectValue) {
                SUBJECT_MATHS -> {
//                    imageView.setImageResource(com.app.tensquare.R.drawable.img_maths)
                    imageView.setImageResource(com.app.tensquare.R.drawable.img_maths)
                }
                SUBJECT_SCIENCE -> {
                    imageView.setImageResource(com.app.tensquare.R.drawable.img_science)
                }
                SUBJECT_PHYSICS -> {
                    imageView.setImageResource(com.app.tensquare.R.drawable.img_physics)
                }
                SUBJECT_CHEMISTRY -> {
                    imageView.setImageResource(com.app.tensquare.R.drawable.img_chemistry)
                }
                SUBJECT_BIOLOGY -> {
                    imageView.setImageResource(com.app.tensquare.R.drawable.img_biology)
                }
                SUBJECT_SANSKRIT -> {
                    imageView.setImageResource(com.app.tensquare.R.drawable.san_img_2)
                }
                SUBJECT_HINDI -> {
                    imageView.setImageResource(com.app.tensquare.R.drawable.hindi_img_2)
                }
                SUBJECT_SOCIAL_STUDIES -> {
                    imageView.setImageResource(com.app.tensquare.R.drawable.ss_img_2)
                }
                SUBJECT_ENGLISH -> {
                    imageView.setImageResource(com.app.tensquare.R.drawable.eng_img_2)
                }

            }
        }

    }

    /* override fun submitList(list: List<SubjectData>?) {
         super.submitList(list?.let { ArrayList(it) })
     }*/

    class DiffCallback : DiffUtil.ItemCallback<SubjectData>() {
        override fun areItemsTheSame(old: SubjectData, aNew: SubjectData) =
            old._id == aNew._id /*&& old.isSelected == aNew.isSelected*/

        override fun areContentsTheSame(old: SubjectData, aNew: SubjectData) =
            old == aNew /*&& old.isSelected == aNew.isSelected*/

        override fun getChangePayload(
            oldItem: SubjectData,
            newItem: SubjectData
        ): Any? {
            if (oldItem._id == newItem._id) {
                return if (oldItem.isSelected == newItem.isSelected) {
                    super.getChangePayload(oldItem, newItem)
                } else {
                    val diff = Bundle()
                    diff.putBoolean(IS_SELECTED, newItem.isSelected)
                    diff
                }
            }

            return super.getChangePayload(oldItem, newItem)
        }
    }


}