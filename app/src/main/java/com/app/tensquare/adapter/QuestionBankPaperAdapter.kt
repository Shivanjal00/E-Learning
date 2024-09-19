package com.app.tensquare.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.tensquare.R
import com.app.tensquare.databinding.RowQueBankPdfBinding
import com.app.tensquare.ui.questionbankpaper.questionList
import com.app.tensquare.utils.showToast
import java.util.ArrayList

class QuestionBankPaperAdapter(private val context: Context,
                                private val filesList: ArrayList<String>,
                               private val list: MutableList<questionList>,
                               private val listener: (flag: Int, position: Int, item: questionList) -> Unit
) : RecyclerView.Adapter<QuestionBankPaperAdapter.CustomerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionBankPaperAdapter.CustomerViewHolder {
        val binding =
            RowQueBankPdfBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return CustomerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return this.list.size
    }

    override fun onBindViewHolder(holder: QuestionBankPaperAdapter.CustomerViewHolder, position: Int) {
        var currentItem = this.list[position]
        holder.bind(currentItem, position)
    }

    inner class CustomerViewHolder(private val binding: RowQueBankPdfBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: questionList, position: Int) {

            binding.apply {

                txtTitle.text = item.fileName

                if (item.name.isNotEmpty()){
                    txtName.text = item.name
                }else{
                    txtName.visibility = View.GONE
                }

                /*
                try {

                    //                imgDownload.setImageResource(R.drawable.ic_download)
                    DrawableCompat.setTint(imgDownload.getBackground(),
                        ContextCompat.getColor(context, R.color.dwn_gray_bg));

/*
                     Log.e("Check => " ,
                         item.document?.substring(item.document.lastIndexOf('/') + 1).toString()
                     )

                    var oldData : String = ""
                    oldData = item.document?.substring(item.document.lastIndexOf('/') + 1).toString()
                    var newData : String = ""
                    newData = item.fileName + ".pdf"
*/

                    filesList.forEach {
                        if (it.uppercase().endsWith("PDF"))
                            if (it.uppercase().startsWith("QUESTION_BANK_PAPER")) {
//                                var urlFileName: String = "QUESTION_BANK_PAPER_${item.document?.substring(item.document.lastIndexOf('/') + 1)}"
                                var addLastPath : String = ""
                                addLastPath = item.fileName + ".pdf"
                                var urlFileName: String = "QUESTION_BANK_PAPER_${addLastPath}"
                                if (urlFileName == it){
                                    DrawableCompat.setTint(imgDownload.getBackground(),
                                        ContextCompat.getColor(context, R.color.dwn_green_bg))
                                    imgDownload.isEnabled = false
                                }

                            }
                        Log.e("Files*****", it)
                    }

                }catch (e : Exception){
                    e.printStackTrace()
                }
                 */



//                if (item.description.isNotEmpty()){
//                    txtDesc.text = item.description
//                }else{
//                    txtDesc.visibility = View.GONE
//                }

                loutMain.setOnClickListener {
                    listener.invoke(1, position, item)
                }

                imgDownload.setOnClickListener {
                    listener.invoke(2, position, item)
                }

                imgShare.setOnClickListener {
                    listener.invoke(3, position, item)
                }

            }



        }
    }

}