package com.app.tensquare.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.app.tensquare.databinding.FragmentIntro1Binding

interface callBack {

    fun name() {

    }


    fun address()

}


class Intro1Fragment : Fragment() {

    private lateinit var binding: FragmentIntro1Binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIntro1Binding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    /*    Glide.with(this)
            .setDefaultRequestOptions(RequestOptions().priority(Priority.HIGH))
            .load(R.drawable.education_12)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(binding.imgIntro1);
     */

        //getDataFromServer()
    }


}