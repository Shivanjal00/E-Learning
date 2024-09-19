package com.app.tensquare.activity

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.tensquare.adapter.ChapterVideoAdapter
import com.app.tensquare.adapter.RevisionVideoInSubjectAdapter
import com.app.tensquare.base.AppBaseActivity
import com.app.tensquare.customUiExample.CustomUiActivity
import com.app.tensquare.databinding.ActivityRevisionVideoInSubjectBinding
import com.app.tensquare.databinding.PopupComingSoonBinding
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.ui.chapter.ChaptersInSubjectActivity
import com.app.tensquare.ui.home.HomeViewModel
import com.app.tensquare.ui.login.LoginActivity
import com.app.tensquare.ui.notes.NotesInSubjectActivity
import com.app.tensquare.ui.revisionvideo.RevisionVideo
import com.app.tensquare.ui.revisionvideo.RevisionVideoActivity
import com.app.tensquare.ui.revisionvideo.RevisionVideoViewModel
import com.app.tensquare.ui.subscription.EnrolmentPlanPricingNewActivity
import com.app.tensquare.utils.*
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray


@AndroidEntryPoint
class RevisionVideoInSubjectActivity : AppBaseActivity() {
    private lateinit var binding: ActivityRevisionVideoInSubjectBinding
    private lateinit var revisionVideoInSubjectAdapter: RevisionVideoInSubjectAdapter
    private var revisionVideoList = mutableListOf<RevisionVideo>()
    private val homeViewModel: HomeViewModel by viewModels()

    private val viewModel: RevisionVideoViewModel by viewModels()
    private var subjectId: String? = null
    private var module: Int? = null
    private var page = 0
    private var length: Int? = null
    private var isNoDataShow:Boolean = true
    var mDialog: Dialog? = null
    private var noDataTxt:Boolean = true

    companion object{
        var isSubscribed:Boolean? = false
        var selectSubCode:String = "2"
        var isListEmpty:Boolean = false
    }

    var request = HashMap<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppUtills.setLan(this)
        binding = ActivityRevisionVideoInSubjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isSubscribed = prefs.isSubPlan()
        ChapterVideoAdapter.isSubscribe = prefs.isSubPlan()
        selectSubCode = prefs.getSelectSub().toString()

        init()
        setListeners()
        initObservers()

        //getDataFromServer()

    }

    override fun init() {
        binding.txtSubjectName.text = intent.getStringExtra("subject_name")
//        binding.txtSubjectModuleName.text = MODULE_NAME_REVISION_VIDEO  //JACK
        binding.txtSubjectModuleName.text = getString(com.app.tensquare.R.string.revision_video_n)

        module = intent.getIntExtra("module", 0)

        this.setTileData(
            intent.extras!!,
            binding.txtSubjectModuleName,
            binding.imgSubject,
            binding.llSubjectTile
        )

        binding.rvRevisionVideo.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            revisionVideoInSubjectAdapter =
                RevisionVideoInSubjectAdapter(this@RevisionVideoInSubjectActivity) { flag: Int, revisionVideo: RevisionVideo ->
//                    if (!prefs.isGuestUser()) {
//                    if (prefs.isEnrolled()) {
                    if (prefs.isSubPlan()) {
                        Intent(
                            this@RevisionVideoInSubjectActivity,
                            CustomUiActivity::class.java
                        ).also {
                            it.putExtra("subject_name", intent.getStringExtra("subject_name"))
                            it.putExtra("module", module)
                            it.putExtra("isContentVisible", true)
                            it.putExtra("video_id", revisionVideo._id)
                            it.putExtra("title", revisionVideo.title)

                            startActivity(it)
                        }
                    } else {
//                        showToastCenter(getString(com.app.tensquare.R.string.please_enrol))
                        if (revisionVideo.lock == 0){
                            Intent(this@RevisionVideoInSubjectActivity, EnrolmentPlanPricingNewActivity::class.java).also {
                                startActivity(it)
                            }
                        }else{

                            Intent(
                                this@RevisionVideoInSubjectActivity,
                                CustomUiActivity::class.java
                            ).also {
                                it.putExtra("subject_name", intent.getStringExtra("subject_name"))
                                it.putExtra("module", module)
                                it.putExtra("isContentVisible", true)
                                it.putExtra("video_id", revisionVideo._id)
                                it.putExtra("title", revisionVideo.title)
                                startActivity(it)
                            }

                        }

                    }
                }
            adapter = revisionVideoInSubjectAdapter
        }

        if (module == MODULE_REVISION_VIDEO_WITH_CHAPTER) {
            /*val resultant = intent.getStringExtra("listData")
            val gson = Gson()
            val revisionVideoListResponse =
                gson.fromJson(resultant, RevisionVideoListResponse::class.java)
            revisionVideoListResponse.data.list.forEach {
                revisionVideoList.add(it as RevisionVideo)
            }*/

            val list = JSONArray(intent.getStringExtra("listData"))
            for (i in 0 until list.length()) {
                val obj = list.getJSONObject(i)
                revisionVideoList.add(
                    RevisionVideo(
                        _id = obj.getString("_id"),
                        thumbnail = obj.getString("thumbnail"),
                        title = obj.getString("title"),
                        description = obj.getString("description"),
                        lock = obj.getInt("lock")
                    )
                )
            }
            //revisionVideoList.addAll(revisionVideoListResponse)
            revisionVideoInSubjectAdapter.submitList(revisionVideoList)
        } else {
            subjectId = intent.getStringExtra("subject_id")
            getRevisionVideoList()
        }

    }

    private fun getRevisionVideoList() {
        request = HashMap<String, String>().also {
            it["pageNo"] = (++page).toString()
            it["languageId"] = prefs.getSelectedLanguageId() ?: ""
            subjectId?.let { it1 -> it["id"] = it1 }
        }
        showProgressDialog()
        viewModel.getRevisionVideoList(request)
    }

    override fun setListsAndAdapters() {

    }

    override fun setListeners() {

        binding.imgBack.setOnClickListener { finish() }

        /*binding.txtProceed.setOnClickListener {
            Intent(
                this@RevisionVideoInSubjectActivity,
                RevisionVideoActivity::class.java
            ).also {
                it.putExtra("subject_name", intent.getStringExtra("subject_name"))
                it.putExtra("module", intent.getIntExtra("module", 0))

                startActivity(it)
            }
        }*/
    }

    override fun initObservers() {
        viewModel.revisionVideoListResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    val data = response.data

                    length = length ?: data?.length
                    println("length = $length")

                    if (data?.list?.isNotEmpty() == true) {
                        val list = revisionVideoInSubjectAdapter.currentList.toMutableList()
                        list.addAll(data.list)
                        revisionVideoInSubjectAdapter.submitList(list)
                        binding.txtComingSoon.text = data.comingSoon?.message ?: ""
                        binding.txtComingSoon.visibility = View.VISIBLE
                        if (isNoDataShow){
                            noDataTxt = false
                            binding.txtNoData.visibility = View.GONE
                            isNoDataShow = false
                        }
                        isListEmpty = false

                    } else {
                        try {
                            isListEmpty = true
                            revisionVideoInSubjectAdapter.notifyDataSetChanged()
                        }catch (e : Exception){
                            e.printStackTrace()
                        }

                        if (noDataTxt){
                            binding.txtComingSoon.visibility = View.GONE
                            binding.txtNoData.visibility = View.VISIBLE
                        }
//                        showToast(getString(com.app.elearning.R.string.no_more_data_found))
//                        binding.txtNoData.visibility = View.VISIBLE
                    }
                }
                is NetworkResult.Error -> {
//                    showToast(response.message)
//                    binding.txtNoData.visibility = View.VISIBLE
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
                        viewModel.getRevisionVideoList(request)
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

//                            requireActivity().showToast("3")

                }
            }
        }

        binding.nsv.viewTreeObserver.addOnScrollChangedListener {
            val view: View = binding.nsv.getChildAt(binding.nsv.childCount - 1) as View
            val diff: Int = view.bottom - (binding.nsv.height + binding.nsv.scrollY)
            println("lengthViewTreeObserver = $page")
            try {
                if (diff == 0 && page <= length!!) {
                    if (module == MODULE_REVISION_VIDEO) {
                        getRevisionVideoList()
                    }
                    // your pagination code
                }
            }catch (e: Exception){
                e.printStackTrace()
            }

        }
    }


    /*private fun getDataFromServer() {
        for (i in 0..5) {
            val subject = PracticeSession()
            sessionList.add(subject)
        }

        revisionVideoInSubjectAdapter.submitList(sessionList)
    }*/


}