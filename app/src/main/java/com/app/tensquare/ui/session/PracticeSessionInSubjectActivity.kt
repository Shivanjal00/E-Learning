package com.app.tensquare.ui.session

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.tensquare.base.AppBaseActivity
import com.app.tensquare.databinding.ActivityPracticeSessionInSubjectBinding
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.ui.home.HomeViewModel
import com.app.tensquare.ui.instruction.InstructionsActivity
import com.app.tensquare.ui.login.LoginActivity
import com.app.tensquare.ui.subscription.EnrolmentPlanPricingNewActivity
import com.app.tensquare.utils.ACCESS_TOKEN_EXPIRED
import com.app.tensquare.utils.AppUtills
import com.app.tensquare.utils.JUMP_TO_SUBJECT_DETAIL
import com.app.tensquare.utils.LOG_OUT
import com.app.tensquare.utils.MODULE_PRACTICE_SESSION
import com.app.tensquare.utils.MODULE_TEST
import com.app.tensquare.utils.OTHER_DEVISE_LOGIN
import com.app.tensquare.utils.REFRESH_TOKEN_EXPIRED
import com.app.tensquare.utils.STATUS_FAILURE
import com.app.tensquare.utils.STATUS_SUCCESS
import com.app.tensquare.utils.SUBJECT
import com.app.tensquare.utils.setTileData
import com.app.tensquare.utils.showToast
import com.app.tensquare.utils.showToastCenter
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray
import org.json.JSONObject

@AndroidEntryPoint
class PracticeSessionInSubjectActivity : AppBaseActivity() {
    private lateinit var binding: ActivityPracticeSessionInSubjectBinding
    private lateinit var practiceSessionInSubjectAdapter: PracticeSessionInSubjectAdapter
    private var sessionList = mutableListOf<PracticeSession>()
    private var closingReceiver: BroadcastReceiver = MyBroadcastReceiver()
    private val viewModel: SessionViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    private var subjectId: String? = null
    private var module: Int? = null
    private var noDataTxt: Boolean = true
//    private lateinit var preferences: SharedPrefManager

    companion object {
        var isSubscribed: Boolean? = false
        var selectSubCode: String = "2"
    }

    private var startTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPracticeSessionInSubjectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val filter = IntentFilter(JUMP_TO_SUBJECT_DETAIL)
        this.registerReceiver(closingReceiver, filter)

        isSubscribed = prefs.isSubPlan()
        selectSubCode = prefs.getSelectSub().toString()
        startTime = System.currentTimeMillis()
        init()
        setListeners()
        initObservers()

        getDataFromServer()

    }

    private fun getDataFromServer() {
        showProgressDialog()
        subjectId?.let { viewModel.getPracticeSessionList(it) }
    }

    override fun init() {

        subjectId = intent.getStringExtra("subject_id")
        module = intent.getIntExtra("module", 0)

        binding.txtSubjectName.text = intent.getStringExtra("subject_name")
        this.setTileData(
            intent.extras!!,
            binding.txtSubjectModuleName,
            binding.imgSubject,
            binding.llSubjectTile
        )

        binding.txtSubjectModuleName.text =
            when (module) {
                MODULE_PRACTICE_SESSION ->
//                    MODULE_NAME_PRACTICE_SESSION
                    getString(com.app.tensquare.R.string.practice_session)

                MODULE_TEST ->
//                    MODULE_NAME_TEST
                    getString(com.app.tensquare.R.string.test)

                else -> ""
            }
        AppUtills.trackEvent(
            this,
            "PracticeSessionInSubjectActivity",
            "${binding.txtSubjectModuleName.text}_page"
        )
        binding.rvPracticeSession.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            practiceSessionInSubjectAdapter =
                PracticeSessionInSubjectAdapter { items: MutableList<PracticeSession>, isChecked: Boolean, practiceSession: PracticeSession ->

                    var element = items.first() {
                        it._id == practiceSession._id
                    }

                    Log.e(
                        "PrintElement",
                        "PrintElement = ${element.isChecked} -- ${element._id} ==  ${isChecked}"
                    )
                    val index = items.indexOf(element)

                    //items[position].isSelected = !items[position].isSelected
                    if (element != null) {
                        element = element.copy(isChecked = isChecked)
                    }

                    if (element != null) {
                        items[index] = element
                    }
                    if (!isChecked) // to check if any of the item is unchecked
                        binding.chkSelectAll.isChecked = false
                    else { // to check weather all the rows are checked
                        var count = 0
                        items.forEach {
                            if (it.isChecked)
                                count++
                        }
                        if (count == items.size) binding.chkSelectAll.isChecked = true
                    }
                    (binding.rvPracticeSession.adapter as PracticeSessionInSubjectAdapter).submitList(
                        items
                    )

                    var isAnyChapterSelected: Boolean = false
                    for (item in items) {
                        if (item.isChecked) {
                            isAnyChapterSelected = true
                            break
                        }
                    }
                    binding.txtProceed.isVisible = isAnyChapterSelected


                }
            adapter = practiceSessionInSubjectAdapter
        }
    }

    override fun setListsAndAdapters() {
        TODO("Not yet implemented")
    }

    override fun setListeners() {
        binding.imgBack.setOnClickListener { finish() }

        binding.txtProceed.setOnClickListener {
            if (!prefs.isGuestUser()) {
//            if (prefs.isEnrolled()) {
//            if (prefs.isSubPlan()) {
                Intent(
                    this@PracticeSessionInSubjectActivity,
                    InstructionsActivity::class.java
                ).also {
                    //var selectedChaptersIds = StringBuilder()

                    val chapterIds = JSONArray()


                    practiceSessionInSubjectAdapter.currentList.forEach { it1 ->
                        if (it1.isChecked) {
                            chapterIds.put(it1._id)

                            /*selectedChaptersIds =
                                selectedChaptersIds.append(if (selectedChaptersIds.isEmpty()) it1._id else ",${it1._id}")*/
                        }
                    }
                    Log.e("Chapterslist =>", chapterIds.toString())
                    it.putExtra("subject_id", subjectId)
                    it.putExtra("chapterIds", chapterIds.toString())
                    //it.putExtra("chapterIds", selectedChaptersIds.toString())
                    it.putExtra("subject_name", intent.getStringExtra("subject_name"))
                    it.putExtra("module", module)
                    it.putExtra(SUBJECT, intent.getIntExtra(SUBJECT, 0))

                    startActivity(it)
                }
            } else {
                Intent(
                    this@PracticeSessionInSubjectActivity,
                    EnrolmentPlanPricingNewActivity::class.java
                ).also {
                    startActivity(it)
                }
                showToastCenter(getString(com.app.tensquare.R.string.please_enrol))
            }
        }

        binding.chkSelectAll.setOnCheckedChangeListener { _, isChecked ->
            var items = practiceSessionInSubjectAdapter.currentList.toMutableList()

            binding.txtProceed.isVisible = isChecked

            items.forEach {
                val index = items.indexOf(it)
                if (isChecked != it.isChecked) {
                    if (prefs.isSubPlan()) {
                        val element = it.copy(isChecked = isChecked)
                        items[index] = element
                    } else {
                        if (it.lock == 1) {
                            val element = it.copy(isChecked = isChecked)
                            items[index] = element
                        }
                    }

                }
            }

            (binding.rvPracticeSession.adapter as PracticeSessionInSubjectAdapter).submitList(
                items
            )

        }
    }

    override fun initObservers() {
        viewModel.practiceSessionListResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    if (response.data != null) {
                        if (response.data.status == STATUS_SUCCESS) {
                            sessionList = response.data.data as MutableList<PracticeSession>
                            practiceSessionInSubjectAdapter.submitList(sessionList)

                            binding.txtComingSoon.text = response.data.comingSoon?.message ?: ""

                            var countLocked = 0

                            sessionList.forEach {
                                Log.e("PrintSessionList","PrintSessionList = ${it.lock}")
                                if (it.lock == 1) {
                                    countLocked++
                                }
                            }

                            val isAllLock = sessionList.any { it.lock == 1 }

                            if (sessionList.size > 1) {
                                if (isAllLock) {
                                    binding.loutSltAll.visibility = View.VISIBLE
                                } else {
                                    binding.loutSltAll.visibility = View.GONE
                                }
                            } else {
                                binding.loutSltAll.visibility = View.GONE
                            }

                            /*if (prefs.isSubPlan()) {
                                if (sessionList.size > 1) {
                                    binding.loutSltAll.visibility = View.VISIBLE
                                } else {
                                    binding.loutSltAll.visibility = View.GONE
                                }
                            } else {
                                if (countLocked > 1) {
                                    binding.loutSltAll.visibility = View.VISIBLE
                                } else {
                                    binding.loutSltAll.visibility = View.GONE
                                }

                            }*/

//                        binding.loutSltAll.visibility = View.VISIBLE
                            noDataTxt = false
                            binding.txtNoData.text = response.data.comingSoon?.message ?: ""
                            binding.txtNoData.visibility = if (sessionList.isEmpty()) View.VISIBLE else View.GONE
                        } else {
                            if (noDataTxt) {
                                binding.txtNoData.visibility = View.VISIBLE
                                binding.txtNoData.text = response.data.comingSoon?.message ?: ""
                                binding.loutSltAll.visibility = View.GONE
                                binding.txtComingSoon.visibility = View.GONE
                            }
//                        showToast(getString(com.app.elearning.R.string.no_chapter_found))

                        }
                    } else {
                        binding.txtNoData.visibility = View.VISIBLE
                        binding.txtNoData.text = response.data?.comingSoon?.message ?: ""
                        binding.loutSltAll.visibility = View.GONE
                        when (response.message) {
                            ACCESS_TOKEN_EXPIRED -> {
                                homeViewModel.getRefreshToken(prefs.getRefreshToken().toString())
                            }

                            REFRESH_TOKEN_EXPIRED -> {

                            }

                            OTHER_DEVISE_LOGIN -> {
                                val intent = Intent(this, LoginActivity::class.java)
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

                }

                is NetworkResult.Error -> {
                    binding.txtNoData.text = response.data?.comingSoon?.message ?: ""
                    binding.txtNoData.visibility = View.VISIBLE
                    binding.loutSltAll.visibility = View.GONE
//                    showToast(response.message)
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

                        OTHER_DEVISE_LOGIN -> {
                            val intent = Intent(this, LoginActivity::class.java)
                            intent.putExtra("Expired", "Expired")
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            val broadcast = Intent(LOG_OUT)
                            sendBroadcast(broadcast)
                            startActivity(intent)
                            finish()
                        }

                        else -> {
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

        homeViewModel.refreshTokenResponse.observe(this) {
            when (it) {
                is NetworkResult.Loading -> {}
                is NetworkResult.Success -> {
                    dismissProgressDialog()
                    it.data?.let { data ->

                        Log.e("token_Home = >", data.accessToken.toString())
                        prefs.setUserToken(data.accessToken)
                        prefs.setRefreshToken(data.refreshToken)
                        subjectId?.let { viewModel.getPracticeSessionList(it) }
                    }
                }

                is NetworkResult.Error -> {
                    dismissProgressDialog()
                    if (it.message == REFRESH_TOKEN_EXPIRED) {
//                            requireActivity().showToast("2")
                        Intent(this, LoginActivity::class.java).also {
                            startActivity(it)
                        }
                        finish()
                    } else if (it.message == OTHER_DEVISE_LOGIN) {

                        val intent = Intent(this, LoginActivity::class.java)
                        intent.putExtra("Expired", "Expired")
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()

                    } else showToast(it.message)

//                            requireActivity().showToast("3")

                }
            }
        }


    }


    /* private fun getDataFromServer() {
         for (i in 0..5) {
             val subject = PracticeSession()
             sessionList.add(subject)
         }

         practiceSessionInSubjectAdapter.submitList(sessionList)
     }*/

    private inner class MyBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val userData = prefs.getUserData()?.let { JSONObject(it) }
        val userName = userData?.getString("name") ?: ""
        val userPhoneNumber = userData?.getString("mobile") ?: ""
        Log.e("userDataName = > ", "userPhoneNumber -> $userPhoneNumber")
        AppUtills.activitySpendTime(
            startTime,
            binding.txtSubjectModuleName.text.toString(),
            userName,
            userPhoneNumber
        )
        unregisterReceiver(this.closingReceiver)
    }


}