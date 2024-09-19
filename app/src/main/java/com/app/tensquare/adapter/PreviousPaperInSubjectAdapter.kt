package com.app.tensquare.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.tensquare.base.ComingSoon
import com.app.tensquare.databinding.RowPreviousYearPaperBinding
import com.app.tensquare.ui.paper.PreviousYearPaper
import com.app.tensquare.utils.AppUtills.redirectToEnrollment
import com.app.tensquare.utils.PREVIOUS_YEAR_PAPER_MIN_REQ

private const val IS_CHECKED = "is.checked"

private const val SHOW_MENU = 1
private const val HIDE_MENU = 2

class PreviousPaperInSubjectAdapter(
    private val comingSoon: ComingSoon?,
    private val list: MutableList<PreviousYearPaper>,
    private val listener: (flag: Int, position: Int, item: PreviousYearPaper) -> Unit
) : RecyclerView.Adapter<PreviousPaperInSubjectAdapter.CustomerViewHolder>() {


    /*private lateinit var list: MutableList<PreviousYearPaper>

    constructor(list: MutableList<PreviousYearPaper>) : super {
        this.list = list
    }*/

    /*override fun getItemViewType(position: Int): Int {
        return if (list[position].isClicked) {
            SHOW_MENU
        } else {
            HIDE_MENU
        }
    }*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val binding =
            RowPreviousYearPaperBinding.inflate(
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
        var currentItem = this.list[position]
        holder.bind(currentItem, position)
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

    inner class CustomerViewHolder(private val binding: RowPreviousYearPaperBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PreviousYearPaper, position: Int) {

            binding.apply {

                txtComingSoon.text = comingSoon?.message ?: ""
                // item as PreviousYearPaper
                txtName.text = item.title
//                txtType1.text = item.boardName
//                txtType2.text = item.year.toString()
//                txtType3.text = item.setNumber.toString()

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

                //txtDesc.text = item.boardName

                /*if (item.isClicked)
                    binding.clForeground.animate().translationX(-200f)*/
                /*.duration = 400L*/
                //else {
                //if (binding.clForeground.width > 0 && binding.clForeground.translationX == -200f)
                //binding.clForeground.animate().translationX(0f)
                /*.duration = 400L*/
                //}

                /*clForeground.setOnClickListener {
                    if (it.translationX == -llAction.width.toFloat())
                        it.animate().translationX(0f)
                    else
                        it.animate().translationX(-llAction.width.toFloat())
                }*/

                //animateCard(item.isClicked)

                if (item.enrollmentPlanStatus){
                    binding.imgLock.visibility = View.GONE
                    binding.imgShare.visibility = View.VISIBLE
                    binding.imgDownload.visibility = View.VISIBLE
                }else{
                    binding.imgLock.visibility = View.VISIBLE
                    binding.imgShare.visibility = View.GONE
                    binding.imgDownload.visibility = View.GONE
                }

                // ------------- Change hide download
                clForeground.setOnClickListener {
                    if (item.enrollmentPlanStatus) listener.invoke(2, position, item) else root.context.redirectToEnrollment()
                }

                imgShare.setOnClickListener {
                    listener.invoke(3, position, item)
                }

                imgDownload.setOnClickListener {
                    listener.invoke(1, position, item)
                }
                //------------end
            }


            Log.e("Chapter-list => " , itemCount.toString())
            if (itemCount < PREVIOUS_YEAR_PAPER_MIN_REQ){
                if (position == itemCount - 1) {
                    binding.txtComingSoon.visibility = View.VISIBLE
                } else {
                    binding.txtComingSoon.visibility = View.GONE
                }
            }

        }
    }

}


/*
class PreviousPaperInSubjectAdapter(private val listener: (items: MutableList<PreviousYearPaper>, checked: Boolean, flag: Int, modelPaper: PreviousYearPaper) -> Unit) :
    ListAdapter<PreviousYearPaper, PreviousPaperInSubjectAdapter.CustomerViewHolder>(DiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val binding =
            RowPreviousYearPaperBinding.inflate(
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

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int, payload: List<Any>) {
        var currentItem = getItem(position)

        if (payload.isEmpty() || payload[0] !is Bundle) {
            holder.bind(currentItem)
        } else {
            val bundle = payload[0] as Bundle
            holder.update(bundle)
        }
    }


    inner class CustomerViewHolder(private val binding: RowPreviousYearPaperBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Any) {

            binding.apply {

                item as PreviousYearPaper
                txtName.text = item.title
                //txtDesc.text = item.boardName

                if (item.isClicked)
                    binding.clForeground.animate().translationX(-200f)*/
/*.duration =
              400L*//*

                else {
                    //if (binding.clForeground.width > 0 && binding.clForeground.translationX == -200f)
                    binding.clForeground.animate().translationX(0f)*/
/*.duration =
                        400L*//*


                }

                //clForeground.setOnClickListener {
                */
/*if (it.translationX == -llAction.width.toFloat())
                    it.animate().translationX(0f)
                else
                    it.animate().translationX(-llAction.width.toFloat())*//*

                //}

                //animateCard(item.isClicked)

                clForeground.setOnClickListener {
                    listener(currentList.toMutableList(), !item.isClicked, 2, item)
                }

                llAction.setOnClickListener {
                    listener.invoke(currentList.toMutableList(), false, 1, item)
                }
            }

        }

        fun update(bundle: Bundle) {
            if (bundle.containsKey(IS_CHECKED)) {
                val checked = bundle.getBoolean(IS_CHECKED)
                if (checked)
                    binding.clForeground.animate()
                        .translationX(-200f)*/
/*.duration =
                    400L*//*

                else {
                    //if (binding.clForeground.width > 0 && binding.clForeground.translationX == -200f)
                    binding.clForeground.animate().translationX(0f)*/
/*.duration =
                        400L*//*


                }
            }
        }

        private fun animateCard(isClicked: Boolean) {
            if (isClicked)
                binding.clForeground.animate()
                    .translationX(-200f)*/
/*.duration =
                    400L*//*

            else {
                //if (binding.clForeground.width > 0 && binding.clForeground.translationX == -200f)
                    binding.clForeground.animate().translationX(0f)*/
/*.duration =
                        400L*//*


            }
        }

    }

*/
/* override fun submitList(list: List<PracticeSession>?) {
super.submitList(list?.let { ArrayList(it) })
}*//*


    class DiffCallback : DiffUtil.ItemCallback<PreviousYearPaper>() {
        override fun areItemsTheSame(old: PreviousYearPaper, aNew: PreviousYearPaper): Boolean {
            return old._id != aNew._id
        }

        override fun areContentsTheSame(old: PreviousYearPaper, aNew: PreviousYearPaper): Boolean {
            return old == aNew

        }

        override fun getChangePayload(
            oldItem: PreviousYearPaper,
            newItem: PreviousYearPaper
        ): Any? {
            if (oldItem._id == newItem._id) {
                return if (oldItem.isClicked == newItem.isClicked) {
                    super.getChangePayload(oldItem, newItem)
                } else {
                    val diff = Bundle()
                    diff.putBoolean(IS_CHECKED, newItem.isClicked)
                    diff
                }
            }

            return super.getChangePayload(oldItem, newItem)
        }
    }


}*/
