package com.app.tensquare.ui.latestupdate

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.tensquare.R
import com.app.tensquare.databinding.RowLatestUpdateBinding
import com.bumptech.glide.Glide
import java.util.*

class LatestUpdatesAdapter(
    private val context: Context,
    private val list: ArrayList<Update>,
    private val listener: (flag: Int, modelPaper: Update) -> Unit
) : RecyclerView.Adapter<LatestUpdatesAdapter.CustomerViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val binding =
            RowLatestUpdateBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return CustomerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        val currentItem = list[position]
        holder.bind(currentItem, position)
    }


    inner class CustomerViewHolder(private val binding: RowLatestUpdateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Update, position: Int) {

            binding.apply {
                txtName.text = item.name
                //txtDesc.text = item.boardName

                if(item.fileName.isNotEmpty()){
                    txtPdfTitle.text = item.fileName
                }

                if (item.fileType == 2) {
                    imgThumbnail.visibility = View.VISIBLE
                    llPdf.visibility = View.GONE

                    if(item.thumbnail.isNotEmpty()){
                        Glide.with(context).load(item.thumbnail)
//                        .placeholder(R.drawable.img_sample_thumbnail)
                            .into(imgThumbnail)
                    }

                } else {
                    imgThumbnail.visibility = View.GONE
                    llPdf.visibility = View.VISIBLE
                    if (item.isClicked) {
                        clForeground.translationX = -200f
                    } else {
                        clForeground.translationX = 0f
                    }

                }

                clForeground.setOnClickListener {
                    if (item.fileType == 2) {
                        listener.invoke(1, item)
                    } else {
                        listener.invoke(2, item)
                        /*try {
                            if (item.isClicked) {
                                clForeground.translationX = -200f
                            } else {
                                clForeground.translationX = 0f
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }*/
                        /*if (it.translationX == -llAction.width.toFloat())
                            it.animate().translationX(0f)
                        else
                            it.animate().translationX(-llAction.width.toFloat())*/
                    }

                }

                /*llAction.setOnClickListener {
                    listener.invoke(2, item)
                }*/
            }

        }

    }

    /* override fun submitList(list: List<PracticeSession>?) {
         super.submitList(list?.let { ArrayList(it) })
     }*/

   /* class DiffCallback : DiffUtil.ItemCallback<Update>() {
        override fun areItemsTheSame(old: Update, aNew: Update): Boolean {
            return old._id != aNew._id

        }


        override fun areContentsTheSame(old: Update, aNew: Update): Boolean {
            return old._id == aNew._id

        }
    }*/

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