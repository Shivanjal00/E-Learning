package com.app.tensquare.ui.subscription

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.app.tensquare.R
import com.app.tensquare.databinding.ActivityEnrolmentPlanBinding
import com.app.tensquare.utils.AppUtills
import com.app.tensquare.utils.ENROLLMENT_CLASS_WISE
import com.app.tensquare.utils.ENROLLMENT_SUBJECT_WISE
import com.app.tensquare.utils.showToast

class EnrolmentPlanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEnrolmentPlanBinding
    private var selectedPlan: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnrolmentPlanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppUtills.disableSsAndRecording(this@EnrolmentPlanActivity)

//        showToast("1")

        binding.apply {
            imgBack.setOnClickListener { finish() }

            llSubWisePricing.setOnClickListener {
                it.setBackgroundResource(R.drawable.bg_selected_plan)
                binding.llClassWisePricing.setBackgroundResource(R.drawable.bg_rect_gray_stroke)
                selectedPlan = ENROLLMENT_SUBJECT_WISE
            }

            llClassWisePricing.setOnClickListener {
                it.setBackgroundResource(R.drawable.bg_selected_plan)
                binding.llSubWisePricing.setBackgroundResource(R.drawable.bg_rect_gray_stroke)
                selectedPlan = ENROLLMENT_CLASS_WISE
            }

            txtContinue.setOnClickListener {
                if (selectedPlan != null)
                    Intent(
                        this@EnrolmentPlanActivity,
                        EnrolmentPlanPricingActivity::class.java
                    ).also {
                        it.putExtra("SELECTED_PLAN", selectedPlan)
                        startActivity(it)
                    }
                else
                    showToast(getString(com.app.tensquare.R.string.select_a_plan))
            }
        }

    }


}