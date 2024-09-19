package com.app.tensquare.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.*
import com.app.tensquare.R
import com.app.tensquare.adapter.FaqsAdapter
import com.app.tensquare.base.AppBaseActivity
import com.app.tensquare.databinding.ActivityFaqsAndTxnBinding
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.ui.appdetail.AppEndViewModel
import com.app.tensquare.ui.appdetail.FAQ
import com.app.tensquare.ui.home.HomeViewModel
import com.app.tensquare.ui.login.LoginActivity
import com.app.tensquare.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FaqsActivity : AppBaseActivity() {
    private lateinit var binding: ActivityFaqsAndTxnBinding
    private lateinit var faqsAdapter: FaqsAdapter
    private var sessionList = mutableListOf<FAQ>()

    private val viewModel: AppEndViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppUtills.setLan(this)
        binding = ActivityFaqsAndTxnBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        setListeners()
        initObservers()
        //getDataFromServer()

    }

    override fun init() {
        binding.txtTitle.text = getString(R.string.faqs)
        binding.rvFaqs.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            faqsAdapter =
                FaqsAdapter(this@FaqsActivity) { flag: Int, subject: FAQ ->
                }
            adapter = faqsAdapter
        }

        showProgressDialog()
        viewModel.getFAQS(prefs.getSelectedLanguageId().toString())
    }

    override fun setListsAndAdapters() {
        TODO("Not yet implemented")
    }

    override fun setListeners() {
        binding.imgBack.setOnClickListener { finish() }
    }

    override fun initObservers() {
        homeViewModel.refreshTokenResponse.observe(this){
            when (it){
                is NetworkResult.Loading -> {}
                is NetworkResult.Success -> {
                    dismissProgressDialog()
                    it.data?.let {data ->

                        Log.e("token_Home = >" , data.accessToken.toString())
                        prefs.setUserToken(data.accessToken)
                        prefs.setRefreshToken(data.refreshToken)

                        viewModel.getFAQS(prefs.getSelectedLanguageId().toString())

                    }
                }
                is NetworkResult.Error -> {
                    dismissProgressDialog()
                    if (it.message == REFRESH_TOKEN_EXPIRED ) {
//                            requireActivity().showToast("2")
                        Intent(this, LoginActivity::class.java).also {
                            startActivity(it)
                        }
                        finish()
                    }else if (it.message == OTHER_DEVISE_LOGIN) {

                        val intent =  Intent(this, LoginActivity::class.java)
                        intent.putExtra("Expired", "Expired")
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()

                    } else showToast(it.message)
                }
            }
        }

        viewModel.faqsResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    if (response.data?.status == STATUS_SUCCESS) {
                        val data = response.data.data
                        if (data.isNotEmpty()) {
                            faqsAdapter.submitList(data)
                            binding.rvFaqs.visibility = View.VISIBLE
                        } else {
                            showToast(response.data.message)
                            binding.txtNoData.visibility = View.VISIBLE
                        }
                    } else {

                        binding.txtNoData.visibility = View.VISIBLE

                        if (response.data?.message == OTHER_DEVISE_LOGIN) {
                            val intent =  Intent(this, LoginActivity::class.java)
                            intent.putExtra("Expired", "Expired")
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            val broadcast = Intent(LOG_OUT)
                            sendBroadcast(broadcast)
                            startActivity(intent)
                            finish()
                        }else{
                            showToast(response.data?.message)
                        }

                    }
                }
                is NetworkResult.Error -> {
                    dismissProgressDialog()
                    if (response.message == REFRESH_TOKEN_EXPIRED ) {
                        homeViewModel.getRefreshToken(prefs.getRefreshToken().toString())
                    }else if (response.message == OTHER_DEVISE_LOGIN) {

                        val intent =  Intent(this, LoginActivity::class.java)
                        intent.putExtra("Expired", "Expired")
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()

                    } else showToast(response.message)
                }
                is NetworkResult.Loading -> {
                    // show a progress bar
                }
            }
        }

    }

    /*private fun getDataFromServer() {
        for (i in 0..5) {
            val subject = PracticeSession()
            sessionList.add(subject)
        }
        faqsAdapter.submitList(sessionList)
    }*/

}