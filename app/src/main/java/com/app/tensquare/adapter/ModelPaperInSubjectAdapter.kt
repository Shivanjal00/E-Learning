package com.app.tensquare.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.tensquare.databinding.RowModelPaperInSubjectBinding
import com.app.tensquare.ui.paper.ModelPaper
import com.app.tensquare.ui.paper.PreviousYearPaper
import com.app.tensquare.utils.AppUtills.redirectToEnrollment


class ModelPaperInSubjectAdapter(
    private val list: MutableList<ModelPaper>,
    private val listener: (flag: Int, modelPaper: Any) -> Unit
) : RecyclerView.Adapter<ModelPaperInSubjectAdapter.CustomerViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val binding =
            RowModelPaperInSubjectBinding.inflate(
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
        var currentItem = list[position]
        /*currentItem = if (getItem(position) is ModelPaper)
            getItem(position) as ModelPaper
        else
            getItem(position) as PreviousYearPaper*/
        holder.bind(currentItem)
    }

    fun showMenu(position: Int) {
        for (i in list.indices) {
            list[i].isClicked = false
        }
        list[position].isClicked = true
        notifyDataSetChanged()
    }


    fun isMenuShown(): Boolean {
        for (i in list.indices) {
            if (list[i].isClicked) {
                return true
            }
        }
        return false
    }

    fun closeMenu() {
        for (i in list.indices) {
            list[i].isClicked = false
        }
        notifyDataSetChanged()
    }

    inner class CustomerViewHolder(private val binding: RowModelPaperInSubjectBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Any) {

            binding.apply {
                if (item is ModelPaper) {
                    item as ModelPaper
//                    txtName.text = item.title
//                    txtDesc.text = item.subTitle

//                    if(item.title.isNotEmpty() && item.subTitle.isNotEmpty()){
//                        txtName.text = item.title + " | " + item.subTitle
//                    }else if (item.title.isNotEmpty()){
                        txtName.text = item.title
//                    }else if (item.subTitle.isNotEmpty()){
//                        txtName.text = item.subTitle
//                    }




                    if (item.isClicked) {
                        clForeground.translationX = -200f
                    } else {
                        clForeground.translationX = 0f
                    }

                    try {
                        txtPdfTitle.text = item.fileName
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    if(item.enrollmentPlanStatus){
                        imgLock.visibility = View.GONE
                        imgShare.visibility = View.VISIBLE
                        imgDownload.visibility = View.VISIBLE
                    }else{
                        imgLock.visibility = View.VISIBLE
                        imgShare.visibility = View.GONE
                        imgDownload.visibility = View.GONE
                    }

                } else {
                    item as PreviousYearPaper
                    txtName.text = item.title
                    txtDesc.text = item.boardName
                }


                /*clForeground.setOnClickListener {
                    if (it.translationX == -llAction.width.toFloat())
                        it.animate().translationX(0f)
                    else
                        it.animate().translationX(-llAction.width.toFloat())
                }*/

                // ---Change hide
                clForeground.setOnClickListener {
                    if ((item as ModelPaper).enrollmentPlanStatus) listener.invoke(1, item) else root.context.redirectToEnrollment()
                }

                imgShare.setOnClickListener {
                    listener.invoke(2, item)
                }

                imgDownload.setOnClickListener {
                    listener.invoke(3, item)
                }

//                llAction.setOnClickListener {
//                    listener.invoke(1, item)
//                }
                //--------------End

            }

        }

    }

    /* override fun submitList(list: List<PracticeSession>?) {
         super.submitList(list?.let { ArrayList(it) })
     }*/

    /*class DiffCallback : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(old: Any, aNew: Any): Boolean {
            return if (old is ModelPaper) {
                old as ModelPaper
                aNew as ModelPaper
                old._id != aNew._id
            } else {
                old as PreviousYearPaper
                aNew as PreviousYearPaper
                old._id != aNew._id
            }

        }


        override fun areContentsTheSame(old: Any, aNew: Any): Boolean {
            return if (old is ModelPaper) {
                old as ModelPaper
                aNew as ModelPaper
                old == aNew
            } else {
                old as PreviousYearPaper
                aNew as PreviousYearPaper
                old._id == aNew._id
            }

        }
    }*/


}