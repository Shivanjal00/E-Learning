package com.app.tensquare.ui.questionbank

import android.app.Activity
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Html
import android.util.Log
import android.view.*
import android.view.View.OnTouchListener
import android.view.inputmethod.InputMethodManager
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.tensquare.R
import com.app.tensquare.activity.TestResultActivity
import com.app.tensquare.base.AppBaseActivity
import com.app.tensquare.base.SharedPrefManager
import com.app.tensquare.databinding.ActivityPracticeQuestion1Binding
import com.app.tensquare.databinding.EndPracticeDialogBinding
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.ui.home.HomeActivity
import com.app.tensquare.ui.home.HomeViewModel
import com.app.tensquare.ui.login.LoginActivity
import com.app.tensquare.utils.*
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
//import kotlinx.android.synthetic.main.activity_practice_question.view.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit

private val decimalFormat = DecimalFormat("00")
@RequiresApi(Build.VERSION_CODES.O)
private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
private val sdfFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

@AndroidEntryPoint
class PracticeQuestionActivity : AppBaseActivity(), QuestionNoAdapter.OnQuestionSelection {
    private lateinit var questionNoAdapter: QuestionNoAdapter
    private lateinit var binding: ActivityPracticeQuestion1Binding
    private val viewModel: QuestionBankViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    private var selectedAnswer: String? = ""
    private var answer: String? = null
    private var answerString: String? = null
    private var currentPracticeQuestion: PracticeQuestion? = null // only practice question

    private var questionIndex = 0

    private lateinit var questionBankList: List<Any>
    private lateinit var practiceQuestionList: List<PracticeQuestion>

    private var closingReceiver: BroadcastReceiver = MyBroadcastReceiver()

    private var module: Int = 0

    private val answerBaseQuestion: ArrayList<Answer> by lazy { ArrayList<Answer>() }

    private var page = 1
    private var lengthNo = 1

    private var answerHashList: HashMap<String, AnswerDetail> = HashMap()
    private var duration: String? = "00:00"
    private var quesCountList: ArrayList<QuesCount> = ArrayList()

    private lateinit var chapterIdArray: JSONArray
    private var chapterIdsStr: StringBuilder? = null

    private lateinit var startTime: String
    private lateinit var startDate: Date
    private lateinit var endTime: String
    private lateinit var endDate: Date
    private lateinit var timeDiff: String

    private var countDownTimer: CountDownTimer? = null

    private lateinit var preferences: SharedPrefManager

    private var isAlreadyLoaded : Boolean = false
    private var isbackPos : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPracticeQuestion1Binding.inflate(layoutInflater)
        setContentView(binding.root)
        val filter = IntentFilter(JUMP_TO_SUBJECT_DETAIL)
        this.registerReceiver(closingReceiver, filter)


        this.preferences = prefs

        Log.e("userToken" , preferences.getString("user_token").toString())

        try {
        /*    val current = LocalDateTime.now()
            startTime = current.format(formatter)
            startDate = sdfFormatter.parse(startTime)   */

            val current = Calendar.getInstance().time   //--
            startDate = current //--

//            println("Current Date and Time is: $startTime")

        }catch (e : Exception){
            e.printStackTrace()
        }


        val imm = (this@PracticeQuestionActivity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)!!
        imm!!.hideSoftInputFromWindow(binding.ettest.getWindowToken(), 0)
        this@PracticeQuestionActivity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        /*loadWebView(
            binding.webView,
            "<p>Out of the following find the number that is not a square of a natural number :</p><p> </p><figure class=\"image image_resized\" style=\"width:37.34%;\"><img src=\"https://bhanulearning.s3.ap-south-1.amazonaws.com/question/images/questionImage_1659524660481.jpg\"></figure><p> </p>"
        )*/
        //val webView = WebView(this)
        //setContentView(webView)

        // Enable Javascript

        // Enable Javascript
        //  webView.settings.javaScriptEnabled = true

        // Add a WebViewClient

        // Add a WebViewClient
        /*binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {

                // Inject CSS when page is done loading
                injectCSS(binding.webView)
                super.onPageFinished(view, url)
            }
        }*/

        // Load a webpage
        //webView.loadUrl("https://www.google.com");
        //binding.webView.loadUrl("<p>Out of the following find the number that is not a square of a natural number :</p><p> </p><figure class=\"image image_resized\" style=\"width:37.34%;\"><img src=\"https://bhanulearning.s3.ap-south-1.amazonaws.com/question/images/questionImage_1659524660481.jpg\"></figure><p> </p>")
        /*loadWebView(
            binding.webView,
            "<p>Out of the following find the number that is not a square of a natural number :</p><p> </p><figure class=\"image image_resized\" style=\"width:37.34%;\"><img src=\"https://bhanulearning.s3.ap-south-1.amazonaws.com/question/images/questionImage_1659524660481.jpg\"></figure><p> </p>"
        )*/
        /*loadWebView(
            binding.webView,
            "<p>Out of the following find the number that is not a square of a natural number :</p><p>&nbsp;</p><figure class=\"image image_resized\" style=\"width:63.54%;\"><img src=\"https://bhanulearning.s3.ap-south-1.amazonaws.com/question/images/questionImage_1659524660481.jpg\" style=\"width:63.54%;\"></figure><p>&nbsp;</p>"
        )*/
        init()
        initObservers()
        setListeners()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun injectCSS(webView: WebView) {
        try {
            val inputStream: InputStream = assets.open("style.css")
            val buffer = ByteArray(inputStream.available())
            inputStream.read(buffer)
            inputStream.close()
            val encoded: String = Base64.getEncoder().encodeToString(buffer)
            webView.loadUrl(
                "javascript:(function() {" +
                        "var parent = document.getElementsByTagName('head').item(0);" +
                        "var style = document.createElement('style');" +
                        "style.type = 'text/css';" +  // Tell the browser to BASE64-decode the string into your script !!!
                        "style.innerHTML = window.atob('" + encoded + "');" +
                        "parent.appendChild(style)" +
                        "})()"
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun init() {

        module = intent.getIntExtra("module", 0)


        loadWebViewSettings(binding.webView)
        loadWebViewSettings(binding.webOption1)
        loadWebViewSettings(binding.webOption2)
        loadWebViewSettings(binding.webOption3)
        loadWebViewSettings(binding.webOption4)

        when (module) {
            MODULE_PRACTICE_SESSION -> {

//                enableOrDisableAllOptions(true) //JACK
//                showAnswerStatus(false) //JACK
                binding.imgBack.visibility = View.INVISIBLE

                binding.txtTitle.text = getString(R.string.practice_session)
                chapterIdArray = JSONArray(intent.getStringExtra("chapterIds"))
                chapterIdsStr = StringBuilder()
                for (i in 0 until chapterIdArray.length()) {
                    chapterIdsStr?.append(if (chapterIdsStr?.isEmpty() == true) chapterIdArray[i] else ",${chapterIdArray[i]}")
                }

                /*val timer: CountUpTimer = object : CountUpTimer(30000, 1000) {
                    fun onTick(second: Int) {
                        timerView.setText(second.toString())
                    }
                }

                timer.start()*/
                //stopwatch()
                //binding.rlTimer.visibility = View.VISIBLE
                binding.txtEndOrSubmit.text = getString(com.app.tensquare.R.string.end); // "End"
                binding.rlToolbar.setBackgroundResource(R.drawable.header_common)
                getQuestionListByChapterId()
            }
            MODULE_TEST -> {

                binding.imgBack.visibility = View.INVISIBLE

                chapterIdArray = JSONArray(intent.getStringExtra("chapterIds"))
                chapterIdsStr = StringBuilder()
                for (i in 0 until chapterIdArray.length()) {
                    chapterIdsStr?.append(if (chapterIdsStr?.isEmpty() == true) chapterIdArray[i] else ",${chapterIdArray[i]}")
                }

                binding.rvCount.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                questionNoAdapter =
                    QuestionNoAdapter(
                        clickable = true,
                        context = this@PracticeQuestionActivity,
                        list = quesCountList,
                        listener = this
                    )
                binding.rvCount.adapter = questionNoAdapter
                binding.txtEndOrSubmit.visibility = View.VISIBLE //JACK
//                binding.txtEndOrSubmit.text = "Submit"
                binding.txtEndOrSubmit.text = getString(R.string.submit)
                binding.txtTitle.visibility = View.GONE
                binding.rlNextPrev.visibility = View.VISIBLE
                ///binding.llQuesNo.visibility = View.VISIBLE
                binding.rlTimer.visibility = View.VISIBLE
                binding.rlToolbar.setBackgroundResource(R.drawable.bg_header_y)
                getTestQuestionList()
            }
            MODULE_TEST_SOLUTION -> {
                val idList = intent.getStringExtra("question_ids")
                val gson = Gson()
                val idCollection = gson.fromJson(idList, ArrayList::class.java) as ArrayList<String>
                println(idCollection)
                for (i in idCollection.indices) {
                    quesCountList.add(
                        QuesCount(
                            id = idCollection[i],
                            no = i + 1,
                            isSelected = i == 0
                        )
                    )
                }

                binding.rvCount.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                questionNoAdapter =
                    QuestionNoAdapter(
                        context = this@PracticeQuestionActivity,
                        true,
                        list = quesCountList,
                        listener = this
                    )
                binding.rvCount.adapter = questionNoAdapter
                binding.txtEndOrSubmit.visibility = View.GONE
                binding.txtTitle.text = getString(R.string.solutions)
                binding.rlNextPrev.visibility = View.GONE
                binding.rlTimer.visibility = View.GONE
                binding.rlToolbar.setBackgroundResource(R.drawable.bg_header_y)

                binding.txtCheck.visibility = View.VISIBLE
                binding.txtCheck.text = getString(com.app.tensquare.R.string.back_to_home)

                getSolutions(quesCountList[0].id)
            }
        }
    }

    override fun setListsAndAdapters() {
        TODO("Not yet implemented")
    }

    override fun setListeners() {

        binding.chkBookMark.setOnCheckedChangeListener { compoundButton, check ->
            //if (check) {
            quesCountList[questionIndex].isBookMarked = check
            questionNoAdapter.notifyDataSetChanged()
            //}
        }

        binding.imgBack.setOnClickListener { finish() }

        binding.txtCheck.setOnClickListener { // this button is workable only for practice session, for test it is hidden
//            if (binding.txtCheck.text == "Check") {
            if (binding.txtCheck.text == getString(R.string.check)) {
                if (selectedAnswer != null && selectedAnswer != "") {
                    answer?.let {
                        setAnswerStatus(it, it == selectedAnswer)

                        currentPracticeQuestion?.let { question ->
                            answerBaseQuestion.add(
                                Answer(
                                    question._id,
                                    question.difficultyLevel,
                                    if (it == selectedAnswer) 1 else 0
                                )
                            )
                        }
                    }

                    enableOrDisableAllOptions(false)
                    binding.txtCheck.text = getString(R.string.next)
                    questionIndex += 1
                } else
                    showToast(getString(com.app.tensquare.R.string.please_select_an_option))
            }
            else if(binding.txtCheck.text == getString(com.app.tensquare.R.string.back_to_home)){
                // JACK
                val intent = Intent(
                    this@PracticeQuestionActivity,
                    HomeActivity::class.java
                )
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()

            }
            else {
                if (questionIndex < questionBankList.size) {

                    Log.e("Size_1" , questionIndex.toString() + " --- " + questionBankList.size.toString())

                    setUpQuestionAndOption(questionBankList[questionIndex] , questionIndex)

                    selectAnOption(null)
                    binding.txtCheck.text = getString(R.string.check)
                    binding.txtCheck.visibility = View.GONE
                    enableOrDisableAllOptions(true)
                    showAnswerStatus(false)
                } else {

                    Log.e("Size_2" , questionIndex.toString() + " --- " + questionBankList.size.toString())

                    // JACK
                    selectAnOption(null)
                    binding.txtCheck.text = getString(R.string.check)
                    binding.txtCheck.visibility = View.GONE
                    enableOrDisableAllOptions(true)
                    showAnswerStatus(false)
                    //---------
                    getQuestionListByChapterId()
                    //showEndingDialog()

                }
            }
        }

        binding.txtNext.setOnClickListener {


            //saving current question's Answer
            println("pre" + questionIndex)
            println("pre" + selectedAnswer)
            println("pre" + selectedAnswer)
//            Log.e("show_ans_list_before" , answerHashList.toString())
            answerHashList[(questionBankList[questionIndex] as Question)._id]?.answer =
                selectedAnswer.toString()
            answerHashList[(questionBankList[questionIndex] as Question)._id]?.position = questionIndex
            Log.e("show_ans_list_after" , answerHashList.toString())
            println("post" + questionIndex)
            // fetching next question data and answer if selected before
            selectedAnswer = ""
            if (questionIndex < questionBankList.size - 1) {
                ++questionIndex
                //binding.rvCount.smoothScrollToPosition(questionIndex)
                binding.rvCount.layoutManager?.scrollToPosition(questionIndex)
                val selectedOption =
                    answerHashList[(questionBankList[questionIndex] as Question)._id]?.answer
                answerHashList[(questionBankList[questionIndex] as Question)._id]?.position = questionIndex
                setUpQuestionAndOption(questionBankList[questionIndex] , questionIndex)
                binding.chkBookMark.isChecked = quesCountList[questionIndex].isBookMarked
                when (selectedOption) {
                    "A" -> {
                        selectAnOption(binding.rlOption1, false)
                    }
                    "B" -> {
                        selectAnOption(binding.rlOption2, false)
                    }
                    "C" -> {
                        selectAnOption(binding.rlOption3, false)
                    }
                    "D" -> {
                        selectAnOption(binding.rlOption4, false)
                    }
                    else ->
                        selectAnOption(null)
                }


                onSelection(1, questionIndex)

                if (!binding.txtPrev.isVisible) {
                    binding.txtPrev.visibility = View.VISIBLE
                }
                if (questionIndex == questionBankList.size - 1) {
                    binding.txtNext.visibility = View.GONE
                    binding.txtEndOrSubmit.visibility = View.VISIBLE
                }
            } else {
                binding.txtNext.visibility = View.GONE
                binding.txtEndOrSubmit.visibility = View.VISIBLE

            }

        }

        binding.txtPrev.setOnClickListener {

            binding.txtEndOrSubmit.visibility = View.VISIBLE //JACK
            //saving current question's answer
            answerHashList[(questionBankList[questionIndex] as Question)._id]?.answer =
                selectedAnswer.toString()
            answerHashList[(questionBankList[questionIndex] as Question)._id]?.position = questionIndex
            //fetching previous question's answer
            --questionIndex
            if (questionIndex >= 0) {
                binding.txtNext.visibility = View.VISIBLE
                binding.rvCount.layoutManager?.scrollToPosition(questionIndex)
                //binding.rvCount.smoothScrollToPosition(questionIndex)
                val selectedOption =
                    answerHashList[(questionBankList[questionIndex] as Question)._id]?.answer

                answerHashList[(questionBankList[questionIndex] as Question)._id]?.position = questionIndex
                binding.chkBookMark.isChecked = quesCountList[questionIndex].isBookMarked
                setUpQuestionAndOption(questionBankList[questionIndex] , questionIndex)
                when (selectedOption) {
                    "A" -> {
                        selectAnOption(binding.rlOption1, false)
                    }
                    "B" -> {
                        selectAnOption(binding.rlOption2, false)
                    }
                    "C" -> {
                        selectAnOption(binding.rlOption3, false)
                    }
                    "D" -> {
                        selectAnOption(binding.rlOption4, false)
                    }
                    else ->
                        selectAnOption(null)
                }


                !binding.txtPrev.isVisible.let { true }

                onSelection(1, questionIndex)
                if (questionIndex == 0) {
                    binding.txtPrev.visibility = View.GONE
                }
            } else
                binding.txtPrev.visibility = View.GONE



        }


        binding.txtEndOrSubmit.setOnClickListener {

    /*        val current = LocalDateTime.now()
            endTime = current.format(formatter)
            endDate = sdfFormatter.parse(endTime)   */

            val current = Calendar.getInstance().time   //--
            endDate = current   //--

            val diff: Long = endDate.time - startDate.time
            val seconds = diff / 1000
            val minutes = seconds / 60

            timeDiff = "$minutes:$seconds"
            showEndingDialog(false)
        }

        binding.apply {

            webOption1.setOnTouchListener(object : OnTouchListener {
                override fun onTouch(p0: View?, event: MotionEvent?): Boolean {
                    if (event?.action == MotionEvent.ACTION_UP) {

                        val drawableAConstantState = ContextCompat.getDrawable(
                            this@PracticeQuestionActivity,
                            R.drawable.bg_rect_gray_stroke
                        )?.constantState
                        //rl.setBackgroundResource(if (rl.background?.constantState == drawableAConstantState) R.drawable.B else R.drawable.A)

                        if (rlOption1.background.constantState == drawableAConstantState) {
                            selectedAnswer = "A"
                            selectAnOption(rlOption1, false)
                            showCheckButton()
                        } else {
                            selectedAnswer = ""
                            selectAnOption(rlOption1, true)
                        }
                        return true;
                    }


                    return false
                }

            })

            webOption2.setOnTouchListener(object : OnTouchListener {
                override fun onTouch(p0: View?, event: MotionEvent?): Boolean {
                    if (event?.action == MotionEvent.ACTION_UP) {

                        val drawableAConstantState = ContextCompat.getDrawable(
                            this@PracticeQuestionActivity,
                            R.drawable.bg_rect_gray_stroke
                        )?.constantState

                        if (rlOption2.background.constantState == drawableAConstantState) {
                            selectedAnswer = "B"
                            selectAnOption(rlOption2, false)
                            showCheckButton()
                        } else {
                            selectedAnswer = ""
                            selectAnOption(rlOption2, true)
                        }
                        return true;
                    }
                    return false
                }

            })

            webOption3.setOnTouchListener(object : OnTouchListener {
                override fun onTouch(p0: View?, event: MotionEvent?): Boolean {
                    if (event?.action == MotionEvent.ACTION_UP) {
                        val drawableAConstantState = ContextCompat.getDrawable(
                            this@PracticeQuestionActivity,
                            R.drawable.bg_rect_gray_stroke
                        )?.constantState

                        if (rlOption3.background.constantState == drawableAConstantState) {
                            selectedAnswer = "C"
                            selectAnOption(rlOption3, false)
                            showCheckButton()
                        } else {
                            selectedAnswer = ""
                            selectAnOption(rlOption3, true)
                        }
                        return true;
                    }
                    return false
                }

            })

            webOption4.setOnTouchListener(object : OnTouchListener {
                override fun onTouch(p0: View?, event: MotionEvent?): Boolean {
                    if (event?.action == MotionEvent.ACTION_UP) {
                        val drawableAConstantState = ContextCompat.getDrawable(
                            this@PracticeQuestionActivity,
                            R.drawable.bg_rect_gray_stroke
                        )?.constantState

                        if (rlOption4.background.constantState == drawableAConstantState) {
                            selectedAnswer = "D"
                            selectAnOption(rlOption4, false)
                            showCheckButton()
                        } else {
                            selectedAnswer = ""
                            selectAnOption(rlOption4, true)
                        }
                        return true;
                    }
                    return false
                }

            })


        }


    }

    override fun initObservers() {
        homeViewModel.refreshTokenResponse.observe(this){
            when (it){
                is NetworkResult.Loading -> {}
                is NetworkResult.Success -> {
                    dismissProgressDialog()
                    it.data?.let {data ->

                        Log.e("token_Home = >" , data.accessToken.toString())
                        prefs.setUserToken(data.accessToken)
                        prefs.setRefreshToken(data.refreshToken)

                        init()
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

        viewModel.response.observe(this) { response ->
            dismissProgressDialog()
            binding.llMain.visibility = View.VISIBLE
            when (response) {
                is NetworkResult.Success -> {
                    questionBankList = response.data?.questionList as List<Question>
                    if (questionBankList.isNotEmpty()) {
                        Log.e("PrintquestionBankList","questionBankList = ${response.data}")
                        duration = response.data?.duration
                        binding.txtTimer.text = duration
                        duration?.let { startCountDownTime(duration) }
                        setUpQuestionAndOption(questionBankList[questionIndex] , questionIndex) // as 0

                        for (i in questionBankList.indices){
                            val que_id = (questionBankList as List<Question>)[i]._id
                            answerHashList.put(que_id, AnswerDetail(que_id, "" , i) )
                        }

                        for (i in questionBankList.indices) {
                            quesCountList.add(QuesCount(no = i + 1, isSelected = i == 0))
                        }
                        questionNoAdapter.notifyDataSetChanged()
                    } else {
                        binding.rlTimer.visibility = View.GONE
                        binding.rlNextPrev.visibility = View.GONE
                        binding.llMain.visibility = View.GONE
                        binding.rvCount.visibility = View.GONE
                        binding.txtNoData.visibility = View.VISIBLE

                        isbackPos = true;

                        //JACK
                        binding.txtEndOrSubmit.visibility = View.GONE
                    }


                }
                is NetworkResult.Error -> {
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

        viewModel.testAnswerSubmitResponse.observe(this) { response ->
            //binding.progressBar.visibility = View.GONE
            dismissProgressDialog()
            binding.llMain.visibility = View.VISIBLE
            when (response) {
                is NetworkResult.Success -> {
                    Intent(this@PracticeQuestionActivity, TestResultActivity::class.java).also {
                        it.putExtra("Resultant", Gson().toJson(response.data).toString())
                        startActivity(it)
                    }
                    countDownTimer?.cancel()
                }
                is NetworkResult.Error -> {
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

        viewModel.questionByChapterIdResponse.observe(this) { response ->
            //binding.progressBar.visibility = View.GONE
            dismissProgressDialog()
            binding.llMain.visibility = View.VISIBLE
            when (response) {
                is NetworkResult.Success -> {
//                    if(response.data != null){
//                        lengthNo = response.data.length;
//                    }
                    questionBankList = response.data?.questionList ?: ArrayList()
                    if (questionBankList.isNotEmpty()) {
                        questionIndex = 0
                        setUpQuestionAndOption(questionBankList[questionIndex] , questionIndex) // as 0

                        isAlreadyLoaded = true;

                    } else {

                        if (!isAlreadyLoaded){
//                        showToast(getString(com.app.elearning.R.string.no_more_question_available))
                        binding.llMain.visibility = View.GONE
                        binding.txtNoData.visibility = View.VISIBLE
                        binding.txtNoData.text = getString(com.app.tensquare.R.string.no_more_question_available)
                        }else{

                        /*    val current = LocalDateTime.now()
                            endTime = current.format(formatter)
                            endDate = sdfFormatter.parse(endTime)   */

                            val current = Calendar.getInstance().time
                            endDate = current

                            val diff: Long = endDate.time - startDate.time
                            val seconds = diff / 1000
                            val minutes = seconds / 60

                            timeDiff = "$minutes:$seconds"
                            showEndingDialog(true)

                        }

                    }
                }
                is NetworkResult.Error -> {
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

        viewModel.practiceTimeSubmissionResponse.observe(this) { response ->
            dismissProgressDialog()
            binding.llMain.visibility = View.VISIBLE
            when (response) {
                is NetworkResult.Success -> {
//                    showToast(response.message)
                }
                is NetworkResult.Error -> {
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

        viewModel.testAnswerResponse.observe(this) { response ->
            dismissProgressDialog()
            binding.llMain.visibility = View.VISIBLE
            when (response) {
                is NetworkResult.Success -> {
                    val answer = response.data as TestAnswerSheet
                    setUpQuestionAndOption(answer , questionIndex) // as 0

                    selectedAnswer = response.data.userChooseOption

                    this.answer?.let {
                        setAnswerStatus(it, it == selectedAnswer)

                        when (selectedAnswer) {
                            "A" -> {
                                selectAnOption(binding.rlOption1, false)
                            }
                            "B" -> {
                                selectAnOption(binding.rlOption2, false)
                            }
                            "C" -> {
                                selectAnOption(binding.rlOption3, false)
                            }
                            "D" -> {
                                selectAnOption(binding.rlOption4, false)
                            }
                            else ->
                                selectAnOption(null)
                        }

                    }

                    enableOrDisableAllOptions(false)

                }
                is NetworkResult.Error -> {
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

    private fun showCheckButton() {
        if (!binding.txtCheck.isVisible && module == MODULE_PRACTICE_SESSION) {
            binding.txtCheck.visibility = View.VISIBLE
        }
    }

    private fun enableOrDisableAllOptions(clickable: Boolean) {
        binding.apply {
            rlOption1.isClickable = clickable
            rlOption2.isClickable = clickable
            rlOption3.isClickable = clickable
            rlOption4.isClickable = clickable
        }
    }

    private fun selectAnOption(linearLayout: View?, isChecked: Boolean = true) {

        binding.apply {
            rlOption1.setBackgroundResource(R.drawable.bg_rect_gray_stroke)
            rlOption2.setBackgroundResource(R.drawable.bg_rect_gray_stroke)
            rlOption3.setBackgroundResource(R.drawable.bg_rect_gray_stroke)
            rlOption4.setBackgroundResource(R.drawable.bg_rect_gray_stroke)
            if (!isChecked)
                linearLayout?.let {
                    it.setBackgroundResource(R.drawable.bg_rect_black_stroke)
                }
        }
    }

    private var mDialog: Dialog? = null
    private fun showEndingDialog(isEnded : Boolean) {

        if (mDialog != null) {
            if (mDialog!!.isShowing) {
                return
            }
        }
        mDialog = Dialog(this@PracticeQuestionActivity)

        val dialogBinding: EndPracticeDialogBinding =
            EndPracticeDialogBinding.inflate(layoutInflater)

        mDialog!!.setContentView(dialogBinding.root)
        mDialog!!.setCanceledOnTouchOutside(false)
        mDialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        mDialog!!.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        mDialog!!.window!!.setGravity(Gravity.BOTTOM)

        when (module) {
            MODULE_PRACTICE_SESSION -> {
                dialogBinding.txtTitle.text = getString(R.string.end_practice_session)


                if (isEnded){
                    dialogBinding.txtTitle.text = getString(R.string.end_practice_session)

                    dialogBinding.txtMessage.text = getString(R.string.no_more_question_available)
//                    dialogBinding.txtResume.visibility = View.GONE
//                    dialogBinding.txtExit.text = getString(R.string.submit)
                    dialogBinding.txtExit.visibility = View.GONE
                    dialogBinding.txtResume.text = getString(R.string.end_sen)
//                    dialogBinding.txtExit.setTextColor(Color.parseColor("#FFFFFF"));
//                    dialogBinding.txtExit.setBackgroundResource(R.drawable.bg_btn_light_black)

                }else{
                    dialogBinding.txtTitle.text = getString(R.string.end_practice_session)
//                    dialogBinding.txtMessage.text = getString(R.string.sure_wanna_exit)
                    dialogBinding.txtMessage.visibility = View.GONE
                    dialogBinding.txtResume.text = getString(R.string.end_sen)
                    dialogBinding.txtExit.text = getString(R.string.con_test)
                }

            }
            MODULE_TEST -> {
                dialogBinding.txtTitle.text = getString(R.string.submit_test)

            /*    val userData = preferences.getUserData()?.let { JSONObject(it) }
                if (userData != null) {
                    dialogBinding.txtMessage.text = "Hey ${userData.getString("name")}, are you sure you want to end this test?"
                }else{
                    dialogBinding.txtMessage.text = "Hey John, are you sure you want to end this test?"
                }   */
                dialogBinding.txtMessage.visibility = View.GONE

                dialogBinding.txtResume.text = getString(R.string.submit)
                dialogBinding.txtExit.text = getString(R.string.con_test)
            }
        }


        dialogBinding.txtResume.setOnClickListener {
            when (module) {
                MODULE_PRACTICE_SESSION -> {
//                    mDialog!!.dismiss()
                    submitPracticeSessionTime()
                    val broadcast = Intent(JUMP_TO_SUBJECT_DETAIL)
                    sendBroadcast(broadcast)
                    finish()
                    mDialog!!.dismiss()
                }
                MODULE_TEST -> {
                    answerHashList[(questionBankList[questionIndex] as Question)._id]?.answer =
                        selectedAnswer.toString()
                    answerHashList[(questionBankList[questionIndex] as Question)._id]?.position = questionIndex
                    submitTest(binding.txtTimer.text.toString())
                }
            }
        }

        dialogBinding.txtExit.setOnClickListener {
//            if (module == MODULE_PRACTICE_SESSION) {
//                submitPracticeSessionTime()
//                val broadcast = Intent(JUMP_TO_SUBJECT_DETAIL)
//                sendBroadcast(broadcast)
//                finish()
//            }
            mDialog!!.dismiss()

        }
        mDialog!!.show()
    }

    private fun submitTest(timeLast: String) {
        val answerDetailList = ArrayList<AnswerDetail>()

        //println(answerHashList.values)

        answerHashList.map {
            answerDetailList.add(it.value)
        }

        answerDetailList.sortWith { item, t1 ->
            val s1: Int = item.position
            val s2: Int = t1.position
            s1.compareTo(s2)
        }

        val spentTime: String? =
            duration?.let { it1 -> getTimeDiff(binding.txtTimer.text.toString(), it1) }

        val chapterIdList = ArrayList<String>()

        for (i in 0 until chapterIdArray.length()) {
            chapterIdList.add(chapterIdArray[i].toString())
        }

        val testAnswerSubmitRequest = spentTime?.let { it ->
            TestAnswerSubmitRequest(
                totalTimeOfQuestions = timeLast,
                answerDetails = answerDetailList,
                userSpendTime = it,
                subjectId = intent.getStringExtra("subject_id")!!,
                chapterIds = chapterIdList
            )
        }

        if (testAnswerSubmitRequest != null) {
            mDialog?.dismiss()
            showProgressDialog()
            viewModel.submitTestAnswers(testAnswerSubmitRequest)
            Log.e("Save Ans Data = >" , testAnswerSubmitRequest.toString())
        }
    }

    override fun onBackPressed() {
        if (mDialog != null && mDialog!!.isShowing) {
            mDialog!!.dismiss()
        } else {
           if(module == MODULE_PRACTICE_SESSION || module == MODULE_TEST){

               if (isbackPos){
                   super.onBackPressed()
               }

           }else{
               super.onBackPressed()
           }

        }
    }

    private fun getTestQuestionList() {
        //binding.progressBar.visibility = View.VISIBLE
        val testQuestionRequest = chapterIdsStr?.let {
            TestQuestionRequest(
                subjectId = intent.getStringExtra("subject_id")!!, //TODO:CHANGE THIS
                chapterIds = it.toString()
//                subjectId = "6304c19d7ab5c99037f50669",
//                chapterIds = "6305bff67ab5c99037f50c3b"
            )
        }
        if (testQuestionRequest != null) {
            showProgressDialog()
            Log.e("PrinttestQuestionReq","testQuestionRequest = $testQuestionRequest")
            viewModel.getTestQuestionList(testQuestionRequest)
        }
    }

    private fun getSolutions(questId: String) {
        showProgressDialog()
        viewModel.getTestAnswer(questId)
    }

    private fun getQuestionListByChapterId() {
        //binding.progressBar.visibility = View.VISIBLE
        //showProgressDialog()
        //intent.getStringExtra("chapterIds")?.let { viewModel.getQuestionByChapterId(it) }

        showProgressDialog()
        val answerSubmissionRequest = chapterIdsStr?.let {
            PracticeQuestionRequest(
                subjectId = intent.getStringExtra("subject_id")!!,
                chapterIds = it.toString(),
                answerBaseQuestion = this.answerBaseQuestion,
                pageNo = page
            )
        }
        if (answerSubmissionRequest != null) {
            viewModel.getPracticeSessionQuestionList(answerSubmissionRequest)
            Log.e("submitdata => " , answerSubmissionRequest.toString()) //JACK
        }
        page++
    }

    private fun submitPracticeSessionTime() {
        showProgressDialog()
        val request = chapterIdsStr?.let {
            PracticeSessionTimeRequest(
                subjectId = intent.getStringExtra("subject_id")!!,
                chapterIds = it.toString(),
                userSpendTime = timeDiff
            )
        }
        if (request != null) {
            viewModel.submitPracticeSessionTime(request)
        }
    }

    private fun setUpQuestionAndOption(questionData: Any , index : Int) {

        binding.ettest.setText("")
        binding.ettest.requestFocus()

        binding.apply {


            if (questionData is Question) {
                var question = questionData as Question
                loadWebView(webOption1, question.option1)
                loadWebView(webOption2, question.option2)
                loadWebView(webOption3, question.option3)
                loadWebView(webOption4, question.option4)
                //answer = question.correct.uppercase()
                //answerString = question.answerDetails
                loadWebView(webView, question.name)

            } else if (questionData is PracticeQuestion) {
                var question = questionData as PracticeQuestion
                loadWebView(webOption1, question.option1)
                loadWebView(webOption2, question.option2)
                loadWebView(webOption3, question.option3)
                loadWebView(webOption4, question.option4)
                answer = question.correct.uppercase()
                answerString = question.answerDetails
                loadWebView(webView, question.name)

                currentPracticeQuestion = question // to pass some data to submit api
            } else {
                var question = questionData as TestAnswerSheet
                loadWebView(webOption1, question.option1)
                loadWebView(webOption2, question.option2)
                loadWebView(webOption3, question.option3)
                loadWebView(webOption4, question.option4)
                answer = question.correct.uppercase()
                answerString = question.answerDetails
                loadWebView(webView, question.name)
            }


            //----------JACK
            if (module == MODULE_TEST) {

                questionIndex = index

                selectedAnswer =
                    answerHashList[(questionBankList[questionIndex] as Question)._id]?.answer
                answerHashList[(questionBankList[questionIndex] as Question)._id]?.position = questionIndex

                if (selectedAnswer.equals("A")) {

                    selectedAnswer = "A"
                    selectAnOption(rlOption1, false)
                    showCheckButton()

                } else if (selectedAnswer.equals("B")) {

                    selectedAnswer = "B"
                    selectAnOption(rlOption2, false)
                    showCheckButton()

                } else if (selectedAnswer.equals("C")) {

                    selectedAnswer = "C"
                    selectAnOption(rlOption3, false)
                    showCheckButton()

                } else if (selectedAnswer.equals("D")) {

                    selectedAnswer = "D"
                    selectAnOption(rlOption4, false)
                    showCheckButton()

                }
            }




        }
    }

    private fun loadWebViewSettings(webView: WebView) {

        webView.setOnLongClickListener {
            true
        }

        webView.settings.javaScriptEnabled = true
        webView.webChromeClient = WebChromeClient()
        webView.isVerticalScrollBarEnabled = true
        webView.settings.mediaPlaybackRequiresUserGesture = false
        webView.settings.loadsImagesAutomatically = true
        webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
    }

    private fun loadWebView(webView: WebView, string: String) {

        webView.isHapticFeedbackEnabled = false
        webView.settings.javaScriptEnabled = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN


        webView.loadDataWithBaseURL("http://bar/", "<script type='text/x-mathjax-config'>"
                +"MathJax.Hub.Config({ "
                +"showMathMenu: false, "
                +"jax: ['input/MathML','output/HTML-CSS'], "
                +"extensions: ['mml2jax.js'], "
                +"TeX: { extensions: ['noErrors.js','noUndefined.js'] }, "
                +"messageStyle: 'none'"
                +"});</script>"
                +"<script type='text/javascript' "
                +"src='https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.7/MathJax.js?config=TeX-AMS-MML_HTMLorMML'"
                +"></script><span id='math'><style>img{display: inline; height: auto; max-width: 100%;}</style>"+string+"</span>","text/html","utf-8","");


    }

    private fun setAnswerStatus(answer: String, isCorrect: Boolean) {
        showAnswerStatus(false)
        binding.apply {
            when (selectedAnswer) {
                "A" -> {
                    imgAnsStatus1.visibility = View.VISIBLE
                    if (isCorrect)
                        imgAnsStatus1.setImageResource(R.drawable.ic_tick_green)
                    else
                        imgAnsStatus1.setImageResource(R.drawable.ic_cross_incorrect)
                }
                "B" -> {
                    imgAnsStatus2.visibility = View.VISIBLE
                    if (isCorrect)
                        imgAnsStatus2.setImageResource(R.drawable.ic_tick_green)
                    else
                        imgAnsStatus2.setImageResource(R.drawable.ic_cross_incorrect)
                }
                "C" -> {
                    imgAnsStatus3.visibility = View.VISIBLE
                    if (isCorrect)
                        imgAnsStatus3.setImageResource(R.drawable.ic_tick_green)
                    else
                        imgAnsStatus3.setImageResource(R.drawable.ic_cross_incorrect)
                }
                "D" -> {
                    imgAnsStatus4.visibility = View.VISIBLE
                    if (isCorrect)
                        imgAnsStatus4.setImageResource(R.drawable.ic_tick_green)
                    else
                        imgAnsStatus4.setImageResource(R.drawable.ic_cross_incorrect)
                }

            }

            binding.apply {
                when (answer) {
                    "A" -> {
                        llAns1.visibility = View.VISIBLE
                        //answerString?.let { setTextInHtmlForm(txtAnawer1, it) }
                        answerString?.let { loadWebView(webAnswer1, it) }

                    }
                    "B" -> {
                        llAns2.visibility = View.VISIBLE
                        //answerString?.let { setTextInHtmlForm(txtAnawer2, it) }
                        answerString?.let { loadWebView(webAnswer2, it) }
                    }
                    "C" -> {
                        llAns3.visibility = View.VISIBLE
                        //answerString?.let { setTextInHtmlForm(txtAnawer3, it) }
                        answerString?.let { loadWebView(webAnswer3, it) }
                    }
                    "D" -> {
                        llAns4.visibility = View.VISIBLE
                        //answerString?.let { setTextInHtmlForm(txtAnawer4, it) }
                        answerString?.let { loadWebView(webAnswer4, it) }
                    }

                }
            }


        }
    }


    private fun showAnswerStatus(shouldShow: Boolean) {
        binding.apply {
            imgAnsStatus1.isVisible = shouldShow
            imgAnsStatus2.isVisible = shouldShow
            imgAnsStatus3.isVisible = shouldShow
            imgAnsStatus4.isVisible = shouldShow
            llAns1.isVisible = shouldShow
            llAns2.isVisible = shouldShow
            llAns3.isVisible = shouldShow
            llAns4.isVisible = shouldShow
        }
    }

    private fun setTextInHtmlForm(textView: TextView, text: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.text = Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
            textView.text = textView.text.toString().trim()
        } else {
            textView.text = Html.fromHtml(text)
            textView.text = textView.text.toString().trim()
        }
    }

    private inner class MyBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (countDownTimer != null)
            countDownTimer?.cancel()
        unregisterReceiver(this.closingReceiver)
    }

    private fun startCountDownTime(duration: String?) {
        val minutes: Long = duration?.split(":")?.get(0).toString().toLong() * 60000
        countDownTimer = object : CountDownTimer(minutes, 1000) {
            override fun onTick(millisUntilFinished: Long) {
           /*     val second = decimalFormat.format(
                    (millisUntilFinished / 1000) % 60
                )

                val minutes = decimalFormat.format(
                    (millisUntilFinished / 60000) % 60
                )

                binding.txtTimer.text = "$minutes:$second"  */

                val sec = String.format("%02d", (millisUntilFinished / 1000) % 60)
                val min = String.format("%02d", (millisUntilFinished / 60000) % 60)

                binding.txtTimer.text = "$min:$sec"

            }

            override fun onFinish() {
                submitTest("00:00")
            }
        }.start()
    }

    private fun getTimeDiff(start: String, end: String): String {
        val format = SimpleDateFormat("mm:ss")
        val date1 = format.parse(start)
        val date2 = format.parse(end)
        val diff: Long = date2.time - date1.time
        //val seconds = diff / 1000
        //val minutes = seconds / 60
        //val hours = minutes / 60

   /*     var seconds = decimalFormat.format(diff / 1000)
        val minutes = decimalFormat.format(seconds.toLong() / 60)
        seconds = decimalFormat.format(seconds.toLong() % 60)
        return "$minutes:$seconds"  */

        var sec = String.format("%02d", (diff / 1000))
        val min = String.format("%02d", (sec.toLong() / 60))
        sec = String.format("%02d", (sec.toLong() % 60))

        return "$min:$sec"
    }

    override fun onSelection(flag: Int, position: Int) {

        if (flag == 1) {
            for (ques in quesCountList) {
                if (ques.isSelected) {
                    ques.isSelected = false
                    break
                }else{

                }
            }

            // ---------

            quesCountList[position].isSelected = true
            questionNoAdapter.notifyDataSetChanged()
        } else if (flag == 2) {
            if (module == MODULE_TEST) {

                answerHashList[(questionBankList[questionIndex] as Question)._id]?.answer =
                    selectedAnswer.toString()
                answerHashList[(questionBankList[questionIndex] as Question)._id]?.position = questionIndex
                println("post" + questionIndex)
                // fetching next question data and answer if selected before

                //if (questionIndex < questionBankList.size - 1) {
                selectedAnswer = ""
                questionIndex = position
                val selectedOption =
                    answerHashList[(questionBankList[questionIndex] as Question)._id]?.answer
                answerHashList[(questionBankList[questionIndex] as Question)._id]?.position = questionIndex
                setUpQuestionAndOption(questionBankList[questionIndex] , questionIndex)
                binding.chkBookMark.isChecked = quesCountList[questionIndex].isBookMarked
                when (selectedOption) {
                    "A" -> {
                        selectAnOption(binding.rlOption1, false)
                    }
                    "B" -> {
                        selectAnOption(binding.rlOption2, false)
                    }
                    "C" -> {
                        selectAnOption(binding.rlOption3, false)
                    }
                    "D" -> {
                        selectAnOption(binding.rlOption4, false)
                    }
                    else ->
                        selectAnOption(null)
                }
                //enableOrDisableAllOptions(true)
                //showAnswerStatus(false)

                onSelection(1, questionIndex)

                if (questionIndex > 0) {
                    binding.txtPrev.visibility = View.VISIBLE
                } else
                    binding.txtPrev.visibility = View.GONE

                if (questionIndex < questionBankList.size - 1) {
                    binding.txtNext.visibility = View.VISIBLE
//                    binding.txtEndOrSubmit.visibility = View.GONE
                    binding.txtEndOrSubmit.visibility = View.VISIBLE  // JACK
                } else {
                    binding.txtNext.visibility = View.GONE
                    binding.txtEndOrSubmit.visibility = View.VISIBLE
                }


            } else {
                for (ques in quesCountList) {
                    if (ques.isSelected) {
                        ques.isSelected = false
                        break
                    }
                }
                quesCountList[position].isSelected = true
                questionNoAdapter.notifyDataSetChanged()
                getSolutions(quesCountList[position].id)
            }
        }
    }



    private fun timerDisplay(time: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(time)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit
            .MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time))

        return " %02d:%02d".format(minutes, seconds)

    }

}
