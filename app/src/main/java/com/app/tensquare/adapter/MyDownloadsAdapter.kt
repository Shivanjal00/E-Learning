package com.app.tensquare.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.tensquare.databinding.RowDownloadBinding
import java.util.*


class MyDownloadsAdapter(
    private val prefix: String,
    private val listener1: (MutableList<String>, str: String, position: Int, flag: Int) -> Unit
) :
    ListAdapter<String, MyDownloadsAdapter.CustomerViewHolder>(DiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val binding =
            RowDownloadBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem, position)
    }


    inner class CustomerViewHolder(private val binding: RowDownloadBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(item: String, position: Int) {

            binding.apply {
//                txtTitle.text = item

//                txtDesc.text = item.removePrefix(prefix)

                try {

                    var fileName =""
                    fileName = item.removePrefix(prefix)

                    val startIndex = fileName.length - 4
//                val substring = fileName.subSequence(startIndex,  fileName.length)
                    val substring = fileName.subSequence(0 , startIndex)
                    txtDesc.text = substring

                }catch (e : Exception){
                    e.printStackTrace()
                    txtDesc.text = item.removePrefix(prefix)
                }


            }

            binding.root.setOnClickListener {
                listener1.invoke(currentList.toMutableList(), item, position, 1)
            }
            binding.imgDelete.setOnClickListener {
                listener1.invoke(currentList.toMutableList(), item, position, 2)
            }

        }

    }


    class DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(old: String, aNew: String) =
            old == aNew

        override fun areContentsTheSame(old: String, aNew: String) =
            old == aNew
    }


}