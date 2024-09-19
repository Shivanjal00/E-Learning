package com.app.tensquare.ui.appdetail

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.viewModels
import com.app.tensquare.R
import com.app.tensquare.base.AppBaseActivity
import com.app.tensquare.databinding.ActivityAboutAndTermsBinding
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.ui.home.HomeViewModel
import com.app.tensquare.ui.login.LoginActivity
import com.app.tensquare.utils.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AboutAndTermsActivity : AppBaseActivity() {
    private lateinit var binding: ActivityAboutAndTermsBinding
    private val viewModel: AppEndViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutAndTermsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        setListeners()
        initObservers()
    }

    override fun init() {
        if (intent.getStringExtra(ACTION) == ABOUT_US) {
            binding.txtTitle.text = getString(R.string.about_us)
            showProgressDialog()
            viewModel.getAboutUs(prefs.getSelectedLanguageId().toString() , "1")
            binding.webView.visibility = View.VISIBLE
        } else {
            binding.txtTitle.text = getString(R.string.terms_and_conditions)
            showProgressDialog()
            viewModel.getPrivacyPolicy(prefs.getSelectedLanguageId().toString() , "2")
            binding.webView.visibility = View.VISIBLE
//            openWebview();
        }

    }

    private fun openWebview(data: String) {

        binding.webView.setWebViewClient(MyBrowser())
        binding.webView.getSettings().setLoadsImagesAutomatically(true)
        binding.webView.getSettings().setJavaScriptEnabled(true)
        binding.webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY)
        binding.webView.loadData(data ,
            "text/html; charset=utf-8", "utf-8");

    }

    private class MyBrowser : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }
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

                        init()
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

        viewModel.aboutUsResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {

                    if (response.data?.status == STATUS_SUCCESS){

                        if (response.data?.data != null){

                            val data = response.data?.data!!
//                        binding.txt.text = data.content
//                        binding.txt.visibility = View.VISIBLE

                            openWebview(data.content)
                        }

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


                    /*if (data._id.isNotEmpty() && data.password.isEmpty()) {
                        prefs.setUserToken(data.token)
                        Log.d("USER_TOKEN", prefs.getUserToken().toString())
                        Intent(this@SignUpActivity, PasswordActivity::class.java).also {
                            it.putExtra(ACTION, SIGN_UP)
                            it.putExtra(TEMP_TOKEN, data.token)
                            startActivity(it)
                        }
                    } else if (data._id.isEmpty() && data.password.isEmpty()) {
                        Intent(this@SignUpActivity, OtpVerificationActivity::class.java).also {
                            it.putExtra(ACTION, SIGN_UP)
                            it.putExtra(TEMP_TOKEN, data.token)
                            it.putExtra(MOBILE, binding.edtMobile.text.toString())
                            it.putExtra(SIGNUP_NAME, binding.edtName.text.toString())
                            startActivity(it)
                        }
                    } else {
                        showToast(response.data.message)
                    }*/

                }
                is NetworkResult.Error -> {
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
                    // show a progress bar
                }
            }
        }

        viewModel.privacyPolicyResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {

                    if (response.data?.status == STATUS_SUCCESS){

                        if (response.data?.data != null){
                            val data = response.data?.data!!
//                        binding.txt.text = data.content
//                        binding.txt.visibility = View.VISIBLE
                            openWebview(data.content)
                        }

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
                    showToast(response.message)
                }
                is NetworkResult.Loading -> {
                    // show a progress bar
                }
            }
        }
    }
}