package com.app.tensquare.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.tensquare.databinding.*
import com.app.tensquare.ui.notification.NotificationData


class NotificationAdapter(
    val context: Context,
    private val listener: (MutableList<NotificationData>, position: Int, notification: NotificationData) -> Unit
) :
    ListAdapter<NotificationData, NotificationAdapter.CustomerViewHolder>(DiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val binding =
            RowNotificationBinding.inflate(
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


    inner class CustomerViewHolder(private val binding: RowNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: NotificationData, position: Int) {

            binding.apply {
                txtName.text = item.title
                txtDesc.text = item.description
                txtDateTime.text = item.createdAt

                //Glide.with(context).load(item.image).into(imgProfile)

                clForeground.setOnClickListener {
                    if (it.translationX == -llAction.width.toFloat())
                        it.animate().translationX(0f)
                    else
                        it.animate().translationX(-llAction.width.toFloat())
                }

                llAction.setOnClickListener {
                    listener.invoke(currentList.toMutableList(), position, item)
                }
            }

        }

    }

    /* override fun submitList(list: List<PracticeSession>?) {
         super.submitList(list?.let { ArrayList(it) })
     }*/

    class DiffCallback : DiffUtil.ItemCallback<NotificationData>() {
        override fun areItemsTheSame(old: NotificationData, aNew: NotificationData) =
            old.id == aNew.id

        override fun areContentsTheSame(old: NotificationData, aNew: NotificationData) =
            old.isExpanded == aNew.isExpanded
    }


}