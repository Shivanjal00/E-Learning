package com.app.tensquare.ui.questionbankpaper

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import com.app.tensquare.adapter.QuestionBankPaperAdapter
import com.app.tensquare.base.AppBaseActivity
import com.app.tensquare.databinding.ActivityQuestionBankPaperActivityBinding
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.ui.chapter.ChaptersInSubjectActivity
import com.app.tensquare.ui.home.HomeViewModel
import com.app.tensquare.ui.login.LoginActivity
import com.app.tensquare.ui.paper.DownloadViewModel
import com.app.tensquare.ui.pdf.PdfViewerActivity
import com.app.tensquare.utils.*
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.ResponseBody
import java.io.File
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL

@AndroidEntryPoint
class QuestionBankPaperActivity : AppBaseActivity() {

    private lateinit var binding: ActivityQuestionBankPaperActivityBinding
    private var questionBankPaperAdapter: QuestionBankPaperAdapter? = null
    private var queBankPaperList = mutableListOf<questionList>()

    private val viewModel: QuestionBankPaperViewModel by viewModels()
    private val viewModel1: DownloadViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private var fileName: String? = null
    private var urlString: String? = null

    private var subjectId: String? = null
    private var module: Int? = null
    private var page = 0
    private var length: Int? = null
    private var noDataTxt:Boolean = true

    var filesList = java.util.ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionBankPaperActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSubjectTile()

        init()
        setListeners()
        initObservers()

        val currentDir = filesDir

        filesList = java.util.ArrayList<String>()
        currentDir.list()?.let { filesList.addAll(it) }



    }

    override fun init() {

        subjectId = intent.getStringExtra("subject_id")
//        subjectId = "6304c19d7ab5c99037f50669"
        binding.txtSubjectName.text = intent.getStringExtra("subject_name")
        getSubjectPaper()

    }

    private fun getSubjectPaper() {
        showProgressDialog()
        subjectId?.let { viewModel.getQuestionBankPaperResponseList(it,prefs.getSelectedLanguageId() ?: "", ++page) }
    }

    override fun setListsAndAdapters() {
        TODO("Not yet implemented")
    }

    override fun setListeners() {

        binding.imgBack.setOnClickListener { finish() }

    }

    override fun initObservers() {

        viewModel1.responseBody.observe(this) {
            val writtenToDisk: Boolean = writeResponseBodyToDisk(it)
            Log.d("TAG", "file download was a success? $writtenToDisk")

            if (writtenToDisk){
                showToast(getString(com.app.tensquare.R.string.download_successful))
            }else{
//                showToast(getString(com.app.tensquare.R.string.something_went_wrong_p))
            }

        }

        viewModel.questionbankpaperResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    val data = response.data
                    length = length ?: data?.length

                    queBankPaperList = data?.questionList as ArrayList<questionList>

                    if (queBankPaperList.isNotEmpty()) {
                        queBankPaperList = data.questionList as ArrayList<questionList>
                        questionBankPaperAdapter =
                            QuestionBankPaperAdapter(this@QuestionBankPaperActivity , filesList , queBankPaperList)
                            { flag: Int, position: Int, paper: questionList ->//flag: Int, modelPaper: Any ->

                                if (flag == 1) {
                                    if (paper.document.isNotEmpty()){
                                        startActivity(
                                            Intent(
                                                this@QuestionBankPaperActivity,
                                                PdfViewerActivity::class.java
                                            ).also {
                                                it.putExtra("file", paper.document)
                                                it.putExtra("VIA", PDF_VIA_URL)
                                            })
                                    }
                                }else if (flag == 2){
//                                    showToast(paper.document)
                                    showToast(getString(com.app.tensquare.R.string.downloading))
                                    if (paper.document.isNotEmpty() && paper.fileName.isNotEmpty()) {
                                        setupUrlAndFileName(paper.document , paper.fileName)
                                    }

//                                    deviceDownload(paper.document , paper.fileName)

                                }else if (flag == 3){

                                    try {
                                        if (paper.document.isNotEmpty() && paper.subjectId.isNotEmpty() &&
                                            paper.subjectId != null){
                                            showToast(getString(com.app.tensquare.R.string.sharing))
                                            shareDoc(paper.document , paper.subjectId)
                                        }
                                    }catch (e : Exception){
                                        e.printStackTrace()
                                    }

                                }

                            }
                        binding.rvQuePaper.adapter = questionBankPaperAdapter
                        questionBankPaperAdapter?.notifyDataSetChanged()

                        //previousPaperInSubjectAdapter.submitList(previousYearPaperList)
                        noDataTxt = false
                        binding.txtNoData.visibility = View.GONE
                    } else {
                        if (noDataTxt){
                            binding.txtNoData.visibility = View.VISIBLE
                        }
//                        showToast(getString(R.string.no_data_found))
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

        homeViewModel.refreshTokenResponse.observe(this){
            when (it){
                is NetworkResult.Loading -> {}
                is NetworkResult.Success -> {
                    dismissProgressDialog()
                    it.data?.let {data ->

                        Log.e("token_Home = >" , data.accessToken.toString())
                        prefs.setUserToken(data.accessToken)
                        prefs.setRefreshToken(data.refreshToken)
                        subjectId?.let { viewModel.getQuestionBankPaperResponseList(it,prefs.getSelectedLanguageId() ?: "", ++page) }
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

                    getSubjectPaper()

            }
        }

    }

    private fun writeResponseBodyToDisk(body: ResponseBody): Boolean {
        return try {
            val fos = openFileOutput(fileName, MODE_PRIVATE)
            fos.write(body.bytes())
            fos.close()
            true
//        } catch (e: IOException) {
        } catch (e: Exception) {
            false
        }
    }

    private fun setupUrlAndFileName(fileLink: String , fileNamePath: String ) {
        //urlString = fileLink  //JACK
        urlString = fileLink

        var url: URL? = null
        try {
            url = URL(urlString)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }

        val fileDirPath: String? =
            url?.path.toString() //url.path will give images/default/sample.pdf so to get file name we have to trim this

        var addLastPath : String = fileNamePath + ".pdf"

//        fileName = "QUESTION_BANK_PAPER_${fileDirPath?.substring(fileDirPath.lastIndexOf('/') + 1)}"
        fileName = "QUESTION_BANK_PAPER_${addLastPath}"

        viewModel1.downloadPdf(fileLink)
    }




    private fun setSubjectTile() {
        binding.apply {
            when (intent.getIntExtra(SUBJECT, 0)) {
                SUBJECT_MATHS -> {
                    imgSubject.setImageDrawable(resources.getDrawable(com.app.tensquare.R.drawable.maths_ic))
                    llSubjectTile.background =
                        resources.getDrawable(com.app.tensquare.R.drawable.bg_maths)

                    val paddingDp = 25
                    val density: Float = this@QuestionBankPaperActivity.resources.displayMetrics.density
                    val paddingPixel: Int = (paddingDp.toInt() * density).toInt()
                    imgSubject.setPadding(
                        paddingPixel,
                        paddingPixel,
                        paddingPixel,
                        paddingPixel
                    )
                }
                SUBJECT_SCIENCE -> {
                    imgSubject.setImageDrawable(resources.getDrawable(com.app.tensquare.R.drawable.science_ic))
                    llSubjectTile.background =
                        resources.getDrawable(com.app.tensquare.R.drawable.bg_science)

                    val paddingDp = 25
                    val density: Float = this@QuestionBankPaperActivity.resources.displayMetrics.density
                    val paddingPixel: Int = (paddingDp.toInt() * density).toInt()
                    imgSubject.setPadding(
                        paddingPixel,
                        paddingPixel,
                        paddingPixel,
                        paddingPixel
                    )
                }
                SUBJECT_PHYSICS -> {

                    imgSubject.setImageDrawable(resources.getDrawable(com.app.tensquare.R.drawable.img_physics))
                    llSubjectTile.background =
                        resources.getDrawable(com.app.tensquare.R.drawable.bg_physics)
                }
                SUBJECT_CHEMISTRY -> {
                    imgSubject.setImageDrawable(resources.getDrawable(com.app.tensquare.R.drawable.img_chemistry))
                    llSubjectTile.background =
                        resources.getDrawable(com.app.tensquare.R.drawable.bg_chemistry)
                }
                SUBJECT_BIOLOGY -> {

                    imgSubject.setImageDrawable(resources.getDrawable(com.app.tensquare.R.drawable.img_biology))
                    llSubjectTile.background =
                        resources.getDrawable(com.app.tensquare.R.drawable.bg_biology)
                }
                SUBJECT_SANSKRIT -> {
                    imgSubject.setImageDrawable(resources.getDrawable(com.app.tensquare.R.drawable.san_ic))
                    llSubjectTile.background =
                        resources.getDrawable(com.app.tensquare.R.drawable.bg_san_12)

                    val paddingDp = 20
                    val density: Float = this@QuestionBankPaperActivity.resources.displayMetrics.density
                    val paddingPixel: Int = (paddingDp.toInt() * density).toInt()
                    imgSubject.setPadding(
                        paddingPixel,
                        paddingPixel,
                        paddingPixel,
                        paddingPixel
                    )

                }
                SUBJECT_HINDI -> {
                    imgSubject.setImageDrawable(resources.getDrawable(com.app.tensquare.R.drawable.hindi_ic))
                    llSubjectTile.background =
                        resources.getDrawable(com.app.tensquare.R.drawable.bg_hindi_12)

                    val paddingDp = 20
                    val density: Float = this@QuestionBankPaperActivity.resources.displayMetrics.density
                    val paddingPixel: Int = (paddingDp.toInt() * density).toInt()
                    imgSubject.setPadding(
                        paddingPixel,
                        paddingPixel,
                        paddingPixel,
                        paddingPixel
                    )

                }
                SUBJECT_SOCIAL_STUDIES -> {
                    imgSubject.setImageDrawable(resources.getDrawable(com.app.tensquare.R.drawable.ss_ic))
                    llSubjectTile.background =
                        resources.getDrawable(com.app.tensquare.R.drawable.bg_biology)

                    val paddingDp = 20
                    val density: Float = this@QuestionBankPaperActivity.resources.displayMetrics.density
                    val paddingPixel: Int = (paddingDp.toInt() * density).toInt()
                    imgSubject.setPadding(
                        paddingPixel,
                        paddingPixel,
                        paddingPixel,
                        paddingPixel
                    )

                }
                SUBJECT_ENGLISH -> {
                    imgSubject.setImageDrawable(resources.getDrawable(com.app.tensquare.R.drawable.eng_ic))
                    llSubjectTile.background =
                        resources.getDrawable(com.app.tensquare.R.drawable.bg_english_15)

                    val paddingDp = 25
                    val density: Float = this@QuestionBankPaperActivity.resources.displayMetrics.density
                    val paddingPixel: Int = (paddingDp.toInt() * density).toInt()
                    imgSubject.setPadding(
                        paddingPixel,
                        paddingPixel,
                        paddingPixel,
                        paddingPixel
                    )

                }

            }
        }
    }

    private fun shareDoc(urlStringEx : String , subjectId : String) {

//        val url = urlStringEx?.removePrefix("https://bhanulearning.s3.ap-south-1.amazonaws.com/latestUpdate/")
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


    private fun deviceDownload(url : String , name : String){

        var nameFinal = name + ".pdf"

//        val pathDir = File(
//            Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_DOWNLOADS
//            ), "TenSquare"
//        )
//
//        if(!pathDir!!.exists()){
//            pathDir!!.mkdirs()
//        }

        try {

            val cfile = File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS) , nameFinal)

            if (cfile.exists()){
                cfile.delete()
            }

            val request = DownloadManager.Request(Uri.parse(url))
            request.setDescription("Downloding...")
            request.setTitle(nameFinal)

            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS , nameFinal)

            val manager = this@QuestionBankPaperActivity.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            manager.enqueue(request)

            val onComplete : BroadcastReceiver = object : BroadcastReceiver(){
                override fun onReceive(p0: Context?, p1: Intent?) {
                    unregisterReceiver(this)
                    showToast(getString(com.app.tensquare.R.string.download_successful))
                }

            }


            registerReceiver(
                onComplete,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
            )


        }catch (e : Exception){
            e.printStackTrace()
        }


    }


}