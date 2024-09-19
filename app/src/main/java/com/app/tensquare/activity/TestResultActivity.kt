package com.app.tensquare.activity

import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.app.tensquare.adapter.TopicAdapter
import com.app.tensquare.databinding.ActivityTestResultBinding
import com.app.tensquare.ui.analysis.TopicsForImprovement
import com.app.tensquare.ui.questionbank.PracticeQuestionActivity
import com.app.tensquare.ui.questionbank.TestAnswerSubmission
import com.app.tensquare.utils.AppUtills
import com.app.tensquare.utils.JUMP_TO_SUBJECT_DETAIL
import com.app.tensquare.utils.MODULE_TEST_SOLUTION
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat
import javax.inject.Inject

private val decimalFormat = DecimalFormat("00")
private val decimalFormat1 = DecimalFormat("00.0")

@AndroidEntryPoint
class TestResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTestResultBinding

    @Inject
    lateinit var topicAdapter: TopicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val resultant = intent.getStringExtra("Resultant")

        AppUtills.disableSsAndRecording(this@TestResultActivity)

        val gson = Gson()
        val testAnswerSubmission = gson.fromJson(resultant, TestAnswerSubmission::class.java)
        println(testAnswerSubmission)

        binding.rvTopic.adapter = topicAdapter

        val topicList =
            testAnswerSubmission.topicsForImprovement as MutableList<TopicsForImprovement>
        /*val topicList = ArrayList<TopicsForImprovement>()
        for (i in 0..2) {
            val topicsForImprovement = TopicsForImprovement(
                _id = Random(1).nextInt().toString(),
                name = "Lorem Ipsum is simply dummy text "
            )
            topicList.add(topicsForImprovement)
        }*/
        topicAdapter.submitList(topicList)

        binding.txtViewSolutions.setOnClickListener {
            val broadcast = Intent(JUMP_TO_SUBJECT_DETAIL)
            sendBroadcast(broadcast)
            Intent(this@TestResultActivity, PracticeQuestionActivity::class.java).also {
                it.putExtra("module", MODULE_TEST_SOLUTION)
                it.putExtra("question_ids", Gson().toJson(testAnswerSubmission.questionIds))
                startActivity(it)
            }
            finish()
        }

        binding.apply {
            txtCorrect.text = "${testAnswerSubmission.userCorrectAnswer}. Correct"
            txtIncorrect.text = "${testAnswerSubmission.userInCorrectAnswer}. Incorrect"
            txtUnanswered.text = "${testAnswerSubmission.userUnAnswered}. Unanswered"

            txtCorrectCount.text = "${testAnswerSubmission.userCorrectAnswer}"
            txtTotalQuesCount.text = "${testAnswerSubmission.questionIds.size}"
            /*txtCorrectPercent.text =
                "${decimalFormat1.format(testAnswerSubmission.percentOfCorrectAnswer)}"*/
            txtCorrectPercent.text = testAnswerSubmission.percentOfCorrectAnswer.toString()

            val totalTimeSplit = testAnswerSubmission.userSpendTime.split(" ")

            txtTotalTime.text = totalTimeSplit[0]
            /*getTimeToMinutes(testAnswerSubmission.userSpendTime)*/
            txtTimeType1.text = totalTimeSplit[1]
            /*getTimeToMinutes(testAnswerSubmission.userSpendTime)*/

            val totalAvgQuesSplit = testAnswerSubmission.averagePerQuestion.split(" ")
            txtAvgTimePerQues.text = totalAvgQuesSplit[0]
            txtTimeType2.text = totalAvgQuesSplit[1]

        }

        Handler(Looper.getMainLooper()).postDelayed(
            {
                try {
                    ObjectAnimator.ofInt(
                        binding.progressScore,
                        "progress",
                        testAnswerSubmission.userCorrectAnswer
                    ).start()
                    ObjectAnimator.ofInt(
                        binding.progPercentage,
                        "progress",
                        testAnswerSubmission.percentOfCorrectAnswer.toInt()
                    ).start()
                    /*ObjectAnimator.ofInt(
                            binding.progTotalTime,
                            "progress",
                            getTimeToMinutes(testAnswerSubmission.userSpendTime).toInt()
                        ).start()*/
                    /*ObjectAnimator.ofInt(
                            binding.progAvgTimePerQues,
                            "progress",
                            testAnswerSubmission.averagePerQuestion
                        ).start()*/
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, 500
        )
        /*Handler(Looper.getMainLooper()).postDelayed(
            {
                ObjectAnimator.ofInt(binding.progressScore, "progress", 3).start()
                ObjectAnimator.ofInt(binding.progPercentage, "progress", 20).start()
                ObjectAnimator.ofInt(binding.progTotalTime, "progress", 40).start()
                ObjectAnimator.ofInt(binding.progAvgTimePerQues, "progress", 7).start()
            }, 500
        )*/

        binding.imgBack.setOnClickListener {
            val broadcast = Intent(JUMP_TO_SUBJECT_DETAIL)
            sendBroadcast(broadcast)
            finish()
        }

    }

    private fun getTimeToMinutes(time: String): String {
        return time.split(":")[0]

        //val format = SimpleDateFormat("mm:ss")
        /*val timeMills = format.parse(time).time
        //val seconds = diff / 1000
        //val minutes = seconds / 60
        //val hours = minutes / 60

        var seconds = decimalFormat.format(timeMills / 1000)
        val minutes = decimalFormat.format(seconds.toLong() / 60)
        seconds = decimalFormat.format(seconds.toLong() % 60)
        return "$minutes"*/
    }

    override fun onBackPressed() {
        val broadcast = Intent(JUMP_TO_SUBJECT_DETAIL)
        sendBroadcast(broadcast)
        finish()
    }
}