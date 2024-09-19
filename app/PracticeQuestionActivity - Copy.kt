package com.app.elearning.ui.questionbank

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.app.elearning.R
import com.app.elearning.activity.TestResultActivity
import com.app.elearning.base.AppBaseActivity
import com.app.elearning.databinding.ActivityPracticeQuestion1Binding
import com.app.elearning.databinding.EndPracticeDialogBinding
import com.app.elearning.network.NetworkResult
import com.app.elearning.response.Question
import com.app.elearning.utils.JUMP_TO_SUBJECT_DETAIL
import com.app.elearning.utils.MODULE_PRACTICE_SESSION
import com.app.elearning.utils.MODULE_TEST
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PracticeQuestionActivity : AppBaseActivity() {
    private lateinit var binding: ActivityPracticeQuestion1Binding
    private val viewModel: QuestionBankViewModel by viewModels()

    private var selectedAnswer: String? = null
    private var answer: String? = null
    private var answerString: String? = null

    private var questionIndex = 0

    private lateinit var questionBankList: List<Any>
    private lateinit var practiceQuestionList: List<PracticeQuestion>

    private var closingReceiver: BroadcastReceiver = MyBroadcastReceiver()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPracticeQuestion1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        initObservers()
        setListeners()


    }

    override fun init() {
        val filter = IntentFilter(JUMP_TO_SUBJECT_DETAIL)
        this.registerReceiver(closingReceiver, filter)

        when {
            intent.getIntExtra("module", 0) == MODULE_PRACTICE_SESSION -> {
                binding.txtEndOrSubmit.text = "End"
                binding.rlToolbar.setBackgroundResource(R.drawable.header_common)
                getQuestionListByChapterId()
            }
            intent.getIntExtra("module", 0) == MODULE_TEST -> {
                binding.txtEndOrSubmit.text = "Submit"
                binding.txtTitle.visibility = View.GONE
                binding.rlNextPrev.visibility = View.VISIBLE
                binding.llQuesNo.visibility = View.VISIBLE
                binding.rlTimer.visibility = View.VISIBLE
                binding.rlToolbar.setBackgroundResource(R.drawable.bg_header_y)
                getQuestionList()
            }
            else -> ""
        }
    }

    override fun setListsAndAdapters() {
        TODO("Not yet implemented")
    }

    override fun setListeners() {
        binding.imgBack.setOnClickListener { finish() }


        binding.txtCheck.setOnClickListener {
            if (binding.txtCheck.text == "Check") {
                if (selectedAnswer != null) {
                    answer?.let {
                        if (it == selectedAnswer)
                            setAnswerStatus(it, true)
                        else
                            setAnswerStatus(it, false)
                    }
                    enableOrDisableAllOptions(false)
                    binding.txtCheck.text = "Next"
                    questionIndex += 1
                } else
                    showToast(getString(com.app.elearning.R.string.please_select_an_option))
            } else {
                if (questionIndex < questionBankList.size) {
                    setUpQuestionAndOption(questionIndex)

                    selectAnOption(null)
                    binding.txtCheck.text = "Check"
                    binding.txtCheck.visibility = View.GONE
                    enableOrDisableAllOptions(true)
                    showAnswerStatus(false)
                } else {
                    showEndingDialog()
                }
            }
        }

        binding.txtNext.setOnClickListener {
            ++questionIndex
            if (questionIndex < questionBankList.size) {
                setUpQuestionAndOption(questionIndex)
                selectAnOption(null)
                enableOrDisableAllOptions(true)
                showAnswerStatus(false)

                if (!binding.txtPrev.isVisible)
                    binding.txtPrev.visibility = View.VISIBLE

            } else {
                showEndingDialog()
            }

        }

        binding.txtPrev.setOnClickListener {
            --questionIndex
            if (questionIndex >= 0) {
                setUpQuestionAndOption(questionIndex)
                selectAnOption(null)
                enableOrDisableAllOptions(true)
                showAnswerStatus(false)

                !binding.txtPrev.isVisible.let { true }

                if (questionIndex == 0) {
                    binding.txtPrev.visibility = View.GONE
                }
            }

        }


        binding.txtEndOrSubmit.setOnClickListener {
            showEndingDialog()
        }

        binding.apply {

            webOption1.setOnTouchListener(object : OnTouchListener {
                override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                    selectedAnswer = "A"
                    selectAnOption(rlOption1)
                    showCheckButton()

                    return false
                }

            })
            /*rlOption1.setOnClickListener {
                selectedAnswer = "A"
                selectAnOption(rlOption1)
                showCheckButton()
            }*/
            webOption2.setOnTouchListener(object : OnTouchListener {
                override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                    selectedAnswer = "B"
                    selectAnOption(rlOption2)
                    showCheckButton()

                    return false
                }

            })
            /*rlOption2.setOnClickListener {
                selectedAnswer = "B"
                selectAnOption(it)
                showCheckButton()
            }*/
            webOption3.setOnTouchListener(object : OnTouchListener {
                override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                    selectedAnswer = "C"
                    selectAnOption(rlOption3)
                    showCheckButton()

                    return false
                }

            })
            /*rlOption3.setOnClickListener {
                selectedAnswer = "C"
                selectAnOption(it)
                showCheckButton()
            }*/
            webOption4.setOnTouchListener(object : OnTouchListener {
                override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                    selectedAnswer = "D"
                    selectAnOption(rlOption4)
                    showCheckButton()

                    return false
                }

            })
            /*rlOption4.setOnClickListener {
                selectedAnswer = "D"
                selectAnOption(it)
                showCheckButton()
            }*/

        }


    }

    override fun initObservers() {
        viewModel.response.observe(this) { response ->
            //binding.progressBar.visibility = View.GONE\
            dismissProgressDialog()
            binding.llMain.visibility = View.VISIBLE
            when (response) {
                is NetworkResult.Success -> {
                    questionBankList = response.data?.data ?: ArrayList()
                    setUpQuestionAndOption(questionIndex) // as 0
                }
                is NetworkResult.Error -> {
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
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
                    practiceQuestionList = response.data?.data ?: ArrayList()
                    setUpQuestionAndOption(questionIndex) // as 0
                }
                is NetworkResult.Error -> {
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                }
                is NetworkResult.Loading -> {
                    // show a progress bar
                }
            }
        }
    }

    private fun showCheckButton() {
        if (!binding.txtCheck.isVisible &&
            intent.getIntExtra("module", 0) == MODULE_PRACTICE_SESSION
        ) {
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

    private fun selectAnOption(linearLayout: View?) {
        binding.apply {
            rlOption1.setBackgroundResource(R.drawable.bg_rect_gray_stroke)
            rlOption2.setBackgroundResource(R.drawable.bg_rect_gray_stroke)
            rlOption3.setBackgroundResource(R.drawable.bg_rect_gray_stroke)
            rlOption4.setBackgroundResource(R.drawable.bg_rect_gray_stroke)
            linearLayout?.let {
                it.setBackgroundResource(R.drawable.bg_rect_black_stroke)
            }
        }
    }

    var mDialog: Dialog? = null
    private fun showEndingDialog() {

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

        when {
            intent.getIntExtra("module", 0) == MODULE_PRACTICE_SESSION -> {
                dialogBinding.txtTitle.text = "End Practice Session"
                dialogBinding.txtMessage.text = "Are you sure you want to Exit?"
                dialogBinding.txtResume.text = "Resume"
                dialogBinding.txtExit.text = "Exit"
            }
            intent.getIntExtra("module", 0) == MODULE_TEST -> {
                dialogBinding.txtTitle.text = "Submit Test"
                dialogBinding.txtMessage.text = "Hey John, are you sure you want to end this test?"
                dialogBinding.txtResume.text = "Submit"
                dialogBinding.txtExit.text = "Cancel"
            }
            else -> ""
        }


        dialogBinding.txtResume.setOnClickListener {
            when {
                intent.getIntExtra("module", 0) == MODULE_PRACTICE_SESSION -> {
                    mDialog!!.dismiss()
                }
                intent.getIntExtra("module", 0) == MODULE_TEST -> {
                    Intent(this@PracticeQuestionActivity, TestResultActivity::class.java).also {
                        startActivity(it)
                    }
                }
                else -> ""
            }
        }

        dialogBinding.txtExit.setOnClickListener {
            if (intent.getIntExtra("module", 0) == MODULE_PRACTICE_SESSION) {
                val broadcast = Intent(JUMP_TO_SUBJECT_DETAIL)
                sendBroadcast(broadcast)
                finish()
            }
            mDialog!!.dismiss()

        }
        mDialog!!.show()
    }

    override fun onBackPressed() {
        if (mDialog != null && mDialog!!.isShowing) {
            mDialog!!.dismiss()
        } else {
            super.onBackPressed()
        }
    }

    private fun getQuestionList() {
        //binding.progressBar.visibility = View.VISIBLE
        showProgressDialog()
        viewModel.getQuestionBank()

    }

    private fun getQuestionListByChapterId() {
        //binding.progressBar.visibility = View.VISIBLE
        showProgressDialog()
        intent.getStringExtra("chapterIds")?.let { viewModel.getQuestionByChapterId(it) }

    }

    private fun setUpQuestionAndOption(index: Int) {
        binding.apply {
            /* setTextInHtmlForm(txtQues, questionBankList[index].name)

             setTextInHtmlForm(txtOption1, questionBankList[index].option1)
             setTextInHtmlForm(txtOption2, questionBankList[index].option2)
             setTextInHtmlForm(txtOption3, questionBankList[index].option3)
             setTextInHtmlForm(txtOption4, questionBankList[index].option4)*/
            var question: Question? = null
            if (questionBankList[index] is Question)
                question = questionBankList[index] as Question

            loadWebView(webOption1, questionBankList[index].option1)
            loadWebView(webOption2, questionBankList[index].option2)
            loadWebView(webOption3, questionBankList[index].option3)
            loadWebView(webOption4, questionBankList[index].option4)

            answer = questionBankList[index].correct.uppercase()
            answerString = questionBankList[index].answerDetails

            webView.isLongClickable = true
            webView.setOnLongClickListener {
                true
            }

            webView.settings.javaScriptEnabled = true
            webView.settings.loadWithOverviewMode = true

            // binding.formulaOne.text = questionBankList[index].name
//            webView.loadData(str, "text/html; charset=UTF-8", null)

            if (Build.VERSION.SDK_INT < 19) {
                webView.loadDataWithBaseURL(
                    "http://bar", "<script type='text/javascript' "
                            + "src='https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.7/MathJax.js?config=TeX-AMS-MML_HTMLorMML'>"
                            + "</script><math display='block'>" + "${questionBankList[index].name}" + "</math>",
                    "text/html", "utf-8", ""
                )
            } else {
                webView.loadDataWithBaseURL(
                    "http://bar", "<script type='text/javascript' "
                            + "src='https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.7/MathJax.js?config=TeX-AMS-MML_HTMLorMML'>"
                            + "</script><math display='block'>" + "${questionBankList[index].name}" + "</math>",
                    "text/html", "utf-8", ""
                )
            }
        }
    }

    private fun loadWebView(webView: WebView, string: String) {
        webView.isLongClickable = true
        webView.setOnLongClickListener {
            true
        }
        webView.settings.javaScriptEnabled = true
        webView.settings.loadWithOverviewMode = true
        webView.loadDataWithBaseURL(
            "http://bar", "<script type='text/javascript' "
                    + "src='https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.7/MathJax.js?config=TeX-AMS-MML_HTMLorMML'>"
                    + "</script><math display='block'>" + string + "</math>",
            "text/html", "utf-8", ""
        )
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
        unregisterReceiver(this.closingReceiver)
    }


}