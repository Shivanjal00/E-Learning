package com.app.tensquare.ui.analysis

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.*
import com.app.tensquare.activity.SelfAnalysisDetailActivity
import com.app.tensquare.adapter.DetailedAnalysisAdapter
import com.app.tensquare.base.AppBaseActivity
import com.app.tensquare.databinding.ActivityDetailedAnalysisBinding
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.ui.home.HomeViewModel
import com.app.tensquare.ui.login.LoginActivity
import com.app.tensquare.utils.ACCESS_TOKEN_EXPIRED
import com.app.tensquare.utils.AppUtills
import com.app.tensquare.utils.LOG_OUT
import com.app.tensquare.utils.OTHER_DEVISE_LOGIN
import com.app.tensquare.utils.REFRESH_TOKEN_EXPIRED
import com.app.tensquare.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AnalysisListActivity : AppBaseActivity() {
    private lateinit var binding: ActivityDetailedAnalysisBinding
    private lateinit var detailedAnalysisAdapter: DetailedAnalysisAdapter
    private var sessionList = mutableListOf<ChapterData>()
    private val viewModel: AnalysisViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppUtills.setLan(this)
        binding = ActivityDetailedAnalysisBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        setListeners()
        setListsAndAdapters()
        initObservers()


        //getDataFromServer()


    }

    override fun init() {
        showProgressDialog()
        intent.getStringExtra("subjectId")?.let { viewModel.getSubjectWiseChapterAnalysisList(it) }
    }

    override fun setListsAndAdapters() {
        binding.rvTopic.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            detailedAnalysisAdapter =
                DetailedAnalysisAdapter { flag: Int, chapterData: ChapterData ->
                    Intent(
                        this@AnalysisListActivity,
                        SelfAnalysisDetailActivity::class.java
                    ).also {
                        it.putExtra("chapterId", chapterData._id)
                        startActivity(it)
                    }
                }
            adapter = detailedAnalysisAdapter
        }
    }

    override fun setListeners() {
        binding.imgBack.setOnClickListener { finish() }

        binding.txtProceed.setOnClickListener {
            Intent(this@AnalysisListActivity, SelfAnalysisDetailActivity::class.java).also {
                startActivity(it)
            }
        }
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

                        showProgressDialog()
                        intent.getStringExtra("subjectId")?.let { viewModel.getSubjectWiseChapterAnalysisList(it) }
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

        viewModel.subjectWiseChapterAnalysisListResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {

                    if (response.data?.status == "Success") {

                        sessionList = response.data?.data!! as MutableList

                        if (sessionList.isNotEmpty()) {
                            detailedAnalysisAdapter.submitList(sessionList)
                            binding.txtNoData.visibility = View.GONE
                            //binding.llMain.visibility = View.VISIBLE
                        } else
                            binding.txtNoData.visibility = View.VISIBLE


                    }else{

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
                    when (response.message) {
                        ACCESS_TOKEN_EXPIRED -> {
//                            prefs.setUserToken(prefs.getRefreshToken())
//                            viewModel.getRefreshToken(prefs.getRefreshToken().toString())
                            homeViewModel.getRefreshToken(prefs.getRefreshToken().toString())
//                            showMessage("1")
                        }
                        REFRESH_TOKEN_EXPIRED -> {
//                           Intent(requireActivity(), LoginActivity::class.java).also {
//                                startActivity(it)
//                            }
//                            requireActivity().finish()
//                            generateNewToken()    // JACK
                        }
                        OTHER_DEVISE_LOGIN ->{
                            val intent =  Intent(this, LoginActivity::class.java)
                            intent.putExtra("Expired", "Expired")
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            val broadcast = Intent(LOG_OUT)
                            sendBroadcast(broadcast)
                            startActivity(intent)
                            finish()
                        }
                        else->{
//                            requireActivity().showToast(response.message)
//                            prefs.setUserToken(prefs.getRefreshToken())
//                            viewModel.getRefreshToken()
//                            viewModel.getRefreshToken(prefs.getUserToken().toString())
//                            generateNewToken()
//                            showMessage("2")
                        }
                    }
                }
                is NetworkResult.Loading -> {
                    dismissProgressDialog()
                    // show a progress bar
                }
            }
        }
    }


    /*private fun getDataFromServer() {
        for (i in 0..4) {
            val subject = PracticeSession()
            sessionList.add(subject)
        }
        detailedAnalysisAdapter.submitList(sessionList)
    }*/


}