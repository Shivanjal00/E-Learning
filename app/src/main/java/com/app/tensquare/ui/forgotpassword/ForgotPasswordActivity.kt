package com.app.tensquare.ui.forgotpassword

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.viewModels
import com.app.tensquare.base.AppBaseActivity
import com.app.tensquare.databinding.ActivityForgotPasswordBinding
import com.app.tensquare.ui.login.LoginSignupViewModel
import com.app.tensquare.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordActivity : AppBaseActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private val viewModel: LoginSignupViewModel by viewModels()

    private  var closingReceiver: BroadcastReceiver = MyBroadcastReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        setListeners()
        initObservers()

    }

    private fun requestForPassword() {
        /*if (binding.edtMobile.text.toString().isNotEmpty()) {
            if (binding.edtMobile.text.toString().trim().length == 10) {
                val request = ForgotPasswordRequest(
                    mobile = binding.edtMobile.text.toString()
                )
                showProgressDialog()
                viewModel.requestForPassword(request)
            } else
                showToast(getString(R.string.enter_10_digit_mobile_no))
        } else
            showToast(getString(R.string.enter_mobile_no))*/
    }


    override fun init() {
        val filter = IntentFilter(JUMP_TO_HOME_BROADCAST)
        this.registerReceiver(closingReceiver, filter)
    }

    override fun setListsAndAdapters() {
        TODO("Not yet implemented")
    }

    override fun setListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.txtProceed.setOnClickListener {
            requestForPassword()

            /*Intent(this, OtpVerificationActivity::class.java).also {
                it.putExtra("ACTION", "FORGOT_PASSWORD")
                startActivity(it)
            }*/
        }
    }


    override fun initObservers() {
/*
        viewModel.forgotPasswordResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    showToast(response.data?.message)
                    Intent(this@ForgotPasswordActivity, OtpVerificationActivity::class.java).also {
                        it.putExtra(ACTION, FORGOT_PASSWORD)
                        it.putExtra(TEMP_TOKEN, response.data?.data)
                        it.putExtra(MOBILE, binding.edtMobile.text.toString())
                        startActivity(it)
                    }
                }
                is NetworkResult.Error -> {
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                }
                is NetworkResult.Loading -> {
                    // show a progress bar
                }
            }
        }
*/
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