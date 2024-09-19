package com.app.tensquare.ui.instruction

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import com.app.tensquare.R
import com.app.tensquare.base.AppBaseActivity
import com.app.tensquare.databinding.ActivityInstructionsBinding
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.ui.home.HomeViewModel
import com.app.tensquare.ui.login.LoginActivity
import com.app.tensquare.ui.questionbank.PracticeQuestionActivity
import com.app.tensquare.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InstructionsActivity : AppBaseActivity() {
    private lateinit var binding: ActivityInstructionsBinding

    private var closingReceiver: BroadcastReceiver = MyBroadcastReceiver()
    private val viewModel: InstructionViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    var request = HashMap<String, String>()

    override fun init() {
        val filter = IntentFilter(JUMP_TO_SUBJECT_DETAIL)
        this.registerReceiver(closingReceiver, filter)

        binding.txtSubjectName.text = intent.getStringExtra("subject_name")

        this.setTileData(
            intent.extras!!,
            binding.txtSubjectModuleName,
            binding.imgSubject,
            binding.llSubjectTile
        )

        request = HashMap<String, String>()
        request["languageId"] = prefs.getSelectedLanguageId().toString()
        when {
            intent.getIntExtra("module", 0) == MODULE_PRACTICE_SESSION -> {
                binding.txtSubjectModuleName.text = getString(R.string.practice_session)
                request["instructionType"] = "1"
            }
            intent.getIntExtra("module", 0) == MODULE_TEST -> {
                binding.txtSubjectModuleName.text = getString(R.string.test)
                request["instructionType"] = "2"
            }
        }

        viewModel.getInstructionList(request)

    }

    override fun setListsAndAdapters() {
        TODO("Not yet implemented")
    }

    override fun setListeners() {
        binding.imgBack.setOnClickListener { finish() }

        binding.txtSubmit.setOnClickListener {
            Intent(
                this@InstructionsActivity,
                PracticeQuestionActivity::class.java
            ).also {
                it.putExtra("subject_id", intent.getStringExtra("subject_id"))
                it.putExtra("subject_name", intent.getStringExtra("subject_name"))
                it.putExtra("chapterIds", intent.getStringExtra("chapterIds"))
                it.putExtra("module", intent.getIntExtra("module", 0))

                startActivity(it)
            }
        }
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

                        viewModel.getInstructionList(request)
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

        viewModel.instructionListResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    val insList: List<String>? = response.data?.paragraphs

//                    showToast(insList!!.size.toString())
                    if (insList != null && insList.isNotEmpty()) {
                        binding.apply {
                            txtIns1.text = insList[0]
                            if (insList[0].isNotEmpty())
                                llIns1.visibility = View.VISIBLE

                            if (insList.size > 1) {
                                txtIns2.text = insList[1]
                                if (insList[1].isNotEmpty())
                                    llIns2.visibility = View.VISIBLE

                                if (insList.size > 2) {
                                    txtIns3.text = insList[2]
                                    if (insList[2].isNotEmpty())
                                        llIns3.visibility = View.VISIBLE

                                    if (insList.size > 3) {
                                        txtIns4.text = insList[3]
                                        if (insList[3].isNotEmpty())
                                            llIns4.visibility = View.VISIBLE

                                        if (insList.size > 4) {
                                            txtIns5.text = insList[4]
                                            if (insList[4].isNotEmpty())
                                                llIns5.visibility = View.VISIBLE
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        binding.txtNoText.visibility = View.VISIBLE
                    }
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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInstructionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        setListeners()
        initObservers()
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