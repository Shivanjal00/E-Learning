package com.app.tensquare.ui.home

import LocaleHelper1
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.app.tensquare.R
import com.app.tensquare.activity.SplashActivity
import com.app.tensquare.activity.SubjectDetailActivity
import com.app.tensquare.adapter.HomeQueBankAdapter
import com.app.tensquare.adapter.HomeSubjectAdapter
import com.app.tensquare.base.AppBaseFragment
import com.app.tensquare.constants.NetworkConstants
import com.app.tensquare.databinding.FragmentHomeBinding
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.ui.initialsetup.InitialViewModel
import com.app.tensquare.ui.initialsetup.Language
import com.app.tensquare.ui.latestupdate.LatestUpdatesActivity
import com.app.tensquare.ui.login.LoginActivity
import com.app.tensquare.ui.paper.ModelPaperInSubjectActivity
import com.app.tensquare.ui.paper.PreviousPaperInSubjectActivity
import com.app.tensquare.ui.profile.ProfileDetail
import com.app.tensquare.ui.questionbankpaper.QuestionBankPaperActivity
import com.app.tensquare.utils.*
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class HomeFragment : AppBaseFragment(), LanguageChangeListener {
    private val viewModel: HomeViewModel by viewModels()
    private val viewModelLang: InitialViewModel by viewModels()
    private lateinit var langList: List<Language>

    private val viewModelupdateLng: UpdateLanguageViewModel by viewModels()

    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeSubjectAdapter: HomeSubjectAdapter
    private var subjectList = mutableListOf<Subject>()

    private lateinit var homeQueBankAdapter: HomeQueBankAdapter

    private val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    private val outputFormat = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())

    private var engCode: String = ""
    private var engName: String = ""
    private var hindiCode: String = ""
    private var hindiName: String = ""
    private var updatedLangName: String = ""

    private var deviceId: String? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AppUtills.languageListener = this

        init(view)
        setListsAndAdapters()
        setListeners()
        initObservers()
        Log.e("REFRESH TOKEN", prefs.getRefreshToken().toString())

//        isOnline(object : CallBackForRetry {
//            override fun onRetry() {
//                showMessage("ready")
//            }
//        })
        //getDataFromServer()
    }

    override fun init(view: View) {
        if (prefs.isGuestUser()) {
            binding.txtProfileName.text = "Hi, Guest"
        } else {
            val userData = JSONObject(prefs.getUserData())
            binding.txtProfileName.text = "Hi, ${userData.getString("name")}"
        }

        try {
            deviceId = AppUtills.getDeviceId(requireContext())
            Log.e("DeviceId  ==> ", deviceId.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            requestQueue = Volley.newRequestQueue(context) // JACK
            requestQueue_2 = Volley.newRequestQueue(context) // JACK
        } catch (e: Exception) {
            e.printStackTrace()
        }

        binding.tvNextPaper.isSelected = true
        getHomeData()
    }

    private fun getHomeData() {
        showProgressDialog()
        val request = HomeDataRequest(
            languageId = prefs.getSelectedLanguageId().toString(),
            classId = prefs.getSelectedClassId().toString()
        )
        prefs.getUserToken()?.let { viewModel.getHomeData(it, request) }
    }

    override fun onResume() {
        super.onResume()
//        showMessage("resume")
//        getHomeData()
    }

    override fun setListsAndAdapters() {
        binding.rvSubject.apply {
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
            homeSubjectAdapter =
                HomeSubjectAdapter(requireActivity()) { subject: Subject, position: Int, flag: Int ->
                    prefs.setIsSubPlan(subject.enrollmentPlanStatus)

                    if (subject.name.equals("Maths") || subject.name.equals("गणित")) {
                        prefs.setSelectSub("0")
                    } else if (subject.name.equals("Science") || subject.name.equals("विज्ञान")) {
                        prefs.setSelectSub("1")
                    } else {
                        prefs.setSelectSub("2")
                    }

                    Intent(requireContext(), SubjectDetailActivity::class.java).also {
                        it.putExtra("subject_name", subject.name)
                        it.putExtra("subject_id", subject._id)
                        it.putExtra("revisionVideoStatus", subject.revisionVideoStatus)
                        it.putExtra(SUBJECT, subject.value)

                        startActivity(it)
                    }

                }
            adapter = homeSubjectAdapter
        }

        binding.rvQueBank.apply {
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
            homeQueBankAdapter =
                HomeQueBankAdapter(requireActivity()) { subject: Subject, position: Int, flag: Int ->
                    Intent(requireContext(), QuestionBankPaperActivity::class.java).also {
                        it.putExtra("subject_name", subject.name)
                        it.putExtra("subject_id", subject._id)
                        it.putExtra(SUBJECT, subject.value)

                        startActivity(it)
                    }
                }
            adapter = homeQueBankAdapter
        }

    }

    override fun initObservers() {
        viewModel.homeDataResponse.observe(viewLifecycleOwner) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    dismissProgressDialog()
                    if (response.data?.subjects != null) {
                        val subjects: ArrayList<Subject> = ArrayList();
                        val className = prefs.getSelectedClassName()
                        Log.e("PrintclassName", "className = $className")

                        for (subject in response.data?.subjects) {
                            if (className.toString() != "12th") {
                                if (subject.value == SUBJECT_MATHS || subject.value == SUBJECT_SCIENCE) {
                                    subjects.add(subject)
                                }
                            } else {
                                if (subject.value == SUBJECT_MATHS || subject.value == SUBJECT_PHYSICS || subject.value == SUBJECT_CHEMISTRY || subject.value == SUBJECT_BIOLOGY) {
                                    subjects.add(subject)
                                }
                            }
                        }
                        homeSubjectAdapter.submitList(subjects)

                        AppUtills.notificationCountListener?.notificationCountListener(response.data.notificationCount)
                        /*try {

                            if (response.data.notificationCount > 0) {
                                HomeActivity.ivNotifyAlert!!.visibility = View.VISIBLE
                            }else{
                                HomeActivity.ivNotifyAlert!!.visibility = View.GONE
                            }

                        }catch (e : Exception){
                            e.printStackTrace()
                        }*/

                    }
                    val questionBank: ArrayList<Subject> = ArrayList();

                    response.data?.subjects?.forEach {
                        Log.e("questionBankStatus", "questionBankStatus = ${it.questionBankStatus}")
                        if (it.questionBankStatus != 0) {
                            questionBank.add(it)
                        }
                    }
                    if (questionBank.isNotEmpty()) {
                        binding.tvTitleQuestionBank.visibility = View.VISIBLE
                        binding.rvQueBank.visibility = View.VISIBLE
                    } else {
                        binding.tvTitleQuestionBank.visibility = View.GONE
                        binding.rvQueBank.visibility = View.GONE
                    }

                    homeQueBankAdapter.submitList(questionBank)
                    viewModelLang.getLanguageList()
//                    getEnrolstatus()

                    /* else
                         requireActivity().showToast(response.message)*/
//                    prefs.setUserToken(prefs.getRefreshToken())
//                    viewModel.getRefreshToken(prefs.getUserToken().toString())
//                    generateNewToken()

                }

                is NetworkResult.Error -> {
                    dismissProgressDialog()
//                    showMessage(response.message)
                    when (response.message) {
                        ACCESS_TOKEN_EXPIRED -> {
//                            prefs.setUserToken(prefs.getRefreshToken())
//                            viewModel.getRefreshToken(prefs.getRefreshToken().toString())
                            generateNewToken()
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
                            val intent = Intent(requireContext(), LoginActivity::class.java)
                            intent.putExtra("Expired", "Expired")
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            val broadcast = Intent(LOG_OUT)
                            requireActivity().sendBroadcast(broadcast)
                            startActivity(intent)
                            requireActivity().finish()
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

        viewModelLang.languageListResponse.observe(viewLifecycleOwner) { response ->

            when (response) {
                is NetworkResult.Success -> {
                    langList = response.data!!
                    dismissProgressDialog()
                    try {
                        for (item in langList) {
                            if (item.name.equals("English") || item.name.equals("अंग्रेज़ी")) {
                                engCode = item._id
                                engName = item.name
                            } else if (item.name.equals("Hindi") || item.name.equals("हिन्दी")) {
                                hindiCode = item._id
                                hindiName = item.name
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

                is NetworkResult.Error -> {
                    if (response.message != OTHER_DEVISE_LOGIN) {
                        val intent = Intent(requireContext(), LoginActivity::class.java)
                        intent.putExtra("Expired", "Expired")
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        val broadcast = Intent(LOG_OUT)
                        requireActivity().sendBroadcast(broadcast)
                        startActivity(intent)
                        requireActivity()
                    }
                }

                is NetworkResult.Loading -> {
                    // show a progress bar
                }
            }
        }

        viewModelupdateLng.updateLanguage.observe(viewLifecycleOwner) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
//                    showMessage(response.data?.message)

                    if (response.data?.status == STATUS_SUCCESS) {

                        val data = response.data.data

                        if (data.language_name == "Hindi" || data.language_name == "हिन्दी") {
                            LocaleHelper1.setLocale(requireContext(), "hi")
                            prefs.setUserLanguage("hi")
                            prefs.setSelectedLanguagenName(data.language_name)
                            prefs.setSelectedLanguageId(data.languageId)
                            prefs.setSelectedClassId(data.classId)
                            prefs.setSelectedClassName(data.className)
                        } else if (data.language_name == "English" || data.language_name == "अंग्रेज़ी") {
                            LocaleHelper1.setLocale(requireContext(), "en")
                            prefs.setUserLanguage("en")
                            prefs.setSelectedLanguagenName(data.language_name)
                            prefs.setSelectedLanguageId(data.languageId)
                            prefs.setSelectedClassId(data.classId)
                            prefs.setSelectedClassName(data.className)
                        }
//                        prefs.setSelectedClassName(getString(R.string.tenth))

                        SplashActivity.isLocaleChange = true
                        Intent(requireActivity(), SplashActivity::class.java).also {
                            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(it)
                        }

                    } else {

                        if (response.data?.message == OTHER_DEVISE_LOGIN) {

                            val intent = Intent(requireActivity(), LoginActivity::class.java)
                            intent.putExtra("Expired", "Expired")
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                            val broadcast = Intent(LOG_OUT)
//                            requireActivity().sendBroadcast(broadcast)
                            startActivity(intent)
                            requireActivity().finish()

                        }

                    }

                }

                is NetworkResult.Error -> {
                    dismissProgressDialog()
                    showMessage(response.message)
                }

                is NetworkResult.Loading -> {
                    // show a progress bar
                }
            }
        }


        viewModel.refreshTokenResponse.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Loading -> {}
                is NetworkResult.Success -> {
                    dismissProgressDialog()
                    it.data?.let { data ->

                        Log.e("token_Home = >", data.accessToken.toString())
                        prefs.setUserToken(data.accessToken)
                        prefs.setRefreshToken(data.refreshToken)
                        getHomeData()
                    }
                }

                is NetworkResult.Error -> {
                    dismissProgressDialog()
                    if (it.message == REFRESH_TOKEN_EXPIRED) {
//                            requireActivity().showToast("2")
                        Intent(requireActivity(), LoginActivity::class.java).also {
                            startActivity(it)
                        }
                        requireActivity().finish()
                    } else if (it.message == OTHER_DEVISE_LOGIN) {

                        val intent = Intent(requireActivity(), LoginActivity::class.java)
                        intent.putExtra("Expired", "Expired")
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        requireActivity().finish()

                    }//                            requireActivity().showToast("3")

                }
            }
        }

    }

    private var requestQueue_2: RequestQueue? = null
    private fun getEnrolstatus() {

        val url = NetworkConstants.BASE_URL + "api/user/getProfileDetails"
        Log.e("API = >", url.toString())

        val strReq: StringRequest = object : StringRequest(
            Request.Method.GET, url,
            Response.Listener { response ->

                // Handle Server response here
                try {
                    val responseObj = JSONObject(response)
//                    val isSuccess = responseObj.getBoolean("isSuccess")
                    Log.e("PROFILE DETAILS = >", response.toString())
                    if (responseObj.getString("status") == STATUS_SUCCESS) {

                        val objData = responseObj.getJSONObject("data")


                        prefs.setIsEnrolled(objData.getBoolean("enrollmentPlanStatus"))
//                        showMessage(objData.getString("enrollmentPlanStatus").toString())
//                        showMessage(prefs.isEnrolled().toString())

                    } else {

                    }


                } catch (e: Exception) { // caught while parsing the response
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { volleyError -> // error occurred
            }) {

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers: MutableMap<String, String> = HashMap()
                headers.put("token", prefs.getUserToken().toString())
                Log.e("get Token = >", headers.toString())
                return headers
            }
        }
        // Adding request to request queue
        requestQueue_2?.add(strReq)


    }

    private var requestQueue: RequestQueue? = null
    private var profileDetail: ProfileDetail? = null
    private fun generateNewToken() {
        viewModel.getRefreshToken(prefs.getRefreshToken().toString())
    }


    override fun setListeners() {

        binding.imgSearch.setOnClickListener {
            Intent(requireActivity(), SearchActivity::class.java).also {
                /*it.putExtra("subject_name", "")
                it.putExtra("module", MODULE_PREVIOUS_YEAR_PAPER_HOME)
                it.putExtra(SUBJECT, PREVIOUS_YEAR_PAPER)*/
                startActivity(it)
            }
        }

        val drawerLayout = requireActivity().findViewById<DrawerLayout>(R.id.drawerlayout)
        drawerLayout.openDrawer(Gravity.LEFT)

        binding.llPreviousYearPaper.setOnClickListener {
            Intent(requireActivity(), PreviousPaperInSubjectActivity::class.java).also {
                it.putExtra("subject_name", "")
                it.putExtra("module", MODULE_PREVIOUS_YEAR_PAPER_HOME)
                it.putExtra(SUBJECT, PREVIOUS_YEAR_PAPER)
                startActivity(it)
            }
        }
        binding.llLatestUpdates.setOnClickListener {
            Intent(requireActivity(), LatestUpdatesActivity::class.java).also {
                it.putExtra("subject_name", "Subject")
                it.putExtra("module", MODULE_LATEST_UPDATE)
                startActivity(it)
            }
        }
        binding.llModelPapers.setOnClickListener {
            Intent(requireActivity(), ModelPaperInSubjectActivity::class.java).also {
                it.putExtra("subject_name", "")
                it.putExtra("module", MODULE_MODEL_PAPER_HOME)
                it.putExtra(SUBJECT, MODEL_PAPER)
                startActivity(it)
            }
        }


        /*HomeActivity.swtchLang?.setOnCheckedChangeListener { compoundButton, b ->
            if (b){
                // Set Hindi
                Log.e("PrintCodehindiCode","hindiCode = $hindiCode -- $hindiName")

                if (hindiCode.isNotEmpty() && hindiName.isNotEmpty()){
                    val request = UpdateLanguageRequest(
                        languageId = hindiCode,
                        languageName = hindiName,
                        classId = prefs.getSelectedClassId() ?: "",
                        className = prefs.getSelectedClassName() ?: ""
                    )
                    if (request != null) {

                        updatedLangName = "Hindi"
                        showProgressDialog()
                        viewModelupdateLng.updateLanguage(request)
                    }
                }

            }else{
                // Set English

                if (engCode.isNotEmpty() && engName.isNotEmpty()){
                    val request = UpdateLanguageRequest(
                        languageId = engCode,
                        languageName = engName,
                        classId = prefs.getSelectedClassId() ?: "",
                        className = prefs.getSelectedClassName() ?: ""
                    )
                    if (request != null) {

                        updatedLangName = "English"
                        showProgressDialog()
                        viewModelupdateLng.updateLanguage(request)
                    }
                }

            }
        }*/

    }

    /*
        private fun getDataFromServer() {
            subjectList.add(
                Subject(
                    name = "Maths",
                    image = R.drawable.img_maths,
                    bg = R.drawable.bg_maths
                )
            )

            subjectList.add(
                Subject(
                    name = "Biology",
                    image = R.drawable.img_biology,
                    bg = R.drawable.bg_biology
                )
            )

            subjectList.add(
                Subject(
                    name = "Physics",
                    image = R.drawable.img_physics,
                    bg = R.drawable.bg_physics
                )
            )

            subjectList.add(
                Subject(
                    name = "Chemistry",
                    image = R.drawable.img_chemistry,
                    bg = R.drawable.bg_chemistry
                )
            )

            homeSubjectAdapter.submitList(subjectList)
        }
    */


    fun formatDate(date: String): String {
        val date: Date = inputFormat.parse(date)
        return outputFormat.format(date)
    }

    override fun onLanguageChange(isChecked: Boolean) {
        if (isChecked) {
            // Set Hindi
            Log.e("PrintCodehindiCode", "hindiCode = $hindiCode -- $hindiName")

            if (hindiCode.isNotEmpty() && hindiName.isNotEmpty()) {
                val request = UpdateLanguageRequest(
                    languageId = hindiCode,
                    languageName = hindiName,
                    classId = prefs.getSelectedClassId() ?: "",
                    className = prefs.getSelectedClassName() ?: ""
                )
                if (request != null) {

                    updatedLangName = "Hindi"
                    showProgressDialog()
                    viewModelupdateLng.updateLanguage(request)
                }
            }

        } else {
            // Set English

            if (engCode.isNotEmpty() && engName.isNotEmpty()) {
                val request = UpdateLanguageRequest(
                    languageId = engCode,
                    languageName = engName,
                    classId = prefs.getSelectedClassId() ?: "",
                    className = prefs.getSelectedClassName() ?: ""
                )
                if (request != null) {

                    updatedLangName = "English"
                    showProgressDialog()
                    viewModelupdateLng.updateLanguage(request)
                }
            }

        }
    }

}