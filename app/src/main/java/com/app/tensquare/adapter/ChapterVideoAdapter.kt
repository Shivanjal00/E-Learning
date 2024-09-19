package com.app.tensquare.adapter

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.tensquare.R
import com.app.tensquare.databinding.RowNextChapterBinding
import com.app.tensquare.ui.revisionvideo.NextVideo
import com.app.tensquare.ui.subscription.EnrolmentPlanPricingNewActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import java.util.*


class ChapterVideoAdapter(
    private val context: Context,
    private val listener: (nextVideo: NextVideo, position: Int) -> Unit
) :
    ListAdapter<NextVideo, ChapterVideoAdapter.CustomerViewHolder>(DiffCallback()) {

        companion object {
           var isSubscribe = false
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val binding =
            RowNextChapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class CustomerViewHolder(private val binding: RowNextChapterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: NextVideo) {
            binding.apply {

                Log.e("PrintRevisionVideoIn","RevisionVideoInSubjectActivity = $isSubscribe ${item.lock}")
                try {
//                    if (isSubscribe){
//                        ivVideoLock.visibility = View.GONE
//                    }else {
//                        ivVideoLock.visibility = if (item.lock == 0) View.VISIBLE else View.GONE
//                    }

                    if (isSubscribe){
                        ivVideoLock.setImageResource(R.drawable.img_play_center)
                    }else {
                        ivVideoLock.setImageResource(if (item.lock == 0) R.drawable.lock_12 else R.drawable.img_play_center)
                    }


                    /*val url = URL(item.thumbnail)
                    val bitmap = BitmapFactory.decodeStream(
                        url.openConnection().getInputStream()
                    )
                    val background = BitmapDrawable(context.resources, bitmap)
                    coMain.setBackgroundDrawable(background)*/

                    Glide.with(context).load(item.thumbnail)
                        .into(object : CustomTarget<Drawable>() {
                            override fun onResourceReady(
                                resource: Drawable,
                                transition: Transition<in Drawable>?
                            ) {
                                coMain.background = resource
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {

                            }
                        })

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            binding.root.setOnClickListener {
                if (isSubscribe){
                    listener.invoke(item, 1)
                }else {
                    if (item.lock == 0) {
                        context.startActivity(Intent(context, EnrolmentPlanPricingNewActivity::class.java))
                    }else {
                        listener.invoke(item, 1)
                    }
                }
            }

        }

    }

    override fun submitList(list: List<NextVideo>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    class DiffCallback : DiffUtil.ItemCallback<NextVideo>() {
        override fun areItemsTheSame(old: NextVideo, aNew: NextVideo) =
            old._id == aNew._id

        override fun areContentsTheSame(old: NextVideo, aNew: NextVideo) =
            old == aNew
    }


}