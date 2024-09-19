package com.app.tensquare.base

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.app.tensquare.R
import com.app.tensquare.ui.customview.CustomProgressBar
import com.app.tensquare.utils.LogUtil
import com.app.tensquare.utils.showToast
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
//import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject

@AndroidEntryPoint
open class BaseFragment : Fragment() {
    lateinit var context1: Context
    lateinit var viewContext: Context

    @Inject
    lateinit var prefs: SharedPrefManager

    //lateinit var prefs: ApplicationPrefs
    lateinit var pDialog: CustomProgressBar
    lateinit var callBackForRetry: CallBackForRetry
    private lateinit var snackbar: Snackbar

    // var folderName = ""
    override fun onCreate(arg0: Bundle?) {
        super.onCreate(arg0)
        context1 = requireActivity().applicationContext
        viewContext = requireActivity()
        //prefs = ApplicationPrefs(context1)
        checkPermission()
        initProgressDialog()
    }

    @SuppressLint("NewApi")
    private fun checkPermission() {
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
    }

    fun hideKeyBoard() {
        try {
            if (activity != null) {
                val imm: InputMethodManager =
                    requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                if (requireActivity().currentFocus != null) imm.hideSoftInputFromWindow(
                    requireActivity().currentFocus!!.windowToken, 0
                )
            }
        } catch (e: Exception) {
            LogUtil.debug2(e.localizedMessage)
            //FirebaseCrashlytics.getInstance().recordException(Exception(e))
        }
    }

    fun isOnline(callBackForRetry: CallBackForRetry): Boolean {
        this.callBackForRetry = callBackForRetry
        val conMgr = context1.applicationContext
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = conMgr.activeNetworkInfo
        if (netInfo == null || !netInfo.isConnected || !netInfo.isAvailable) {
            snackbar =
                Snackbar.make(getRootView(), "You're Offline", Snackbar.LENGTH_INDEFINITE)
            val textView =
                snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
            val button: Button =
                snackbar.view.findViewById(com.google.android.material.R.id.snackbar_action)
//            textView.setTextColor(resources.getColor(R.color.colorAccent))
            textView.setTextColor(resources.getColor(R.color.black))
//            textView.typeface = ResourcesCompat.getFont(context1, R.font.roboto_regular)
            snackbar.view.setBackgroundColor(resources.getColor(R.color.colorPrimary))
//            button.setTextColor(resources.getColor(R.color.colorAccent))
            button.setTextColor(resources.getColor(R.color.black))
//            button.typeface = ResourcesCompat.getFont(context1, R.font.roboto_regular)
            button.isAllCaps = false
            snackbar.setAction(R.string.retry,
                View.OnClickListener { callBackForRetry.onRetry() })
            hideKeyBoard()
            snackbar.show()
            val filter = IntentFilter()
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
            context1.registerReceiver(internetConnectionListener, filter)
            return false
        }
        return true
    }

    private fun getRootView(): View {
        var rootView: View? = null
        if (activity != null) {
            val contentViewGroup =
                requireActivity().findViewById<ViewGroup>(androidx.appcompat.R.id.content)
            if (contentViewGroup != null) rootView = contentViewGroup.getChildAt(0)
            if (rootView == null) rootView = requireActivity().window.decorView.rootView
            return rootView!!
        }
        return rootView!!
    }

    private val internetConnectionListener: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (cm.activeNetworkInfo != null) {
                if (cm.activeNetworkInfo!!.isConnected) {
                    callBackForRetry.onRetry()
                    context.unregisterReceiver(this)
                    if (snackbar.isShown) snackbar.dismiss()
                }
            }
        }
    }

    fun isOnline(): Boolean {
        val conMgr = context1.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = conMgr.activeNetworkInfo
        if (netInfo == null || !netInfo.isConnected || !netInfo.isAvailable) {
            val snackbar = Snackbar.make(getRootView(), "You're Offline", Snackbar.LENGTH_LONG)
            val textView =
                snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
            textView.setTextColor(resources.getColor(R.color.colorAccent))
//            textView.setTypeface(ResourcesCompat.getFont(context1, R.font.roboto_regular))
            textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
            snackbar.view.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            hideKeyBoard()
            snackbar.show()
            return false
        }
        return true
    }

    fun showMessage(msg: String?) {
        if (msg != null) requireActivity().showToast(msg)
    }

    private fun initProgressDialog() {
        pDialog = CustomProgressBar(viewContext)
        pDialog.setCancelable(true)
    }

    fun showProgressDialog() {
        if (!pDialog.isShowing) pDialog.show()
    }

    fun dismissProgressDialog() {
        if (pDialog.isShowing) pDialog.dismiss()
    }

    open fun getEditTextData(editText: EditText): String {
        return editText.text.toString().trim()
    }

    fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context1.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    /*fun showErrorMessage(error: VolleyError) {
        dismissProgressDialog()
        if (error.localizedMessage == null) {
            val response = error.networkResponse
            if (error is ServerError && response != null) {
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
        Toast.makeText(context1, R.string.error_message, Toast.LENGTH_SHORT).show()
    }*/
    fun removeNotification(orderId: Int) {
        val notificationManager =
            viewContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(orderId)
    }

    override fun onDestroy() {
        dismissProgressDialog()
        //        AppController.getInstance().cancelPendingRequests(this.getClass().getSimpleName());
        super.onDestroy()
        callGC()
    }

    private fun callGC() {
        System.gc()
        Runtime.getRuntime().gc()
    }

    fun hashData(text: String?): Boolean {
        return text != null && text.trim { it <= ' ' }.isNotEmpty()
    }
}