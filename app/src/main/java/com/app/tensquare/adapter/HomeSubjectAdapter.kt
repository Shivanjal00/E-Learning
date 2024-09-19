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
import com.app.tensquare.databinding.RowHomeListBinding
import com.app.tensquare.ui.home.Subject
import com.app.tensquare.utils.*
import java.util.*


class HomeSubjectAdapter(
    val context: Context,
    private val listener: (subject: Subject, position: Int, flag: Int) -> Unit
) :
    ListAdapter<Subject, HomeSubjectAdapter.CustomerViewHolder>(DiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val binding =
            RowHomeListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }


    inner class CustomerViewHolder(private val binding: RowHomeListBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(item: Subject) {
            Log.e("CustomerViewHolder","CustomerViewHolder = ${item.value}")
            binding.apply {
                txtName.text = item.name
           /*     if (item.value == 1) {
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
                }   */

                /*
                -bg List
                1) bg maths - light blue ------ maths
                2) bg science - light green  ------- sci
                3) bg biology - m blue  --------- eng
                4) bg chemistry - p
                5) bg latest update - blue  --------- ss
                6) bg previous year papers - v  ------ hindi
                7) bg physics - o  --------- san
                 */

//                if (item.value == SUBJECT_MATHS || item.value == SUBJECT_SCIENCE){
//                    binding.root.visibility = View.VISIBLE
//                }else{
//                    binding.root.visibility = View.GONE
//                }

                if (item.value == SUBJECT_MATHS) {
                    imgBG.setBackgroundResource(R.drawable.bg_maths)
                    imgImage.setImageResource(R.drawable.img_maths)

                /*    val paddingDp = 30
                    val density: Float = context.resources.displayMetrics.density
                    val paddingPixel: Int = (paddingDp.toInt() * density).toInt()
                    binding.imgImage.setPadding(
                        paddingPixel,
                        paddingPixel,
                        paddingPixel,
                        paddingPixel
                    )   */

                } else if (item.value == SUBJECT_SCIENCE) {
                    imgBG.setBackgroundResource(R.drawable.bg_latest_update)
                    imgImage.setImageResource(R.drawable.img_science)

//                    val paddingDp = 30
                    val paddingDp = 25
                    val density: Float = context.resources.displayMetrics.density
                    val paddingPixel: Int = (paddingDp.toInt() * density).toInt()
                    binding.imgImage.setPadding(
                        paddingPixel,
                        paddingPixel,
                        paddingPixel,
                        paddingPixel
                    )
                }else if (item.value == SUBJECT_SANSKRIT) {
                    imgBG.setBackgroundResource(R.drawable.bg_science)
                    imgImage.setImageResource(R.drawable.san_img_2)

                /*    val paddingDp = 30
                    val density: Float = context.resources.displayMetrics.density
                    val paddingPixel: Int = (paddingDp.toInt() * density).toInt()
                    binding.imgImage.setPadding(
                        paddingPixel,
                        paddingPixel,
                        paddingPixel,
                        paddingPixel
                    )   */
                }else if (item.value == SUBJECT_HINDI) {
                    imgBG.setBackgroundResource(R.drawable.bg_biology)
                    imgImage.setImageResource(R.drawable.hindi_img_2)

                  /*  val paddingDp = 25
                    val density: Float = context.resources.displayMetrics.density
                    val paddingPixel: Int = (paddingDp.toInt() * density).toInt()
                    binding.imgImage.setPadding(
                        paddingPixel,
                        paddingPixel,
                        paddingPixel,
                        paddingPixel
                    )   */

                }else if (item.value == SUBJECT_SOCIAL_STUDIES) {
                    imgBG.setBackgroundResource(R.drawable.bg_previous_year_papers)
                    imgImage.setImageResource(R.drawable.ss_img_2)

                    val paddingDp = 25
//                    val paddingDp = 20
                    val density: Float = context.resources.displayMetrics.density
                    val paddingPixel: Int = (paddingDp.toInt() * density).toInt()
                    binding.imgImage.setPadding(
                        paddingPixel,
                        paddingPixel,
                        paddingPixel,
                        paddingPixel
                    )
                }else if (item.value == SUBJECT_ENGLISH) {
                    imgBG.setBackgroundResource(R.drawable.bg_maths)
                    imgImage.setImageResource(R.drawable.eng_img_2)

//                    val paddingDp = 30
                     val paddingDp = 25
                    val density: Float = context.resources.displayMetrics.density
                    val paddingPixel: Int = (paddingDp.toInt() * density).toInt()
                    binding.imgImage.setPadding(
                        paddingPixel,
                        paddingPixel,
                        paddingPixel,
                        paddingPixel
                    )

                } else if (item.value == SUBJECT_PHYSICS) {
                    imgBG.setBackgroundResource(R.drawable.bg_physics)
                    imgImage.setImageResource(R.drawable.img_physics)

//                    val paddingDp = 30
                   /* val paddingDp = 25
                    val density: Float = context.resources.displayMetrics.density
                    val paddingPixel: Int = (paddingDp.toInt() * density).toInt()
                    binding.imgImage.setPadding(
                        paddingPixel,
                        paddingPixel,
                        paddingPixel,
                        paddingPixel
                    )*/
                } else if (item.value == SUBJECT_CHEMISTRY) {
                    imgBG.setBackgroundResource(R.drawable.bg_chemistry)
                    imgImage.setImageResource(R.drawable.img_chemistry)

//                    val paddingDp = 30
                    /*val paddingDp = 25
                    val density: Float = context.resources.displayMetrics.density
                    val paddingPixel: Int = (paddingDp.toInt() * density).toInt()
                    binding.imgImage.setPadding(
                        paddingPixel,
                        paddingPixel,
                        paddingPixel,
                        paddingPixel
                    )*/
                }
                else if (item.value == SUBJECT_BIOLOGY) {
                    imgBG.setBackgroundResource(R.drawable.bg_biology)
                    imgImage.setImageResource(R.drawable.img_biology)

//                    val paddingDp = 30
                    /*val paddingDp = 25
                    val density: Float = context.resources.displayMetrics.density
                    val paddingPixel: Int = (paddingDp.toInt() * density).toInt()
                    binding.imgImage.setPadding(
                        paddingPixel,
                        paddingPixel,
                        paddingPixel,
                        paddingPixel
                    )*/
                }



            }

            binding.root.setOnClickListener {
                listener.invoke(item, 1, 1)
            }

        }

    }

    override fun submitList(list: List<Subject>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    class DiffCallback : DiffUtil.ItemCallback<Subject>() {
        override fun areItemsTheSame(old: Subject, aNew: Subject) =
            old._id == aNew._id

        override fun areContentsTheSame(old: Subject, aNew: Subject) =
            old == aNew
    }


}