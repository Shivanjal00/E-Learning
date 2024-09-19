package com.app.tensquare.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.tensquare.databinding.RowModelPaperInSubjectBinding
import com.app.tensquare.databinding.RowRevisionVideoInSubjectBinding
import com.app.tensquare.ui.home.Doc
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import java.util.*


class SearchAdapter(
    private val list: ArrayList<Doc>,
    val context: Context,
    private val listener: (doc: Doc, position: Int, flag: Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == 1) {
            val binding = RowRevisionVideoInSubjectBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            CustomerViewHolder(binding)
        } else {
            val binding = RowModelPaperInSubjectBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            CustomerViewHolder2(binding)
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = list[position]
        if (holder.itemViewType == 1)
            (holder as CustomerViewHolder).bind(currentItem, position)
        else
            (holder as CustomerViewHolder2).bind(currentItem, position)
    }

    override fun getItemViewType(position: Int): Int {
//        return if (list[position].videoURL != null && ) 1 else 2
        return if (list[position].videoURL != null && list[position].videoURL!!.isNotEmpty()) 1 else 2
    }


    inner class CustomerViewHolder(private val binding: RowRevisionVideoInSubjectBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(item: Doc, position: Int) {

            binding.apply {

                try {
                    txtName.text = item.title
                    txtDesc.text = item.description
                    if (item.thumbnail.isNotEmpty()) {
                        Glide.with(context)
                            .setDefaultRequestOptions(RequestOptions().priority(Priority.HIGH))
                            .load(item.thumbnail)
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(binding.imgThumbnail)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            binding.root.setOnClickListener {
                listener.invoke(item, position, 1)
            }

        }

    }

    inner class CustomerViewHolder2(private val binding: RowModelPaperInSubjectBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(item: Doc, position: Int) {

            binding.apply {
                try {
                    txtName.text = item.title
                    txtDesc.visibility = View.GONE
                    txtPdfTitle.text = item.description

                    /* if (item.isClicked) {
                         clForeground.translationX = -200f
                     } else {
                         clForeground.translationX = 0f
                     }*/

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                /*if (item.value == 1) {
                    imgBG.setBackgroundResource(R.drawable.bg_maths)
                    imgImage.setImageResource(R.drawable.img_maths)
                } else if (item.value == 2) {
                    imgBG.setBackgroundResource(R.drawable.bg_science)
                    imgImage.setImageResource(R.drawable.img_science)

                    val paddingDp = 30
                    val density: Float = context.resources.displayMetrics.density
                    val paddingPixel: Int = (paddingDp.toInt() * density).toInt()
                    binding.imgImage.setPadding(
                        paddingPixel,
                        paddingPixel,
                        paddingPixel,
                        paddingPixel
                    )
                }*/
            }

            binding.root.setOnClickListener {
                listener.invoke(item, position, 1)
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

    override fun getItemCount(): Int {
        return list.size
    }

}