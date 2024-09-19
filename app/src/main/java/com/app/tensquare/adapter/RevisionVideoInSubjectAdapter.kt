package com.app.tensquare.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.tensquare.R
import com.app.tensquare.activity.RevisionVideoInSubjectActivity
import com.app.tensquare.databinding.RowRevisionVideoInSubjectBinding
import com.app.tensquare.ui.notes.NotesInSubjectActivity
import com.app.tensquare.ui.revisionvideo.RevisionVideo
import com.app.tensquare.utils.CHAPTER_LIST
import com.app.tensquare.utils.CHAPTER_LIST_MATHS
import com.app.tensquare.utils.CHAPTER_LIST_SCIENCE
import com.bumptech.glide.Glide


class RevisionVideoInSubjectAdapter(
    private val context: Context,
    private val listener: (flag: Int, revisionVideo: RevisionVideo) -> Unit) :
    ListAdapter<RevisionVideo, RevisionVideoInSubjectAdapter.CustomerViewHolder>(DiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val binding =
            RowRevisionVideoInSubjectBinding.inflate(
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


    inner class CustomerViewHolder(private val binding: RowRevisionVideoInSubjectBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RevisionVideo) {

            binding.apply {
                txtName.text = item.title
                txtDesc.text = item.description

                if(item.thumbnail.isNotEmpty()){
                    Glide.with(context).load(item.thumbnail)
//                        .placeholder(R.drawable.img_sample_thumbnail)
                        .into(imgThumbnail)
                }
                conMain.setOnClickListener {
                    listener.invoke(1, item)
                }
            }

            if (RevisionVideoInSubjectActivity.isSubscribed == true){
                binding.imgLock.visibility = View.GONE
            }else{
                if (item.lock == 0){
                    binding.imgLock.visibility = View.VISIBLE
                }else{
                    binding.imgLock.visibility = View.GONE
                }
            }


            Log.e("Video-list => " , itemCount.toString())

            if (RevisionVideoInSubjectActivity.isListEmpty == true){

                if (RevisionVideoInSubjectActivity.selectSubCode.equals("0")){
                    if (itemCount < CHAPTER_LIST_MATHS){
                        if (position == itemCount - 1) {
//                            binding.txtComingSoon.visibility = View.VISIBLE
                        } else {
                            binding.txtComingSoon.visibility = View.GONE
                        }
                    }
                }else if (RevisionVideoInSubjectActivity.selectSubCode.equals("1")){
                    if (itemCount < CHAPTER_LIST_SCIENCE){
                        if (position == itemCount - 1) {
//                            binding.txtComingSoon.visibility = View.VISIBLE
                        } else {
                            binding.txtComingSoon.visibility = View.GONE
                        }
                    }
                }

            }




        }

    }

    /* override fun submitList(list: List<PracticeSession>?) {
         super.submitList(list?.let { ArrayList(it) })
     }*/

    class DiffCallback : DiffUtil.ItemCallback<RevisionVideo>() {
        override fun areItemsTheSame(old: RevisionVideo, aNew: RevisionVideo) =
            old._id == aNew._id

        override fun areContentsTheSame(old: RevisionVideo, aNew: RevisionVideo) =
            old._id == aNew._id
    }


}