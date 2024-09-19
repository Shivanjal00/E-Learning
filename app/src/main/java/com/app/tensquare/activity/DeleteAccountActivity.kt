package com.app.tensquare.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.app.tensquare.HiltApplication
import com.app.tensquare.R
import com.app.tensquare.base.AppBaseActivity
import com.app.tensquare.databinding.ActivityDeleteAccountBinding
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.ui.chapter.ChaptersInSubjectActivity
import com.app.tensquare.ui.home.HomeViewModel
import com.app.tensquare.ui.login.LoginActivity
import com.app.tensquare.ui.login.LoginRequest
import com.app.tensquare.ui.login.LoginSignupViewModel
import com.app.tensquare.ui.otp.OtpVerificationActivity
import com.app.tensquare.utils.ACCESS_TOKEN_EXPIRED
import com.app.tensquare.utils.ACTION
import com.app.tensquare.utils.AppUtills
import com.app.tensquare.utils.IS_DELETED
import com.app.tensquare.utils.LOGIN
import com.app.tensquare.utils.LOG_OUT
import com.app.tensquare.utils.MOBILE
import com.app.tensquare.utils.OTHER_DEVISE_LOGIN
import com.app.tensquare.utils.REFRESH_TOKEN_EXPIRED
import com.app.tensquare.utils.STATUS_SUCCESS
import com.app.tensquare.utils.TEMP_TOKEN
import com.app.tensquare.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
//import kotlinx.android.synthetic.main.activity_logout.*
import org.json.JSONObject

@AndroidEntryPoint
class DeleteAccountActivity : AppBaseActivity() {

    private val homeViewModel: HomeViewModel by viewModels()
    private val loginViewModel: LoginSignupViewModel by viewModels()
    private lateinit var binding: ActivityDeleteAccountBinding
    private var deviceId: String? = ""
    private var mobileNumber: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppUtills.setLan(this)
        binding = ActivityDeleteAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            deviceId = AppUtills.getDeviceId(this@DeleteAccountActivity)
            Log.e("DeviceId  ==> " , deviceId.toString())
        }catch (e : Exception){
            e.printStackTrace()
        }

        init()
        initObservers()
    }

    override fun init() {
        binding.imgBack.setOnClickListener { finish() }

        binding.txtYes.setOnClickListener {
            showProgressDialog()
            val userData = prefs.getUserData()?.let { JSONObject(it) }
            mobileNumber = userData?.getString("mobile") ?: ""
            val loginRequest = LoginRequest(
                mobile = mobileNumber,
                languageName = prefs.getSelectedLanguageName()!!,
                deviceId = deviceId.toString()
                //password = "binding.edtPassword.text.toString()"
            )
            loginViewModel.login(loginRequest)
        }
        binding.txtNo.setOnClickListener {
            finish()
        }
    }

    override fun setListsAndAdapters() {

    }

    override fun setListeners() {

    }

    override fun initObservers() {
        loginViewModel.response.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    if (response.data?.status == STATUS_SUCCESS) {
//                        val broadcast = Intent(JUMP_TO_HOME_BROADCAST)
//                        sendBroadcast(broadcast)
                        startActivity(Intent(this, OtpVerificationActivity::class.java).apply {
                            putExtra(ACTION, LOGIN)
                            putExtra(IS_DELETED, true)
                            putExtra(TEMP_TOKEN, response.data.data)
                            putExtra(MOBILE, mobileNumber)
                            //it.putExtra(PASSWORD, "binding.edtPassword.text.toString()")

                        })

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


        homeViewModel.refreshTokenResponse.observe(this){
            when (it){
                is NetworkResult.Loading -> {}
                is NetworkResult.Success -> {
                    dismissProgressDialog()
                    it.data?.let {data ->

                        Log.e("token_Home = >" , data.accessToken.toString())
                        prefs.setUserToken(data.accessToken)
                        prefs.setRefreshToken(data.refreshToken)


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
    }
}