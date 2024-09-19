package com.app.tensquare.ui.subscription

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.viewModels
import com.app.tensquare.R
import com.app.tensquare.adapter.EnrollmentSubjectAdapter
import com.app.tensquare.base.AppBaseActivity
import com.app.tensquare.databinding.*
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.ui.home.HomeViewModel
import com.app.tensquare.ui.initialsetup.ClassData
import com.app.tensquare.ui.initialsetup.InitialViewModel
import com.app.tensquare.ui.login.LoginActivity
import com.app.tensquare.ui.transaction.CreateOrderData
import com.app.tensquare.utils.ACCESS_TOKEN_EXPIRED
import com.app.tensquare.utils.AppUtills
import com.app.tensquare.utils.LOG_OUT
import com.app.tensquare.utils.OTHER_DEVISE_LOGIN
import com.app.tensquare.utils.REFRESH_TOKEN_EXPIRED
import com.app.tensquare.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EnrolmentPlanPricingNewActivity : AppBaseActivity() {
    private lateinit var binding: ActivityEnrolmentPlanPricingNewBinding
    private lateinit var subjectSelectionAdapter: EnrollmentSubjectAdapter

    //private var subjectList = mutableListOf<SubjectToEnrol>()
    private val viewModel: SubscriptionViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var classList: List<ClassData>

    //private var subjectList = mutableListOf<SubjectData>()
    private var subjectList = mutableListOf<EnrolmentSubject>()

    private val request: HashMap<String, String> by lazy { HashMap() }

    private var selectedPlanId: String? = null

    private var selectedPlan: EnrolmentSubject? = null

    private var payableAmt: Double = 0.0

    private var subscriptionData: SubscriptionDetail? = null

    private lateinit var enrolmentId: String

    private lateinit var preOrderData: CreateOrderData

    companion object{
        var txtContinue : TextView? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnrolmentPlanPricingNewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Checkout.preload(applicationContext)

        txtContinue = binding.txtContinue

        initObservers()
        init()
        setListsAndAdapters()
        setListeners()
    }

    /*private fun getClassList() {
        showProgressDialog()
        viewModel1.getClassList(prefs.getSelectedLanguageId().toString())
    }

    private fun getSubjectList() {
        showProgressDialog()
        viewModel1.getSubjectList(prefs.getSelectedLanguageId().toString())
    }*/

    override fun init() {
        /*binding.txtActualPrice.also {
            it.paintFlags = it.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        }*/

        /*if (intent.getIntExtra("SELECTED_PLAN", 0) == ENROLLMENT_SUBJECT_WISE) {
            binding.txtTitle.text = "Subject wise pricing"
            binding.rvSubject.visibility = View.VISIBLE*/

        request["classId"] = prefs.getSelectedClassId().toString()

        showProgressDialog()
        viewModel.getSubscriptionDetails(request)

        //getSubjectList()
        //getDataFromServer()
        /*} else {
            binding.txtTitle.text = "Class wise pricing"
            binding.rvSubject.visibility = View.GONE
            showProgressDialog()
            getClassList()
        }*/
    }

    override fun setListsAndAdapters() {
        binding.rvSubject.apply {
            subjectSelectionAdapter =
                EnrollmentSubjectAdapter(this@EnrolmentPlanPricingNewActivity) { items: MutableList<EnrolmentSubject>, checked: Boolean, subject: EnrolmentSubject ->
                    var preSelectedItem = items.find { it.isSelected }
                    val preSelectedItemIndex = items.indexOf(preSelectedItem)
                    if (preSelectedItem != null) {
                        preSelectedItem = preSelectedItem.copy(isSelected = false)
                        items[preSelectedItemIndex] = preSelectedItem
                    }


                    var element = items.first {
                        it._id == subject._id
                    }
                    val index = items.indexOf(element)

                    //items[position].isSelected = !items[position].isSelected
                    if (element != null) {
                        element = element.copy(isSelected = true)
                    }

                    if (element != null) {
                        items[index] = element
                    }

                    selectedPlanId = element._id
                    selectedPlan = element

                    (binding.rvSubject.adapter as EnrollmentSubjectAdapter).submitList(items)

                    /*val subjectStringBuilder = StringBuilder()
                    items.forEach {
                        if (it.isSelected) {
                            if (subjectStringBuilder.isNotEmpty())
                                subjectStringBuilder.append(",${it._id}")
                            else
                                subjectStringBuilder.append(it._id)
                        }
                    }

                    request["subjectId"] = subjectStringBuilder.toString()*/

                    //request["subjectId"] = items.first { it.isSelected }._id

                    /*if (request.isNotEmpty()) {
                        showProgressDialog()
                        viewModel.getSubscriptionDetails(request)
                    }*/
                }
            adapter = subjectSelectionAdapter
        }
    }

    override fun setListeners() {
        binding.apply {
            imgBack.setOnClickListener { finish() }

            txtContinue.setOnClickListener {
                if (selectedPlanId != null)
                    Intent(
                        this@EnrolmentPlanPricingNewActivity,
                        SelectedPlanPricingActivity::class.java
                    ).also {
                        it.putExtra("plan_detail", Gson().toJson(selectedPlan).toString())
                        startActivity(it)
                    }
                else
                    showToast(getString(R.string.select_a_plan))
            }
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

                        showProgressDialog()
                        viewModel.getSubscriptionDetails(request)
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

        viewModel.subscriptionDetails.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    //if (response.data?.status == STATUS_SUCCESS) {
                    val subjectListData = response.data as MutableList<EnrolmentSubject>

                    subjectList.addAll(subjectListData)

                    /*if (subjectListData.size > 1) {
                        subjectList.add(
                            EnrolmentSubject(
                                _id = "",
                                className = subjectListData[0].className,
                                crossAmount = subjectListData[0].crossAmount + subjectListData[1].crossAmount,
                                finalAmount = subjectListData[0].finalAmount + subjectListData[1].finalAmount,
                                subjectName = "${subjectListData[0].subjectName} + ${subjectListData[1].subjectName}"
                            )
                        )
                    }*/

                    subjectSelectionAdapter.submitList(subjectList)
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
        /*viewModel1.classListResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    //if (response.data?.status == STATUS_SUCCESS) {
                    classList = response.data?.classList!!
                    val classNameList = ArrayList<String>()
                    *//*classNameList.add("Select Class")
                    for (item in classList.map { it.name }) {
                        classNameList.add(item)
                    }*//*
                    binding.llMain.visibility = View.VISIBLE
                }
                is NetworkResult.Error -> {
                    showToast(response.message)
                }
                is NetworkResult.Loading -> {
                    // show a progress bar
                }
            }
        }
        viewModel1.subjectListResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    subjectList = response.data!! as MutableList
                    subjectSelectionAdapter.submitList(subjectList)
                    binding.llMain.visibility = View.VISIBLE
                }
                is NetworkResult.Error -> {
                    showToast(response.message)
                }
                is NetworkResult.Loading -> {
                    // show a progress bar
                }
            }
        }*/

    }


}