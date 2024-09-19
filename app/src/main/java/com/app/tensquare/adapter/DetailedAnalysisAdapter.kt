package com.app.tensquare.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.tensquare.databinding.RowDetailedAnalysisBinding
import com.app.tensquare.ui.analysis.ChapterData


class DetailedAnalysisAdapter(private val listener: (flag: Int, subject: ChapterData) -> Unit) :
    ListAdapter<ChapterData, DetailedAnalysisAdapter.CustomerViewHolder>(DiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val binding =
            RowDetailedAnalysisBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return CustomerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }


    inner class CustomerViewHolder(private val binding: RowDetailedAnalysisBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ChapterData) {

            binding.apply {
                txtName.text = item.name
                txtDesc.text = item.description

                root.setOnClickListener {
                    listener.invoke(1, item)
                }
            }

        }

    }

    override fun submitList(list: List<ChapterData>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    class DiffCallback : DiffUtil.ItemCallback<ChapterData>() {
        override fun areItemsTheSame(old: ChapterData, aNew: ChapterData) =
            old._id == aNew._id

        override fun areContentsTheSame(old: ChapterData, aNew: ChapterData) =
            old == aNew
    }


}