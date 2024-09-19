package com.app.tensquare.activity

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.app.tensquare.adapter.MyDownloadsAdapter
import com.app.tensquare.adapter.TopicAdapter
import com.app.tensquare.base.AppBaseActivity
import com.app.tensquare.databinding.ActivitySelfAnalysisDetailBinding
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.response.Subject
import com.app.tensquare.ui.analysis.AnalysisViewModel
import com.app.tensquare.ui.home.HomeViewModel
import com.app.tensquare.ui.login.LoginActivity
import com.app.tensquare.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SelfAnalysisDetailActivity : AppBaseActivity() {
    private lateinit var binding: ActivitySelfAnalysisDetailBinding
    private lateinit var myDownloadsAdapter: MyDownloadsAdapter
    private var downloadsList = mutableListOf<Subject>()
    private val viewModel: AnalysisViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    @Inject
    lateinit var topicAdapter: TopicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppUtills.setLan(this)
        binding = ActivitySelfAnalysisDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        setListeners()
        initObservers()
        /*Handler(Looper.getMainLooper()).postDelayed(
            {
                ObjectAnimator.ofInt(binding.progressScore, "progress", 65).start()
            }, 500
        )*/


    }

    override fun init() {
        binding.rvTopic.adapter = topicAdapter

        showProgressDialog()
        intent.getStringExtra("chapterId")?.let { viewModel.getChapterWiseAnalysis(it) }


    }

    override fun setListsAndAdapters() {

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
                        showProgressDialog()
                        intent.getStringExtra("chapterId")?.let { viewModel.getChapterWiseAnalysis(it) }

                    }
                }
                is NetworkResult.Error -> {
                    dismissProgressDialog()
                    if (it.message == REFRESH_TOKEN_EXPIRED ) {
//                            requireActivity().showToast("2")
                        homeViewModel.getRefreshToken(prefs.getRefreshToken().toString())
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

        viewModel.chapterAnalysisResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {

                    if (response.data?.status == STATUS_SUCCESS){

                        val data = response.data?.data!!
                        binding.apply {
                            ObjectAnimator.ofInt(
                                binding.progressScore,
                                "progress",
                                data.percentOfCorrectAnswer
                            ).start()
                            txtAvgScore.text = "${data.percentOfCorrectAnswer}%"
                            txtTotalTests.text = data.totalTestAttemped.toString()
                            timeSpendOnTest.text = data.userSpendTime
                            txtAvgTimePerQues.text = data.averagePerQuestion
                            txtTimeSpendOnPractice.text = data.timeSpendOnPractice
                        }
                        topicAdapter.submitList(data.topicsForImprovement)

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

                    }

                }
                is NetworkResult.Error -> {
                    showToast(response.message)
                }
                is NetworkResult.Loading -> {
                    // show a progress bar
                }
            }
        }
    }


}