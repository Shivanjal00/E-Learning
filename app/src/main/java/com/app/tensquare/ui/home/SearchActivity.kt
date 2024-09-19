package com.app.tensquare.ui.home

import android.R
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.tensquare.adapter.SearchAdapter
import com.app.tensquare.base.AppBaseActivity
import com.app.tensquare.customUiExample.CustomUiActivity
import com.app.tensquare.databinding.ActivitySearchBinding
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.ui.login.LoginActivity
import com.app.tensquare.ui.paper.DownloadViewModel
import com.app.tensquare.ui.pdf.PdfViewerActivity
import com.app.tensquare.ui.revisionvideo.RevisionVideoActivity
import com.app.tensquare.ui.subscription.EnrolmentPlanPricingNewActivity
import com.app.tensquare.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchActivity : AppBaseActivity() {

    private lateinit var searchAdapter: SearchAdapter
    private lateinit var searchAdapter2: SearchAdapter
    private var docList = ArrayList<Doc>()
    private var videoList = ArrayList<Doc>()
    private val viewModel: HomeViewModel by viewModels()
    private val viewModel1: DownloadViewModel by viewModels()
    private lateinit var binding: ActivitySearchBinding

    private var fileName: String? = null
    private var urlString: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        setListeners()
        setListsAndAdapters()
        initObservers()
        //initSwiping()

    }

    override fun init() {
        binding.edtSearch.requestFocus()
        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if (p0 != null && p0.isNotEmpty() &&  p0.toString().length > 2) {
                    docList.clear()
                    videoList.clear()
//                    showProgressDialog()
                    val request = SearchRequest(
                        languageId = prefs.getSelectedLanguageId().toString(),
                        searchKey = p0.toString(),
                        pageNo = 1
                    )
                    viewModel.searchSomething(request)
                }else{
                    docList.clear()
                    videoList.clear()
                    setListsAndAdapters()

                    binding.txt1.visibility = View.GONE
                    binding.line1.visibility = View.GONE
                    binding.rvPDF.visibility = View.GONE
                    binding.txt2.visibility = View.GONE
                    binding.line2.visibility = View.GONE
                    binding.rvVideo.visibility = View.GONE
                }

            }

            override fun afterTextChanged(p0: Editable?) {
//                if (p0 != null && p0.isNotEmpty() && p0.toString().length > 2) {
//                    docList.clear()
//                    videoList.clear()
////                    showProgressDialog()
//                    val request = SearchRequest(
//                        languageId = prefs.getSelectedLanguageId().toString(),
//                        searchKey = p0.toString(),
//                        pageNo = 1
//                    )
//                    viewModel.searchSomething(request)
//                }
            }
        })

    }

    override fun setListsAndAdapters() {
        binding.rvPDF.apply {
            layoutManager =
                LinearLayoutManager(this@SearchActivity, LinearLayoutManager.VERTICAL, false)
            searchAdapter =
                SearchAdapter(docList, this@SearchActivity) { doc: Doc, position: Int, flag: Int ->
//                    if (!prefs.isGuestUser()) {
//                    if (prefs.isEnrolled()) {
                    if (doc.enrollmentPlanStatus) {
                        if (flag == 1) {
                            if (doc.document != null && doc.document.isNotEmpty()){

                                startActivity(
                                    Intent(
                                        this@SearchActivity,
                                        PdfViewerActivity::class.java
                                    ).also {
                                        it.putExtra("file", doc.document!![0])
                                        it.putExtra("VIA", PDF_VIA_URL)
                                    })

                            }

                            /*showToast("Downloading...")
                            setupUrlAndFileName(*//*modelPaper.document*//*)
                            viewModel1.downloadPdf(doc.document!![0])*/
                        }
                    } else {
                        Intent(this@SearchActivity, EnrolmentPlanPricingNewActivity::class.java).also {
                            startActivity(it)
                        }
                       }
                }
            adapter = searchAdapter
        }

        binding.rvVideo.apply {
            layoutManager =
                LinearLayoutManager(this@SearchActivity, LinearLayoutManager.VERTICAL, false)
            searchAdapter2 =
                SearchAdapter(
                    videoList,
                    this@SearchActivity
                ) { doc: Doc, position: Int, flag: Int ->
                    try {
//                        if (!prefs.isGuestUser()) {
//                        if (prefs.isEnrolled()) {
                        if (doc.enrollmentPlanStatus) {
                            Intent(context, CustomUiActivity::class.java).also {
                                it.putExtra("subject_name", intent.getStringExtra("subject_name"))
                                it.putExtra("module", MODULE_SEARCH)
                                it.putExtra("video_id", doc._id)
                                it.putExtra("title", doc.title)
                                startActivity(it)
                            }
                        } else {
                            Intent(this@SearchActivity, EnrolmentPlanPricingNewActivity::class.java).also {
                                startActivity(it)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()

                    }
                }
            adapter = searchAdapter2
        }


    }

    override fun setListeners() {

        binding.imgBack.setOnClickListener { finish() }

        /*binding.txtProceed.setOnClickListener {
            try {
                if (!prefs.isGuestUser()) {
                    if (urlString != null && fileName != null) {
                        startActivity(
                            Intent(
                                this@SearchActivity,
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
        /*viewModel1.responseBody.observe(this) {
            val writtenToDisk: Boolean = writeResponseBodyToDisk(it)
            Log.d("TAG", "file download was a success? $writtenToDisk")
        }*/

        viewModel.refreshTokenResponse.observe(this){
            when (it){
                is NetworkResult.Loading -> {}
                is NetworkResult.Success -> {
                    dismissProgressDialog()
                    it.data?.let {data ->

                        Log.e("token_Home = >" , data.accessToken.toString())
                        prefs.setUserToken(data.accessToken)
                        prefs.setRefreshToken(data.refreshToken)
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

        viewModel.searchResponse.observe(this) { response ->
//            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {

                    docList.clear()
                    videoList.clear()

                    val data = response.data
                    val list = data?.docs
                    if (list?.isNotEmpty() == true) {
                        list.forEach {
//                            if (it.videoURL != null && it.document == null) { // jack
//                            if (it.videoURL != null && it.videoURL.isNotEmpty() && it.document == null) {
                            if (it.videoURL!!.isNotEmpty()) {
                                videoList.add(it)
                            } else {
                                docList.add(it)
                            }
                        }
                        /* docList = data?.docs!! as MutableList<Doc>
                         videoList = data?.docs!! as MutableList<Doc>*/
                        if (docList.isNotEmpty()) {
                            searchAdapter.notifyDataSetChanged()
                            binding.apply {
                                txt1.visibility = View.VISIBLE
                                line1.visibility = View.VISIBLE
                                rvPDF.visibility = View.VISIBLE
                            }
                        } else {
                            binding.apply {
                                txt1.visibility = View.GONE
                                line1.visibility = View.GONE
                                rvPDF.visibility = View.GONE
                            }

                        }

                        if (videoList.isNotEmpty()) {
                            searchAdapter2.notifyDataSetChanged()
                            binding.apply {
                                txt2.visibility = View.VISIBLE
                                line2.visibility = View.VISIBLE
                                rvVideo.visibility = View.VISIBLE
                            }
                        } else {
                            binding.apply {
                                txt2.visibility = View.GONE
                                line2.visibility = View.GONE
                                rvVideo.visibility = View.GONE
                            }

                        }
                        binding.txtNoData.visibility = View.GONE
                    } else {
                        binding.txt1.visibility = View.GONE
                        binding.line1.visibility = View.GONE
                        binding.txt2.visibility = View.GONE
                        binding.line2.visibility = View.GONE
                        binding.rvPDF.adapter?.notifyDataSetChanged()
                        binding.rvVideo.adapter?.notifyDataSetChanged()
                        binding.txtNoData.visibility = View.VISIBLE
                    }
                }
                is NetworkResult.Error -> {
                    when (response.message) {
                        ACCESS_TOKEN_EXPIRED -> {
//                            prefs.setUserToken(prefs.getRefreshToken())
//                            viewModel.getRefreshToken(prefs.getRefreshToken().toString())
                            viewModel.getRefreshToken(prefs.getRefreshToken().toString())
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

    /*private fun writeResponseBodyToDisk(body: ResponseBody): Boolean {
        return try {
            val fos = openFileOutput(fileName, MODE_PRIVATE)
            fos.write(body.bytes())
            fos.close()

            true
        } catch (e: IOException) {
            false
        }
    }*/


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
                    binding.rvPDF.post(Runnable {
                        searchAdapter?.showMenu(
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
                            urlString = docList[viewHolder.adapterPosition].document?.get(0)
                            setupUrlAndFileName()
                        }
                    } else {
                        background.setBounds(0, 0, 0, 0)
                    }
                    background.draw(c)
                }
            }

        val itemTouchHelper = ItemTouchHelper(touchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvPDF)

        binding.rvPDF.setOnScrollChangeListener(View.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            binding.rvPDF.post(
                Runnable { searchAdapter?.closeMenu() })
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
        if (searchAdapter != null && searchAdapter?.isMenuShown() == true) {
            searchAdapter?.closeMenu()
            binding.txtProceed.visibility = View.GONE
            urlString = null
            fileName = null
        } else {
            super.onBackPressed()
        }
    }*/

}