package com.app.tensquare.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.tensquare.databinding.*
import com.app.tensquare.ui.analysis.TopicsForImprovement
import javax.inject.Inject


class TopicAdapter @Inject constructor() :
    ListAdapter<TopicsForImprovement, TopicAdapter.CustomerViewHolder>(DiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val binding =
            RowTopicBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return CustomerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem, position)
    }


    inner class CustomerViewHolder(private val binding: RowTopicBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TopicsForImprovement, position: Int) {

            binding.apply {
                txtName.text = item.name
            }

        }

    }

    /* override fun submitList(list: List<PracticeSession>?) {
         super.submitList(list?.let { ArrayList(it) })
     }*/

    class DiffCallback : DiffUtil.ItemCallback<TopicsForImprovement>() {
        override fun areItemsTheSame(old: TopicsForImprovement, aNew: TopicsForImprovement) =
            old._id == aNew._id

        override fun areContentsTheSame(old: TopicsForImprovement, aNew: TopicsForImprovement) =
            old == aNew
    }


}