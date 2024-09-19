package com.app.tensquare.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.tensquare.R
import com.app.tensquare.databinding.RowFaqsBinding
import com.app.tensquare.ui.appdetail.FAQ


class FaqsAdapter(
    private val context: Context,
    private val listener: (flag: Int, faq: FAQ) -> Unit
) :
    ListAdapter<FAQ, FaqsAdapter.CustomerViewHolder>(DiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val binding =
            RowFaqsBinding.inflate(
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


    inner class CustomerViewHolder(private val binding: RowFaqsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FAQ) {

            binding.apply {
                txtQues.text = item.question
                txtAns.text = item.answer

                imgAction.setOnClickListener {
                    txtAns.isVisible = !txtAns.isVisible
                    imgAction.setImageDrawable(
                        if (txtAns.isVisible)
                            context.getDrawable(
                                R.drawable.ic_remove
                            ) else
                            context.getDrawable(
                                R.drawable.ic_add_24
                            )
                    )
                }

            }

        }

    }

    override fun submitList(list: List<FAQ>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    class DiffCallback : DiffUtil.ItemCallback<FAQ>() {
        override fun areItemsTheSame(old: FAQ, aNew: FAQ) =
            old._id != aNew._id

        override fun areContentsTheSame(old: FAQ, aNew: FAQ) =
            old._id != aNew._id
        //old.isChecked != aNew.isChecked
    }


}