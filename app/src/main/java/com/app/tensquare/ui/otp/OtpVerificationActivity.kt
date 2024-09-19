package com.app.tensquare.ui.otp

import LocaleHelper1
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import com.app.tensquare.R
import com.app.tensquare.activity.SplashActivity
import com.app.tensquare.activity.DeleteAccountViewModel
import com.app.tensquare.activity.ThankYouActivity
import com.app.tensquare.ui.home.HomeActivity
import com.app.tensquare.base.AppBaseActivity
import com.app.tensquare.databinding.ActivityOtpVerificationBinding
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.ui.login.LoginActivity
import com.app.tensquare.ui.login.LoginSignupViewModel
import com.app.tensquare.utils.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat

@AndroidEntryPoint
class OtpVerificationActivity : AppBaseActivity() {
    private lateinit var binding: ActivityOtpVerificationBinding
    private val viewModel: LoginSignupViewModel by viewModels()
    private val deleteAccountViewModel: DeleteAccountViewModel by viewModels()
    private var token: String? = null
    private var deviceId: String? = ""

    private var closingReceiver: BroadcastReceiver = MyBroadcastReceiver()
    private lateinit var timer: CountDownTimer
    private var isDeleted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            Build.VERSION_CODES.N
            deviceId = AppUtills.getDeviceId(this@OtpVerificationActivity)
            Log.e("DeviceId  ==> " , deviceId.toString())
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

        token = intent.getStringExtra(TEMP_TOKEN)
        isDeleted = intent.getBooleanExtra(IS_DELETED, false)

        startCountDownTime()

        binding.apply {
            txtMessage.text = "${getString(R.string.sent_sms_to)} ${intent.getStringExtra(MOBILE)}"
        }
    }

    override fun setListsAndAdapters() {
        TODO("Not yet implemented")
    }

    override fun setListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnResendOtp.setOnClickListener {
            startCountDownTime()
            resendOtp()
        }

        binding.txtProceed.setOnClickListener {
            try {
                val imm = (this@OtpVerificationActivity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
                if (imm != null) {
                    Log.d("DEBUG", "InputMethodManager instance is not null")
                    imm.hideSoftInputFromWindow(binding.otpPinView.getWindowToken(), 0)
                } else {
                    Log.e("DEBUG", "InputMethodManager instance is null")
                }

                if (this@OtpVerificationActivity.window != null) {
                    Log.d("DEBUG", "Window instance is not null")
                    this@OtpVerificationActivity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
                } else {
                    Log.e("DEBUG", "Window instance is null")
                }
            } catch (e: Exception) {
                Log.e("DEBUG", "Exception caught: ${e.message}")
            }

            verifyForOTP()
            /*if (intent.getStringExtra("ACTION") == "SIGNUP" || intent.getStringExtra("ACTION") == "FORGOT_PASSWORD")
                Intent(this, PasswordActivity::class.java).also {
                    it.putExtra("ACTION", intent.getStringExtra("ACTION"))
                    startActivity(it)
                }
            else {

                Intent(this, HomeActivity::class.java).also {
                    it.putExtra("ACTION", "LOGIN")
                    startActivity(it)
                }
            }*/
        }
    }

    private fun verifyForOTP() {
        if (binding.otpPinView.text?.isNotEmpty() == true) {
            if (binding.otpPinView.text?.length!! == 4) {
                val request = token?.let {
                    OtpRequest(
                        otp = binding.otpPinView.text.toString(),
                        token = it,
                        languageId = prefs.getSelectedLanguageId()!!,
                        languageName = prefs.getSelectedLanguageName()!!,
                        classId = prefs.getSelectedClassId()!!,
                        className = prefs.getSelectedClassName()!!,
                        deviceId = deviceId.toString()
                        //push_token = prefs.getDeviceToken()
                    )
                }
                if (isDeleted) {
                    request?.copy(isDeleted = "1")
                }
                if (request != null) {
                    showProgressDialog()
                    prefs.getDeviceToken()?.let { viewModel.verifyOTP(it, request) }
                }
            } else
                SnackbarMessage(getString(com.app.tensquare.R.string.enter_valid_opt))

        } else {
            SnackbarMessage(getString(com.app.tensquare.R.string.enter_opt))
        }
    }

    private fun resendOtp() {
        val request = JsonObject()
        request.addProperty("mobile", intent.getStringExtra(MOBILE))
        /*if (intent.getStringExtra(ACTION) == LOGIN) {
            request.addProperty("password", intent.getStringExtra(PASSWORD))
        } else*/ if (intent.getStringExtra(ACTION) == SIGN_UP) {
            request.addProperty("name", intent.getStringExtra(SIGNUP_NAME))
            request.addProperty("termCondition", 1)
        }

        showProgressDialog()
        prefs.getDeviceToken()?.let { viewModel.resendOTP(it, request) }
    }


    override fun initObservers() {
        deleteAccountViewModel.deleteAccountResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
//                    showToast(response.data?.message)

                    if (response.data?.status == "Success") {
                        showToast(getString(R.string.str_account_deleted))

                        Intent(this, SplashActivity::class.java).also {
                            prefs.setIsGuestUser(true)
                            prefs.setUserToken("")
                            prefs.setUserId("")
                            prefs.setUserPinnedToHome(false)
                            val broadcast = Intent(LOG_OUT)
                            sendBroadcast(broadcast)
                            Log.d("USER_TOKEN", prefs.getUserToken().toString())
                            startActivity(it)
                            finish()
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
                    dismissProgressDialog()
                    if (response.message == REFRESH_TOKEN_EXPIRED ) {
//                            requireActivity().showToast("2")
//                        homeViewModel.getRefreshToken(prefs.getRefreshToken().toString())
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

        viewModel.verifyOtpResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    if (response.data?.status == STATUS_SUCCESS) {
                        if (isDeleted) {
                            deleteAccountViewModel.deleteAccount()
                            return@observe
                        }

                        val data = response.data.data
                        prefs.setUserToken(data.token)
                        prefs.setRefreshToken(data.refreshToken)
                        prefs.setUserId(data.id)
                        prefs.setUserPinnedToHome(true)
                        prefs.setIsGuestUser(false)
                        prefs.setIsEnrolled(data.enrollmentPlanStatus)
                        prefs.setSelectedLanguageId(data.languageId)
//                        prefs.setSelectedLanguagenName(data.languageName)
                        prefs.setSelectedClassId(data.classId)
                        Log.d("USER_TOKEN", prefs.getUserToken().toString())

                        prefs.setUserData(Gson().toJson(data).toString())

                        if (response.data.data.languageName == "Hindi") {
                            LocaleHelper1.setLocale(this, "hi")
                        } else {
                            LocaleHelper1.setLocale(this, "en")
                        }

                        if (intent.getStringExtra(ACTION) == SIGN_UP
                        /*|| intent.getStringExtra(ACTION) == FORGOT_PASSWORD
                        || intent.getStringExtra(ACTION) == RESET_PASSWORD*/
                        ) {
                            /*Intent(this, PasswordActivity::class.java).also {
                                it.putExtra(ACTION, intent.getStringExtra(ACTION))
                                startActivity(it)
                            }*/

                            showToast(response.data.message)

                            Intent(
                                this@OtpVerificationActivity,
                                ThankYouActivity::class.java
                            ).also {
                                it.putExtra(ACTION, intent.getStringExtra(ACTION))
                                startActivity(it)
                            }
                        } else {

                            Intent(this, HomeActivity::class.java).also {
                                it.putExtra(ACTION, LOGIN)
                                val broadcast = Intent(JUMP_TO_HOME_BROADCAST)
                                sendBroadcast(broadcast)
                                startActivity(it)
                            }
                        }
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
                    showToast(getString(com.app.tensquare.R.string.invalid_otp))
//                    showToast(response.message)
                }
                is NetworkResult.Loading -> {
                    // show a progress bar
                }
            }
        }

        viewModel.resendOtpResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    if (response.data?.status == STATUS_SUCCESS) {
                        token = response.data.data
                        Log.d("USER_TOKEN", prefs.getUserToken().toString())
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
    }

    private inner class MyBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(this.closingReceiver)
        timer.cancel()
    }

    private fun startCountDownTime() {
        timer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                var decimalFormat = DecimalFormat("00")
                binding.txtTimer.text = "00:" + decimalFormat.format(millisUntilFinished / 1000)
//                timeTv.text = "00:" + millisUntilFinished / 1000 + " sec"
                //here you can have your logic to set text to edittext
                binding.btnResendOtp.isEnabled = false
                binding.btnResendOtp.alpha = 0.5f
                // binding.leftTimeLl.setVisibility(View.VISIBLE)
            }

            override fun onFinish() {
                binding.btnResendOtp.isEnabled = true
                binding.btnResendOtp.alpha = 1.0f
                //binding.leftTimeLl.setVisibility(View.GONE)
            }
        }.start()
    }

}