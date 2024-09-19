package com.app.tensquare.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import com.app.tensquare.base.AppBaseActivity
import com.app.tensquare.databinding.ActivitySignupBinding
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.ui.appdetail.AboutAndTermsActivity
import com.app.tensquare.ui.otp.OtpVerificationActivity
import com.app.tensquare.utils.*
import dagger.hilt.android.AndroidEntryPoint
//import kotlinx.android.synthetic.main.activity_signup.*

@AndroidEntryPoint
class SignUpActivity : AppBaseActivity() {
    private lateinit var binding: ActivitySignupBinding
    private val loginSignupViewModel: LoginSignupViewModel by viewModels()


    private var deviceId: String? = ""

    /*@Inject
    lateinit var prefss: SharedPrefManager*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            deviceId = AppUtills.getDeviceId(this@SignUpActivity)
            Log.e("DeviceId  ==> " , deviceId.toString())
        }catch (e : Exception){
            e.printStackTrace()
        }

        setListeners()
        initObservers()

    }

    override fun init() {
        TODO("Not yet implemented")
    }

    override fun setListsAndAdapters() {
        TODO("Not yet implemented")
    }

    override fun setListeners() {

        binding.btnBack.setOnClickListener { finish() }

        binding.txtProceed.setOnClickListener {

            val imm = (this@SignUpActivity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)!!
            imm!!.hideSoftInputFromWindow(binding.edtName.getWindowToken(), 0)
            this@SignUpActivity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

            val imm1 = (this@SignUpActivity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)!!
            imm1!!.hideSoftInputFromWindow(binding.edtMobile.getWindowToken(), 0)
            this@SignUpActivity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)


            proceedToSignUp()
            /*Intent(this, OtpVerificationActivity::class.java).also {
                it.putExtra("ACTION", "SIGNUP")
                startActivity(it)
            }*/
        }

        binding.txtSignIn.setOnClickListener {
            Intent(this, LoginActivity::class.java).also {
                startActivity(it)
            }
        }

        /*binding.btnTC.setOnClickListener {
            Intent(this, AboutAndTermsActivity::class.java).also {
                it.putExtra(ACTION, TERMS_AND_CONDITIONS)
                startActivity(it)
            }
        }*/

        binding.btnPrivacyPolicy.setOnClickListener {
            Intent(this, AboutAndTermsActivity::class.java).also {
                it.putExtra(ACTION, TERMS_AND_CONDITIONS)
                startActivity(it)
            }
        }

    }
    private fun proceedToSignUp() {
        Log.d("USER_TOKEN", prefs.getUserToken().toString())
        if (!isValid())
            return
        val request = prefs.getSelectedLanguageId()?.let {
            SignupRequest(
                mobile = binding.edtMobile.text.toString(),
                name = binding.edtName.text.toString(),
                termCondition = 1,
                languageId = it,
                classId = prefs.getSelectedClassId().toString(),
                languageName = prefs.getSelectedLanguageName() ?: "English",
                className = prefs.getSelectedClassName().toString(),
                deviceId = deviceId ?: ""
            )
        }
        showProgressDialog()
        prefs.getDeviceToken()?.let { loginSignupViewModel.signup(it, request!!) }
    }

    override fun initObservers() {
        loginSignupViewModel.signupResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    if (response.data?.status == STATUS_SUCCESS) {
                        val data = response.data?.data!!
                        /*if (data._id.isNotEmpty() && data.password.isEmpty()) {
                            prefs.setUserToken(data.token)
                            Log.d("USER_TOKEN", prefs.getUserToken().toString())
                            Intent(this@SignUpActivity, PasswordActivity::class.java).also {
                                it.putExtra(ACTION, SIGN_UP)
                                it.putExtra(TEMP_TOKEN, data.token)
                                startActivity(it)
                            }
                        } else if (data._id.isEmpty() && data.password.isEmpty()) {*/

//" -- User already exist -- "
                        if (response.data?.message.equals(getString(com.app.tensquare.R.string.user_already_exist))){
                            showToast(response.data?.message)
                        }else{

                            Intent(this@SignUpActivity, OtpVerificationActivity::class.java).also {
                                it.putExtra(ACTION, SIGN_UP)
                                it.putExtra(TEMP_TOKEN, data.token)
                                it.putExtra(MOBILE, binding.edtMobile.text.toString())
                                it.putExtra(SIGNUP_NAME, binding.edtName.text.toString())
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

    private fun isValid(): Boolean {
        binding.apply {
            when {
                TextUtils.isEmpty(edtName.text.toString().trim()) -> {
                    SnackbarMessage(getString(com.app.tensquare.R.string.enter_name))
                    return false
                }
                edtName.text.toString().trim().length < 3 -> {
                    SnackbarMessage(getString(com.app.tensquare.R.string.name_length_validation))
                    return false
                }
                TextUtils.isEmpty(edtMobile.text.toString().trim()) -> {
                    SnackbarMessage(getString(com.app.tensquare.R.string.enter_mobile_no))
                    return false
                }
                edtMobile.text.toString().trim().length < 10 -> {
                    SnackbarMessage(getString(com.app.tensquare.R.string.mobile_no_should_have_ten_digits))
                    return false
                }
                !chkTerms.isChecked -> {
                    SnackbarMessage(getString(com.app.tensquare.R.string.accept_terms_and_conditions))
                    return false
                }
                else -> {
                    return true
                }
            }
        }
    }
}