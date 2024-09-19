package com.app.tensquare.ui.password

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.viewModels
import com.app.tensquare.base.AppBaseActivity
import com.app.tensquare.databinding.ActivityPasswordBinding
import com.app.tensquare.ui.login.LoginSignupViewModel
import com.app.tensquare.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PasswordActivity : AppBaseActivity() {
    private lateinit var binding: ActivityPasswordBinding
    private val viewModel: LoginSignupViewModel by viewModels()

    private  var closingReceiver: BroadcastReceiver = MyBroadcastReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        setListeners()
        initObservers()

    }

    override fun init() {
        val filter = IntentFilter(JUMP_TO_HOME_BROADCAST)
        this.registerReceiver(closingReceiver, filter)

        binding.txtHeaderTitle.text =
            if (intent.getStringExtra(ACTION) == FORGOT_PASSWORD) "Reset Password"
            else "Set Your Password"
    }

    override fun setListsAndAdapters() {
        TODO("Not yet implemented")
    }

    override fun setListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.txtProceed.setOnClickListener {
          //  setPasswordToServer()
            /*Intent(this@PasswordActivity, ThankYouActivity::class.java).also {
                it.putExtra("ACTION", "SIGNUP")
                startActivity(it)
            }*/
        }
    }

   /* private fun setPasswordToServer() {
        if (!isValid())
            return
        //val request = intent.getStringExtra("token")?.let {
        val request = NewPasswordRequest(
            password = binding.edtPassword.text.toString(),
            confirmPassword = binding.edtConfirmPassword.text.toString()
        )
        //}
        showProgressDialog()
        viewModel.setPassword(request)
    }*/


    override fun initObservers() {
/*
        viewModel.passwordResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    showToast(response.data?.message)
                    prefs.setUserPinnedToHome(true)
                    Intent(this@PasswordActivity, ThankYouActivity::class.java).also {
                        it.putExtra(ACTION, intent.getStringExtra(ACTION))
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

    private fun isValid(): Boolean {
        when {
            binding.edtPassword.text.toString().isEmpty() -> {
                showToast(getString(com.app.tensquare.R.string.enter_password))
                return false
            }
            binding.edtConfirmPassword.text.toString().isEmpty() -> {
                showToast(getString(com.app.tensquare.R.string.enter_confirm_password))
                return false
            }
            binding.edtPassword.text.toString() != binding.edtConfirmPassword.text.toString() -> {
                showToast(getString(com.app.tensquare.R.string.password_mismatched))
                return false
            }
            else ->
                return true
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