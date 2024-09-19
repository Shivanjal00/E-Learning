package com.app.tensquare.ui.session

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.tensquare.base.ComingSoon
import com.app.tensquare.databinding.RowPracticeSessionInSubjectBinding
import com.app.tensquare.ui.chapter.ChaptersInSubjectActivity
import com.app.tensquare.utils.AppUtills.redirectToEnrollment
import com.app.tensquare.utils.CHAPTER_LIST
import com.app.tensquare.utils.CHAPTER_LIST_MATHS
import com.app.tensquare.utils.CHAPTER_LIST_SCIENCE
import java.util.*

private const val IS_CHECKED = "is.checked"

class PracticeSessionInSubjectAdapter(private val listener: (items: MutableList<PracticeSession>, isChecked: Boolean, subject: PracticeSession) -> Unit) :
    ListAdapter<PracticeSession, PracticeSessionInSubjectAdapter.CustomerViewHolder>(DiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val binding =
            RowPracticeSessionInSubjectBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return CustomerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    override fun onBindViewHolder(
        holder: PracticeSessionInSubjectAdapter.CustomerViewHolder,
        pos: Int
    ) {
        onBindViewHolder(holder, pos, Collections.emptyList())
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


    inner class CustomerViewHolder(private val binding: RowPracticeSessionInSubjectBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PracticeSession) {

            binding.apply {
                txtName.text = item.name

//                if (item.description.isNotEmpty())
//                    txtDesc.text = item.description
//                else
//                    txtDesc.visibility = View.GONE

                //checkBox.isVisible = item.isChecked
                checkBox.isChecked = item.isChecked
                checkBox.setOnCheckedChangeListener { _, isChecked ->
                    listener.invoke(currentList.toMutableList(), isChecked, item)
                }

                imgLock.setOnClickListener {
                    binding.root.context.redirectToEnrollment()
                }
            }

            if (PracticeSessionInSubjectActivity.isSubscribed == true){
                binding.checkBox.isEnabled = true
                binding.checkBox.visibility = View.VISIBLE
                binding.imgLock.visibility = View.GONE
            }else{
                if (item.lock == 0){
                    binding.checkBox.visibility = View.GONE
                    binding.imgLock.visibility = View.VISIBLE
                    binding.checkBox.isEnabled = false
                }else{
                    binding.checkBox.visibility = View.VISIBLE
                    binding.imgLock.visibility = View.GONE
                    binding.checkBox.isEnabled = true
                }
            }


            Log.e("Sub-list => " , itemCount.toString())
            if (PracticeSessionInSubjectActivity.selectSubCode.equals("0")){
                if (itemCount < CHAPTER_LIST_MATHS){
                    if (position == itemCount - 1) {
//                        binding.txtComingSoon.visibility = View.VISIBLE
                    } else {
                        binding.txtComingSoon.visibility = View.GONE
                    }
                }
            }else if (PracticeSessionInSubjectActivity.selectSubCode.equals("1")){
                if (itemCount < CHAPTER_LIST_SCIENCE){
                    if (position == itemCount - 1) {
//                        binding.txtComingSoon.visibility = View.VISIBLE
                    } else {
                        binding.txtComingSoon.visibility = View.GONE
                    }
                }
            }


        }

        fun update(bundle: Bundle) {
            if (bundle.containsKey(IS_CHECKED)) {
                val checked = bundle.getBoolean(IS_CHECKED)
                //binding.checkBox.isVisible = checked
                binding.checkBox.isChecked = checked
            }
        }

    }

    /* override fun submitList(list: List<PracticeSession>?) {
         super.submitList(list?.let { ArrayList(it) })
     }*/

    class DiffCallback : DiffUtil.ItemCallback<PracticeSession>() {
        override fun areItemsTheSame(old: PracticeSession, aNew: PracticeSession) =
            old._id == aNew._id

        override fun areContentsTheSame(old: PracticeSession, aNew: PracticeSession) =
            //This method is called by diffutil only if areItemsTheSame method returns true.
            old == aNew

        override fun getChangePayload(oldItem: PracticeSession, newItem: PracticeSession): Any? {
            if (oldItem._id == newItem._id) {
                return if (oldItem.isChecked == newItem.isChecked) {
                    super.getChangePayload(oldItem, newItem)
                } else {
                    val diff = Bundle()
                    diff.putBoolean(IS_CHECKED, newItem.isChecked)
                    diff
                }
            }

            return super.getChangePayload(oldItem, newItem)
        }
    }


}