package com.app.tensquare.ui.latestupdate

import android.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.tensquare.base.AppBaseActivity
import com.app.tensquare.customUiExample.CustomUiActivity
import com.app.tensquare.databinding.ActivityLatestUpdatesBinding
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.ui.chapter.ChaptersInSubjectActivity
import com.app.tensquare.ui.home.HomeViewModel
import com.app.tensquare.ui.login.LoginActivity
import com.app.tensquare.ui.paper.DownloadViewModel
import com.app.tensquare.ui.pdf.PdfViewerActivity
import com.app.tensquare.ui.revisionvideo.RevisionVideoActivity
import com.app.tensquare.utils.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LatestUpdatesActivity : AppBaseActivity() {
    private lateinit var binding: ActivityLatestUpdatesBinding
    private lateinit var latestUpdateAdapter: LatestUpdatesAdapter
    private var dataList = ArrayList<Update>()
    private val viewModel: LatestUpdateViewModel by viewModels()
    private val viewModel1: DownloadViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    private var module: Int? = null
    private var page = 0
    private var length: Int? = null

    private var fileName: String? = null
    private var urlString: String? = null

    private var noDataTxt:Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLatestUpdatesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        setListeners()
        initObservers()
        //getDataFromServer()
        getLatestUpdateList()
        //initSwiping()
    }

    private fun getLatestUpdateList() {
        showProgressDialog()
        Log.e("PrintLanguageId","is id = ${prefs.getSelectedLanguageId()}")
        viewModel.getLatestUpdateList(++page , prefs.getSelectedLanguageId().toString())
    }

    override fun init() {
        binding.txtSubjectName.visibility = View.GONE
//        binding.txtSubjectModuleName.text = MODULE_NAME_LATEST_UPDATES
        binding.txtSubjectModuleName.text = getString(com.app.tensquare.R.string.latest_updates)

        binding.rvModelPaper.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            latestUpdateAdapter =
                LatestUpdatesAdapter(this@LatestUpdatesActivity , dataList) { flag: Int, update: Update ->
                    if (!prefs.isGuestUser()) {
//                    if (prefs.isEnrolled()) {
                        if (flag == 1)
                            Intent(
                                this@LatestUpdatesActivity,
                                CustomUiActivity::class.java
                            ).also {
                                it.putExtra("module", MODULE_LATEST_UPDATE)
                                it.putExtra("video_id", update._id)
                                it.putExtra("title", update.name)
                                startActivity(it)
                            }
                        else if (flag == 2) {
                            startActivity(
                                Intent(
                                    this@LatestUpdatesActivity,
                                    PdfViewerActivity::class.java
                                ).also {
                                    it.putExtra("file", update.document)
                                    it.putExtra("VIA", PDF_VIA_URL)
                                })

                            /*showToast("Downloading...")
                            setupUrlAndFileName(*//*modelPaper.document*//*)
                            viewModel1.downloadPdf(update.document)*/
                        }
                    } else
                        showToast(getString(com.app.tensquare.R.string.please_enrol))
                }
            adapter = latestUpdateAdapter

        }

        //showProgressDialog()
        //intent.getStringExtra("subject_id")?.let { viewModel.getPreviousYearPaperList(it, 1) }

    }

    override fun setListsAndAdapters() {
        TODO("Not yet implemented")
    }

    override fun setListeners() {
        binding.imgBack.setOnClickListener { finish() }

        /*binding.txtProceed.setOnClickListener {
            try {
                if (!prefs.isGuestUser()) {
                    if (urlString != null && fileName != null) {
                        startActivity(
                            Intent(
                                this@LatestUpdatesActivity,
                                PdfViewerActivity::class.java
                            ).also {
                                it.putExtra("file", urlString)
                                it.putExtra("VIA", PDF_VIA_URL)
                            })
                    }
                } else {
                    showToast("Please login")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }*/
    }

    override fun initObservers() {
        viewModel.latestUpdateListResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    val data = response.data
                    if (data != null) {
                        length = length ?: data.length
                        println("length = $length")
                        dataList.addAll(data.list)
                    }

                    if (dataList.isNotEmpty()) {
                        latestUpdateAdapter.notifyDataSetChanged()

                        noDataTxt = false
                        binding.txtComingSoon.text = data?.comingSoon?.message ?: ""
                        binding.txtNoData.visibility = if (dataList.isEmpty()) View.VISIBLE else View.GONE
                    } else {
                        if (noDataTxt){
                            binding.txtNoData.visibility = View.VISIBLE
                            binding.txtNoData.visibility = View.GONE
                        }
//                        showToast(getString(com.app.elearning.R.string.no_more_data_found))
                    }

                }
                is NetworkResult.Error -> {
//                    showToast(response.message)
                    binding.txtNoData.visibility = View.VISIBLE
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
                        viewModel.getLatestUpdateList(++page , prefs.getSelectedLanguageId().toString())
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
            if (diff == 0 && page <= length!!) {
                getLatestUpdateList()
            }
        }

    }

    private fun getDataFromServer() {
        /* for (i in 0..5) {
             val subject = PreviousYearPaper(
                 _id = Random(1).nextInt().toString(),
                 boardName = "CBSE",
                 document = "www.document.com",
                 setNumber = 2,
                 title = "English",
                 year = 2020
             )
             previousYearPaperList.add(subject)
         }
         latestUpdateAdapter.submitList(previousYearPaperList)*/
    }

/*
    private fun initSwiping() {
        val touchHelperCallback: ItemTouchHelper.SimpleCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                private val background = ColorDrawable(resources.getColor(R.color.darker_gray))
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    binding.rvModelPaper.post(Runnable {
                        latestUpdateAdapter?.showMenu(
                            viewHolder.adapterPosition
                        )
                    })
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX / 3,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )

                    val itemView = viewHolder.itemView
                    if (dX > 0) {
                        background.setBounds(
                            itemView.left,
                            itemView.top,
                            itemView.left + dX.toInt(),
                            itemView.bottom
                        )
                    } else if (dX < 0) {
                        background.setBounds(
                            itemView.right + dX.toInt(),
                            itemView.top,
                            itemView.right,
                            itemView.bottom
                        )
                        if (itemView.translationX < -200f) {
                            binding.txtProceed.visibility = View.VISIBLE
                            urlString = dataList[viewHolder.adapterPosition].document
                            setupUrlAndFileName()
                        }
                    } else {
                        background.setBounds(0, 0, 0, 0)
                    }
                    background.draw(c)
                }
            }

        val itemTouchHelper = ItemTouchHelper(touchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvModelPaper)

        binding.rvModelPaper.setOnScrollChangeListener(View.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            binding.rvModelPaper.post(
                Runnable { latestUpdateAdapter.closeMenu() })
        })
    }
*/

    /*private fun setupUrlAndFileName(*//*fileLink: String*//*) {
        //urlString = fileLink

        var url: URL? = null
        try {
            url = URL(urlString)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }

        val fileDirPath: String? =
            url?.path.toString() //url.path will give images/default/sample.pdf so to get file name we have to trim this

        fileName = "MODEL_PAPER_${fileDirPath?.substring(fileDirPath.lastIndexOf('/') + 1)}"
    }*/

    /*override fun onBackPressed() {
        if (latestUpdateAdapter != null && latestUpdateAdapter?.isMenuShown() == true) {
            latestUpdateAdapter?.closeMenu()
            binding.txtProceed.visibility = View.GONE
            urlString = null
            fileName = null
        } else {
            super.onBackPressed()
        }
    }*/

}