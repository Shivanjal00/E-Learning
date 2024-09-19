package com.app.tensquare.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.tensquare.databinding.RowChapterInSubjectBinding
import com.app.tensquare.ui.chapter.ChaptersInSubjectActivity
import com.app.tensquare.ui.session.PracticeSession
import com.app.tensquare.utils.CHAPTER_LIST_MATHS
import com.app.tensquare.utils.CHAPTER_LIST_SCIENCE

class ChapterInSubjectAdapter(private val revisionVideoStatus: Int = 1, private val listener: (flag: Int, practiceSession: PracticeSession) -> Unit) :
    ListAdapter<PracticeSession, ChapterInSubjectAdapter.CustomerViewHolder>(DiffCallback()) {


        companion object {
            var comingSoon: String = ""
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val binding =
            RowChapterInSubjectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }


    inner class CustomerViewHolder(private val binding: RowChapterInSubjectBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PracticeSession) {

//            try {
//                var widthLout = binding.cvChapterVideo.width - 38 -9 - 15 - 45
//
//                Log.e("width =>" , binding.cvChapterVideo.width.toString())
//
//                binding.videoTxt.maxWidth = widthLout
//            }catch (e : Exception){
//                e.printStackTrace()
//            }
            binding.cvChapterVideo.visibility = if (revisionVideoStatus != 0) View.VISIBLE else View.GONE

            binding.txtComingSoon.text = comingSoon

            binding.apply {
                txtName.text = item.name
            }

//            binding.llChapterVideo.setOnClickListener {
            binding.cvChapterVideo.setOnClickListener {
                listener.invoke(1, item)
            }
            binding.llPracticeSession.setOnClickListener {
                listener.invoke(2, item)
            }
            binding.llTest.setOnClickListener {
                listener.invoke(3, item)
            }
            binding.llNotesResources.setOnClickListener {
                listener.invoke(4, item)
            }

            if (ChaptersInSubjectActivity.isSubscribed == true){
                binding.imgLock.visibility = View.GONE
            }else{
                if (item.lock == 0){
                    binding.imgLock.visibility = View.VISIBLE
                }else{
                    binding.imgLock.visibility = View.GONE
                }
            }


            Log.e("Chapter-list => " , itemCount.toString())

            if (ChaptersInSubjectActivity.selectSubCode.equals("0")){
                if (itemCount < CHAPTER_LIST_MATHS){
                    if (position == itemCount - 1) {
//                        binding.txtComingSoon.visibility = View.VISIBLE
                    } else {
                        binding.txtComingSoon.visibility = View.GONE
                    }
                }
            }else if (ChaptersInSubjectActivity.selectSubCode.equals("1")){
                if (itemCount < CHAPTER_LIST_SCIENCE){
                    if (position == itemCount - 1) {
//                        binding.txtComingSoon.visibility = View.VISIBLE
                    } else {
                        binding.txtComingSoon.visibility = View.GONE
                    }
                }
            }


        }

    }

    class DiffCallback : DiffUtil.ItemCallback<PracticeSession>() {
        override fun areItemsTheSame(old: PracticeSession, aNew: PracticeSession) =
            old._id == aNew._id

        override fun areContentsTheSame(old: PracticeSession, aNew: PracticeSession) =
            old == aNew
    }


}