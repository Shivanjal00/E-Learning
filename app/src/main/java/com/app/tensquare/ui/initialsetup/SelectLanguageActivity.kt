package com.app.tensquare.ui.initialsetup

import LocaleHelper1
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.app.tensquare.R
import com.app.tensquare.activity.IntroSliderActivity
import com.app.tensquare.base.AppBaseActivity
import com.app.tensquare.databinding.ActivitySelectLanguageBinding
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.ui.login.LoginActivity
import com.app.tensquare.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectLanguageActivity : AppBaseActivity() {

    private val viewModel: InitialViewModel by viewModels()
    private lateinit var binding: ActivitySelectLanguageBinding
    private var closingReceiver: BroadcastReceiver = MyBroadcastReceiver()
    private lateinit var languageList: List<Language>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        DrawableCompat.setTint(binding.btnHindi.getBackground(), ContextCompat.getColor(context, R.color.unselectlng_bg_clr));
        DrawableCompat.setTint(binding.btnEnglish.getBackground(), ContextCompat.getColor(context, R.color.unselectlng_bg_clr));

        init()
        setListeners()
        initObservers()
    }

    override fun init() {
        //prefs.setUserToken(prefs.getRefreshToken())
        getLanguageList()

        val filter = IntentFilter(JUMP_TO_HOME_BROADCAST)
        this.registerReceiver(closingReceiver, filter)
    }

    private fun getLanguageList() {
        showProgressDialog()
        viewModel.getLanguageList()
    }

    override fun setListsAndAdapters() {

    }

    override fun setListeners() {
        binding.btnHindi.setOnClickListener() {

            try {

                DrawableCompat.setTint(binding.btnHindi.getBackground(), ContextCompat.getColor(context, R.color.status_bar_color));

                prefs.setSelectedLanguageId(languageList.find { it.name.uppercase() == "HINDI" }?._id)
                prefs.setSelectedLanguagenName("Hindi")
                prefs.setUserLanguage("hi")
                LocaleHelper1.setLocale(this,"hi")
                //val i = Intent(this@SelectLanguageActivity, SelectClassActivity::class.java)
                val i = Intent(this@SelectLanguageActivity, IntroSliderActivity::class.java)
                startActivity(i)


//                AppUtills.setLocaleLanguage("hi", this@SelectLanguageActivity)
//                //LocaleUtil.setLocale(this@SelectLanguageActivity, "hi")
//                AppUtills.setLan(this@SelectLanguageActivity)

            }catch (e : Exception){
                e.printStackTrace()
            }

        }

        binding.btnEnglish.setOnClickListener() {

            try {

                    DrawableCompat.setTint(binding.btnEnglish.getBackground(), ContextCompat.getColor(context, R.color.status_bar_color));

                prefs.setSelectedLanguageId(languageList.find { it.name.uppercase() == "ENGLISH" }?._id)
                prefs.setSelectedLanguagenName("English")
                prefs.setUserLanguage("en")
                LocaleHelper1.setLocale(this,"en")
                //val i = Intent(this@SelectLanguageActivity, SelectClassActivity::class.java)
                val i = Intent(this@SelectLanguageActivity, IntroSliderActivity::class.java)
                startActivity(i)

//                AppUtills.setLocaleLanguage("en", this@SelectLanguageActivity)
//                //LocaleUtil.setLocale(this@SelectLanguageActivity, "en")
//                AppUtills.setLan(this@SelectLanguageActivity)

            }catch (e : Exception){
                e.printStackTrace()
            }

        }
    }

    override fun initObservers() {
        viewModel.languageListResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    binding.llButtons.visibility = View.VISIBLE
                    languageList = response.data!!
                }
                is NetworkResult.Error -> {
                    when (response.message) {
                        ACCESS_TOKEN_EXPIRED -> {
                            prefs.setUserToken(prefs.getRefreshToken())
                            viewModel.getRefreshToken(prefs.getUserToken().toString())
                        }

                        else -> {
                            showToast(response.message.toString())
                        }
                    }
                }
                is NetworkResult.Loading -> {
                    // show a progress bar
                }
            }
        }

        viewModel.refreshTokenResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    //binding.llButtons.visibility = View.VISIBLE
                    prefs.setUserToken(response.data?.accessToken)
                    prefs.setRefreshToken(response.data?.refreshToken)
                    getLanguageList()
                }
                is NetworkResult.Error -> {
                   showToast(response.message.toString())
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
        DrawableCompat.setTint(binding.btnHindi.getBackground(), ContextCompat.getColor(context, R.color.unselectlng_bg_clr));
        DrawableCompat.setTint(binding.btnEnglish.getBackground(), ContextCompat.getColor(context, R.color.unselectlng_bg_clr));

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(this.closingReceiver)
    }
}