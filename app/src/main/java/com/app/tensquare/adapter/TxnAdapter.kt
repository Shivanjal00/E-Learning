package com.app.tensquare.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.tensquare.databinding.*
import com.app.tensquare.ui.transaction.Transaction


class TxnAdapter(private val listener: (flag: Int, subject: Transaction) -> Unit) :
    ListAdapter<Transaction, TxnAdapter.CustomerViewHolder>(DiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val binding =
            RowTxnBinding.inflate(
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


    inner class CustomerViewHolder(private val binding: RowTxnBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Transaction) {
            binding.apply {
                txtQues.text = "NA"
                txtAns.text = item.createdAt
                txtAmt.text = item.amount
            }
        }

    }

    override fun submitList(list: List<Transaction>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    class DiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(old: Transaction, aNew: Transaction) =
            old._id != aNew._id

        override fun areContentsTheSame(old: Transaction, aNew: Transaction) =
            old == aNew
    }


}