package com.app.tensquare.ui.initialsetup

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.app.tensquare.R
import com.app.tensquare.base.AppBaseActivity
import com.app.tensquare.databinding.ActivitySelectUserBinding
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.ui.home.HomeActivity
import com.app.tensquare.ui.login.LoginActivity
import com.app.tensquare.ui.login.SignUpActivity
import com.app.tensquare.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectUserActivity : AppBaseActivity() {
    private lateinit var binding: ActivitySelectUserBinding
    private val viewModel: InitialViewModel by viewModels()
    private var closingReceiver: BroadcastReceiver = MyBroadcastReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        setListeners()
        initObservers()

        DrawableCompat.setTint(binding.btnExistingUser.getBackground(), ContextCompat.getColor(context, R.color.unselectlng_bg_clr));
        DrawableCompat.setTint(binding.btnNewUser.getBackground(), ContextCompat.getColor(context, R.color.unselectlng_bg_clr));

    }

    private fun getGuestToken() {
        showProgressDialog()
//        viewModel.getGuestToken("1234")
    }

    override fun init() {
        val filter = IntentFilter(JUMP_TO_HOME_BROADCAST)
        this.registerReceiver(closingReceiver, filter)
    }

    override fun setListsAndAdapters() {
        TODO("Not yet implemented")
    }

    override fun setListeners() {
        binding.apply {
            btnBack.setOnClickListener { finish() }

            btnExistingUser.setOnClickListener {
                DrawableCompat.setTint(btnExistingUser.getBackground(), ContextCompat.getColor(context, R.color.status_bar_color));
                val i = Intent(this@SelectUserActivity, LoginActivity::class.java)
                startActivity(i)
            }
            btnNewUser.setOnClickListener {
                DrawableCompat.setTint(btnNewUser.getBackground(), ContextCompat.getColor(context, R.color.status_bar_color));
                val i = Intent(this@SelectUserActivity, SignUpActivity::class.java)
                startActivity(i)
            }
            txtSkip.setOnClickListener {
//                getGuestToken()

            }
        }
    }

    override fun initObservers() {
        viewModel.guestTokenResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    prefs.setUserToken(response.data?.accessToken)
                    prefs.setIsGuestUser(true)
                    Intent(this@SelectUserActivity, HomeActivity::class.java).also {
                        it.putExtra(ACTION, "")
                        val broadcast = Intent(JUMP_TO_HOME_BROADCAST)
                        sendBroadcast(broadcast)
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
    }

    private inner class MyBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        DrawableCompat.setTint(binding.btnExistingUser.getBackground(), ContextCompat.getColor(context, R.color.unselectlng_bg_clr));
        DrawableCompat.setTint(binding.btnNewUser.getBackground(), ContextCompat.getColor(context, R.color.unselectlng_bg_clr));
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(this.closingReceiver)
    }

    override fun onBackPressed() {

    }
}