package com.app.tensquare.ui.paper

import android.content.Intent
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.app.tensquare.R
import com.app.tensquare.adapter.PreviousPaperInSubjectAdapter
import com.app.tensquare.base.AppBaseActivity
import com.app.tensquare.databinding.ActivityModelPaperInSubjectBinding
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.ui.chapter.ChaptersInSubjectActivity
import com.app.tensquare.ui.home.HomeViewModel
import com.app.tensquare.ui.login.LoginActivity
import com.app.tensquare.ui.pdf.PdfViewerActivity
import com.app.tensquare.ui.subscription.EnrolmentPlanPricingNewActivity
import com.app.tensquare.utils.*
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import dagger.hilt.android.AndroidEntryPoint
//import kotlinx.android.synthetic.main.row_model_paper_in_subject.view.*
import okhttp3.ResponseBody
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL


@AndroidEntryPoint
class PreviousPaperInSubjectActivity : AppBaseActivity() {
    private lateinit var binding: ActivityModelPaperInSubjectBinding
    private var previousPaperInSubjectAdapter: PreviousPaperInSubjectAdapter? = null
    private lateinit var paperList: List<ModelPaper>
    private var previousYearPaperList = mutableListOf<PreviousYearPaper>()

    private val viewModel: PaperViewModel by viewModels()
    private val viewModel1: DownloadViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private var fileName: String? = null
    private var urlString: String? = null

    private var subjectId: String? = null
    private var module: Int? = null
    private var page = 0
    private var length: Int? = null
    private var noDataTxt:Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModelPaperInSubjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        setListeners()
        initObservers()
//        initSwiping()

    }

    override fun init() {

        binding.txtNoData.text = getString(com.app.tensquare.R.string.coming_soon)

        module = intent.getIntExtra("module", 0)

        //getDataFromServer()

        if (module == MODULE_PREVIOUS_YEAR_PAPER) {
            binding.txtSubjectName.text = intent.getStringExtra("subject_name")
            this.setTileData(
                intent.extras!!,
                binding.txtSubjectModuleName,
                binding.imgSubject,
                binding.llSubjectTile
            )

            subjectId = intent.getStringExtra("subject_id")
            getSubjectPaper()
        } else {
            binding.txtSubjectName.visibility = View.GONE
            binding.txtSubjectModuleName.background =
                getDrawable(com.app.tensquare.R.drawable.bg_lower_tile_prev_paper)
            binding.txtSubjectModuleName.setTextColor(
                ContextCompat.getColor(
                    this@PreviousPaperInSubjectActivity,
                    com.app.tensquare.R.color.white
                )
            )

            binding.imgSubject.setImageDrawable(resources.getDrawable(com.app.tensquare.R.drawable.img_previous_paper))
            binding.llSubjectTile.background =
                resources.getDrawable(com.app.tensquare.R.drawable.bg_previous_year_papers)


            val paddingDp = 30
            val density: Float = context.resources.displayMetrics.density
            val paddingPixel: Int = (paddingDp.toInt() * density).toInt()
            binding.imgSubject.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel)
            getGeneralPaper()
        }
//        binding.txtSubjectModuleName.text = MODULE_NAME_PREVIOUS_YEAR_PAPER
        binding.txtSubjectModuleName.text = getString(com.app.tensquare.R.string.previous_year_paper)

    }

    private fun getSubjectPaper() {
        showProgressDialog()
        subjectId?.let { viewModel.getPreviousYearPaperList(it, ++page ,prefs.getSelectedLanguageId().toString()) }
    }

    private fun getGeneralPaper() {
        showProgressDialog()
        viewModel.getHomePreviousYearPaperList(++page , prefs.getSelectedLanguageId().toString())
    }


    override fun setListsAndAdapters() {
        TODO("Not yet implemented")
    }

    override fun setListeners() {
        binding.imgBack.setOnClickListener { finish() }


        binding.txtProceed.setOnClickListener {
            try {
//                if (!prefs.isGuestUser()) {
//                if (prefs.isEnrolled()) {

                if (module == MODULE_PREVIOUS_YEAR_PAPER) {

//                    if (!prefs.isGuestUser()) {
                    if (prefs.isSubPlan()) {
                        if (urlString != null && fileName != null) {
                            startActivity(
                                Intent(
                                    this@PreviousPaperInSubjectActivity,
                                    PdfViewerActivity::class.java
                                ).also {
                                    it.putExtra("file", urlString)
                                    it.putExtra("VIA", PDF_VIA_URL)
                                })
                        }
                    } else {
                        showToastCenter(getString(R.string.please_enrol))
                    }

                }else{
                    if (urlString != null && fileName != null) {
                        startActivity(
                            Intent(
                                this@PreviousPaperInSubjectActivity,
                                PdfViewerActivity::class.java
                            ).also {
                                it.putExtra("file", urlString)
                                it.putExtra("VIA", PDF_VIA_URL)
                            })
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    override fun initObservers() {
        viewModel1.responseBody.observe(this) {
            val writtenToDisk: Boolean = writeResponseBodyToDisk(it)
            Log.d("TAG", "file download was a success? $writtenToDisk")

            if (writtenToDisk){
                showToast(getString(com.app.tensquare.R.string.download_successful))
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
                        finish()
                        startActivity(Intent(this, ChaptersInSubjectActivity::class.java))
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


        viewModel.previousYearPaperResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    val data = response.data
                    length = length ?: data?.length
                    previousYearPaperList = data?.list as ArrayList<PreviousYearPaper>
                    if (previousYearPaperList.isNotEmpty()) {
                        previousYearPaperList =
                            data.list as ArrayList<PreviousYearPaper>
                        previousPaperInSubjectAdapter =
                            PreviousPaperInSubjectAdapter(data.comingSoon,previousYearPaperList) { flag: Int, position: Int, paper: PreviousYearPaper ->//flag: Int, modelPaper: Any ->
//                                if (!prefs.isGuestUser()) {
//                                if (prefs.isEnrolled()) {
                                if (prefs.isSubPlan()) {
                                    if (flag == 1) {
                                        showToast(getString(com.app.tensquare.R.string.downloading))
//                                        setupUrlAndFileName(/*modelPaper.document*/)
//                                        viewModel1.downloadPdf(paper.document)

                                        if (paper.document.isNotEmpty() &&
                                            paper.title.isNotEmpty() && paper.fileName.isNotEmpty()){
                                            setupUrlAndFileName(paper.document , paper.fileName , paper.title)
                                        }

                                    }else if (flag == 3) {
                                        try {

                                            if (paper.document.isNotEmpty() && paper.subjectId.isNotEmpty() &&
                                                    paper.subjectId != null){

                                                showToast(getString(com.app.tensquare.R.string.sharing))
                                                shareDoc(paper.document , paper.subjectId)
                                            }

                                        }catch (e : Exception){
                                            e.printStackTrace()
                                        }

                                    } else if (flag == 2) {

                                        try {
                                            if (paper.document != null && paper.fileName != null) {
                                                startActivity(
                                                    Intent(
                                                        this@PreviousPaperInSubjectActivity,
                                                        PdfViewerActivity::class.java
                                                    ).also {
                                                        it.putExtra("file", paper.document)
                                                        it.putExtra("VIA", PDF_VIA_URL)
                                                    })
                                            }

                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }

                                    }
                                } else {
                                    Intent(this@PreviousPaperInSubjectActivity, EnrolmentPlanPricingNewActivity::class.java).also {
                                        startActivity(it)
                                    }
//                                    showToastCenter(getString(com.app.tensquare.R.string.please_enrol))
                                }
                            }
                        binding.rvModelPaper.adapter = previousPaperInSubjectAdapter
                        previousPaperInSubjectAdapter?.notifyDataSetChanged()
                        //previousPaperInSubjectAdapter.submitList(previousYearPaperList)
                        noDataTxt = false
                        binding.txtNoData.visibility = View.GONE
                    } else {
//                        if (noDataTxt){FV
                        binding.txtNoData.visibility = View.VISIBLE
                        binding.txtNoData.text = response.data.comingSoon.message
//                        }
//                        showToast(getString(R.string.no_data_found))
                    }
                }
                is NetworkResult.Error -> {
//                    showToast(response.message)
                    Log.e("PrintErrorRespo","PrintErrorRespo = ${response.data?.comingSoon} - ${response.comingSoon == null}")
                    binding.txtNoData.visibility = View.VISIBLE
                    binding.txtNoData.text = response.data?.comingSoon?.message ?: ""
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

        viewModel.homePreviousYearPaperResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    val data = response.data
                    length = length ?: data?.length
                    previousYearPaperList = data?.list as ArrayList<PreviousYearPaper>
                    if (previousYearPaperList.isNotEmpty()) {
                        previousYearPaperList = data.list as ArrayList<PreviousYearPaper>
                        previousPaperInSubjectAdapter =
                            PreviousPaperInSubjectAdapter(data.comingSoon,previousYearPaperList) { flag: Int, position: Int, paper: PreviousYearPaper ->//flag: Int, modelPaper: Any ->
//                                if (!prefs.isGuestUser()) {
//                                if (prefs.isEnrolled()) {
                                if (paper.enrollmentPlanStatus) {
                                    if (flag == 1) {

//                                        setupUrlAndFileName(/*modelPaper.document*/)
//                                        viewModel1.downloadPdf(paper.document)

                                        if (paper.document.isNotEmpty() &&
                                            paper.title.isNotEmpty() && paper.fileName.isNotEmpty()){
                                            showToast(getString(com.app.tensquare.R.string.downloading))
                                            setupUrlAndFileName(paper.document , paper.fileName , paper.title)
                                        }

                                    } else if (flag == 3) {
                                        try {

                                            if (paper.document.isNotEmpty() && paper.subjectId.isNotEmpty() &&
                                                paper.subjectId != null){
                                                showToast(getString(com.app.tensquare.R.string.sharing))
                                                shareDoc(paper.document , paper.subjectId)
                                            }

                                        }catch (e : Exception){
                                            e.printStackTrace()
                                        }

                                    }else if (flag == 2) {

                                        try {
                                                if (paper.document != null && paper.fileName != null) {
                                                    startActivity(
                                                        Intent(
                                                            this@PreviousPaperInSubjectActivity,
                                                            PdfViewerActivity::class.java
                                                        ).also {
                                                            it.putExtra("file", paper.document)
                                                            it.putExtra("VIA", PDF_VIA_URL)
                                                        })
                                                }

                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }

                                    }
                                } else
                                    showToast(getString(com.app.tensquare.R.string.please_enrol))
                            }
                        binding.rvModelPaper.adapter = previousPaperInSubjectAdapter
                        previousPaperInSubjectAdapter?.notifyDataSetChanged()
                        //previousPaperInSubjectAdapter.submitList(previousYearPaperList)
                        noDataTxt = false
                        binding.txtNoData.visibility = View.GONE
                    } else {
//                        if (noDataTxt){
                        binding.txtNoData.visibility = View.VISIBLE
                        binding.txtNoData.text = response.data?.comingSoon?.message ?: ""
//                        }
//                        showToast(getString(R.string.no_data_found))
//                        binding.txtNoData.visibility = View.VISIBLE
                    }
                }
                is NetworkResult.Error -> {
//                    showToast(response.message)
                    Log.e("PrintErrorRespo","PrintErrorRespo = ${response.data == null} - ${response.comingSoon == null}")
                    binding.txtNoData.visibility = View.VISIBLE
                    binding.txtNoData.text = response.data?.comingSoon?.message ?: ""
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

        binding.nsv.viewTreeObserver.addOnScrollChangedListener {
            val view: View = binding.nsv.getChildAt(binding.nsv.childCount - 1) as View
            val diff: Int = view.bottom - (binding.nsv.height + binding.nsv.scrollY)
            println("lengthViewTreeObserver = $page")
            if (diff == 0 && page <= length!!) {
                if (module == MODULE_PREVIOUS_YEAR_PAPER) {
                    getSubjectPaper()
                } else {
                    getGeneralPaper()
                }
            }
        }

    }

    /*private fun getDataFromServer() {
        for (i in 0..5) {
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
        previousPaperInSubjectAdapter?.notifyDataSetChanged()
    }*/

    private fun setSubjectTile() {
        binding.apply {
            when (intent.getIntExtra("SUBJECT", 0)) {
                SUBJECT_MATHS -> {
                    txtSubjectModuleName.background =
                        getDrawable(com.app.tensquare.R.drawable.bg_lower_tile_maths)

                    imgSubject.setImageDrawable(resources.getDrawable(com.app.tensquare.R.drawable.img_maths))
                    llSubjectTile.background =
                        resources.getDrawable(com.app.tensquare.R.drawable.bg_maths)
                }
                SUBJECT_SCIENCE -> {
                    txtSubjectModuleName.background =
                        getDrawable(com.app.tensquare.R.drawable.bg_lower_tile_science)
                    txtSubjectModuleName.setTextColor(
                        ContextCompat.getColor(
                            this@PreviousPaperInSubjectActivity,
                            com.app.tensquare.R.color.white
                        )
                    )

                    imgSubject.setImageDrawable(resources.getDrawable(com.app.tensquare.R.drawable.img_physics))
                    llSubjectTile.background =
                        resources.getDrawable(com.app.tensquare.R.drawable.bg_science)
                }
                SUBJECT_PHYSICS -> {
                    txtSubjectModuleName.background =
                        getDrawable(com.app.tensquare.R.drawable.bg_lower_tile_physics)
                    txtSubjectModuleName.setTextColor(
                        ContextCompat.getColor(
                            this@PreviousPaperInSubjectActivity,
                            com.app.tensquare.R.color.white
                        )
                    )

                    imgSubject.setImageDrawable(resources.getDrawable(com.app.tensquare.R.drawable.img_physics))
                    llSubjectTile.background =
                        resources.getDrawable(com.app.tensquare.R.drawable.bg_physics)
                }
                SUBJECT_CHEMISTRY -> {
                    txtSubjectModuleName.background =
                        getDrawable(com.app.tensquare.R.drawable.bg_lower_tile_chemistry)
                    txtSubjectModuleName.setTextColor(
                        ContextCompat.getColor(
                            this@PreviousPaperInSubjectActivity,
                            com.app.tensquare.R.color.white
                        )
                    )

                    imgSubject.setImageDrawable(resources.getDrawable(com.app.tensquare.R.drawable.img_chemistry))
                    llSubjectTile.background =
                        resources.getDrawable(com.app.tensquare.R.drawable.bg_chemistry)
                }
                SUBJECT_BIOLOGY -> {
                    txtSubjectModuleName.background =
                        getDrawable(com.app.tensquare.R.drawable.bg_lower_tile_biology)
                    txtSubjectModuleName.setTextColor(
                        ContextCompat.getColor(
                            this@PreviousPaperInSubjectActivity,
                            com.app.tensquare.R.color.white
                        )
                    )

                    imgSubject.setImageDrawable(resources.getDrawable(com.app.tensquare.R.drawable.img_biology))
                    llSubjectTile.background =
                        resources.getDrawable(com.app.tensquare.R.drawable.bg_biology)
                }
                /*PREVIOUS_YEAR_PAPER -> {
                    txtSubjectModuleName.background =
                        getDrawable(com.app.elearning.R.drawable.bg_lower_tile_prev_paper)
                    txtSubjectModuleName.setTextColor(
                        ContextCompat.getColor(
                            this@PreviousPaperInSubjectActivity,
                            com.app.elearning.R.color.white
                        )
                    )

                    imgSubject.setImageDrawable(resources.getDrawable(com.app.elearning.R.drawable.img_previous_paper))
                    llSubjectTile.background =
                        resources.getDrawable(com.app.elearning.R.drawable.bg_previous_year_papers)


                    val paddingDp = 30
                    val density: Float = context.resources.displayMetrics.density
                    val paddingPixel: Int = (paddingDp.toInt() * density).toInt()
                    imgSubject.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel)
                }
                else -> ""*/
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
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
                        previousPaperInSubjectAdapter?.showMenu(
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
                            urlString =
                                previousYearPaperList[viewHolder.adapterPosition].document
//                            setupUrlAndFileName() ///UNHIDE WHEN OPEN SWIPE
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
                Runnable { previousPaperInSubjectAdapter?.closeMenu() })
        })
    }


    override fun onBackPressed() {
        if (previousPaperInSubjectAdapter != null && previousPaperInSubjectAdapter?.isMenuShown() == true) {
            previousPaperInSubjectAdapter?.closeMenu()
            binding.txtProceed.visibility = View.GONE
            urlString = null
            fileName = null
        } else {
            super.onBackPressed()
        }
    }

    private fun writeResponseBodyToDisk(body: ResponseBody): Boolean {
        return try {
            val fos = openFileOutput(fileName, MODE_PRIVATE)
            fos.write(body.bytes())
            fos.close()
            true
        } catch (e: IOException) {
            false
        }
    }

    private fun setupUrlAndFileName(fileLink: String , fileNamePath: String , filetitle: String) {
        //urlString = fileLink
/*
        var url: URL? = null
        try {
            url = URL(urlString)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }

        val fileDirPath: String? =
            url?.path.toString() //url.path will give images/default/sample.pdf so to get file name we have to trim this
*/

//        fileName = "PREVIOUS_PAPER_${fileDirPath?.substring(fileDirPath.lastIndexOf('/') + 1)}"

        var addLastPath : String = fileNamePath + " " + filetitle  + ".pdf"
        fileName = "PREVIOUS_PAPER_${addLastPath}"

        viewModel1.downloadPdf(fileLink)

    }

    private fun shareDoc(urlStringEx : String , subjectId : String) {

//        val url = urlStringEx?.removePrefix("https://bhanulearning.s3.ap-south-1.amazonaws.com/pdf/")
        val url = urlStringEx?.removePrefix("https://bhanulearning.s3.ap-south-1.amazonaws.com/")

        val sendUrl = subjectId +"/"+url;

        val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
//            .setLink(Uri.parse("https://elearningdynamic.page.link/$url?&name=doc"))
            .setLink(Uri.parse("https://elearningdynamic.page.link/$sendUrl?&name=doc"))
            .setDomainUriPrefix("elearningdynamic.page.link")
            .setAndroidParameters(DynamicLink.AndroidParameters.Builder(packageName).build())
            .buildDynamicLink()
        val dynamicLinkUri = dynamicLink.uri
        FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLongLink(dynamicLinkUri)
            .buildShortDynamicLink()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val shortLink = task.result.shortLink.toString()
                    Log.d("TASK", "=====>>>>success$shortLink")
                    val intent = Intent()
                    intent.action = Intent.ACTION_SEND
                    intent.putExtra(Intent.EXTRA_TEXT, shortLink)
                    intent.type = "text/plain"
                    startActivity(intent)
                } else {
                    val intent = Intent()
                    intent.action = Intent.ACTION_SEND
                    intent.putExtra(Intent.EXTRA_TEXT, dynamicLinkUri.toString())
                    intent.type = "text/plain"
                    startActivity(intent)
                }
            }
    }

}