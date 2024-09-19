package com.app.tensquare.ui.chapter

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.tensquare.R
import com.app.tensquare.activity.IntroSliderActivity
import com.app.tensquare.ui.instruction.InstructionsActivity
import com.app.tensquare.activity.RevisionVideoInSubjectActivity
import com.app.tensquare.ui.notes.NotesInSubjectActivity
import com.app.tensquare.adapter.ChapterInSubjectAdapter
import com.app.tensquare.adapter.ChapterVideoAdapter
import com.app.tensquare.base.AppBaseActivity
import com.app.tensquare.customUiExample.CustomUiActivity
import com.app.tensquare.databinding.ActivityChapterInSubjectBinding
import com.app.tensquare.databinding.EnrolmentDialogBinding
import com.app.tensquare.databinding.PopupComingSoonBinding
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.ui.home.HomeViewModel
import com.app.tensquare.ui.initialsetup.InitialViewModel
import com.app.tensquare.ui.login.LoginActivity
import com.app.tensquare.ui.revisionvideo.RevisionVideoActivity
import com.app.tensquare.ui.revisionvideo.RevisionVideoViewModel
import com.app.tensquare.ui.session.PracticeSession
import com.app.tensquare.ui.session.SessionViewModel
import com.app.tensquare.ui.subscription.EnrolmentPlanPricingNewActivity
import com.app.tensquare.utils.*
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray

@AndroidEntryPoint
class ChaptersInSubjectActivity : AppBaseActivity() {
    private lateinit var binding: ActivityChapterInSubjectBinding
    private lateinit var chapterInSubjectAdapter: ChapterInSubjectAdapter
    private var chapterList = mutableListOf<PracticeSession>()
    private val viewModel: SessionViewModel by viewModels()
    private val viewModel1: RevisionVideoViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private var revisionVideoStatus = 1


    private var noDataTxt:Boolean = true

    companion object{
        var isSubscribed:Boolean? = false
        var selectSubCode:String = "2"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppUtills.setLan(this@ChaptersInSubjectActivity)
        binding = ActivityChapterInSubjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        revisionVideoStatus = intent.getIntExtra("revisionVideoStatus",1)
        isSubscribed = prefs.isSubPlan()
        ChapterVideoAdapter.isSubscribe = prefs.isSubPlan()
        selectSubCode = prefs.getSelectSub().toString()

        init()
        setListeners()
        initObservers()

        showProgressDialog()
        intent.getStringExtra("subject_id")?.let {
            viewModel.getPracticeSessionList(it)
        }

        //getDataFromServer()

        /*binding.llRevisionVideo.setOnClickListener {
            Intent(context, RevisionVideoActivity::class.java).also {
                startActivity(it)
            }
        }*/
    }

    override fun init() {
        binding.txtSubjectName.text = intent.getStringExtra("subject_name")

        this.setTileData(
            intent.extras!!,
            binding.txtSubjectModuleName,
            binding.imgSubject,
            binding.llSubjectTile
        )

        binding.rvChapter.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            chapterInSubjectAdapter =
                ChapterInSubjectAdapter(revisionVideoStatus) { flag: Int, practiceSession: PracticeSession ->

                    when (flag) {
                        1 -> {
//                            if (!prefs.isGuestUser()) {
//                            if (prefs.isEnrolled()) {
                            if (prefs.isSubPlan()) {
                                val request = HashMap<String, String>().also {
                                    it["pageNo"] = "1"
                                    it["chapterId"] = practiceSession._id
                                    it["languageId"] = prefs.getSelectedLanguageId() ?: ""
                                    intent.getStringExtra("subject_id")
                                        ?.let { it1 -> it["id"] = it1 }
                                }
                                showProgressDialog()
                                viewModel1.getRevisionVideoList(request)
                                /*Intent(context, RevisionVideoActivity::class.java).also {
                                    it.putExtra("subject_name", intent.getStringExtra("subject_name"))
                                    it.putExtra("subject_id", intent.getStringExtra("subject_id"))
                                    //it.putExtra("module", intent.getIntExtra("module", 0))
                                    it.putExtra("module", MODULE_REVISION_VIDEO_WITH_CHAPTER)
                                    it.putExtra("video_id", practiceSession._id)
                                    it.putExtra("title", practiceSession.name).toString()
                                    startActivity(it)
                                }*/
                            } else {
//                                showEnrolmentDialog()
//                                showToastCenter(getString(com.app.tensquare.R.string.please_enrol))
                                if (practiceSession.lock == 0){
                                    Intent(this@ChaptersInSubjectActivity, EnrolmentPlanPricingNewActivity::class.java).also {
                                        startActivity(it)
                                    }
                                }else{
                                    val request = HashMap<String, String>().also {
                                        it["pageNo"] = "1"
                                        it["chapterId"] = practiceSession._id
                                        it["languageId"] = prefs.getSelectedLanguageId() ?: ""
                                        intent.getStringExtra("subject_id")
                                            ?.let { it1 -> it["id"] = it1 }
                                    }
                                    showProgressDialog()
                                    viewModel1.getRevisionVideoList(request)
                                }

                            }
                        }
                        2 -> {
//                            if (!prefs.isGuestUser()) {
//                            if (prefs.isEnrolled()) {
                            if (prefs.isSubPlan()) {
                                Intent(context, InstructionsActivity::class.java).also {
                                    it.putExtra(SUBJECT, intent.getIntExtra(SUBJECT, 0))
                                    it.putExtra("subject_id", intent.getStringExtra("subject_id"))
                                    it.putExtra(
                                        "subject_name",
                                        intent.getStringExtra("subject_name")
                                    )
                                    it.putExtra(
                                        "chapterIds",
                                        JSONArray().put(practiceSession._id).toString()
                                    )
                                    it.putExtra("module", MODULE_PRACTICE_SESSION)
                                    startActivity(it)
                                }
                            } else {
//                                showEnrolmentDialog()
//                                showToastCenter(getString(com.app.tensquare.R.string.please_enrol))
                                if (practiceSession.lock == 0){
                                    Intent(this@ChaptersInSubjectActivity, EnrolmentPlanPricingNewActivity::class.java).also {
                                        startActivity(it)
                                    }
                                }else{
                                    Intent(context, InstructionsActivity::class.java).also {
                                        it.putExtra(SUBJECT, intent.getIntExtra(SUBJECT, 0))
                                        it.putExtra("subject_id", intent.getStringExtra("subject_id"))
                                        it.putExtra(
                                            "subject_name",
                                            intent.getStringExtra("subject_name")
                                        )
                                        it.putExtra(
                                            "chapterIds",
                                            JSONArray().put(practiceSession._id).toString()
                                        )
                                        it.putExtra("module", MODULE_PRACTICE_SESSION)
                                        startActivity(it)
                                    }
                                }
                            }
                        }
                        3 -> {
//                            if (!prefs.isGuestUser()) {
//                            if (prefs.isEnrolled()) {
                            if (prefs.isSubPlan()) {
                                Intent(context, InstructionsActivity::class.java).also {
                                    it.putExtra(SUBJECT, intent.getIntExtra(SUBJECT, 0))
                                    it.putExtra("subject_id", intent.getStringExtra("subject_id"))
                                    it.putExtra(
                                        "subject_name",
                                        intent.getStringExtra("subject_name")
                                    )
                                    it.putExtra(
                                        "chapterIds",
                                        JSONArray().put(practiceSession._id).toString()
                                    )
                                    it.putExtra("module", MODULE_TEST)
                                    startActivity(it)
                                }
                            } else {
//                                showEnrolmentDialog()
//                                showToastCenter(getString(com.app.tensquare.R.string.please_enrol))
                                if (practiceSession.lock == 0){
                                    Intent(this@ChaptersInSubjectActivity, EnrolmentPlanPricingNewActivity::class.java).also {
                                        startActivity(it)
                                    }
                                }else{
                                    Intent(context, InstructionsActivity::class.java).also {
                                        it.putExtra(SUBJECT, intent.getIntExtra(SUBJECT, 0))
                                        it.putExtra("subject_id", intent.getStringExtra("subject_id"))
                                        it.putExtra(
                                            "subject_name",
                                            intent.getStringExtra("subject_name")
                                        )
                                        it.putExtra(
                                            "chapterIds",
                                            JSONArray().put(practiceSession._id).toString()
                                        )
                                        it.putExtra("module", MODULE_TEST)
                                        startActivity(it)
                                    }
                                }
                            }
                        }
                        4 -> {
                            if (practiceSession.lock == 0){
                                Intent(this@ChaptersInSubjectActivity, EnrolmentPlanPricingNewActivity::class.java).also {
                                    startActivity(it)
                                }
                            }else{
                                Intent(context, NotesInSubjectActivity::class.java).also {
                                    it.putExtra(SUBJECT, intent.getIntExtra(SUBJECT, 0))
                                    it.putExtra(
                                        "subject_name",
                                        intent.getStringExtra("subject_name")
                                    )
                                    it.putExtra("subject_id", intent.getStringExtra("subject_id"))
                                    it.putExtra("module", MODULE_NOTE_RESOURCE_WITH_CHAPTER)
                                    it.putExtra("chapterId", practiceSession._id)
                                    startActivity(it)
                                }
                            }

                        }

                    }
                }
            adapter = chapterInSubjectAdapter
        }


/*
        binding.rvPracticeSession.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            practiceSessionInSubjectAdapter =
                PracticeSessionInSubjectAdapter { items: MutableList<PracticeSession>, isChecked: Boolean, practiceSession: PracticeSession ->

                    var element = items.first {
                        it._id == practiceSession._id
                    }
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


                }
            adapter = practiceSessionInSubjectAdapter
        }
*/
    }

    var mDialog: Dialog? = null
    private fun showEnrolmentDialog() {

//        val userData = preferences.getUserData()?.let { JSONObject(it) }
        val txtMgs = getString(com.app.tensquare.R.string.kindly_first_register_or_login)
        val btnText1 = getString(com.app.tensquare.R.string.yes)

        if (mDialog != null) {
            if (mDialog!!.isShowing) {
                return
            }
        }
        mDialog = Dialog(this@ChaptersInSubjectActivity)

        val dialogBinding: EnrolmentDialogBinding =
            EnrolmentDialogBinding.inflate(layoutInflater)

        mDialog!!.setContentView(dialogBinding.root)
        mDialog!!.setCanceledOnTouchOutside(true)
        mDialog!!.setCancelable(true)
        mDialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        mDialog!!.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        mDialog!!.window!!.setGravity(Gravity.CENTER)

        dialogBinding.txtMessage.text = txtMgs
        dialogBinding.txtChoosePlan.text = btnText1

        dialogBinding.txtChoosePlan.setOnClickListener {

            mDialog!!.dismiss()
            Intent(this@ChaptersInSubjectActivity, IntroSliderActivity::class.java).also {
                startActivity(it)
            }

        }

        dialogBinding.imgClose.setOnClickListener {
            mDialog!!.dismiss()
        }

        mDialog!!.show()
    }



    override fun setListsAndAdapters() {
        TODO("Not yet implemented")
    }

    override fun setListeners() {
        binding.imgBack.setOnClickListener { finish() }

        /*binding.txtProceed.setOnClickListener {
            Intent(
                this@PracticeSessionInSubjectActivity,
                InstructionsActivity::class.java
            ).also {
                it.putExtra("subject_name", intent.getStringExtra("subject_name"))
                it.putExtra("module", intent.getIntExtra("module", 0))

                startActivity(it)
            }
        }*/

        /*binding.chkSelectAll.setOnCheckedChangeListener { _, isChecked ->
            var items = practiceSessionInSubjectAdapter.currentList.toMutableList()

            items.forEach {
                val index = items.indexOf(it)
                if (isChecked != it.isChecked) {
                    val element = it.copy(isChecked = isChecked)
                    items[index] = element
                }
            }

            (binding.rvPracticeSession.adapter as PracticeSessionInSubjectAdapter).submitList(
                items
            )
        }*/
    }

    override fun initObservers() {
        viewModel.practiceSessionListResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    if (response.data != null) {
                        if (response.data.status == STATUS_FAILURE && response.data.message == OTHER_DEVISE_LOGIN) {
                            val intent =  Intent(this, LoginActivity::class.java)
                            intent.putExtra("Expired", "Expired")
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            val broadcast = Intent(LOG_OUT)
                            sendBroadcast(broadcast)
                            startActivity(intent)
                            finish()
                            return@observe
                        }

                        (response.data.data as MutableList<PracticeSession>?)?.let {
                            chapterList = it
                        }

                        chapterInSubjectAdapter.submitList(chapterList)

                        noDataTxt = false
                        binding.txtComingSoon.text = response.data.comingSoon?.message ?: ""
//                        binding.txtNoData.visibility = if (chapterList.isEmpty()) View.VISIBLE else View.GONE
                        binding.txtComingSoon.visibility = View.VISIBLE
                    } else {
                        if (noDataTxt){
//                            binding.txtNoData.visibility = View.VISIBLE
                            binding.txtComingSoon.text = response.data?.comingSoon?.message ?: ""
                            binding.txtComingSoon.visibility = View.VISIBLE
                        }

//                        showToast(getString(com.app.elearning.R.string.no_chapter_found))
                    }
                }
                is NetworkResult.Error -> {
//                    showToast("Error = ${response.message}")
                    binding.txtComingSoon.text = response.data?.comingSoon?.message ?: ""
                    binding.txtComingSoon.visibility = View.VISIBLE
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

        homeViewModel.refreshTokenResponse.observe(this){
            when (it){
                is NetworkResult.Loading -> {}
                is NetworkResult.Success -> {
                    dismissProgressDialog()
                    it.data?.let {data ->

                        Log.e("token_Home = >" , data.accessToken.toString())
                        prefs.setUserToken(data.accessToken)
                        prefs.setRefreshToken(data.refreshToken)

                        showProgressDialog()
                        intent.getStringExtra("subject_id")?.let {
                            viewModel.getPracticeSessionList(it)
                        }
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

        viewModel1.revisionVideoListResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    val list = response.data?.list
                    if (list?.isNotEmpty() == true) {
                        if (list.size > 1) {
                            Intent(this, RevisionVideoInSubjectActivity::class.java).also {
                                it.putExtra(SUBJECT, intent.getIntExtra(SUBJECT, 0))
                                it.putExtra(
                                    "subject_name",
                                    intent.getStringExtra("subject_name")
                                )
                                it.putExtra("subject_id", intent.getStringExtra("subject_id"))
                                it.putExtra("module", MODULE_REVISION_VIDEO_WITH_CHAPTER)
                                it.putExtra("listData", Gson().toJson(list))
                                startActivity(it)
                            }
                        } else {
                            Intent(context, CustomUiActivity::class.java).also {
                                it.putExtra(
                                    "subject_name",
                                    intent.getStringExtra("subject_name")
                                )
                                it.putExtra("isContentVisible", true)
                                it.putExtra("subject_id", intent.getStringExtra("subject_id"))
                                //it.putExtra("module", intent.getIntExtra("module", 0))
                                it.putExtra("module", MODULE_REVISION_VIDEO)
                                it.putExtra("video_id", list[0]._id)
                                it.putExtra("title", list[0].title).toString()
                                startActivity(it)
                            }
                        }
                    } else {
//                        showToast(getString(com.app.tensquare.R.string.no_more_videos))
                        openPopup()
                    }
                }
                is NetworkResult.Error -> {
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

    var mnDialog: Dialog? = null
    private fun openPopup() {
        try {

            mnDialog = Dialog(this@ChaptersInSubjectActivity)

            val dialogBinding: PopupComingSoonBinding =
                PopupComingSoonBinding.inflate(layoutInflater)
            mnDialog!!.setContentView(dialogBinding.root)
            mnDialog!!.setCanceledOnTouchOutside(true)
            mnDialog!!.setCancelable(true)
            mnDialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            mnDialog!!.window!!.setLayout(
//                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            mnDialog!!.window!!.setGravity(Gravity.CENTER)

            dialogBinding.ivClose.setOnClickListener {
                mnDialog!!.dismiss()
            }

            mnDialog!!.show()

        }catch (e:Exception){
            e.printStackTrace()
        }
    }


    /*private fun getDataFromServer() {
        for (i in 0..5) {
            val subject = Subject()
            chapterList.add(subject)
        }

        chapterInSubjectAdapter.submitList(chapterList)
    }*/

}