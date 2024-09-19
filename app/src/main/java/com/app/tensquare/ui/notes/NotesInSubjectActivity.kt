package com.app.tensquare.ui.notes

import android.R
import android.content.Intent
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.*
import com.app.tensquare.activity.RevisionVideoInSubjectActivity
import com.app.tensquare.adapter.NotesInSubjectAdapter
import com.app.tensquare.base.AppBaseActivity
import com.app.tensquare.databinding.ActivityNotesInSubjectBinding
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.ui.chapter.ChaptersInSubjectActivity
import com.app.tensquare.ui.home.HomeViewModel
import com.app.tensquare.ui.login.LoginActivity
import com.app.tensquare.ui.paper.DownloadViewModel
import com.app.tensquare.ui.pdf.PdfViewerActivity
import com.app.tensquare.ui.session.PracticeSessionInSubjectActivity
import com.app.tensquare.ui.subscription.EnrolmentPlanPricingNewActivity
import com.app.tensquare.utils.*
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.ResponseBody
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@AndroidEntryPoint
class NotesInSubjectActivity : AppBaseActivity() {
    private lateinit var binding: ActivityNotesInSubjectBinding
    private var notesInSubjectAdapter: NotesInSubjectAdapter? = null
//    private var notesList: List<Notes> = ArrayList<Notes>()
    private var notesList_sen = mutableListOf<Notes>()

    private val viewModel: NotesViewModel by viewModels()
    private val viewModel1: DownloadViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private var fileName: String? = null
    private var urlString: String? = null

    private var page = 0
    private var length: Int? = null
    private var noDataTxt:Boolean = true
    var request = HashMap<String, String>()

    companion object{
        var isShowComing:Boolean? = false
        var isSubscribed:Boolean? = false
        var selectSubCode:String = "2"
        var isListEmpty:Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotesInSubjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isSubscribed = prefs.isSubPlan()
        selectSubCode = prefs.getSelectSub().toString()

        init()
        setListeners()
        initObservers()
//        initSwiping()

        request = HashMap<String, String>()
        intent.getStringExtra("subject_id")?.let { request.put("id", it) }
        request.put("languageId", prefs.getSelectedLanguageId() ?: "")

        if (intent.getIntExtra("module", 0) == MODULE_NOTE_RESOURCE_WITH_CHAPTER)
            intent.getStringExtra("chapterId")?.let { request.put("chapterId", it) }

        request.put("pageNo" , (++page).toString())

        if (intent.hasExtra("inMain")){
            if (intent.getStringExtra("inMain").equals("inMain")){
                isShowComing = true
            }else{
                isShowComing = false
            }
        }else{
            isShowComing = false
        }

        Log.e("req -> " , request.toString())

        showProgressDialog()
        viewModel.getNotesList(request)

        //getDataFromServer()

        /*val itemTouchHelperCallback: ItemTouchHelper.SimpleCallback =
            RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.rvNotes)*/


    }

    private fun getNoteList() {

        request = HashMap<String, String>()
        intent.getStringExtra("subject_id")?.let { request.put("id", it) }

        if (intent.getIntExtra("module", 0) == MODULE_NOTE_RESOURCE_WITH_CHAPTER)
            intent.getStringExtra("chapterId")?.let { request.put("chapterId", it) }

        request.put("pageNo" , (++page).toString())

        showProgressDialog()
        viewModel.getNotesList(request)

    }


    override fun init() {
        binding.txtSubjectName.text = intent.getStringExtra("subject_name")
//        binding.txtSubjectModuleName.text = MODULE_NAME_NOTE_RESOURCE
        binding.txtSubjectModuleName.text = getString(com.app.tensquare.R.string.note_resources)

        this.setTileData(
            intent.extras!!,
            binding.txtSubjectModuleName,
            binding.imgSubject,
            binding.llSubjectTile
        )

        /* when {
                intent.getIntExtra("module", 0) == MODULE_NOTE_RESOURCE ->
                    MODULE_NAME_NOTE_RESOURCE
                intent.getIntExtra("module", 0) == MODULE_MODEL_PAPER ->
                    MODULE_NAME_MODEL_PAPER
                else -> ""
            }*/
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
                if (prefs.isSubPlan()) {
                    if (urlString != null && fileName != null) {
                        startActivity(
                            Intent(
                                this@NotesInSubjectActivity,
                                PdfViewerActivity::class.java
                            ).also {
                                it.putExtra("file", urlString)
                                it.putExtra("VIA", PDF_VIA_URL)
                            })
                    }
                } else {
                    Intent(this@NotesInSubjectActivity, EnrolmentPlanPricingNewActivity::class.java).also {
                        startActivity(it)
                    }
//                    showToastCenter(getString(com.app.tensquare.R.string.please_enrol))
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

        viewModel.notesListResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    if (response.data?.status == STATUS_SUCCESS) {

                        if (response.data.data != null) {
                            length = length ?: response.data.data?.length
                            Log.e("len -> ", length.toString())
//                            notesList_sen = response.data.data.list as MutableList<Notes>
                            notesList_sen.addAll(response.data.data.list)

                            if(response.data.data.list.isEmpty()){
                                isListEmpty = true
                            }else{
                                isListEmpty = false
                            }

                        }


//                        notesList = response.data.data.list as ArrayList<Notes>
//                        if (notesList.isNotEmpty()) {
                        if (notesList_sen.isNotEmpty() == true) {
//                        if (response.data.data.list.isNotEmpty() == true) {
                            //notesInSubjectAdapter.submitList(notesList)

                            binding.rvNotes.apply {
                                layoutManager =
                                    LinearLayoutManager(
                                        context,
                                        LinearLayoutManager.VERTICAL,
                                        false
                                    )
                                notesInSubjectAdapter =
                                    NotesInSubjectAdapter(response.data.data.comingSoon, notesList_sen) { flag: Int, notes: Notes ->
                                        // urlString = modelPaper.document
//                                        if (!prefs.isGuestUser()) {
//                                        if (prefs.isEnrolled()) {
                                        if (prefs.isSubPlan()) {
                                            if (flag == 1) {
                                                showToast(getString(com.app.tensquare.R.string.downloading))
//                                                setupUrlAndFileName(/*modelPaper.document*/)  //JACK h

                                            /*    if (notes.document.isNotEmpty()) {
                                                    setupUrlAndFileName(notes.document[0])    //JACK e
                                                    viewModel1.downloadPdf(notes.document[0])
                                                }   */
                                                if (notes.document.isNotEmpty() && notes.fileName[0].isNotEmpty()) {
                                                    setupUrlAndFileName(notes.document[0] , notes.fileName[0])
                                                }

                                            } else if (flag == 2) {

                                                try {
                                                    if (notes.document[0].isNotEmpty()
                                                        && notes.subjectId.isNotEmpty() &&
                                                        notes.subjectId != null){
                                                        showToast(getString(com.app.tensquare.R.string.sharing))
                                                        shareDoc(notes.document[0] , notes.subjectId)
                                                    }

                                                }catch (e : Exception){
                                                    e.printStackTrace()
                                                }

                                            } else if (flag == 3) {

                                                if (notes.document != null && notes.fileName != null && notes.document.isNotEmpty()) {
                                                    startActivity(
                                                        Intent(
                                                            this@NotesInSubjectActivity,
                                                            PdfViewerActivity::class.java
                                                        ).also {
                                                            it.putExtra("file", notes.document[0])
                                                            it.putExtra("VIA", PDF_VIA_URL)
                                                        })
                                                }

                                            }
                                        } else {
//                                            showToastCenter(getString(com.app.tensquare.R.string.please_enrol))
                                            if (notes.lock == 0){
                                                Intent(this@NotesInSubjectActivity, EnrolmentPlanPricingNewActivity::class.java).also {
                                                    startActivity(it)
                                                }
//                                                showToastCenter(getString(com.app.tensquare.R.string.please_enrol))
                                            }else{

                                                if (flag == 1) {
                                                    showToast(getString(com.app.tensquare.R.string.downloading))
//                                                setupUrlAndFileName(/*modelPaper.document*/)  //JACK h

                                                    /*    if (notes.document.isNotEmpty()) {
                                                            setupUrlAndFileName(notes.document[0])    //JACK e
                                                            viewModel1.downloadPdf(notes.document[0])
                                                        }   */
                                                    if (notes.document.isNotEmpty() && notes.fileName[0].isNotEmpty()) {
                                                        setupUrlAndFileName(notes.document[0] , notes.fileName[0])
                                                    }

                                                } else if (flag == 2) {

                                                    try {
                                                        if (notes.document[0].isNotEmpty()
                                                            && notes.subjectId.isNotEmpty() &&
                                                            notes.subjectId != null){
                                                            showToast(getString(com.app.tensquare.R.string.sharing))
                                                            shareDoc(notes.document[0] , notes.subjectId)
                                                        }

                                                    }catch (e : Exception){
                                                        e.printStackTrace()
                                                    }

                                                } else if (flag == 3) {

                                                    if (notes.document != null && notes.fileName != null && notes.document.isNotEmpty()) {
                                                        startActivity(
                                                            Intent(
                                                                this@NotesInSubjectActivity,
                                                                PdfViewerActivity::class.java
                                                            ).also {
                                                                it.putExtra("file", notes.document[0])
                                                                it.putExtra("VIA", PDF_VIA_URL)
                                                            })
                                                    }

                                                }

                                            }


                                        }
                                    }
                                adapter = notesInSubjectAdapter
                            }

                            noDataTxt = false
                            binding.txtNoData.visibility = View.GONE


                        } else {

                            if (noDataTxt){
                                binding.txtNoData.visibility = View.VISIBLE
                                binding.txtNoData.text = response.data?.data?.comingSoon?.message ?: ""
                            }
//                            notesInSubjectAdapter?.notifyDataSetChanged()
//                            showToast(response.data.message)
                        }
                    } else {
//                        showToast(response.data?.message)

                        if (response.data?.message == OTHER_DEVISE_LOGIN) {
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
                        viewModel.getNotesList(request)
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

                    getNoteList()

                    // your pagination code
                }
            }catch (e: Exception){
                e.printStackTrace()
            }

        }


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
                    binding.rvNotes.post(Runnable {
                        notesInSubjectAdapter?.showMenu(
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
                            if (notesList[viewHolder.adapterPosition].document.isNotEmpty())
                                urlString = notesList[viewHolder.adapterPosition].document[0]
//                            setupUrlAndFileName(urlString.toString())   // JACK
                        }
                    } else {
                        background.setBounds(0, 0, 0, 0)
                    }
                    background.draw(c)
                }
            }

        val itemTouchHelper = ItemTouchHelper(touchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvNotes)

        binding.rvNotes.setOnScrollChangeListener(View.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            binding.rvNotes.post(
                Runnable { notesInSubjectAdapter?.closeMenu() })
        })
    }
*/

    override fun onBackPressed() {
        if (notesInSubjectAdapter != null && notesInSubjectAdapter?.isMenuShown() == true) {
            notesInSubjectAdapter?.closeMenu()
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
        } catch (e: Exception) {
            false
        }
    }


    /*private fun getDataFromServer() {
        for (i in 0..5) {
            val subject = PracticeSession()
            sessionList.add(subject)
        }
        notesInSubjectAdapter.submitList(sessionList)
    }*/

//    private fun setupUrlAndFileName(/*fileLink: String*/) {   //JACK
    private fun setupUrlAndFileName(fileLink: String , fileNamePath: String) {
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



//        var uniqeNum : String = "0000";
//        try {
//            val randomPin = (Math.random() * 9000).toInt() + 1000

//            uniqeNum = randomPin.toString()

//            Log.e("TimeDate ==> " , randomPin.toString() + "   "  + randomPin.toString().length.toString())

//        }catch (e : Exception){
//            e.printStackTrace();
//        }

        var addLastPath : String = fileNamePath + ".pdf"

        fileName = "NOTES_RESOURCES_${addLastPath}"
//        fileName = "NOTES_RESOURCES_${fileDirPath?.substring(fileDirPath.lastIndexOf('/') + 1)}"

        viewModel1.downloadPdf(fileLink)
    }

    private fun shareDoc(urlStringEx : String , subjectId : String) {

//        val url = urlStringEx?.removePrefix("https://bhanulearning.s3.ap-south-1.amazonaws.com/pdf/")
        val url = urlStringEx?.removePrefix("https://bhanulearning.s3.ap-south-1.amazonaws.com/")

        val sendUrl = subjectId +"/"+url

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