package com.app.tensquare.ui.paper

import android.R
import android.content.Intent
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.app.tensquare.adapter.ModelPaperInSubjectAdapter
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
import okhttp3.ResponseBody
import java.io.*
import java.net.MalformedURLException
import java.net.URL


@AndroidEntryPoint
class ModelPaperInSubjectActivity : AppBaseActivity() {
    private lateinit var binding: ActivityModelPaperInSubjectBinding
    private var modelPaperInSubjectAdapter: ModelPaperInSubjectAdapter? = null
    private var paperList = mutableListOf<ModelPaper>()
    //private lateinit var previousYearPaperList: List<PreviousYearPaper>

    private val viewModel: PaperViewModel by viewModels()
    private val viewModel1: DownloadViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()


    ////
    //private var filePath: String = "https://www.orimi.com/pdf-test.pdf"
    private var fileName: String? = null

    //private lateinit var url: URL
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


        /*try {
            url = URL(filePath)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }

        fileName =
            url.path //url.path will give images/default/sample.pdf so to get file name we have to trim this

        fileName = fileName.substring(fileName.lastIndexOf('/') + 1)*/

    }

    override fun init() {

        binding.txtNoData.text = getString(com.app.tensquare.R.string.releasing_in_first_week_of_january)

        module = intent.getIntExtra("module", 0)
        subjectId = intent.getStringExtra("subject_id")

        binding.txtSubjectModuleName.text = when (module) {
            MODULE_MODEL_PAPER ->
//                MODULE_NAME_MODEL_PAPER
                getString(com.app.tensquare.R.string.model_papers)
            MODULE_PREVIOUS_YEAR_PAPER ->
//                MODULE_NAME_PREVIOUS_YEAR_PAPER
                getString(com.app.tensquare.R.string.previous_year_paper)
            else -> /*MODULE_NAME_MODEL_PAPER*/ getString(com.app.tensquare.R.string.model_papers)
        }

        this.setTileData(
            intent.extras!!,
            binding.txtSubjectModuleName,
            binding.imgSubject,
            binding.llSubjectTile
        )

        if (module == MODULE_MODEL_PAPER) {
            binding.txtSubjectName.text = intent.getStringExtra("subject_name")
            getSubjectModelPaper()
        } else {
            binding.txtSubjectName.visibility = View.GONE
            getGeneralModelPaper()
            //getDataFromServer()
        }

    }

    private fun getSubjectModelPaper() {
        showProgressDialog()
        /*when {
        intent.getIntExtra("module", 0) == MODULE_MODEL_PAPER ->*/
        subjectId?.let { viewModel.getModelPaperList(it, ++page , prefs.getSelectedLanguageId().toString()) }
        /*intent.getIntExtra("module", 0) == MODULE_PREVIOUS_YEAR_PAPER ->
        intent.getStringExtra("subject_id")
            ?.let { viewModel.getPreviousYearPaperList(it, 1) }
    else -> ""
    }*/
    }

    private fun getGeneralModelPaper() {
        showProgressDialog()
        viewModel.getHomeModelPaperList(++page ,  prefs.getSelectedLanguageId().toString())
    }

    override fun setListsAndAdapters() {

    }

    override fun setListeners() {
        binding.imgBack.setOnClickListener { finish() }

        binding.txtProceed.setOnClickListener {
            try {
//                if (!prefs.isGuestUser()) {
//                if (prefs.isEnrolled()) {
                if (module == MODULE_MODEL_PAPER) {
//                    if (!prefs.isGuestUser()) {
                    if (prefs.isSubPlan()) {
                        if (urlString != null && fileName != null) {
                            startActivity(
                                Intent(
                                    this@ModelPaperInSubjectActivity,
                                    PdfViewerActivity::class.java
                                ).also {
                                    it.putExtra("file", urlString)
                                    it.putExtra("VIA", PDF_VIA_URL)
                                })
                        }
                    } else {
                        Intent(this@ModelPaperInSubjectActivity, EnrolmentPlanPricingNewActivity::class.java).also {
                            startActivity(it)
                        }
                    }

                }else{

                    if (urlString != null && fileName != null) {
                        startActivity(
                            Intent(
                                this@ModelPaperInSubjectActivity,
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

        viewModel.modelPaperListResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    val data = response.data
                    if (data != null) {
                        length = length ?: data?.length
                        println("length = $length")
                        paperList = data.list as MutableList<ModelPaper>
                    }
//                    showToast("in")
                    if (paperList.isNotEmpty()) {
                        binding.rvModelPaper.apply {
                            binding.txtComingSoon.visibility = View.VISIBLE
                            binding.txtComingSoon.text = data?.comingSoon?.message ?: ""
                            modelPaperInSubjectAdapter =
                                ModelPaperInSubjectAdapter(paperList) { flag: Int, modelPaper: Any ->
                                    modelPaper as ModelPaper
                                    // urlString = modelPaper.document
//                                    if (!prefs.isGuestUser()) {
//                                    if (prefs.isEnrolled()) {
//                                    if (prefs.isSubPlan()) {
                                    if (modelPaper.enrollmentPlanStatus) {
//                                        showToast(getString(com.app.tensquare.R.string.downloading))      //-----
//                                        setupUrlAndFileName(/*modelPaper.document*/)  //-----
//                                        viewModel1.downloadPdf(modelPaper.document)   //-----

                                        if (flag == 1) {

                                            try {

                                                if (modelPaper.document != null && modelPaper.fileName != null) {
                                                    startActivity(
                                                        Intent(
                                                            this@ModelPaperInSubjectActivity,
                                                            PdfViewerActivity::class.java
                                                        ).also {
                                                            it.putExtra("file", modelPaper.document)
                                                            it.putExtra("VIA", PDF_VIA_URL)
                                                        })
                                                }

                                            }catch (e : Exception){
                                                e.printStackTrace()
                                            }

                                        }else if (flag == 2) {

                                            try {

                                                if (modelPaper.document.isNotEmpty() && modelPaper.subjectId.isNotEmpty() &&
                                                    modelPaper.subjectId != null){
                                                    showToast(getString(com.app.tensquare.R.string.sharing))
                                                    shareDoc(modelPaper.document , modelPaper.subjectId)
                                                }

                                            }catch (e : Exception){
                                                e.printStackTrace()
                                            }

                                        }else if (flag == 3) {

//                                            if (modelPaper.document.isNotEmpty() &&
//                                                modelPaper.title.isNotEmpty() && modelPaper.fileName.isNotEmpty()){
//                                                showToast(getString(com.app.tensquare.R.string.downloading))
//                                                setupUrlAndFileName(modelPaper.document , modelPaper.fileName , modelPaper.title)
//                                            }

                                            if (modelPaper.document.isNotEmpty() && modelPaper.fileName.isNotEmpty()){
                                                showToast(getString(com.app.tensquare.R.string.downloading))
                                                setupUrlAndFileName(modelPaper.document , modelPaper.fileName , modelPaper.title)
                                            }

                                        }

                                    } else {
                                        Intent(this@ModelPaperInSubjectActivity, EnrolmentPlanPricingNewActivity::class.java).also {
                                            startActivity(it)
                                        }
                                    }
                                }
                            adapter = modelPaperInSubjectAdapter
                        }
                        //modelPaperInSubjectAdapter.notifyDataSetChanged()
                        //modelPaperInSubjectAdapter.submitList(paperList as List<Any>?)
                        noDataTxt = false
                        binding.txtNoData.visibility = View.GONE
                    } else {
//                        if (noDataTxt){
                        binding.txtNoData.visibility = View.VISIBLE
                        binding.txtNoData.text = response.data?.comingSoon?.message ?: ""
                            binding.txtComingSoon.visibility = View.GONE
//                        }
//                        showToast(getString(com.app.elearning.R.string.no_data_found))
                    }
                }
                is NetworkResult.Error -> {
//                    showToast(response.message)
                    binding.txtNoData.visibility = View.VISIBLE
                    binding.txtNoData.text = response.data?.comingSoon?.message ?: ""
                    binding.txtComingSoon.visibility = View.GONE
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

        viewModel.homeModelPaperListResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    val data = response.data
                    if (data != null) {
                        paperList = data.list as MutableList<ModelPaper>
                        length = length ?: data?.length
                        println("length = $length")
                    }
//                    showToast("out")
                    if (paperList.isNotEmpty()) {
                        binding.txtComingSoon.visibility = View.VISIBLE
                        binding.txtComingSoon.text = data?.comingSoon?.message ?: ""
                        binding.rvModelPaper.apply {
                            modelPaperInSubjectAdapter =
                                ModelPaperInSubjectAdapter(paperList) { flag: Int, modelPaper: Any ->
                                    modelPaper as ModelPaper

                                    if (modelPaper.enrollmentPlanStatus){
//                                    if (!prefs.isGuestUser()) {
//                                    if (prefs.isEnrolled()) {
//                                        showToast(getString(com.app.tensquare.R.string.downloading))  //--------
//                                        setupUrlAndFileName(/*modelPaper.document*/)  //--------

                                        if (flag == 1) {

                                            try {

                                                if (modelPaper.document != null && modelPaper.fileName != null) {
                                                    startActivity(
                                                        Intent(
                                                            this@ModelPaperInSubjectActivity,
                                                            PdfViewerActivity::class.java
                                                        ).also {
                                                            it.putExtra("file", modelPaper.document)
                                                            it.putExtra("VIA", PDF_VIA_URL)
                                                        })
                                                }

                                            }catch (e : Exception){
                                                e.printStackTrace()
                                            }

                                        }else if (flag == 2) {

                                            try {

                                                if (modelPaper.document.isNotEmpty() && modelPaper.subjectId.isNotEmpty() &&
                                                    modelPaper.subjectId != null){
                                                    showToast(getString(com.app.tensquare.R.string.sharing))
                                                    shareDoc(modelPaper.document , modelPaper.subjectId)
                                                }

                                            }catch (e : Exception){
                                                e.printStackTrace()
                                            }

                                        }else if (flag == 3) {

//                                            if (modelPaper.document.isNotEmpty() &&
//                                                modelPaper.title.isNotEmpty() && modelPaper.fileName.isNotEmpty()){
//                                                showToast(getString(com.app.tensquare.R.string.downloading))
//                                                setupUrlAndFileName(modelPaper.document , modelPaper.fileName , modelPaper.title)
//                                            }

                                            if (modelPaper.document.isNotEmpty() && modelPaper.fileName.isNotEmpty()){
                                                showToast(getString(com.app.tensquare.R.string.downloading))
                                                setupUrlAndFileName(modelPaper.document , modelPaper.fileName , modelPaper.title)
                                            }

                                        }


                                    } else
                                        showToast(getString(com.app.tensquare.R.string.please_enrol))
                                    //viewModel1.downloadPdf((modelPaper as ModelPaper).document)

                                    /*val request =
                                        DownloadManager.Request(Uri.parse("$url"))
                                    request.setTitle(fileName)
                                    request.setMimeType("application/pdf")
                                    request.allowScanningByMediaScanner()
                                    request.setAllowedOverMetered(true)
                                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                  *//*  request.setDestinationInExternalPublicDir(
                                            Environment.DIRECTORY_DOWNLOADS,
                                            fileName
                                        )*//*
                                        val dm =
                                            getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                                        dm.enqueue(request)*/

                                }
                            adapter = modelPaperInSubjectAdapter
                        }
                        noDataTxt = false
                        binding.txtNoData.visibility = View.GONE
                    } else {
                        if (noDataTxt){
                            binding.txtComingSoon.visibility = View.GONE
                            binding.txtNoData.visibility = View.VISIBLE
                            binding.txtNoData.text = response.data?.comingSoon?.message ?: ""
                        }
//                        showToast(getString(com.app.elearning.R.string.no_data_found))
                    }
                }
                is NetworkResult.Error -> {
//                    showToast(response.message)
                }
                is NetworkResult.Loading -> {
                    // show a progress bar
                }
            }
        }

        /*viewModel.previousYearPaperResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    if (response.data?.status == "Success") {
                        previousYearPaperList = response.data.data
                        modelPaperInSubjectAdapter.submitList(previousYearPaperList)
                    } else
                        showToast(response.data?.message)
                }
                is NetworkResult.Error -> {
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                }
                is NetworkResult.Loading -> {
                    // show a progress bar
                }
            }
        }*/

        binding.nsv.viewTreeObserver.addOnScrollChangedListener {
            val view: View = binding.nsv.getChildAt(binding.nsv.childCount - 1) as View
            val diff: Int = view.bottom - (binding.nsv.height + binding.nsv.scrollY)
            println("lengthViewTreeObserver = $page")
            if (diff == 0 && page <= length!!) {
                if (module == MODULE_MODEL_PAPER) {
                    getSubjectModelPaper()
                } else {
                    getGeneralModelPaper()
                }
            }
        }

    }

    /*private fun getDataFromServer() {
        for (i in 0..5) {
            val subject = ModelPaper(
                _id = Random(1).nextInt().toString(),
                document = "www.document.com",
                title = "English",
                subTitle = "lorem ipsum"
            )
            paperList.add(subject)
        }
        modelPaperInSubjectAdapter?.notifyDataSetChanged()
        //modelPaperInSubjectAdapter.submitList(paperList as List<Any>?)
    }*/

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
                        modelPaperInSubjectAdapter?.showMenu(
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
                            urlString = paperList[viewHolder.adapterPosition].document
//                            setupUrlAndFileName()
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
                Runnable { modelPaperInSubjectAdapter?.closeMenu() })
        })
    }

    override fun onBackPressed() {
        if (modelPaperInSubjectAdapter != null && modelPaperInSubjectAdapter?.isMenuShown() == true) {
            modelPaperInSubjectAdapter?.closeMenu()
            binding.txtProceed.visibility = View.GONE
            urlString = null
            fileName = null
        } else {
            super.onBackPressed()
        }
    }

    private fun writeResponseBodyToDisk(body: ResponseBody): Boolean {
        return try {
            /*val newDir = File("${getExternalFilesDir(null)}${File.separator}model")
            if (!newDir.exists()) {
                newDir.mkdir()
            }
            val futureStudioIconFile: File =
                File("${newDir.path}${File.separator}$fileName")


            val fos =
                FileOutputStream(futureStudioIconFile) //Use the stream as usual to write into the file.

            fos.write(body.bytes())
            fos.flush()
            fos.close()*/

            /*val myDir = context.getDir("model", MODE_PRIVATE) //Creating an internal dir;
            if (!myDir.exists()) {
                myDir.mkdir()
            }*/
            val fos = openFileOutput(fileName, MODE_PRIVATE)
            fos.write(body.bytes())
            fos.close()

            /*val futureStudioIconFile: File =
                File("${myDir.path}${File.separator}$fileName")
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                val fileReader = ByteArray(4096)
                val fileSize = body.contentLength()
                var fileSizeDownloaded: Long = 0
                inputStream = body.byteStream()
                outputStream = FileOutputStream(futureStudioIconFile)
                while (true) {
                    val read: Int = inputStream.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream.write(fileReader, 0, read)
                    fileSizeDownloaded += read.toLong()
                    Log.d("TAG", "file download: $fileSizeDownloaded of $fileSize")
                }
                outputStream.flush()
                true
            } catch (e: IOException) {
                false
            } finally {
                if (inputStream != null) {
                    inputStream.close()
                }
                if (outputStream != null) {
                    outputStream.close()
                }
            }*/
            true
        } catch (e: IOException) {
            false
        }


/*
        return try {
            // todo change the file location/name according to your needs

           */
/* val mydir = context.getDir(fileName, Context.MODE_PRIVATE)
            if (!mydir.exists())
                mydir.mkdirs()
            Log.e("My Directory", mydir.absolutePath)

            val futureStudioIconFile = File(mydir, "$fileName")*//*


            val futureStudioIconFile: File =
                File("${getExternalFilesDir(null)}${File.separator}$fileName")
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                val fileReader = ByteArray(4096)
                val fileSize = body.contentLength()
                var fileSizeDownloaded: Long = 0
                inputStream = body.byteStream()
                outputStream = FileOutputStream(futureStudioIconFile)
                while (true) {
                    val read: Int = inputStream.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream.write(fileReader, 0, read)
                    fileSizeDownloaded += read.toLong()
                    Log.d("TAG", "file download: $fileSizeDownloaded of $fileSize")
                }
                outputStream.flush()
                true
            } catch (e: IOException) {
                false
            } finally {
                if (inputStream != null) {
                    inputStream.close()
                }
                if (outputStream != null) {
                    outputStream.close()
                }
            }
        } catch (e: IOException) {
            false
        }
*/
    }

//    private fun setupUrlAndFileName(/*fileLink: String*/) {
    private fun  setupUrlAndFileName(fileLink: String , fileNamePath: String , subfiletitle: String) {
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
        fileName = "MODEL_PAPER_${fileDirPath?.substring(fileDirPath.lastIndexOf('/') + 1)}"
    */

//    if (subfiletitle.isNotEmpty()){
//        var addLastPath : String = fileNamePath + "_" + subfiletitle  + ".pdf"
//        fileName = "MODEL_PAPER_${addLastPath}"
//    }else{
        var addLastPath : String = fileNamePath + ".pdf"
        fileName = "MODEL_PAPER_${addLastPath}"
//    }

//        var addLastPath : String = fileNamePath + "_" + subfiletitle  + ".pdf"
//        fileName = "MODEL_PAPER_${addLastPath}"

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