package com.app.tensquare.ui.subscription

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.app.tensquare.R
import com.app.tensquare.activity.ThankYouActivity
import com.app.tensquare.base.AppBaseActivity
import com.app.tensquare.base.SharedPrefManager
import com.app.tensquare.constants.NetworkConstants
import com.app.tensquare.databinding.*
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.ui.home.HomeViewModel
import com.app.tensquare.ui.login.LoginActivity
import com.app.tensquare.ui.profile.ProfileViewModel
import com.app.tensquare.ui.profile.State
import com.app.tensquare.ui.transaction.CreateOrderData
import com.app.tensquare.ui.transaction.SignVerificationRequest
import com.app.tensquare.utils.*
import com.google.gson.Gson
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.util.Locale


@AndroidEntryPoint
class SelectedPlanPricingActivity : AppBaseActivity(), PaymentResultWithDataListener {
    private lateinit var binding: ActivitySelectedPlanPricingBinding

    private val viewModel: SubscriptionViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()

    private val request: HashMap<String, String> by lazy { HashMap() }

    private var payableAmt: Double = 0.0
    private var discountAmt: Double = 0.0
    private var totalSumAmt: Double = 0.0

    /*private var subscriptionData: SubscriptionDetail? = null*/

    private lateinit var enrolmentId: String
    private var sendCode: String = ""
    private var subjectName: String = ""
    private var stateId: String = ""

    private lateinit var preOrderData: CreateOrderData

    private var planDetailJSON = JSONObject()

    private lateinit var preferences: SharedPrefManager

    private var deviceId: String? = ""
    private var requestQueue_token: RequestQueue? = null
    private var couponDialog: Dialog? = null
    private var couponDialogBinding: CouponCodeAndStateDialogLayoutBinding? = null
    private var stateList: ArrayList<State> = arrayListOf(State("", "Select State"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectedPlanPricingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Checkout.preload(applicationContext)

        this.preferences = prefs

        try {
            deviceId = AppUtills.getDeviceId(this@SelectedPlanPricingActivity)
            Log.e("DeviceId  ==> ", deviceId.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        initObservers()
        init()
        setListeners()


    }

    private fun showCouponDialog(
        stateList: List<State>,
        onCouponApplied: (String,stateId:String) -> Unit
    ){
        var statePosition = 0
        couponDialog = Dialog(this)
        couponDialogBinding = CouponCodeAndStateDialogLayoutBinding.inflate(layoutInflater)
        couponDialog?.setContentView(couponDialogBinding!!.root)
        couponDialog?.setCanceledOnTouchOutside(true)
        couponDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        couponDialog?.window?.setLayout(
            (getScreenWidth(this) * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val adapter = ArrayAdapter(
            this@SelectedPlanPricingActivity,
            R.layout.row_class_list_item,
            R.id.class_name,
            stateList
        )
        couponDialogBinding?.spinnerState?.adapter = adapter
        couponDialogBinding?.spinnerState?.setPopupBackgroundResource(R.drawable.spinner_item_bg)
        couponDialogBinding?.txtApply?.setOnClickListener {

            if (couponDialogBinding?.edtCoupon?.text?.toString().isNullOrEmpty()) {
                couponDialogBinding?.txtCouponError?.visibility = View.VISIBLE
                couponDialogBinding?.txtCouponError?.text = "* ${getString(R.string.key_continue_coupon)}"
                return@setOnClickListener
            }
            if (statePosition == 0) {
                couponDialogBinding?.txtCouponError?.visibility = View.VISIBLE
                couponDialogBinding?.txtCouponError?.text = "* ${getString(R.string.str_please_select_state)}"
                return@setOnClickListener
            }
            onCouponApplied(couponDialogBinding?.edtCoupon?.text.toString() ?: "" , stateList[statePosition]._id)
        }
        couponDialogBinding?.txtContinue?.setOnClickListener {
            if (statePosition == 0) {
                couponDialogBinding?.txtCouponError?.visibility = View.VISIBLE
                couponDialogBinding?.txtCouponError?.text = "* ${resources.getString(R.string.str_please_select_state)}"
//                showToast(getString(R.string.str_please_select_state))
                return@setOnClickListener
            }else if (couponDialogBinding?.edtCoupon?.text.toString().trim().isNotEmpty()) {
                onCouponApplied(couponDialogBinding?.edtCoupon?.text.toString() ?: "" , stateList[statePosition]._id)
//                couponDialogBinding?.txtCouponError?.visibility = View.VISIBLE
//                couponDialogBinding?.txtCouponError?.text = "* ${getString(R.string.key_continue_coupon)}"
                return@setOnClickListener
            }
            couponDialog?.dismiss()
        }
        couponDialogBinding?.closeButton?.setOnClickListener {
            finish()
            couponDialog?.dismiss()
            return@setOnClickListener
        }
        couponDialogBinding?.spinnerState?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                stateId = stateList[position]._id
                statePosition = position
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        couponDialog?.setCancelable(false)
        couponDialog?.show()
    }

    fun getScreenWidth(activity: Activity): Int {
        val size = Point()
        activity.windowManager.defaultDisplay.getSize(size)
        return size.x
    }

    private fun setPriceDetail(actualPrice: Int, finalPrice: Int) {
        binding.apply {
            txtActualPrice1.text = "₹ $actualPrice"
            txtTotalSum.text = "₹ $finalPrice"
            totalSumAmt = finalPrice.toDouble()
            txtPayableAmt.text = "₹ $finalPrice"
            payableAmt = finalPrice.toDouble()

        }
    }

    override fun init() {


        profileViewModel.getStateList(prefs.getSelectedLanguageId().toString())

        planDetailJSON = intent.getStringExtra("plan_detail")?.let { JSONObject(it) }!!

        requestQueue = Volley.newRequestQueue(context) // JACK

        try {

            requestQueue_token = Volley.newRequestQueue(context) // JACK
        } catch (e: Exception) {
            e.printStackTrace()
        }

        binding.apply {
            txtClassName.text = planDetailJSON.getString("className")
            val subjectArray = planDetailJSON.getJSONArray("subjectName")
            val name = planDetailJSON.getString("name")
            val offer = planDetailJSON.getString("offer")

           /* if (planDetailJSON.getString("className") == "10th") {
                txtOfferName.text = resources.getString(R.string.launch_offer)
            }else {
                txtOfferName.text = resources.getString(R.string.offer_name)
            }*/

            txtOfferName.text = offer
            txtOfferName.text = offer

            if (subjectArray.length() > 1) {
                val strBuilder = StringBuilder()
                for (i in 0 until subjectArray.length()) {
                    strBuilder.append(
                        when {
                            strBuilder.isEmpty() -> subjectArray.getString(i)
                            else -> ",${subjectArray.getString(i)}"
                        }
                    )
                }
//                txtSubjectName.text = strBuilder.toString()
                subjectName = strBuilder.toString()
            } else {
//                txtSubjectName.text = subjectArray.getString(0)
                subjectName = subjectArray.getString(0)
            }

            txtSubjectName.text = name //planDetailJSON.getString("name")

            txtActualPrice.text = "₹ ${planDetailJSON.getInt("crossAmount")} /-"
            txtOfferPrice.text = "₹ ${planDetailJSON.getInt("finalAmount")} /-"
            enrolmentId = planDetailJSON.getString("_id")
            setPriceDetail(
                planDetailJSON.getInt("crossAmount"),
                planDetailJSON.getInt("finalAmount")
            )

            txtValidTill.text = "${getString(R.string.valid_till)}"
            txtExDate.text = "${planDetailJSON.getString("expiredDate")}"
        }

        binding.txtActualPrice.also {
            it.paintFlags = it.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }

        binding.txtActualPrice1.also {
            it.paintFlags = it.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
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
                    txtApply.text = getString(R.string.apply)
                    payableAmt = planDetailJSON.getInt("finalAmount").toDouble()!!
                    payableAmt =
                        Math.round(java.lang.Double.valueOf(payableAmt)).toDouble()    //JACK
                    txtPayableAmt.text = "₹ $payableAmt"
                    txtDiscAmt.text = "- ₹ 0"
                    discountAmt = 0.0
                    sendCode = ""
                }

            })

//            txtApply.setOnClickListener {
//                if (txtApply.text.toString() != getString(R.string.applied)) {
//                    Log.e(
//                        "SubjectName Array  ==>",
//                        edtCoupon.text.toString() + "----" + "-" + subjectName + "-"
//                    )
//                    showProgressDialog()
//                    viewModel.applyCoupon(edtCoupon.text.toString(), "",subjectName)
//                }
//            }

            imgBack.setOnClickListener {
//                if (prefs.getUserLanguage() == "hi") {
//                    AppUtills.setLocaleLanguage("hi", this@SelectedPlanPricingActivity)
//                    //LocaleUtil.setLocale(this@SplashActivity, "hi")
//                } else {
//                    AppUtills.setLocaleLanguage("en", this@SelectedPlanPricingActivity)
//                    //LocaleUtil.setLocale(this@SplashActivity, "en")
//                }
                finish()
            }

            txtSubscribe.setOnClickListener {
                if (!prefs.isGuestUser()) {
                    //startPayment()
                    /*Intent(this@EnrolmentPlanPricingActivity, PaymentMethodActivity::class.java).also {
                        startActivity(it)
                    }*/

//                    showToast("" +payableAmt.toString() + "\n" + enrolmentId + "\n" + discountAmt.toString()
//                            + "\n" +  totalSumAmt.toString() + "\n" + sendCode.toString())

                    Log.e(
                        "chechPayment--->",
                        "amount --" + payableAmt.toString() + "\n" +
                                "enrollmentId --" + enrolmentId + "\n" +
                                "promoAmount --" + discountAmt.toString() + "\n" +
                                "finalAmount --" + totalSumAmt.toString() + "\n" +
                                "promoCode --" + sendCode.toString()
                    )

                    if (payableAmt > 0) {

//                        request, enrollmentId , promoAmount , promoCode , finalAmount

                        showProgressDialog()
//                        viewModel.createOrder(payableAmt.toString(), enrolmentId)
                        viewModel.createOrder(
                            payableAmt.toString(),
                            enrolmentId,
                            discountAmt.toString(),
                            sendCode.toString(),
                            totalSumAmt.toString(),
                            stateId
                        )

                    } else {
                        //call new api
                        zeroAmt(
                            enrolmentId,
                            discountAmt.toString(),
                            sendCode.toString(),
                            totalSumAmt.toString()
                        )
                    }


                } else
                    showToast(getString(com.app.tensquare.R.string.please_login))
            }
        }
    }

    var resMsg = ""
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

        /*viewModel.subscriptionDetails.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    *//*subscriptionData = response.data?.data as SubscriptionDetail
                    payableAmt = subscriptionData!!.finalAmount.toDouble()
                    binding.apply {
                        enrolmentId = subscriptionData!!._id
                        txtActualPrice1.text = "₹ ${subscriptionData!!.crossAmount.toDouble()}"
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
            when (response) {
                is NetworkResult.Success -> {
                    preOrderData = response.data as CreateOrderData
                    Log.e("PrintOrderDAta","preOrderData = $preOrderData")
                    startPayment(preOrderData)

                }

                is NetworkResult.Error -> {
                    if (response.message != OTHER_DEVISE_LOGIN) {
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
                    if (response.message == ACCESS_TOKEN_EXPIRED) {
                        generateNewToken("1")
                    } else {
                        showToast(response.message)
                    }
                }

                is NetworkResult.Loading -> {
                    // show a progress bar
                }
            }
        }

        viewModel.verifySignature.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    prefs.setIsEnrolled(true)
                    Intent(this@SelectedPlanPricingActivity, ThankYouActivity::class.java).also {
                        it.putExtra("ACTION", "PAYMENT")
                        startActivity(it)
                    }
                }

                is NetworkResult.Error -> {
                    if (response.message == OTHER_DEVISE_LOGIN) {
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
                    showToast(response.message)
                }

                is NetworkResult.Loading -> {
                    // show a progress bar
                }
            }
        }

        viewModel.applyCouponResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    binding.apply {
                        couponDialog?.dismiss()
                        couponDialogBinding?.txtCouponError?.visibility = View.GONE
                        val data = response.data
                        txtApply.text = getString(R.string.applied)
                        couponDialogBinding?.txtApply?.text = getString(R.string.applied)
                        couponDialogBinding?.txtApply?.setTextColor(Color.parseColor("#043F39"))
//                        var discountAmt = 0.0
                        discountAmt = 0.0
                        if (data?.isPercent == true) {
                            val finalAmt = planDetailJSON.getInt("finalAmount").toDouble()
                            if (finalAmt != null) {
                                discountAmt = (finalAmt.times(data.amount)).div(100.0)
                            }
                        } else {
                            discountAmt = data?.amount.toString().toDouble()
                        }
                        txtDiscAmt.text = "- ₹ $discountAmt"

                        payableAmt = planDetailJSON.getInt("finalAmount").minus(discountAmt)!!

                        payableAmt =
                            Math.round(java.lang.Double.valueOf(payableAmt)).toDouble()    //JACK
                        txtPayableAmt.text = "₹ $payableAmt"


                        sendCode = couponDialogBinding?.edtCoupon?.text?.toString() ?: ""
                    }
                }

                is NetworkResult.Error -> {

                    couponDialogBinding?.txtCouponError?.visibility = View.VISIBLE
                    val message = if (response.message == "Invalid Code") getString(R.string.invaild_coupon_code) else response.message
                    couponDialogBinding?.txtCouponError?.text = "* ${response.message}"
                    showToast(response.message)
                    if (response.message == OTHER_DEVISE_LOGIN) {

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
//                    showToast(getString(com.app.elearning.R.string.something_went_wrong_p))
                }

                is NetworkResult.Loading -> {
                    // show a progress bar
                }
            }
        }
        profileViewModel.stateListResponse.observe(this){
            when(it) {
                is NetworkResult.Error -> {
                    if (it.data?.message == OTHER_DEVISE_LOGIN) {

                        val intent =  Intent(this, LoginActivity::class.java)
                        intent.putExtra("Expired", "Expired")
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        val broadcast = Intent(LOG_OUT)
                        sendBroadcast(broadcast)
                        startActivity(intent)
                        finish()

                    }else{
                        showToast(it.data?.message)
                    }
                }
                is NetworkResult.Loading -> {}
                is NetworkResult.Success -> {
                    if (it.data?.status == STATUS_SUCCESS) {
                        it.data.let {
                            stateList.clear()

                            stateList.add(State("",resources.getString(R.string.select_state)))
                            stateList.addAll(it.data)
                            showCouponDialog(
                                stateList
                            ) { couponCode , stateId ->
                                viewModel.applyCoupon(couponCode.toString(),stateId, subjectName)
                            }
                        }
                    } else {
                        if (it.data?.message == OTHER_DEVISE_LOGIN) {

                            val intent =  Intent(this, LoginActivity::class.java)
                            intent.putExtra("Expired", "Expired")
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            val broadcast = Intent(LOG_OUT)
                            sendBroadcast(broadcast)
                            startActivity(intent)
                            finish()

                        }else{
                            showToast(it.data?.message)
                        }
                    }
                }
            }

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

            val userData = preferences.getUserData()?.let { JSONObject(it) }

            val prefill = JSONObject()
            if (userData != null) {
                prefill.put("contact", userData.getString("mobile"))
            }
//            prefill.put("email", "gaurav.kumar@example.com")
//            prefill.put("contact", "9876543210")

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

        try {
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        Log.e("PaymentFailure***", Gson().toJson(p2).toString())
//        showToast(p1)     // JACK
//        showToast(getString(com.app.elearning.R.string.something_went_wrong_p))
        showToast(getString(com.app.tensquare.R.string.payment_cancelled))
    }

    private var requestQueue: RequestQueue? = null
    private fun zeroAmt(
        enrolmentId: String,
        promoAmount: String,
        promoCode: String,
        finalAmount: String
    ) {

        val url = NetworkConstants.BASE_URL + "api/order/create-user-subscription"

        Log.e("API = >", url.toString())

        val strReq: StringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->

                // Handle Server response here
                try {
                    val responseObj = JSONObject(response)
//                    val isSuccess = responseObj.getBoolean("isSuccess")
                    Log.e("Plan = >", response.toString())
                    if (responseObj.getString("status") == STATUS_SUCCESS) {
                        if (responseObj.getString("message") == "Data already exist" || responseObj.getString("message") == "डाटा पहले से मौजूद") {
                            showToast(responseObj.getString("message"))
                            return@Listener
                        }

                        prefs.setIsEnrolled(true)
                        Intent(
                            this@SelectedPlanPricingActivity,
                            ThankYouActivity::class.java
                        ).also {
                            it.putExtra("ACTION", "PAYMENT")
                            startActivity(it)
                            finish()
                        }

                    } else {

                        if (responseObj.getString("message") == OTHER_DEVISE_LOGIN) {

                            val intent = Intent(this, LoginActivity::class.java)
                            intent.putExtra("Expired", "Expired")
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            val broadcast = Intent(LOG_OUT)
                            sendBroadcast(broadcast)
                            startActivity(intent)
                            finish()
                        } else if (responseObj.getString("message") == ACCESS_TOKEN_EXPIRED) {
                            generateNewToken("0")
                        } else {
                            showToast(getString(com.app.tensquare.R.string.something_went_wrong_p))
                        }

                    }


                } catch (e: Exception) { // caught while parsing the response
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { volleyError -> // error occurred
            }) {

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers: MutableMap<String, String> = java.util.HashMap()
                headers.put("token", prefs.getUserToken().toString())
                Log.e("get Token = >", headers.toString())
                return headers
            }

            override fun getParams(): MutableMap<String, String> {
                val params: MutableMap<String, String> = java.util.HashMap()
                params.put("amount", "0")
                params.put("enrollmentId", enrolmentId)
                params.put("promoAmount", promoAmount)
                params.put("promoCode", promoCode)
                params.put("finalAmount", finalAmount)
                params.put("stateId", stateId)
                Log.e("get data = >", params.toString())
                return params
            }

        }
        // Adding request to request queue
        requestQueue?.add(strReq)

    }


    private fun generateNewToken(type: String) {

        val url = NetworkConstants.BASE_URL + "api/user/refreshAccessToken"

        Log.e("API = >", url.toString())

        val strReq: StringRequest = object : StringRequest(
            Request.Method.GET, url,
            Response.Listener { response ->

                // Handle Server response here
                try {
                    val responseObj = JSONObject(response)
//                    val isSuccess = responseObj.getBoolean("isSuccess")
                    Log.e("TOKEN API RESPOSE = >", response.toString())
                    if (responseObj.getString("status") == STATUS_SUCCESS) {

                        val objData = responseObj.getJSONObject("data")

                        Log.e("token_Home = >", objData.getString("accessToken").toString())
                        prefs.setUserToken(objData.getString("accessToken").toString())
                        prefs.setRefreshToken(objData.getString("refreshToken").toString())
                        // goto
//                        init()

                        if (type.equals("0")) {
                            zeroAmt(
                                enrolmentId,
                                discountAmt.toString(),
                                sendCode.toString(),
                                totalSumAmt.toString()
                            )
                        } else {
                            showProgressDialog()
//                        viewModel.createOrder(payableAmt.toString(), enrolmentId)
                            viewModel.createOrder(
                                payableAmt.toString(),
                                enrolmentId,
                                discountAmt.toString(),
                                sendCode.toString(),
                                totalSumAmt.toString(),
                                stateId
                            )
                        }

                    } else {

                        if (responseObj.getString("message") == OTHER_DEVISE_LOGIN) {

                            val intent = Intent(this, LoginActivity::class.java)
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


                } catch (e: Exception) { // caught while parsing the response
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { volleyError -> // error occurred
            }) {

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers: MutableMap<String, String> = HashMap()
                headers.put("token", prefs.getRefreshToken().toString())
                headers.put("device_id", deviceId.toString())
                Log.e("get Token = >", headers.toString())
                return headers
            }
        }
        // Adding request to request queue
        requestQueue_token?.add(strReq)

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