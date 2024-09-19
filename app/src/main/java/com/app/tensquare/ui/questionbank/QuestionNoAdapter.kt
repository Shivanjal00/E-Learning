package com.app.tensquare.ui.questionbank

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.tensquare.R
import com.app.tensquare.databinding.RowQuestionSelection1Binding
//import kotlinx.android.synthetic.main.row_question_selection.view.*
import java.util.*


class QuestionNoAdapter(
    private val context: Context,
    private val clickable: Boolean = false,
    private val list: ArrayList<QuesCount>,
    private val listener: OnQuestionSelection
) : RecyclerView.Adapter<QuestionNoAdapter.CustomerViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val binding =
            RowQuestionSelection1Binding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return CustomerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return this.list.size
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        val currentItem = list[position]
        holder.bind(currentItem, position)
    }


    inner class CustomerViewHolder(private val binding: RowQuestionSelection1Binding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: QuesCount, position: Int) {

            binding.apply {
                txtCount.text = item.no.toString()
                if (item.isSelected) {
                    txtCount.setTextColor(context.resources.getColor(R.color.gray_165))
                    imgBackgound.visibility = View.VISIBLE
                } else {
                    txtCount.setTextColor(context.resources.getColor(R.color.gray_30))
                    imgBackgound.visibility = View.INVISIBLE
                }

                if (item.isBookMarked) {
                    imgDot.visibility = View.VISIBLE
                } else {
                    imgDot.visibility = View.GONE
                }


                root.setOnClickListener {
                    if (!item.isSelected && clickable)
                        listener.onSelection(2, position)
                }

            }
            /*binding.apply {
                txtCount.text = item.no.toString()
                if (item.isSelected) {
                    txtCount.setTextColor(context.resources.getColor(R.color.gray_165))
                    root.rlCard.background =
                        context.resources.getDrawable(R.drawable.bg_selected_question_with_dot)
                } else {
                    txtCount.setTextColor(context.resources.getColor(R.color.gray_30))
                    root.rlCard.background =
                        context.resources.getDrawable(R.drawable.bg_unselected_question_with_dot)
                }

                root.setOnClickListener {
                    if (!item.isSelected && clickable)
                        listener.onSelection(2, position)
                }

            }*/

        }

    }

    interface OnQuestionSelection {
        fun onSelection(flag: Int, position: Int)
    }


}