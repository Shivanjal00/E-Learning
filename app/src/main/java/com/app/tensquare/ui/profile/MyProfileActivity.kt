package com.app.tensquare.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.tensquare.HiltApplication
import com.app.tensquare.R
import com.app.tensquare.base.AppBaseActivity
import com.app.tensquare.base.SharedPrefManager
import com.app.tensquare.databinding.ActivityMyProfileBinding
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.ui.chapter.ChaptersInSubjectActivity
import com.app.tensquare.ui.home.HomeViewModel
import com.app.tensquare.ui.login.LoginActivity
import com.app.tensquare.ui.subscription.EnrolmentPlanPricingNewActivity
import com.app.tensquare.utils.*
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
//import kotlinx.android.synthetic.main.drawer_home_screen.view.*
import org.json.JSONObject
import javax.inject.Inject


@AndroidEntryPoint
class MyProfileActivity : AppBaseActivity() {
    private lateinit var binding: ActivityMyProfileBinding
    private val viewModel: ProfileViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private var profileDetail: ProfileDetail? = null

    private var isSomethingChanged: Boolean = false

    @Inject
    lateinit var sharedPrefManager: SharedPrefManager

//    private var planList = mutableListOf<EnrolmentPlan>()
    private val planList: ArrayList<EnrolmentPlan> = ArrayList<EnrolmentPlan>()

    private lateinit var purchasePlanAdapter: PurchasePlanAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        setListeners()
        initObservers()

    }

    override fun init() {
        showProgressDialog()
        prefs.getUserToken()?.let { viewModel.getProfileDetail(it) }


        /* set rv */
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(
            this,
            RecyclerView.VERTICAL, false
        )
        binding.rvPlan.setLayoutManager(layoutManager)
        binding.rvPlan.setNestedScrollingEnabled(false)
        binding.rvPlan.setItemAnimator(DefaultItemAnimator())

    }

    override fun setListsAndAdapters() {
        TODO("Not yet implemented")
    }

    override fun setListeners() {
        binding.imgBack.setOnClickListener {
            Intent().also {
                it.putExtra(
                    "isSomethingChanged",
                    isSomethingChanged
                )
                setResult(RESULT_OK, it)
            }
            finish()
        }

        binding.imgEdit.setOnClickListener {
            Intent(this@MyProfileActivity, EditProfileActivity::class.java).also {
                it.putExtra("profile_detail", Gson().toJson(profileDetail).toString())

                //startActivity(it)

                resultLauncher.launch(it)
            }
        }

        binding.txtSubscribe.setOnClickListener {
            Intent(this@MyProfileActivity, EnrolmentPlanPricingNewActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    override fun initObservers() {
        viewModel.profileResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    if (response.data?.status == STATUS_SUCCESS) {

                        planList.clear()
                        val data_1 = response.data.data
                        profileDetail = response.data.data
                        profileDetail?.let {
                            binding.apply {
                                txtName.text = profileDetail!!.name
                                txtMobile.text = profileDetail!!.mobile
                                txtLanguage.text = profileDetail!!.languageName
//                                txtClass.text = profileDetail!!.className //JACK

                                if(profileDetail!!.className.contains("10th") ||
                                    profileDetail!!.className.contains("10 वीं")){
                                    txtClass.text = getString(com.app.tensquare.R.string.tenth)
                                }else if (profileDetail!!.className.contains("11th") ||
                                    profileDetail!!.className.contains("11 वीं")){
                                    txtClass.text = getString(com.app.tensquare.R.string.eleven)
                                }else if (profileDetail!!.className.contains("12th") ||
                                    profileDetail!!.className.contains("12 वीं")){
                                    txtClass.text = getString(com.app.tensquare.R.string.twelfth)
                                }else{
                                    txtClass.text = profileDetail!!.className
                                }

                                txtEmail.text = profileDetail!!.email.ifEmpty { "-" }
                                txtState.text = profileDetail!!.stateName.ifEmpty { "-" }
                                txtSchoolName.text = profileDetail!!.schoolName.ifEmpty { "-" }
                                txtDistrict.text = profileDetail!!.district.ifEmpty { "-" }


//                                showToast(profileDetail!!.enrollmentPlanStatus.toString())
                                prefs.setIsEnrolled(profileDetail!!.enrollmentPlanStatus)
//                                val jsonObject = JSONObject(prefs.getUserData().toString())
                                val jsonObject = JSONObject(Gson().toJson(data_1).toString())
                                jsonObject.put("profilePic", profileDetail!!.profilePic)
                                prefs.setUserData(jsonObject.toString())


//                                if (profileDetail?.enrollmentPlanStatus == true) {
//                                    txtEnrolmentPlan.visibility = View.VISIBLE
//                                    llDetailCard.visibility = View.VISIBLE
//
//                                    txtActiveClass.text =
//                                        "Active: ${profileDetail?.enrolmentPlan?.className} Grade"
//                                    txtValidTill.text =
//                                        "Valid Till : ${profileDetail?.enrolmentPlan?.expiredDate}"
//                                } else {
//                                    txtSubscribe.visibility = View.VISIBLE
//                                }

                                if (profileDetail?.enrollmentPlanStatus == true) {
                                    txtEnrolmentPlan.visibility = View.VISIBLE
                                    rvPlan.visibility = View.VISIBLE

//                                    for (i in 0 until profileDetail!!.enrolmentPlan.size) {
                                        planList.addAll(profileDetail!!.enrolmentPlan)
//                                    }

                                    purchasePlanAdapter = PurchasePlanAdapter(this@MyProfileActivity, planList)
                                    rvPlan.setAdapter(purchasePlanAdapter)
                                    purchasePlanAdapter.notifyDataSetChanged()
                                }

                                Log.e("profilePic", profileDetail!!.profilePic)

                                try {
                                    if (profileDetail!!.profilePic.isNotEmpty()) {
                                        /*Picasso.with(imgProfile.context)
                                            .load(profileDetail!!.profilePic)
                                            .resize(300, 300)
                                            .into(imgProfile)*/

                                        Glide.with(this@MyProfileActivity)
                                            .setDefaultRequestOptions(RequestOptions().priority(Priority.HIGH))
                                            .load(profileDetail!!.profilePic)
                                            .placeholder(R.drawable.profile_place_holder)
                                            .skipMemoryCache(true)
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .into(binding.imgProfile)

                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                            }
                        }
                    } else {

                        Log.e("PrintProfileResponse","my profile success ${response.message}")
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

                        }else{
                            showToast(response.data?.message)
                        }

                    }

                }
                is NetworkResult.Error -> {
                    Log.e("PrintProfileResponse","my profile ${response.message}")
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
                        prefs.getUserToken()?.let { viewModel.getProfileDetail(it) }
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
    }

    var resultLauncher = registerForActivityResult(
        StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            // There are no request codes
            //val data = result.data
            //doSomeOperations()
            isSomethingChanged = result.data?.getBooleanExtra("isSomethingChanged", false) == true
            init()
        }
    }

    override fun onBackPressed() {
        Intent().also {
            it.putExtra(
                "isSomethingChanged",
                isSomethingChanged
            )
            setResult(RESULT_OK, it)
        }
        super.onBackPressed()
    }



}