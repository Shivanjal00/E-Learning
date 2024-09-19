package com.app.tensquare.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.app.tensquare.R
import com.app.tensquare.base.AppBaseActivity
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.ui.login.LoginActivity
import com.app.tensquare.utils.AppUtills
import com.app.tensquare.utils.LOG_OUT
import com.app.tensquare.utils.OTHER_DEVISE_LOGIN
import com.app.tensquare.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import com.app.tensquare.databinding.ActivityLogoutBinding

@AndroidEntryPoint
class LogoutActivity : AppBaseActivity() {

    private val viewModel: LogoutViewModel by viewModels()
    private lateinit var binding: ActivityLogoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppUtills.setLan(this)

        // Initialize View Binding
        binding = ActivityLogoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        initObservers()
    }

    override fun init() {
        // Use binding instead of synthetic accessors
        binding.imgBack.setOnClickListener { finish() }

        binding.txtYes.setOnClickListener {
            showProgressDialog()
            viewModel.logout()
        }

        binding.txtNo.setOnClickListener {
            finish()
        }
    }

    override fun setListsAndAdapters() {
        // To be implemented
    }

    override fun setListeners() {
        // To be implemented
    }

    override fun initObservers() {
        viewModel.logoutResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    if (response.data?.status == "Success") {
                        Intent(this, LoginActivity::class.java).also {
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
                    } else {
                        if (response.data?.message == OTHER_DEVISE_LOGIN) {
                            val intent = Intent(this, LoginActivity::class.java).apply {
                                putExtra("Expired", "Expired")
                                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            val broadcast = Intent(LOG_OUT)
                            sendBroadcast(broadcast)
                            startActivity(intent)
                            finish()
                        } else {
                            showToast(response.data?.message)
                        }
                    }
                }
                is NetworkResult.Error -> {
                    showToast(response.message)
                }
                is NetworkResult.Loading -> {
                    // Show a progress bar
                }
            }
        }
    }
}
