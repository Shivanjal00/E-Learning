package com.app.tensquare.ui.customview

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.app.tensquare.R

class CustomProgressBar(context: Context) : Dialog(context) {
    init {
        setContentView(R.layout.progress_bar_design)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        //val binding :ProgressBarDesignBinding = ProgressBarDesignBinding.inflate(layoutInflater)
        /*val imageView = findViewById<ImageView>(R.id.imgProgress)
        Glide.with(context).load(R.drawable.progress_bar).into(imageView)*/
        //Glide.with(context).load(R.drawable.progress_bar).into(binding.imgProgress)
        if (window != null) window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}