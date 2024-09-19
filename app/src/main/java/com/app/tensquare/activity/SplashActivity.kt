package com.app.tensquare.activity

//import kotlinx.android.synthetic.main.activity_splash_screen.*
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import com.app.tensquare.HiltApplication
import com.app.tensquare.R
import com.app.tensquare.base.AppBaseActivity
import com.app.tensquare.customUiExample.CustomUiActivity
import com.app.tensquare.databinding.EnrolmentDialogBinding
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.ui.home.HomeActivity
import com.app.tensquare.ui.initialsetup.InitialViewModel
import com.app.tensquare.ui.initialsetup.SelectLanguageActivity
import com.app.tensquare.ui.pdf.PdfViewerActivity
import com.app.tensquare.utils.ACCESS_TOKEN_EXPIRED
import com.app.tensquare.utils.ACTION
import com.app.tensquare.utils.AppUtills
import com.app.tensquare.utils.MODULE_REVISION_VIDEO
import com.app.tensquare.utils.PDF_VIA_URL
import com.app.tensquare.utils.showToast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailabilityLight
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SplashActivity : AppBaseActivity() {
    // Splash screen timer
    private val SPLASH_TIME_OUT = 2000L
    companion object {
        var isLocaleChange = false
    }
//    private val SPLASH_TIME_OUT = 2800L
//    private val SPLASH_TIME_OUT = 1500L
    private val viewModel: InitialViewModel by viewModels()

    private var TAG: String = SplashActivity::class.java.simpleName

    private var notificationType: String? = ""

    var mediaControls: MediaController? = null

    var manager: AppUpdateManager? = null
    var task: Task<AppUpdateInfo>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        AppUtills.setLan(this)
        AppUtills.getWindowStatusBarColor(this, R.color.status_bar_color)
        setContentView(R.layout.activity_splash_screen)


        HiltApplication.context = this

        if (isLocaleChange) {
            isLocaleChange = false
            Intent(this@SplashActivity, HomeActivity::class.java).also {
                it.putExtra(ACTION, "")
                it.putExtra("notificationType" , notificationType)
                startActivity(it)
            }
            finish()
            return
        }

        isUserPinnedToHome = prefs.isUserPinnedToHome()

        try {
            if (getIntent().hasExtra("notificationType")){
                notificationType = getIntent().getStringExtra("notificationType").toString()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }


//        isOnline(object : CallBackForRetry{
//            override fun onRetry() {
//                showToast("ready")
//            }
//        })

        try {
//            currentVersion = this.packageManager.getPackageInfo(this.packageName, 0).versionName

            manager = AppUpdateManagerFactory.create(this)
            task = manager!!.appUpdateInfo

        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }


//        appVideoView?.setVideoURI(Uri.parse("android.resource://"
//                + packageName + "/" + R.raw.splash_video_12))

//        appVideoView?.requestFocus()
//
//        // starting the video
//        appVideoView?.start()
//        appVideoView?.setOnCompletionListener {
////            Toast.makeText(applicationContext, "Video completed",
////                Toast.LENGTH_LONG).show()
//        }
//
//        // display a toast message if any
//        // error occurs while playing the video
//        appVideoView?.setOnErrorListener { mp, what, extra ->
////            Toast.makeText(applicationContext, "An Error Occurred " +
////                    "While Playing Video !!!", Toast.LENGTH_LONG).show()
//            false
//        }

        //----------------------

        CoroutineScope(Dispatchers.IO).launch {
            FirebaseApp.initializeApp(this@SplashActivity)
        }
        //handleDeepLink()
        //init()
        Log.e("User_Token", prefs.getUserToken().toString())
        initObservers()

        CoroutineScope(Dispatchers.IO).launch {
            delay(SPLASH_TIME_OUT)

            handleDeepLink()
        }

    }


    override fun init() {



        Handler(Looper.getMainLooper()).postDelayed(
            {



                task?.addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
//                        Toast.makeText(this, "Update", Toast.LENGTH_SHORT).show()

                        Log.e("availableVersionCode","availableVersionCode -> ${appUpdateInfo.availableVersionCode()}")

                        showUpdateDialog()//TODO:CHAGE THIS


                    } else {
                        if (prefs.isUserPinnedToHome()) {
                            Intent(this@SplashActivity, HomeActivity::class.java).also {
                                it.putExtra(ACTION, "")
                                it.putExtra("notificationType" , notificationType)
                                startActivity(it)
                            }
                        } else {
                            Intent(this@SplashActivity, SelectLanguageActivity::class.java).also {
                                startActivity(it)
                            }
                        }
                        finish()

                    }
                }

                task?.addOnFailureListener { e ->
                    e.printStackTrace()

                    if (prefs.isUserPinnedToHome()) {
                        Intent(this@SplashActivity, HomeActivity::class.java).also {
                            it.putExtra(ACTION, "")
                            it.putExtra("notificationType" , notificationType)
                            startActivity(it)
                        }
                    } else {
                        Intent(this@SplashActivity, SelectLanguageActivity::class.java).also {
                            startActivity(it)
                        }
                    }
                    finish()

                }

            }, SPLASH_TIME_OUT
        )

    }

    private fun checkGooglePlayServices(): Boolean {
        // 1
        val status = GoogleApiAvailabilityLight.getInstance().isGooglePlayServicesAvailable(this)
        // 2
        return if (status != ConnectionResult.SUCCESS) {
            Log.e(TAG, "Error")
            // ask user to update google play services and manage the error.
            false
        } else {
            // 3
            Log.i(TAG, "Google play services updated")
            true
        }
    }

    override fun setListsAndAdapters() {
        TODO("Not yet implemented")
    }

    override fun setListeners() {
        TODO("Not yet implemented")
    }

    override fun initObservers() {
        viewModel.guestTokenResponse.observe(this) { response ->
            //dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    prefs.setUserToken(response.data?.accessToken)
                    prefs.setRefreshToken(response.data?.refreshToken)
                    Intent(this@SplashActivity, SelectLanguageActivity::class.java).also {
                        startActivity(it)
                    }
                    finish()
                }
                is NetworkResult.Error -> {
                    when (response.message) {
                        ACCESS_TOKEN_EXPIRED -> {
                            prefs.setUserToken(prefs.getRefreshToken())
                            viewModel.getRefreshToken(prefs.getUserToken().toString())
                        }
                        else ->
                            showToast(response.message)
                    }
                }
                is NetworkResult.Loading -> {
                    // show a progress bar
                }
            }
        }
        viewModel.refreshTokenResponse.observe(this) { response ->
            //dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    prefs.setUserToken(response.data?.accessToken)
                    prefs.setRefreshToken(response.data?.refreshToken)
                    Intent(this@SplashActivity, SelectLanguageActivity::class.java).also {
                        startActivity(it)
                    }
                    finish()
                }
                is NetworkResult.Error -> {
                    showToast(response.message)
                }
                is NetworkResult.Loading -> {
                    // show a progress bar
                }
            }
        }
    }


    private fun handleDeepLink() {
        try {
            FirebaseDynamicLinks.getInstance()
                .getDynamicLink(intent)
                .addOnSuccessListener(this) { pendingDynamicLinkData ->
                    // Get deep link from result (may be null if no link is found)
                    val deepLink: Uri?
                    if (pendingDynamicLinkData != null) {
                        deepLink = pendingDynamicLinkData.link
                        val intent = intent
                        val data = intent.data

                        try {
                            if (data.toString() != null && !data.toString()
                                    .equals("null", ignoreCase = true)
                            ) {

                                val type = deepLink!!.getQueryParameter("name")
                                var id: String? = "";
                                var urlfetch : String? = "";
                                if (type == "resource") {
                                    id = deepLink.lastPathSegment!!.toString()
                                }else{
                                    val pathSegments: List<String> = deepLink.pathSegments
                                    val lastpathSegment = deepLink.pathSegments.size

                                    if (lastpathSegment == 2){

                                        Log.e("last Part =>" , deepLink.pathSegments[lastpathSegment-1].toString())
                                        Log.e("second Last = > " , deepLink.pathSegments[lastpathSegment-2].toString())

                                        id = deepLink.pathSegments[lastpathSegment-2].toString()
                                        urlfetch =  deepLink.pathSegments[lastpathSegment-1].toString()

                                    }else if (lastpathSegment == 3){

                                        Log.e("last Part =>" , deepLink.pathSegments[lastpathSegment-1].toString())
                                        Log.e("second Last = > " , deepLink.pathSegments[lastpathSegment-2].toString())
                                        Log.e("third Last = > " , deepLink.pathSegments[lastpathSegment-3].toString())

                                        id = deepLink.pathSegments[lastpathSegment-3].toString()
                                        urlfetch =  deepLink.pathSegments[lastpathSegment-2].toString() +
                                                "/" + deepLink.pathSegments[lastpathSegment-1].toString()
                                    }

                                }

//                                Log.e("Paths => " , deepLink.toString())
//                                Log.e("Path segment size => " , deepLink.pathSegments.size.toString())

                                try {
                                    if (id!!.isNotEmpty()) {
                                        val firstTime = prefs.isUserPinnedToHome()

                                        //if (!prefs.isGuestUser()) {
                                        if (prefs.isUserPinnedToHome()) {
//                                            if (prefs.isEnrolled()) {
                                                if (type == "resource") {
                                                    Intent(
                                                        this@SplashActivity,
                                                        CustomUiActivity::class.java
                                                    ).also {
                                                        it.putExtra(
                                                            "module",
                                                            MODULE_REVISION_VIDEO
                                                        )
                                                        it.putExtra("video_id", id)
                                                        it.putExtra("title", "")
                                                        startActivity(it)
                                                    }
                                                } else {
                                                    Intent(
                                                        this@SplashActivity,
                                                        PdfViewerActivity::class.java
                                                    ).also {
                                                        val url =
                                                            "https://bhanulearning.s3.ap-south-1.amazonaws.com/pdf/$id"
                                                        it.putExtra("file", url)
                                                        it.putExtra("VIA", PDF_VIA_URL)
                                                        it.putExtra("deepLink" , id)
                                                        it.putExtra("urlfetch" , urlfetch)
                                                        startActivity(it)
                                                    }
                                                }

                                        } else {
                                            Toast.makeText(
                                                this,
                                                getString(com.app.tensquare.R.string.plz_login_first),
                                                Toast.LENGTH_LONG
                                            ).show()
                                            Intent(
                                                this@SplashActivity,
                                                SelectLanguageActivity::class.java
                                            ).also {
                                                startActivity(it)
                                            }
                                        }
                                        finish()


                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                assert(deepLink != null)
                                Log.e("data=>", deepLink.toString())
                            } else {
                                init()
                            }
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        init()
                    }
                }
                .addOnFailureListener(this) { e -> Log.w("TAG", "getDynamicLink:onFailure", e) }

        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }


    }

    // check with the package name
    // if app is available or not
    private fun available(packageName: String): Boolean {
        var available = true
        try {
            // check if available
            packageManager.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            // if not available set
            // available as false
            available = false
        }
        return available
    }

    var mDialog: Dialog? = null
    var isUserPinnedToHome : Boolean = false




    private fun showUpdateDialog() {

        try {

            mDialog = Dialog(this@SplashActivity)

            val layoutInflater = LayoutInflater.from(this@SplashActivity)
            val dialogBinding: EnrolmentDialogBinding =
                EnrolmentDialogBinding.inflate(layoutInflater)

            mDialog?.setContentView(dialogBinding.root)
            mDialog?.setCanceledOnTouchOutside(false)
            mDialog?.setCancelable(false)
            mDialog?.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            mDialog?.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            mDialog?.window?.setGravity(Gravity.CENTER)

            dialogBinding.imgClose.visibility = View.INVISIBLE


            dialogBinding.txtMessage.text = getString(com.app.tensquare.R.string.forcefully_update_mgs)
            dialogBinding.txtChoosePlan.text = getString(com.app.tensquare.R.string.update_btn)


            dialogBinding.txtChoosePlan.setOnClickListener {
                openPlayStore()
            }

            mDialog?.setOnCancelListener {
            }

            mDialog?.show()

        }catch (e: Exception){
            e.printStackTrace()
        }

    }


    private fun openPlayStore() {

        val appPackageName = this@SplashActivity.packageName // package name of the app
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                )
            )
        } catch (e: java.lang.Exception) {
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(
                            "https://play.google.com/store/apps/details?id="
                                    + appPackageName
                        )
                    )
                )
            } catch (ex: java.lang.Exception) {
                ex.printStackTrace()
            }
        }

    }


}