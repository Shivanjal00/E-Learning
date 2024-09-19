package com.app.tensquare.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.tensquare.activity.RevisionVideoInSubjectActivity
import com.app.tensquare.base.ComingSoon
import com.app.tensquare.databinding.RowNotesInSubjectBinding
import com.app.tensquare.ui.notes.Notes
import com.app.tensquare.ui.notes.NotesInSubjectActivity
import com.app.tensquare.ui.session.PracticeSessionInSubjectActivity
import com.app.tensquare.utils.CHAPTER_LIST
import com.app.tensquare.utils.CHAPTER_LIST_MATHS
import com.app.tensquare.utils.CHAPTER_LIST_SCIENCE


class NotesInSubjectAdapter(
//    private val list: ArrayList<Notes>,
    private val comingSoon: ComingSoon,
    private val list: MutableList<Notes>,
    private val listener: (flag: Int, subject: Notes) -> Unit
) : RecyclerView.Adapter<NotesInSubjectAdapter.CustomerViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val binding =
            RowNotesInSubjectBinding.inflate(
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
        holder.bind(currentItem)
    }


    inner class CustomerViewHolder(private val binding: RowNotesInSubjectBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Notes) {

            binding.apply {
                txtComingSoon.text = comingSoon.message
//                txtName.text = item.title
//                txtDesc.text = item.description
                if (item.isClicked) {
                    clForeground.translationX = -200f
                } else {
                    clForeground.translationX = 0f
                }


                /*clForeground.setOnClickListener {
                    if (it.translationX == -llAction.width.toFloat())
                        it.animate().translationX(0f)
                    else
                        it.animate().translationX(-llAction.width.toFloat())
                }*/

                // -------------- Hide dwn & share
                clForeground.setOnClickListener{
                    listener.invoke(3, item)
                }

                imgDownload.setOnClickListener {
                    listener.invoke(1, item)
                }

                imgShare.setOnClickListener {
                    listener.invoke(2, item)
                }



                //---------------------end

                rlCard.setOnLongClickListener {
                    showMenu(position)
                    true
                }

                try {
                    txtPdfTitle.text = item.fileName[0]
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            if (NotesInSubjectActivity.isSubscribed == true){
                binding.imgLock.visibility = View.GONE
                binding.imgShare.visibility = View.VISIBLE
                binding.imgDownload.visibility = View.VISIBLE
            }else{
                if (item.lock == 0){
                    binding.imgLock.visibility = View.VISIBLE
                    binding.imgShare.visibility = View.GONE
                    binding.imgDownload.visibility = View.GONE
                }else{
                    binding.imgLock.visibility = View.GONE
                    binding.imgShare.visibility = View.VISIBLE
                    binding.imgDownload.visibility = View.VISIBLE
                }
            }

            Log.e("Notes-list => " , itemCount.toString())
            if (NotesInSubjectActivity.isShowComing == true){

                if (NotesInSubjectActivity.isListEmpty == true){

                    if (NotesInSubjectActivity.selectSubCode.equals("0")){
                        if (itemCount < CHAPTER_LIST_MATHS){
                            if (position == itemCount - 1) {
                                binding.txtComingSoon.visibility = View.VISIBLE
                            } else {
                                binding.txtComingSoon.visibility = View.GONE
                            }
                        }
                    }else if (NotesInSubjectActivity.selectSubCode.equals("1")){
                        if (itemCount < CHAPTER_LIST_SCIENCE){
                            if (position == itemCount - 1) {
                                binding.txtComingSoon.visibility = View.VISIBLE
                            } else {
                                binding.txtComingSoon.visibility = View.GONE
                            }
                        }
                    }

                }

            }


        }

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


}