package com.app.tensquare.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import com.app.tensquare.R
import com.app.tensquare.base.AppBaseActivity
import com.app.tensquare.databinding.ActivityFeedbackBinding
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.ui.appdetail.AppEndViewModel
import com.app.tensquare.ui.home.HomeViewModel
import com.app.tensquare.ui.login.LoginActivity
import com.app.tensquare.ui.revisionvideo.VideoFeedbackRequest
import com.app.tensquare.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedbackActivity : AppBaseActivity() {
    private lateinit var binding: ActivityFeedbackBinding

    private val viewModel1: AppEndViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppUtills.setLan(this)
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        setListeners()
        initObservers()
    }

    override fun init() {
        if (intent.getIntExtra("screen", 0) == SCREEN_FEEDBACK) {
            binding.txtTitle.text = getString(R.string.feedback)
            //binding.txtSubtitle.text = "Write Feedback"
            binding.imgScreen.setImageDrawable(getDrawable(R.drawable.img_feedback))
            binding.edtMessage.hint = getString(R.string.write_something)
        } else if (intent.getIntExtra("screen", 0) == SCREEN_CONTACT_US) {
            binding.txtTitle.text =  getString(R.string.contact_us)
            binding.txtSubtitle.visibility = View.VISIBLE
            binding.txtSubtitle.text = getString(R.string.write_your_query)
            binding.imgScreen.setImageDrawable(getDrawable(R.drawable.contact_12))
            binding.edtMessage.hint = getString(R.string.message)
        }
    }

    override fun setListsAndAdapters() {
        TODO("Not yet implemented")
    }

    override fun setListeners() {
        binding.imgBack.setOnClickListener { finish() }

        binding.txtSubmit.setOnClickListener {
            if (binding.edtMessage.text.toString().isNotEmpty()) {
                showProgressDialog()
                if (intent.getIntExtra("screen", 0) == SCREEN_FEEDBACK) {
                    val request = intent.getStringExtra("video_id")?.let { it1 ->
                        VideoFeedbackRequest(
                            revisionId = it1,
                            reason = binding.edtMessage.text.toString()
                        )
                    }
                    if (request != null) {
                        viewModel1.postFeedbackOnVideo(request)
                    }
                } else if (intent.getIntExtra("screen", 0) == SCREEN_CONTACT_US) {
                    viewModel1.contactUs(binding.edtMessage.text.toString())
                }
            } else {
                showToast(getString(R.string.enter_a_message))
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
                        if (intent.getIntExtra("screen", 0) == SCREEN_FEEDBACK) {
                            val request = intent.getStringExtra("video_id")?.let { it1 ->
                                VideoFeedbackRequest(
                                    revisionId = it1,
                                    reason = binding.edtMessage.text.toString()
                                )
                            }
                            if (request != null) {
                                viewModel1.postFeedbackOnVideo(request)
                            }
                        } else if (intent.getIntExtra("screen", 0) == SCREEN_CONTACT_US) {
                            viewModel1.contactUs(binding.edtMessage.text.toString())
                        }
                    }
                }
                is NetworkResult.Error -> {
                    dismissProgressDialog()
                    if (it.message == REFRESH_TOKEN_EXPIRED ) {
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

        viewModel1.contactUsResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    if (response.data?.status == STATUS_SUCCESS) {
                        showToast(response.data.message)
                        finish()
                    } else {

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
                    showToast(response.message)
                }
                is NetworkResult.Loading -> {
                    // show a progress bar
                }
            }
        }
        viewModel1.videoFeedbackResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    if (response.data?.status == STATUS_SUCCESS) {
                        showToast(response.data.message)
                        finish()
                    } else {

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
//                            requireActivity().showToast("2")
                        Intent(this, LoginActivity::class.java).also {
                            startActivity(it)
                        }
                        finish()
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
}