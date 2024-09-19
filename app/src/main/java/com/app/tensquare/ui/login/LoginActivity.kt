package com.app.tensquare.ui.login

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import com.app.tensquare.R
import com.app.tensquare.ui.home.HomeActivity
import com.app.tensquare.ui.otp.OtpVerificationActivity
import com.app.tensquare.base.AppBaseActivity
import com.app.tensquare.databinding.ActivityLoginBinding
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.ui.initialsetup.InitialViewModel
import com.app.tensquare.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class LoginActivity : AppBaseActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel1: InitialViewModel by viewModels()
    private val viewModel: LoginSignupViewModel by viewModels()

    private var deviceId: String? = ""

    /*@Inject
    lateinit var prefs: SharedPrefManager*/

    private var closingReceiver: BroadcastReceiver = MyBroadcastReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            deviceId = AppUtills.getDeviceId(this@LoginActivity)
            Log.e("DeviceId  ==> " , deviceId.toString())
        }catch (e : Exception){
            e.printStackTrace()
        }

        try {

            if (getIntent().hasExtra("Expired")){
                if (getIntent().getStringExtra("Expired").equals("Expired")){
                    prefs.setIsGuestUser(true)
                    prefs.setUserToken("")
                    prefs.setUserId("")
                    prefs.setUserPinnedToHome(false)
                }
            }

        }catch (e : Exception){
            e.printStackTrace()
        }

        init()
        initObservers()
        setListeners()
    }

    override fun init() {
        val filter = IntentFilter(JUMP_TO_HOME_BROADCAST)
        this.registerReceiver(closingReceiver, filter)
    }

    override fun setListsAndAdapters() {
        TODO("Not yet implemented")
    }

    private fun isValid(): Boolean {
        binding.apply {

            when {
                TextUtils.isEmpty(edtMobile.text.toString().trim()) -> {
                    SnackbarMessage(getString(R.string.enter_mobile_no))
                    return false
                }
                edtMobile.text.toString().trim().length < 10 -> {
                    SnackbarMessage(getString(R.string.enter_10_digit_mobile_no))
                    return false
                }
                /*TextUtils.isEmpty(edtPassword.text.toString().trim()) -> {
                    SnackbarMessage(getString(R.string.enter_password))
                    return false
                }
                edtPassword.text.toString().trim().length < 6 -> {
                    SnackbarMessage(getString(R.string.enter_least_6_char_password))
                    return false
                }*/
                else -> {
                    return true
                }
            }
        }
    }


    override fun setListeners() {
        binding.apply {
            btnBack.setOnClickListener { finish() }

            txtSignIn.setOnClickListener {

                val imm = (this@LoginActivity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
                imm.hideSoftInputFromWindow(binding.edtMobile.getWindowToken(), 0)
                this@LoginActivity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

                login()
                /*Intent(this@LoginActivity, OtpVerificationActivity::class.java).also {
                    it.putExtra("ACTION", "LOGIN")
                    startActivity(it)
                }*/
            }

            txtSignUp.setOnClickListener {
                Intent(this@LoginActivity, SignUpActivity::class.java).also {
                    //it.putExtra("ACTION", "LOGIN")
                    startActivity(it)
                }
            }

            txtSkip.setOnClickListener {
                try {
//                    getGuestToken()
                }catch (e:Exception){
                    e.printStackTrace()
                }

            }

            /*txtForgotPassword.setOnClickListener {
                Intent(this@LoginActivity, ForgotPasswordActivity::class.java).also {
                    startActivity(it)
                }
            }*/


        }

    }

    private fun getGuestToken() {
        showProgressDialog()
//        viewModel1.getGuestToken("1234")
    }

    private fun login() {
        if (!isValid())
            return

        val selectedLanguage = if (!prefs.getSelectedLanguageName().isNullOrEmpty()) "English" else prefs.getSelectedLanguageName() ?: "English"
        loginRequest = LoginRequest(
            mobile = binding.edtMobile.text.toString(),
            languageName = selectedLanguage,
            deviceId = deviceId ?: ""
            //password = "binding.edtPassword.text.toString()"
        )
        Log.e("PrintdeviceId","deviceId =  $deviceId  UUID = ${UUID.randomUUID()}")
        showProgressDialog()
        loginRequest?.let { viewModel.login(it) }
    }

    private var loginRequest: LoginRequest ?= null

    override fun initObservers() {

        viewModel1.refreshTokenResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
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

                }
                is NetworkResult.Success -> {
                    prefs.setUserToken(response.data?.accessToken)
                    prefs.setRefreshToken(response.data?.refreshToken)

                    showProgressDialog()
                    loginRequest?.let { viewModel.login(it) }
                }
            }
        }
        viewModel.response.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    if (response.data?.status == STATUS_SUCCESS) {
//                        val broadcast = Intent(JUMP_TO_HOME_BROADCAST)
//                        sendBroadcast(broadcast)
                        runOnUiThread {
                            showToast(response.data.message)
                            startActivity(Intent(this@LoginActivity, OtpVerificationActivity::class.java).apply {
                                putExtra(ACTION, LOGIN)
                                putExtra(TEMP_TOKEN, response.data.data.toString())
                                putExtra(MOBILE, binding.edtMobile.text.toString())
                            })
                        }
                    } else {
                        showToast(response.data?.message ?: response.message ?: "something went wrong")
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
                        }
                    }
                }
                is NetworkResult.Error -> {
                    dismissProgressDialog()
                    showToast(response.data?.message ?: response.message ?: "something went wrong")
                    when (response.message) {
                        ACCESS_TOKEN_EXPIRED -> {
//                            prefs.setUserToken(prefs.getRefreshToken())
//                            viewModel.getRefreshToken(prefs.getRefreshToken().toString())
                            viewModel1.getRefreshToken(prefs.getRefreshToken().toString())
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

        //JACK
        try {

            viewModel1.guestTokenResponse.observe(this) { response ->
                dismissProgressDialog()
                when (response) {
                    is NetworkResult.Success -> {
                        prefs.setIsGuestUser(true)
                        prefs.setUserToken(response.data?.accessToken)

                        Intent(this@LoginActivity, HomeActivity::class.java).also {
                            it.putExtra("ACTION", "LOGIN")
                            startActivity(it)
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

        }catch (e:Exception){
            e.printStackTrace()
        }


    }

    private inner class MyBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(this.closingReceiver)
    }

}