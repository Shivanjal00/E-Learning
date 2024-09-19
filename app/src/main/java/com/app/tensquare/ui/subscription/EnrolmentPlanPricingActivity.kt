package com.app.tensquare.ui.subscription

import android.app.Activity
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.app.tensquare.activity.ThankYouActivity
import com.app.tensquare.adapter.SubjectSelectionAdapter
import com.app.tensquare.base.AppBaseActivity
import com.app.tensquare.databinding.*
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.ui.initialsetup.ClassData
import com.app.tensquare.ui.initialsetup.InitialViewModel
import com.app.tensquare.ui.initialsetup.SubjectData
import com.app.tensquare.ui.transaction.CreateOrderData
import com.app.tensquare.ui.transaction.SignVerificationRequest
import com.app.tensquare.utils.AppUtills
import com.app.tensquare.utils.ENROLLMENT_SUBJECT_WISE
import com.app.tensquare.utils.OTHER_DEVISE_LOGIN
import com.app.tensquare.utils.showToast
import com.google.gson.Gson
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import dagger.hilt.android.AndroidEntryPoint
//import kotlinx.android.synthetic.main.activity_enrolment_plan_pricing.*
import org.json.JSONObject

@AndroidEntryPoint
class EnrolmentPlanPricingActivity : AppBaseActivity(), PaymentResultWithDataListener {
    private lateinit var binding: ActivityEnrolmentPlanPricingBinding
    private lateinit var subjectSelectionAdapter: SubjectSelectionAdapter

    //private var subjectList = mutableListOf<SubjectToEnrol>()
    private val viewModel: SubscriptionViewModel by viewModels()
    private val viewModel1: InitialViewModel by viewModels()
    private lateinit var classList: List<ClassData>
    private var subjectList = mutableListOf<SubjectData>()

    private val request: HashMap<String, String> by lazy { HashMap() }

    private var payableAmt: Double = 0.0

    private var subscriptionData: SubscriptionDetail? = null

    private lateinit var enrolmentId: String

    private lateinit var preOrderData: CreateOrderData




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnrolmentPlanPricingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Checkout.preload(applicationContext);

        initObservers()
        init()
        setListeners()
    }

    private fun getClassList() {
        showProgressDialog()
        viewModel1.getClassList(prefs.getSelectedLanguageId().toString())
    }

    private fun getSubjectList() {
        showProgressDialog()
        viewModel1.getSubjectList(prefs.getSelectedLanguageId().toString())
    }

    override fun init() {
        binding.txtActualPrice.also {
            it.paintFlags = it.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }

        if (intent.getIntExtra("SELECTED_PLAN", 0) == ENROLLMENT_SUBJECT_WISE) {
            binding.txtTitle.text = "Subject wise pricing"
            binding.rvSubject.visibility = View.VISIBLE
            binding.llClass.visibility = View.GONE

            binding.rvSubject.apply {
                subjectSelectionAdapter =
                    SubjectSelectionAdapter { items: MutableList<SubjectData>, checked: Boolean, subject: SubjectData ->
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

                        (binding.rvSubject.adapter as SubjectSelectionAdapter).submitList(items)

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

                        request["subjectId"] = items.first { it.isSelected }._id

                        if (request.isNotEmpty()) {
                            showProgressDialog()
                            viewModel.getSubscriptionDetails(request)
                        }
                    }
                adapter = subjectSelectionAdapter
            }

            getSubjectList()
            //getDataFromServer()
        } else {
            binding.txtTitle.text = "Class wise pricing"
            binding.rvSubject.visibility = View.GONE
            binding.llClass.visibility = View.VISIBLE
            showProgressDialog()
            getClassList()
        }
    }

    override fun setListsAndAdapters() {
        TODO("Not yet implemented")
    }

    override fun setListeners() {
        binding.apply {
            edtCoupon.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(p0: Editable?) {
                    txtApply.text = "Apply"
                    payableAmt = subscriptionData?.finalAmount?.toDouble()!!
                    txtPayableAmt.text = "₹ $payableAmt"
                    txtDiscAmt.text = "- ₹ 0"
                }

            })

            txtApply.setOnClickListener {
                if (txtApply.text.toString() != "Applied") {
                    showProgressDialog()
//                    viewModel.applyCoupon(edtCoupon.text.toString())
                }
            }

            imgBack.setOnClickListener { finish() }

            rlHighSchool.setOnClickListener {
                if (!imgHighSchool.isVisible) {
                    imgHighSchool.visibility = View.VISIBLE
                    imgIntermediate.visibility = View.GONE
                    //imgHighSchool.isVisible = !imgHighSchool.isVisible

                    /*if (imgHighSchool.isVisible)
                    selectedClasses.add(classList.first { it.name.uppercase() == "10TH" }._id)
                else
                    selectedClasses.remove(classList.first { it.name.uppercase() == "10TH" }._id)
                val stringBuilder = StringBuilder()
                selectedClasses.forEach {
                    stringBuilder.append(
                        if (stringBuilder.isNotEmpty())
                            ",$it"
                        else
                            it
                    )
                    println("it" + selectedClasses.size)
                }

                request["classId"] = stringBuilder.toString()
                println("request" + request)
                println(selectedClasses.toArray().toString())

                if (request.isNotEmpty())
                    showProgressDialog()
                    viewModel.getSubscriptionDetails(request)*/

                    request["classId"] = classList.first { it.name.uppercase() == "10TH" }._id
                    showProgressDialog()
                    viewModel.getSubscriptionDetails(request)
                }
            }

            rlIntermediate.setOnClickListener {
                if (!imgIntermediate.isVisible) {
                    imgHighSchool.visibility = View.GONE
                    imgIntermediate.visibility = View.VISIBLE
                    //imgIntermediate.isVisible = !imgIntermediate.isVisible
                    //rlIntermediate.setBackgroundResource(if (imgIntermediate.isVisible) R.drawable.bg_rect_black_stroke else R.drawable.bg_rect_gray_stroke)


                    /*if (imgIntermediate.isVisible)
                    selectedClasses.add(classList.first { it.name.uppercase() == "12TH" }._id)
                //classes.append(classList.first { it.name.uppercase() == "10TH" }._id)
                else
                    selectedClasses.remove(classList.first { it.name.uppercase() == "12TH" }._id)
                //classes.filter { it.classList.first { it.name.uppercase() == "10TH" }._id }

                val stringBuilder = StringBuilder()
                selectedClasses.forEach {
                    stringBuilder.append(
                        if (stringBuilder.isNotEmpty())
                            ",$it"
                        else
                            it
                    )

                }

                request["classId"] = stringBuilder.toString()

                if (request.isNotEmpty())
                    showProgressDialog()
                viewModel.getSubscriptionDetails(request)*/


                    request["classId"] = classList.first { it.name.uppercase() == "12TH" }._id
                    showProgressDialog()
                    viewModel.getSubscriptionDetails(request)
                }
            }

            txtSubscribe.setOnClickListener {
                //startPayment()
                /*Intent(this@EnrolmentPlanPricingActivity, PaymentMethodActivity::class.java).also {
                    startActivity(it)
                }*/
                showProgressDialog()
//                viewModel.createOrder(payableAmt.toString(), enrolmentId)
            }
        }
    }

    override fun initObservers() {
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
        }*/
        /*viewModel1.subjectListResponse.observe(this) { response ->
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
        /*viewModel.subscriptionDetails.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                   *//* subscriptionData = response.data?.data as SubscriptionDetail
                    payableAmt = subscriptionData!!.finalAmount.toDouble()
                    binding.apply {
                        enrolmentId = subscriptionData!!._id
                        txtActualPrice.text = "₹ ${subscriptionData!!.crossAmount.toDouble()}"
                        txtTotalSum.text = "₹ ${subscriptionData!!.finalAmount.toDouble()}"
                        txtPayableAmt.text = "₹ ${subscriptionData!!.finalAmount.toDouble()}"
                        llAmtDetail.visibility = View.VISIBLE
                    }*//*
                }
                is NetworkResult.Error -> {
                    showToast(response.message)
                    binding.llAmtDetail.visibility = View.GONE
                }
                is NetworkResult.Loading -> {
                    // show a progress bar
                }
            }
        }*/
        viewModel.createOrder.observe(this) { response ->
            dismissProgressDialog()
            /*when (response) {
                is NetworkResult.Success -> {
                    preOrderData = response.data?.data as CreateOrderData
                    startPayment(preOrderData)

                }
                is NetworkResult.Error -> {
                    showToast(response.message)
                    binding.llAmtDetail.visibility = View.GONE
                }
                is NetworkResult.Loading -> {
                    // show a progress bar
                }
            }*/
        }

        viewModel.verifySignature.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    Intent(this@EnrolmentPlanPricingActivity, ThankYouActivity::class.java).also {
                        it.putExtra("ACTION", "PAYMENT")
                        startActivity(it)
                    }
                }
                is NetworkResult.Error -> {
                    if (response.message != OTHER_DEVISE_LOGIN) {
                        showToast(response.message)
                    }
                    binding.llAmtDetail.visibility = View.GONE
                }
                is NetworkResult.Loading -> {
                    // show a progress bar
                }
            }
        }

        viewModel.applyCouponResponse.observe(this) { response ->
            dismissProgressDialog()
            /*when (response) {
                is NetworkResult.Success -> {
                    binding.apply {
                        val data = response.data?.data
                        txtApply.text = "Applied"
                        var discountAmt = 0.0
                        if (data?.isPercent == true) {
                            val finalAmt = subscriptionData?.finalAmount?.toDouble()
                            if (finalAmt != null) {
                                discountAmt = (finalAmt.times(data.amount)).div(100.0)
                            }
                        } else {
                            discountAmt = data?.amount.toString().toDouble()
                        }
                        txtDiscAmt.text = "- ₹ $discountAmt"

                        payableAmt = subscriptionData?.finalAmount?.minus(discountAmt)!!
                        txtPayableAmt.text = "₹ $payableAmt"

                    }
                }
                is NetworkResult.Error -> {
                    showToast(response.message)
                    binding.llAmtDetail.visibility = View.GONE
                }
                is NetworkResult.Loading -> {
                    // show a progress bar
                }
            }*/
        }

    }

/*
    private fun createOrder() {
        try {
            val razorpay = RazorpayClient("rzp_test_FY1Vnlad5QTd1X", "pXvMuBMhGl1g1LzJ6yzJ7RUl")

            val orderRequest = JSONObject()
            orderRequest.put("amount", 50000) // amount in the smallest currency unit
            orderRequest.put("currency", "INR")
            orderRequest.put("receipt", "order_rcptid_11")

            val order = razorpay.Orders.create(orderRequest)
            } catch (e: RazorpayException) {
            // Handle Exception
            println(e.getMessage());
        }
    }
*/


    private fun startPayment(preOrderData: CreateOrderData) {
        val activity: Activity = this
        val co = Checkout()
        co.setKeyID(preOrderData.access_key)

        try {
            val options = JSONObject()
            options.put("name", "E-learning")
            options.put(
                "description",
                "Enrolment"
            )            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png")
            options.put("theme.color", "#08735F")
            options.put("currency", "INR")
            options.put("order_id", preOrderData.razorpayOrderId)
            options.put("amount", "${preOrderData.amount * 100}")//pass amount in currency subunits

            val retryObj = JSONObject()
            retryObj.put("enabled", true)
            retryObj.put("max_count", 4)
            options.put("retry", retryObj)

            val prefill = JSONObject()
            prefill.put("email", "gaurav.kumar@example.com")
            prefill.put("contact", "9876543210")

            options.put("prefill", prefill)
            co.open(activity, options)
        } catch (e: Exception) {
            showToast(getString(com.app.tensquare.R.string.error_in_payment) + e.message)
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        Log.e("PaymentSuccess***", Gson().toJson(p1).toString())
        /*ntent(this@EnrolmentPlanPricingActivity, ThankYouActivity::class.java).also {
            it.putExtra("ACTION", "PAYMENT")
            startActivity(it)
        }
        showToast(p0)*/

        val signVerificationRequest = p1?.let {
            SignVerificationRequest(
                enrollmentId = enrolmentId,
                razorpayOrderId = it.orderId,
                razorpayPaymentId = it.paymentId,
                razorpaySignature = it.signature,
                secret = preOrderData.secret_key,
                promoCode = preOrderData.promoCode,
                promoAmount = preOrderData.promoAmount,
                finalAmount = preOrderData.finalAmount
            )
        }

        if (signVerificationRequest != null) {
            viewModel.verifySignature(signVerificationRequest)
        }
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        Log.e("PaymentFailure***", Gson().toJson(p2).toString())
        showToast(p1)
    }




/*
    private fun getDataFromServer() {
        subjectList.add(
            SubjectToEnrol(
                id = 1,
                name = "Maths",
                image = R.drawable.img_physics,
                bg = R.drawable.bg_maths
            )
        )

        subjectList.add(
            SubjectToEnrol(
                id = 2,
                name = "Biology",
                image = R.drawable.img_biology,
                bg = R.drawable.bg_biology
            )
        )

        subjectList.add(
            SubjectToEnrol(
                id = 3,
                name = "Physics",
                image = R.drawable.img_physics,
                bg = R.drawable.bg_physics
            )
        )

        subjectList.add(
            SubjectToEnrol(
                id = 4,
                name = "Chemistry",
                image = R.drawable.img_chemistry,
                bg = R.drawable.bg_chemistry
            )
        )

        subjectSelectionAdapter.submitList(subjectList)
    }
*/


}