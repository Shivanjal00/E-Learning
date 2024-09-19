package com.app.tensquare.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.app.tensquare.R
import com.app.tensquare.ui.customview.CustomProgressBar
import com.app.tensquare.utils.showToast
import com.google.android.material.snackbar.Snackbar
import com.google.android.youtube.player.YouTubeBaseActivity
import javax.inject.Inject

open class BaseYoutubeActivity : YouTubeBaseActivity() {
    lateinit var context: Context
    lateinit var viewContext: Context

    @Inject
    lateinit var prefs: SharedPrefManager

    lateinit var pDialog: CustomProgressBar
    lateinit var callBackForRetry: CallBackForRetry
    private lateinit var snackbar: Snackbar
    var folderName = "E-learning"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = applicationContext
        //FirebaseApp.initializeApp(context)
        //prefs = SharedPrefManager(context)
        folderName = getString(R.string.app_name)
        viewContext = this
        initProgressDialog()
    }

    private var doubleBackToExitPressedOnce = false
    fun askForExit() {
        if (doubleBackToExitPressedOnce) {
            setResult(RESULT_CANCELED)
            finishAffinity()
            return
        }
        doubleBackToExitPressedOnce = true
//        showToast("Press again to exit $folderName.")// JACK
        showToast(getString(R.string.press_again_to_exit) + "$folderName.")
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    fun isConnectingToInternet(): Boolean {
        val connectivity = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivity != null) {
            val info = connectivity.allNetworkInfo
            if (info != null) for (anInfo in info) if (anInfo.state == NetworkInfo.State.CONNECTED) {
                return true
            }
        }
        return false
    }

    private fun initProgressDialog() {
        pDialog = CustomProgressBar(this)
        pDialog.setCancelable(false)
    }

    fun showProgressDialog() {
        if (!pDialog.isShowing) pDialog.show()
    }

    fun dismissProgressDialog() {
        if (pDialog.isShowing) pDialog.dismiss()
    }

    fun isOnline(callBackForRetry: CallBackForRetry): Boolean {
        this.callBackForRetry = callBackForRetry
        val conMgr =
            applicationContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = conMgr.activeNetworkInfo
        if (netInfo == null || !netInfo.isConnected || !netInfo.isAvailable) {
            snackbar = Snackbar.make(getRootView()!!, "You're Offline", Snackbar.LENGTH_INDEFINITE)
            val textView =
                snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
            val button =
                snackbar.view.findViewById<Button>(com.google.android.material.R.id.snackbar_action)
            textView.setTextColor(resources.getColor(R.color.colorAccent))
//            textView.typeface = ResourcesCompat.getFont(this, R.font.roboto_regular)
            snackbar.view.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            button.setTextColor(resources.getColor(R.color.colorAccent))
//            button.typeface = ResourcesCompat.getFont(this, R.font.roboto_regular)
            button.isAllCaps = false
            snackbar.setAction(R.string.retry,
                View.OnClickListener { callBackForRetry.onRetry() })
            hideKeyBoard()
            snackbar.show()
            val filter = IntentFilter()
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
            registerReceiver(internetConnectionListener, filter)
            return false
        }
        return true
    }

    private fun getRootView(): View? {
        val contentViewGroup = findViewById<ViewGroup>(android.R.id.content)
        var rootView: View? = null
        if (contentViewGroup != null) rootView = contentViewGroup.getChildAt(0)
        if (rootView == null) rootView = window.decorView.rootView
        return rootView
    }

    private val internetConnectionListener: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            if (cm.activeNetworkInfo != null) {
                if (cm.activeNetworkInfo!!.isConnected) {
                    callBackForRetry.onRetry()
                    unregisterReceiver(this)
                    if (snackbar.isShown) snackbar.dismiss()
                }
            }
        }
    }

    fun isOnline(): Boolean {
        val conMgr =
            applicationContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = conMgr.activeNetworkInfo
        if (netInfo == null || !netInfo.isConnected || !netInfo.isAvailable) {
            val snackbar = Snackbar.make(getRootView()!!, "You're Offline", Snackbar.LENGTH_LONG)
            val textView =
                snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
            textView.setTextColor(resources.getColor(R.color.colorAccent))
//            textView.setTypeface(ResourcesCompat.getFont(this, R.font.roboto_regular))
            textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
            snackbar.view.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            hideKeyBoard()
            snackbar.show()
            return false
        }
        return true
    }

    protected fun hashData(text: String?): Boolean {
        return text != null && text != ""
    }

/*public boolean hasData(String... datas) {
        boolean hasData = true;

        for (String data : datas) {
            if (!hasData(data)) {
                hasData = false;
                break;
            }
        }
        return hasData;
    }

    public boolean hasData(String text) {
        return !(text == null || text.trim().length() == 0);
    }*//*
*/
/*

    *//*

*/
/*public boolean hasData(String... datas) {
        boolean hasData = true;

        for (String data : datas) {
            if (!hasData(data)) {
                hasData = false;
                break;
            }
        }
        return hasData;
    }

    public boolean hasData(String text) {
        return !(text == null || text.trim().length() == 0);
    }*/

    fun isEmpty(text: String?): Boolean {
        return text == null || text.trim { it <= ' ' }.isEmpty()
    }

    fun isNotEmpty(text: String?): Boolean {
        return !isEmpty(text)
    }

    fun getEditTextData(editText: EditText): String {
        return editText.text.toString().trim()
    }

    /*fun showMessage(msg: String?) {
        if (msg != null) Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }*/


/*fun showErrorMessage(error: VolleyError) {
        dismissProgressDialog()
        if (error.localizedMessage == null) {
            val response: NetworkResponse = error.networkResponse
            if (error is ServerError) {
                try {
                    val res = String(
                        response.data,
                        charset = Charsets.UTF_8
                    )
                    LogUtil.debug("--- $res")
                    FirebaseCrashlytics.getInstance()
                        .recordException(Exception(Utils.preparedLog(res)))
                } catch (e1: UnsupportedEncodingException) {
                    e1.printStackTrace()
                }
            }
        } else {
            LogUtil.debug("--- " + error.localizedMessage)
            FirebaseCrashlytics.getInstance()
                .recordException(Exception(Utils.preparedLog(error.localizedMessage)))
        }
        Toast.makeText(context, R.string.error_message, Toast.LENGTH_SHORT).show()
    }*/

    fun hideKeyBoard() {
        try {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            if (imm != null) {
                val view = currentFocus
                if (view != null) imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        } catch (ignored: Exception) {
        }
    }

    override fun onDestroy() {
        dismissProgressDialog()
//        AppController.instance.cancelPendingRequests(this.javaClass.simpleName)
//        context = null
//        viewContext = null
//        prefs = null
//        pDialog = null
        super.onDestroy()
        callGC()
    }

    fun callGC() {
        System.gc()
        Runtime.getRuntime().gc()
    }
}
