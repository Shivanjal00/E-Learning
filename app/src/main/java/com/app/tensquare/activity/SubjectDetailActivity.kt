package com.app.tensquare.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.app.tensquare.databinding.ActivitySubjectDetailBinding
import com.app.tensquare.ui.chapter.ChaptersInSubjectActivity
import com.app.tensquare.ui.notes.NotesInSubjectActivity
import com.app.tensquare.ui.paper.ModelPaperInSubjectActivity
import com.app.tensquare.ui.paper.PreviousPaperInSubjectActivity
import com.app.tensquare.ui.session.PracticeSessionInSubjectActivity
import com.app.tensquare.utils.*
//import kotlinx.android.synthetic.main.activity_intro_slider.view.viewPager2IntroSlider

class SubjectDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySubjectDetailBinding
    private var revisionVideoStatus = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppUtills.setLan(this)
        binding = ActivitySubjectDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppUtills.disableSsAndRecording(this@SubjectDetailActivity)
//        showToast(intent.getIntExtra(SUBJECT, 0).toString())
        setSubjectTile()

        revisionVideoStatus = intent.getIntExtra("revisionVideoStatus",1)
        binding.llRevisionVideo.visibility = if (revisionVideoStatus != 0) View.VISIBLE else View.GONE

        binding.txtSubjectName.text = intent.getStringExtra("subject_name")

        binding.imgBack.setOnClickListener { finish() }

        binding.llChapters.setOnClickListener {
            Intent(this, ChaptersInSubjectActivity::class.java).also {
                it.putExtra("subject_name", intent.getStringExtra("subject_name"))
                it.putExtra("subject_id", intent.getStringExtra("subject_id"))
                it.putExtra("module", MODULE_CHAPTER)
                it.putExtra("revisionVideoStatus", revisionVideoStatus)
                it.putExtra(SUBJECT, intent.getIntExtra(SUBJECT, 0))
                startActivity(it)
            }
        }
        binding.llRevisionVideo.setOnClickListener {
            Intent(this, RevisionVideoInSubjectActivity::class.java).also {
                it.putExtra("subject_name", intent.getStringExtra("subject_name"))
                it.putExtra("subject_id", intent.getStringExtra("subject_id"))
                it.putExtra("module", MODULE_REVISION_VIDEO)
                it.putExtra(SUBJECT, intent.getIntExtra(SUBJECT, 0))
                startActivity(it)
            }
        }
        binding.llPracticeSession.setOnClickListener {
            Intent(this, PracticeSessionInSubjectActivity::class.java).also {
                it.putExtra("subject_name", intent.getStringExtra("subject_name"))
                it.putExtra("subject_id", intent.getStringExtra("subject_id"))
                it.putExtra("module", MODULE_PRACTICE_SESSION)
                it.putExtra(SUBJECT, intent.getIntExtra(SUBJECT, 0))
                startActivity(it)
            }
        }
        binding.llTest.setOnClickListener {
            Intent(this, PracticeSessionInSubjectActivity::class.java).also {
                it.putExtra("subject_name", intent.getStringExtra("subject_name"))
                it.putExtra("subject_id", intent.getStringExtra("subject_id"))
                it.putExtra("module", MODULE_TEST)
                it.putExtra(SUBJECT, intent.getIntExtra(SUBJECT, 0))
                startActivity(it)
            }
        }
        binding.llNotesResources.setOnClickListener {
            Intent(this, NotesInSubjectActivity::class.java).also {
                it.putExtra("subject_name", intent.getStringExtra("subject_name"))
                it.putExtra("subject_id", intent.getStringExtra("subject_id"))
                it.putExtra("module", MODULE_NOTE_RESOURCE)
                it.putExtra("inMain", "inMain")
                it.putExtra(SUBJECT, intent.getIntExtra(SUBJECT, 0))
                startActivity(it)
            }
        }
        binding.llModelPaper.setOnClickListener {
            Intent(this, ModelPaperInSubjectActivity::class.java).also {
                it.putExtra("subject_name", intent.getStringExtra("subject_name"))
                it.putExtra("subject_id", intent.getStringExtra("subject_id"))
                it.putExtra("module", MODULE_MODEL_PAPER)
                it.putExtra(SUBJECT, intent.getIntExtra(SUBJECT, 0))
                startActivity(it)
            }
        }
        binding.llPreviousYearPaper.setOnClickListener {
            Intent(this, PreviousPaperInSubjectActivity::class.java).also {
                it.putExtra("subject_name", intent.getStringExtra("subject_name"))
                it.putExtra("subject_id", intent.getStringExtra("subject_id"))
                it.putExtra("module", MODULE_PREVIOUS_YEAR_PAPER)
                it.putExtra(SUBJECT, intent.getIntExtra(SUBJECT, 0))
                startActivity(it)
            }
        }
    }

    private fun setSubjectTile() {
        binding.apply {
            when (intent.getIntExtra(SUBJECT, 0)) {
                SUBJECT_MATHS -> {
                    imgSubject.setImageDrawable(resources.getDrawable(com.app.tensquare.R.drawable.img_maths))
                    llSubjectTile.background =
                        resources.getDrawable(com.app.tensquare.R.drawable.bg_maths)

                /*    val paddingDp = 30
                    val density: Float = this@SubjectDetailActivity.resources.displayMetrics.density
                    val paddingPixel: Int = (paddingDp.toInt() * density).toInt()
                    imgSubject.setPadding(
                        paddingPixel,
                        paddingPixel,
                        paddingPixel,
                        paddingPixel
                    )   */
                }
                SUBJECT_SCIENCE -> {
                    imgSubject.setImageDrawable(resources.getDrawable(com.app.tensquare.R.drawable.img_science))
                    llSubjectTile.background =
                        resources.getDrawable(com.app.tensquare.R.drawable.bg_latest_update)

//                   val paddingDp = 30
                   val paddingDp = 25
                    val density: Float = this@SubjectDetailActivity.resources.displayMetrics.density
                    val paddingPixel: Int = (paddingDp.toInt() * density).toInt()
                    imgSubject.setPadding(
                        paddingPixel,
                        paddingPixel,
                        paddingPixel,
                        paddingPixel
                    )
                }
                SUBJECT_PHYSICS -> {

                    imgSubject.setImageDrawable(resources.getDrawable(com.app.tensquare.R.drawable.img_science))
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
                    imgSubject.setImageDrawable(resources.getDrawable(com.app.tensquare.R.drawable.san_img_2))
                    llSubjectTile.background =
                        resources.getDrawable(com.app.tensquare.R.drawable.bg_science)

               /*     val paddingDp = 30
                    val density: Float = this@SubjectDetailActivity.resources.displayMetrics.density
                    val paddingPixel: Int = (paddingDp.toInt() * density).toInt()
                    imgSubject.setPadding(
                        paddingPixel,
                        paddingPixel,
                        paddingPixel,
                        paddingPixel
                    )   */

                }
                SUBJECT_HINDI -> {
                    imgSubject.setImageDrawable(resources.getDrawable(com.app.tensquare.R.drawable.hindi_img_2))
                    llSubjectTile.background =
                        resources.getDrawable(com.app.tensquare.R.drawable.bg_biology)

               /*     val paddingDp = 30
                    val density: Float = this@SubjectDetailActivity.resources.displayMetrics.density
                    val paddingPixel: Int = (paddingDp.toInt() * density).toInt()
                    imgSubject.setPadding(
                        paddingPixel,
                        paddingPixel,
                        paddingPixel,
                        paddingPixel
                    )   */

                }
                SUBJECT_SOCIAL_STUDIES -> {
                    imgSubject.setImageDrawable(resources.getDrawable(com.app.tensquare.R.drawable.ss_img_2))
                    llSubjectTile.background =
                        resources.getDrawable(com.app.tensquare.R.drawable.bg_previous_year_papers)

                    val paddingDp = 25
                    val density: Float = this@SubjectDetailActivity.resources.displayMetrics.density
                    val paddingPixel: Int = (paddingDp.toInt() * density).toInt()
                    imgSubject.setPadding(
                        paddingPixel,
                        paddingPixel,
                        paddingPixel,
                        paddingPixel
                    )

                }
                SUBJECT_ENGLISH -> {
                    imgSubject.setImageDrawable(resources.getDrawable(com.app.tensquare.R.drawable.eng_img_2))
                    llSubjectTile.background =
                        resources.getDrawable(com.app.tensquare.R.drawable.bg_maths)

                    val paddingDp = 25
                    val density: Float = this@SubjectDetailActivity.resources.displayMetrics.density
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

}