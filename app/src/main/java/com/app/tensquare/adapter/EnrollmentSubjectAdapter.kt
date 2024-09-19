package com.app.tensquare.adapter

import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.tensquare.R
import com.app.tensquare.databinding.*
import com.app.tensquare.ui.subscription.EnrolmentPlanPricingNewActivity
import com.app.tensquare.ui.subscription.EnrolmentSubject
import java.lang.StringBuilder
import java.util.*

private const val IS_CHECKED = "is.checked"

class EnrollmentSubjectAdapter(
    private val context: Context,
    private val listener: (items: MutableList<EnrolmentSubject>, checked: Boolean, subject: EnrolmentSubject) -> Unit
) :
    ListAdapter<EnrolmentSubject, EnrollmentSubjectAdapter.CustomerViewHolder>(DiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val binding =
            RowEnrolPlanNewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return CustomerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, pos: Int) {
        onBindViewHolder(holder, pos, Collections.emptyList())
    }

    override fun onBindViewHolder(
        holder: CustomerViewHolder,
        position: Int,
        payload: List<Any>
    ) {
        val currentItem = getItem(position)
        if (payload.isEmpty() || payload[0] !is Bundle) {
            holder.bind(currentItem, position)
        } else {
            val bundle = payload[0] as Bundle
            holder.update(bundle)
        }
    }


//    inner class CustomerViewHolder(private val binding: RowSubjectSelectionToEnrolNewBinding) :
    inner class CustomerViewHolder(private val binding: RowEnrolPlanNewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: EnrolmentSubject, position: Int) {

            binding.apply {
                txtClassName.text =
                    "${item.className}" + " Class"

               /* if (item.subjectName.size > 1) {
                    val strBuilder = StringBuilder()
                    item.subjectName.forEach {
                        strBuilder.append(
                            when {
                                strBuilder.isEmpty() -> it
                                else -> "+$it"
                            }
                        )
                    }
                    txtSubjectName.text = strBuilder.toString()
                } else {
                    txtSubjectName.text = item.subjectName[0]
                }*/

                txtSubjectName.text = item.name
                txtOfferDec.text = item.offer

                txtActualPrice.text = "₹ ${item.crossAmount} /-"
//                txtOfferPrice.text = "₹ ${item.finalAmount} /-"
                txtOfferPriceSen.text = "₹ ${item.finalAmount} /-"
                tvExDate.text = item.expiredDate

                binding.txtActualPrice.also {
                    it.paintFlags = it.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                }
                if (item.isSelected) {
//                    binding.rlCard.setBackgroundResource(R.drawable.bg_selected_plan)
//                    binding.rlCard.setBackgroundResource(R.drawable.selected_plan_green_bg_2)
                    binding.ivSetBg.setImageResource(R.drawable.plan_selected_bg)
                } else {
//                    binding.rlCard.setBackgroundResource(R.drawable.bg_rect_gray_stroke)
//                    binding.rlCard.setBackgroundResource(R.drawable.plan_green_bg)
                    binding.ivSetBg.setImageResource(R.drawable.plan_unselected_bg)
                }

                if (item.currentPlan){
                    binding.tvActive.visibility = View.VISIBLE
                }else{
                    binding.tvActive.visibility = View.GONE
                }

                root.setOnClickListener {

                    if (!item.currentPlan){
                        listener.invoke(
                            currentList.toMutableList(),
                            true,
                            item
                        )
                        EnrolmentPlanPricingNewActivity.txtContinue?.visibility = View.VISIBLE
                    }

                }


            }

        }

        fun update(bundle: Bundle) {
            if (bundle.containsKey(IS_CHECKED)) {
                val isSelected = bundle.getBoolean(IS_CHECKED)
                //binding.imgSelection.isVisible = isSelected
                if (isSelected) {
//                    binding.rlCard.setBackgroundResource(R.drawable.bg_selected_plan)
//                    binding.rlCard.setBackgroundResource(R.drawable.selected_plan_green_bg_2)
                    binding.ivSetBg.setImageResource(R.drawable.plan_selected_bg)
                } else {
//                    binding.rlCard.setBackgroundResource(R.drawable.bg_rect_gray_stroke)
//                    binding.rlCard.setBackgroundResource(R.drawable.plan_green_bg)
                    binding.ivSetBg.setImageResource(R.drawable.plan_unselected_bg)
                }
            }
        }

    }

    /* override fun submitList(list: List<PracticeSession>?) {
         super.submitList(list?.let { ArrayList(it) })
     }*/

    class DiffCallback : DiffUtil.ItemCallback<EnrolmentSubject>() {
        override fun areItemsTheSame(old: EnrolmentSubject, aNew: EnrolmentSubject) =
            old._id == aNew._id

        override fun areContentsTheSame(old: EnrolmentSubject, aNew: EnrolmentSubject) =
            old == aNew

        override fun getChangePayload(oldItem: EnrolmentSubject, newItem: EnrolmentSubject): Any? {
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