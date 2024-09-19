package com.app.tensquare.ui.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.tensquare.R
import com.app.tensquare.databinding.DesignActivePlanBinding
import com.app.tensquare.databinding.DesignActivePlanNewBinding
import java.util.ArrayList

class PurchasePlanAdapter(private val context: Context,
                          private val planList: ArrayList<EnrolmentPlan>) :
    RecyclerView.Adapter<PurchasePlanAdapter.Myviewholder>() {

//    var context: Context? = null
//    var planList: List<EnrolmentPlan>? = null
//
//    fun PurchasePlanAdapter(context: Context, planList: List<EnrolmentPlan>) {
//        this.context = context
//        this.planList = planList
//    }

//    inner class Myviewholder(private val binding: DesignActivePlanBinding) :
    inner class Myviewholder(private val binding: DesignActivePlanNewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: EnrolmentPlan) {

            binding.apply {

//                txtActiveClass.text = context.getString(com.app.tensquare.R.string.active) + " ${item?.className} " + context.getString(com.app.tensquare.R.string.grade)
//                txtActiveClass.text = " ${item?.className} " + context.getString(com.app.tensquare.R.string.grade)
                try {
                    txtActiveClass.text = " ${item?.className} " + "Class"
                }catch (e : Exception){
                    e.printStackTrace()
                }

//                txtValidTill.text = context.getString(com.app.tensquare.R.string.valid_till) + " ${item?.expiredDate}"
                txtValidTill.text =  item?.expiredDate

            /*    var subName = ""
                for (i in 0 until item!!.subjectName.size) {
                    if (subName.isEmpty()){
                        subName = item.subjectName[i].toString()
                    }else{
                        subName = subName + " + " + item.subjectName[i].toString()
                    }
                }

                subName = "(" + subName + ")"   */

                txtSub.text = item.name

            }

        }

    }
//    class Myviewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        @BindView(R.id.tvLine)
//        var tvLine: TextView? = null
//
//        init {
//            ButterKnife.bind(this, itemView)
//        }
//    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Myviewholder {
        val binding =
            DesignActivePlanNewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return Myviewholder(binding)
    }

    override fun onBindViewHolder(holder: Myviewholder, position: Int) {
//        val walletListModel: WalletListModel = walletList!![position]
        val currentItem = planList!![position]
        holder.bind(currentItem)

    }

    override fun getItemCount(): Int {
        return planList!!.size
    }



}