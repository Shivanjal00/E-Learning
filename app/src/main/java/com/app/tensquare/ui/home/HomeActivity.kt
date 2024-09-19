package com.app.tensquare.ui.home

import android.app.Dialog
import android.content.*
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.app.tensquare.R
import com.app.tensquare.activity.*
import com.app.tensquare.base.AppBaseActivity
import com.app.tensquare.base.CallBackForRetry
import com.app.tensquare.base.SharedPrefManager
import com.app.tensquare.databinding.ActivityHomeBinding
import com.app.tensquare.databinding.EnrolmentDialogBinding
import com.app.tensquare.ui.analysis.SelfAnalysisFragment
import com.app.tensquare.ui.appdetail.AboutAndTermsActivity
import com.app.tensquare.ui.login.LoginActivity
import com.app.tensquare.ui.notification.NotificationActivity
import com.app.tensquare.ui.profile.MyProfileActivity
import com.app.tensquare.ui.subscription.EnrolmentPlanPricingNewActivity
import com.app.tensquare.ui.transaction.TransactionsActivity
import com.app.tensquare.utils.*
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView
//import kotlinx.android.synthetic.main.drawer_home_screen.view.*
//import kotlinx.android.synthetic.main.row_home_list.view.*
import org.json.JSONObject

@AndroidEntryPoint
class HomeActivity : AppBaseActivity(), NotificationCountListener {

    private lateinit var binding: ActivityHomeBinding
    private var closingReceiver: BroadcastReceiver = MyBroadcastReceiver()

    private lateinit var preferences: SharedPrefManager

    private var notificationType: String? = ""
    private var varsionName: String? = ""
    private var txtEng : TextView? = null
    private var txtHindi : TextView? = null
    private var swtchLang : Switch? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppUtills.notificationCountListener = this
        this.preferences = prefs

//        isOnline(object : CallBackForRetry{
//            override fun onRetry() {
//                showToast("ready")
//            }
//        })

//        ivNotifyAlert = binding.layoutFooter.ivNotifyAlert

        txtEng = binding.navView.getHeaderView(0).findViewById(R.id.txtEng)
        txtHindi = binding.navView.getHeaderView(0).findViewById(R.id.txtHindi)
        swtchLang = binding.navView.getHeaderView(0).findViewById(R.id.swtchLang)

        try {
            if (getIntent().hasExtra("notificationType")){
                notificationType = getIntent().getStringExtra("notificationType").toString()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

        init()
        setListeners()

//        navView = binding.navView


        /*binding.bottomNavigationViewHome.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navMyProfile -> {
                    //changeFragment(QualityManagerHomeFragment())
                    true
                }
                R.id.navMyDownload -> {
                    //changeFragment(FillingCenterFragment())
                    true
                }
                R.id.navSelfAnalysis -> {
                    //changeFragment(MyAccountFragment())
                    true
                }
                else -> false
            }
        }*/
    }

    var homePressed = true
    var doubleBackToExitPressedOnce: Boolean = false
    override fun onBackPressed() {
        /*val test: HomeFragment? =
            supportFragmentManager.findFragmentByTag("HomeFragment") as HomeFragment?*/


        if (supportFragmentManager.findFragmentByTag("HomeFragment")?.isVisible != true &&
            supportFragmentManager.findFragmentByTag("MyDownloadFragment")?.isVisible != true &&
            supportFragmentManager.findFragmentByTag("SelfAnalysisFragment")?.isVisible != true
        ) {
            changeFragment(HomeFragment(), "HomeFragment")
            setHomeScreen()
        } else {
            val backStackEntryCount = supportFragmentManager.backStackEntryCount
            //backStackEntryCount==0 -> no fragments more.. so close the activity with warning
            if (backStackEntryCount == 0) {
                if (homePressed) {
                    if (doubleBackToExitPressedOnce) {
                        super.onBackPressed()
                        finishAffinity()
                        return
                    }
                    doubleBackToExitPressedOnce = true
                    showToast(getString(com.app.tensquare.R.string.please_click_back_again_to_exit))
                    Handler(Looper.getMainLooper()).postDelayed(
                        { doubleBackToExitPressedOnce = false },
                        2000
                    )
                } else {
                    homePressed = true
                }
            } else {
                super.onBackPressed()
            }
        }


    }

    override fun init() {
        val filter = IntentFilter(LOG_OUT)
        this.registerReceiver(closingReceiver, filter)
        if (intent.getStringExtra("ACTION") == "SIGNUP") {
        /*    Handler(Looper.getMainLooper()).postDelayed(
                {
                    showEnrolmentDialog()
                }, 1000L
            )   */
        }

        try {
            varsionName = getString(com.app.tensquare.R.string.version_name) + " - " +
                    AppUtills.getVersion(this@HomeActivity)

            val headerView = binding.navView.getHeaderView(0)
            val tvVersion = headerView.findViewById<TextView>(R.id.tvVersion)
            tvVersion.text = varsionName

        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            if (prefs.getUserLanguage() == "hi") {
                swtchLang?.isChecked = true
                txtHindi?.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.lang_green
                    )
                )
                txtEng?.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.lang_gray
                    )
                )
            }else{
                swtchLang!!.isChecked = false
                txtHindi?.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.lang_gray
                    )
                )
                txtEng?.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.lang_green
                    )
                )
            }
        }catch (e: Exception){
            e.printStackTrace()
        }

        if (preferences.isGuestUser()) {
            val headerView = binding.navView.getHeaderView(0)
            val txtLogout = headerView.findViewById<TextView>(R.id.txtLogout)
            val txtUserName = headerView.findViewById<TextView>(R.id.txtUserName)
            val llDeleteAccount = headerView.findViewById<View>(R.id.llDeleteAccount)

            txtLogout.text = getString(R.string.login)
            txtUserName.text = getString(R.string.guest)
            llDeleteAccount.visibility = View.GONE
        }else {
            val headerView = binding.navView.getHeaderView(0)
            val llDeleteAccount = headerView.findViewById<View>(R.id.llDeleteAccount)
            val txtUserName = headerView.findViewById<TextView>(R.id.txtUserName)
            val txtClassName = headerView.findViewById<TextView>(R.id.txtClassName)
            val txtLogout = headerView.findViewById<TextView>(R.id.txtLogout)
            val imgProfile = headerView.findViewById<ImageView>(R.id.imgProfile)

            llDeleteAccount.visibility = View.VISIBLE

            val userData = preferences.getUserData()?.let { JSONObject(it) }
            if (userData != null) {
                txtUserName.text = userData.getString("name")
                Log.e("Name = >", userData.getString("name"))

                if (userData.getString("className").equals("10th") ||
                    userData.getString("className").equals("10 वीं")) {
                    txtClassName.text = getString(com.app.tensquare.R.string.tenth)
                } else if (userData.getString("className").equals("12th") ||
                    userData.getString("className").equals("12 वीं")) {
                    txtClassName.text = getString(com.app.tensquare.R.string.twelfth)
                } else {
                    txtClassName.text = prefs.getSelectedClassName()
                }

                txtLogout.text = getString(R.string.logout)

                if (userData.getString("profilePic").isNotEmpty()) {
                    Glide.with(this@HomeActivity)
                        .setDefaultRequestOptions(RequestOptions().priority(Priority.HIGH))
                        .load(userData.getString("profilePic"))
                        .placeholder(R.drawable.profile_place_holder)
                        .override(100, 100)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imgProfile)
                }
            }
        }

        if (notificationType.equals("notificationType")){

            Intent(this@HomeActivity, NotificationActivity::class.java).also {
                startActivity(it)
            }

            changeFragment(HomeFragment(), "HomeFragment")
            setHomeScreen()

        }else{
            changeFragment(HomeFragment(), "HomeFragment")
            setHomeScreen()
        }
//        changeFragment(HomeFragment(), "HomeFragment")
//        setHomeScreen()
    }

    override fun setListsAndAdapters() {
        TODO("Not yet implemented")
    }

    override fun setListeners() {
        val llMyProfile = binding.navView.getHeaderView(0).findViewById<View>(R.id.llMyProfile)

        llMyProfile.setOnClickListener {
            if (preferences.isGuestUser()) {
                Intent(this@HomeActivity, LoginActivity::class.java).also {
                    startActivity(it)
                }
            } else {
                Intent(this@HomeActivity, MyProfileActivity::class.java).also {
                    //startActivity(it)
                    resultLauncher.launch(it)
                }
            }

            showDrawer(false)
        }
        val llMyTxns = binding.navView.getHeaderView(0).findViewById<View>(R.id.llMyTxns)

        llMyTxns.setOnClickListener {
            if (preferences.isGuestUser()) {
                Intent(this@HomeActivity, LoginActivity::class.java).also {
                    startActivity(it)
                }
            } else {
                Intent(this@HomeActivity, TransactionsActivity::class.java).also {
                    startActivity(it)
                }
            }

            showDrawer(false)
        }

        val llMyDownloads = binding.navView.getHeaderView(0).findViewById<View>(R.id.llMyDownloads)

        llMyDownloads.setOnClickListener {
            showDrawer(false)
            if (preferences.isGuestUser()) {
                //if (!preferences.isEnrolled()) {
                //    Intent(this@HomeActivity, LoginActivity::class.java).also {
                //        startActivity(it)
                //    }
                showToast(getString(com.app.tensquare.R.string.please_enrol))
            } else {
                changeFragment(MyDownloadFragment(), "MyDownloadFragment")
                binding.layoutFooter.llHome.setBackgroundResource(R.drawable.bg_circle_home_disabled)
                binding.layoutFooter.imgMyDownload.setImageResource(R.drawable.ic_my_download_on)
                binding.layoutFooter.txtMyDownload.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.black
                    )
                )

                binding.layoutFooter.imgSelfAnalysis.setImageResource(R.drawable.ic_home_self_analysis_off)
                binding.layoutFooter.txtSelfAnalysis.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.gray_170
                    )
                )
            }
        }

        val llEnrolmentPlan = binding.navView.getHeaderView(0).findViewById<View>(R.id.llEnrolmentPlan)

        llEnrolmentPlan.setOnClickListener {
            val userData = preferences.getUserData()?.let { JSONObject(it) }
            if (userData?.getBoolean("enrollmentPlanStatus") == true){
                //showEnrolmentDialog()
                Intent(this@HomeActivity, EnrolmentPlanPricingNewActivity::class.java).also {
                    startActivity(it)
                }
            }else{
                Intent(this@HomeActivity, EnrolmentPlanPricingNewActivity::class.java).also {
                    startActivity(it)
                }
            }
            showDrawer(false)
        }


        val llAboutUs = binding.navView.getHeaderView(0).findViewById<View>(R.id.llAboutUs)

        llAboutUs.setOnClickListener {
            Intent(this@HomeActivity, AboutAndTermsActivity::class.java).also {
                it.putExtra(ACTION, ABOUT_US)
                startActivity(it)
            }
            showDrawer(false)
        }



        val llTerms = binding.navView.getHeaderView(0).findViewById<View>(R.id.llTerms)

        llTerms.setOnClickListener {
            Intent(this@HomeActivity, AboutAndTermsActivity::class.java).also {
                it.putExtra(ACTION, TERMS_AND_CONDITIONS)
                startActivity(it)
            }
        }

        val llContactUs = binding.navView.getHeaderView(0).findViewById<View>(R.id.llContactUs)

        llContactUs.setOnClickListener {
            if (preferences.isGuestUser()) {
                Intent(this@HomeActivity, LoginActivity::class.java).also {
                    startActivity(it)
                }
            } else {
                Intent(this@HomeActivity, FeedbackActivity::class.java).also {
                    it.putExtra("screen", SCREEN_CONTACT_US)
                    startActivity(it)
                }
            }
            showDrawer(false)
        }


        val llFaqs = binding.navView.getHeaderView(0).findViewById<View>(R.id.llFaqs)

        llFaqs.setOnClickListener {
            Intent(this@HomeActivity, FaqsActivity::class.java).also {
                startActivity(it)
            }
            showDrawer(false)
        }


        val llLogout = binding.navView.getHeaderView(0).findViewById<View>(R.id.llLogout)

        llLogout.setOnClickListener {
            if (preferences.isGuestUser()) {
                Intent(this@HomeActivity, LoginActivity::class.java).also {
                    startActivity(it)
                }
            } else {
                Intent(this@HomeActivity, LogoutActivity::class.java).also {
                    startActivity(it)
                }
            }
            showDrawer(false)
        }


        val llDeleteAccount = binding.navView.getHeaderView(0).findViewById<View>(R.id.llDeleteAccount)

        llDeleteAccount.setOnClickListener {
            Intent(this@HomeActivity, DeleteAccountActivity::class.java).also {
                startActivity(it)
            }
            showDrawer(false)
        }


        binding.layoutFooter.llHome.setOnClickListener {
            changeFragment(HomeFragment(), "HomeFragment")
            setHomeScreen()
        }

        binding.layoutFooter.llMyProfile.setOnClickListener {
            if (preferences.isGuestUser()) {
                Intent(this@HomeActivity, LoginActivity::class.java).also {
                    startActivity(it)
                }
            } else {
                Intent(this@HomeActivity, MyProfileActivity::class.java).also {
                    startActivity(it)
                }

                binding.layoutFooter.llHome.setBackgroundResource(R.drawable.bg_circle_home_disabled)
                binding.layoutFooter.imgMyDownload.setImageResource(R.drawable.ic_my_download_off)
                binding.layoutFooter.txtMyDownload.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.gray_170
                    )
                )

                binding.layoutFooter.imgSelfAnalysis.setImageResource(R.drawable.ic_home_self_analysis_off)
                binding.layoutFooter.txtSelfAnalysis.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.gray_170
                    )
                )
            }
        }

        binding.layoutFooter.llNotification.setOnClickListener {
            if (preferences.isGuestUser()) {
                Intent(this@HomeActivity, LoginActivity::class.java).also {
                    startActivity(it)
                }
            } else {
                Intent(this@HomeActivity, NotificationActivity::class.java).also {
                    startActivity(it)
                }

                binding.layoutFooter.llHome.setBackgroundResource(R.drawable.bg_circle_home_disabled)
                binding.layoutFooter.imgMyDownload.setImageResource(R.drawable.ic_my_download_off)
                binding.layoutFooter.txtMyDownload.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.gray_170
                    )
                )

                binding.layoutFooter.imgSelfAnalysis.setImageResource(R.drawable.ic_home_self_analysis_off)
                binding.layoutFooter.txtSelfAnalysis.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.gray_170
                    )
                )
            }
        }

        binding.layoutFooter.llMyDownloads.setOnClickListener {
            if (preferences.isGuestUser()) {
//            if (!preferences.isEnrolled()) {
//                Intent(this@HomeActivity, LoginActivity::class.java).also {
//                    startActivity(it)
//                }
                showToast(getString(com.app.tensquare.R.string.please_enrol))
            } else {
                changeFragment(MyDownloadFragment(), "MyDownloadFragment")
                binding.layoutFooter.llHome.setBackgroundResource(R.drawable.bg_circle_home_disabled)
                binding.layoutFooter.imgMyDownload.setImageResource(R.drawable.ic_my_download_on)
                binding.layoutFooter.txtMyDownload.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.black
                    )
                )

                binding.layoutFooter.imgSelfAnalysis.setImageResource(R.drawable.ic_home_self_analysis_off)
                binding.layoutFooter.txtSelfAnalysis.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.gray_170
                    )
                )
            }
        }

        binding.layoutFooter.llSelfAnalysis.setOnClickListener {
            if (preferences.isGuestUser()) {
                Intent(this@HomeActivity, LoginActivity::class.java).also {
                    startActivity(it)
                }
            } else {
                changeFragment(SelfAnalysisFragment(), "SelfAnalysisFragment")

                binding.layoutFooter.llHome.setBackgroundResource(R.drawable.bg_circle_home_disabled)
                binding.layoutFooter.imgMyDownload.setImageResource(R.drawable.ic_my_download_off)
                binding.layoutFooter.txtMyDownload.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.gray_170
                    )
                )

                binding.layoutFooter.imgSelfAnalysis.setImageResource(R.drawable.ic_home_self_analysis_on)
                binding.layoutFooter.txtSelfAnalysis.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.black
                    )
                )
            }
        }

        swtchLang?.setOnCheckedChangeListener { compoundButton, b ->
            if (compoundButton.isPressed) {
                AppUtills.languageListener?.onLanguageChange(b)
            }
        }
    }

    override fun initObservers() {

    }


    private fun changeFragment(fragment: Fragment, tag: String) {
        val fragmentTransaction: FragmentTransaction =
            supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayoutHome, fragment, tag)
        fragmentTransaction.commit()

    }

    var mDialog: Dialog? = null

    private fun showEnrolmentDialog() {

        val userData = preferences.getUserData()?.let { JSONObject(it) }
//        val btnText1 = if (userData?.getBoolean("enrollmentPlanStatus") == true) {    //JACK
//            getString(R.string.upgrade)   //JACK
//        } else {      //JACK
            getString(R.string.choose_a_plan)
//        }     //JACK


        if (mDialog != null) {
            if (mDialog!!.isShowing) {
                return
            }
        }
        mDialog = Dialog(this@HomeActivity)

        val dialogBinding: EnrolmentDialogBinding =
            EnrolmentDialogBinding.inflate(layoutInflater)

        mDialog!!.setContentView(dialogBinding.root)
        mDialog!!.setCanceledOnTouchOutside(true)
        mDialog!!.setCancelable(true)
        mDialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        mDialog!!.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        mDialog!!.window!!.setGravity(Gravity.CENTER)

        if (userData?.getBoolean("enrollmentPlanStatus") == true){
            dialogBinding.txtChoosePlan.text = getString(R.string.upgrade)
            dialogBinding.txtMessage.text = getString(R.string.enroled_yet)
        }else{
            dialogBinding.txtChoosePlan.text = getString(R.string.choose_a_plan)
            dialogBinding.txtMessage.text = getString(R.string.not_enroled_yet)
        }
//        dialogBinding.txtChoosePlan.text = btnText1
   /*     if (userData?.getBoolean("enrollmentPlanStatus") == true) {
            dialogBinding.llDetailCard.visibility = View.VISIBLE
            dialogBinding.txtMessage.visibility = View.GONE

//            val enrolmentPlanJSON = userData.getJSONObject("getEnrollmentPlan")
//            dialogBinding.txtActiveClass.text =
//                "${getString(R.string.active)} ${enrolmentPlanJSON.getString("className")} ${
//                    getString(
//                        R.string.grade
//                    )
//                }"
//            dialogBinding.txtValidTill.text =
//                "${getString(R.string.valid_till)} ${enrolmentPlanJSON.getString("expiredDate")}"

        } else {
            dialogBinding.llDetailCard.visibility = View.GONE
            dialogBinding.txtMessage.visibility = View.VISIBLE
        }

    */ //JACK


        dialogBinding.txtChoosePlan.setOnClickListener {
            /*if (dialogBinding.txtChoosePlan.text.toString()
                    .uppercase(Locale.getDefault()) == btnText1.uppercase(Locale.getDefault())
            ) {*/
            mDialog!!.dismiss()
            Intent(this@HomeActivity, EnrolmentPlanPricingNewActivity::class.java).also {
                startActivity(it)
            }
            /*Intent(this@HomeActivity, EnrolmentPlanActivity::class.java).also {
                startActivity(it)
            }*/
            /*dialogBinding.llDetailCard.visibility = View.VISIBLE
            dialogBinding.txtMessage.visibility = View.GONE
            dialogBinding.txtChoosePlan.text = btnText2*/
            /*} else {

            }*/
        }

        dialogBinding.imgClose.setOnClickListener {
            mDialog!!.dismiss()
        }

        mDialog!!.show()
    }

    override fun onResume() {
        super.onResume()
        var test: HomeFragment? =
            supportFragmentManager.findFragmentByTag("HomeFragment") as HomeFragment?

        val test1: MyDownloadFragment? =
            supportFragmentManager.findFragmentByTag("MyDownloadFragment") as MyDownloadFragment?

        val test2: SelfAnalysisFragment? =
            supportFragmentManager.findFragmentByTag("SelfAnalysisFragment") as SelfAnalysisFragment?

        if (test != null) {
            /*if (!test.isVisible) {
                changeFragment(HomeFragment(), "HomeFragment")
                setHomeScreen()
            } else {*/
            /*test = null
            changeFragment(HomeFragment(), "HomeFragment")*/
            setHomeScreen()
            // NewChange
            try {
                val userData = preferences.getUserData()?.let { JSONObject(it) }
                if (userData != null && userData.getString("profilePic").isNotEmpty()) {
                    val imgProfile = binding.navView.getHeaderView(0).findViewById<ImageView>(R.id.imgProfile)
                    Glide.with(this@HomeActivity)
                        .setDefaultRequestOptions(RequestOptions().priority(Priority.HIGH))
                        .load(userData.getString("profilePic"))
                        .placeholder(R.drawable.profile_place_holder)
                        .override(100, 100)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imgProfile)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            //}
        } else if (test1 != null && test1.isVisible) {
            setMyDownLoadScreen()
        } else if (test2 != null && test2.isVisible) {
            setSelfAnalysisScreen()
            /*if (supportFragmentManager.findFragmentByTag("MyDownloadFragment")?.isVisible == true) {
                changeFragment(MyDownloadFragment(), "MyDownloadFragment")
                setHomeScreen()
            } else {
                changeFragment(HomeFragment(), "HomeFragment")
                setHomeScreen()
            }*/
        }

        // Add Jack
        try {
            if (preferences.isGuestUser()) {
                val txtLogout = binding.navView.getHeaderView(0).findViewById<TextView>(R.id.txtLogout)
                val txtUserName = binding.navView.getHeaderView(0).findViewById<TextView>(R.id.txtUserName)
                txtLogout.text = getString(R.string.login)
                txtUserName.text = getString(R.string.guest)
            } else {
                val userData = preferences.getUserData()?.let { JSONObject(it) }
                if (userData != null) {
                    val txtUserName = binding.navView.getHeaderView(0).findViewById<TextView>(R.id.txtUserName)
                    txtUserName.text = userData.getString("name")

                    val txtClassName = binding.navView.getHeaderView(0).findViewById<TextView>(R.id.txtClassName)
                    val oldText = txtClassName.text.toString()
                    when {
                        userData.getString("className").contains("10th") || userData.getString("className").contains("10 वीं") -> {
                            txtClassName.text = getString(com.app.tensquare.R.string.tenth)
                        }
                        userData.getString("className").contains("11th") || userData.getString("className").contains("11 वीं") -> {
                            txtClassName.text = getString(com.app.tensquare.R.string.eleven)
                        }
                        userData.getString("className").contains("12th") || userData.getString("className").contains("12 वीं") -> {
                            txtClassName.text = getString(com.app.tensquare.R.string.twelfth)
                        }
                        else -> {
                            txtClassName.text = userData.getString("className")
                        }
                    }

                    if (txtClassName.text.toString() != oldText) {
                        binding.layoutFooter.llHome.performClick()
                    }

                    val txtLogout = binding.navView.getHeaderView(0).findViewById<TextView>(R.id.txtLogout)
                    txtLogout.text = getString(R.string.logout)

                    val imgProfile = binding.navView.getHeaderView(0).findViewById<ImageView>(R.id.imgProfile)
                    if (userData.getString("profilePic").isNotEmpty()) {
                        Glide.with(this@HomeActivity)
                            .setDefaultRequestOptions(RequestOptions().priority(Priority.HIGH))
                            .load(userData.getString("profilePic"))
                            .placeholder(R.drawable.profile_place_holder)
                            .override(100, 100)
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imgProfile)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setHomeScreen() {
        binding.layoutFooter.llHome.setBackgroundResource(R.drawable.bg_circle_home_enabled)
        binding.layoutFooter.imgMyDownload.setImageResource(R.drawable.ic_my_download_off)
        binding.layoutFooter.txtMyDownload.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.gray_170
            )
        )

        binding.layoutFooter.imgSelfAnalysis.setImageResource(R.drawable.ic_home_self_analysis_off)
        binding.layoutFooter.txtSelfAnalysis.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.gray_170
            )
        )
    }

    private fun setMyDownLoadScreen() {
        binding.layoutFooter.llHome.setBackgroundResource(R.drawable.bg_circle_home_disabled)
        binding.layoutFooter.imgMyDownload.setImageResource(R.drawable.ic_my_download_on)
        binding.layoutFooter.txtMyDownload.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.black
            )
        )

        binding.layoutFooter.imgSelfAnalysis.setImageResource(R.drawable.ic_home_self_analysis_off)
        binding.layoutFooter.txtSelfAnalysis.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.gray_170
            )
        )
    }

    private fun setSelfAnalysisScreen() {
        binding.layoutFooter.llHome.setBackgroundResource(R.drawable.bg_circle_home_disabled)
        binding.layoutFooter.imgMyDownload.setImageResource(R.drawable.ic_my_download_off)
        binding.layoutFooter.txtMyDownload.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.gray_170
            )
        )

        binding.layoutFooter.imgSelfAnalysis.setImageResource(R.drawable.ic_home_self_analysis_on)
        binding.layoutFooter.txtSelfAnalysis.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.black
            )
        )
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

    private fun showDrawer(show: Boolean) {
        if (show)
            binding.drawerlayout.openDrawer(Gravity.LEFT)
        else
            binding.drawerlayout.closeDrawer(Gravity.LEFT)
    }

    var resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val isSomethingChanged =
                result.data?.getBooleanExtra("isSomethingChanged", false) == true
            if (isSomethingChanged) {
                changeFragment(HomeFragment(), "HomeFragment")
                setHomeScreen()
            }
        }
    }

    override fun notificationCountListener(count: Int) {
        binding.layoutFooter.ivNotifyAlert.visibility = if (count > 0) View.VISIBLE else View.GONE
    }


}