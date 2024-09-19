package com.app.tensquare.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.app.tensquare.BuildConfig
import com.app.tensquare.base.SharedPrefManager
import com.app.tensquare.ui.home.LanguageChangeListener
import com.app.tensquare.ui.home.NotificationCountListener
import com.app.tensquare.ui.subscription.EnrolmentPlanPricingNewActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import java.util.Locale

object AppUtills {

    var languageListener: LanguageChangeListener ?= null
    var notificationCountListener: NotificationCountListener?= null

    /*Use for firebase track event*/
    fun trackEvent(
        activity: Activity,
        itemName: String,
        eventName: String
    ) {
        val firebaseAnalytics = Firebase.analytics
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, itemName)
        firebaseAnalytics.logEvent(eventName,bundle)
        firebaseAnalytics.setCurrentScreen(activity,itemName,null)
    }

    fun activitySpendTime(startTime: Long, text: String, userName: String, userPhoneNumber: String) {
        val firebaseAnalytics = Firebase.analytics
        val params = Bundle()
        val timeDiff: Long = System.currentTimeMillis() - startTime
        val hours = (timeDiff / (1000 * 60 * 60)).toInt()
        val minutes = (timeDiff / (1000 * 60)).toInt() % 60
        val seconds = (timeDiff / 1000).toInt() % 60
        val time = "Hour $hours Minute $minutes Second $seconds"
        params.putString("total${text}_time", time)
        params.putString("user_name", userName)
        params.putString("mobile_number", userPhoneNumber)
        firebaseAnalytics.logEvent("${text}_time_spent", params)
    }

    /** USE FOR CHANGE STATUS BAR COLOR*/
//    @SuppressLint("NewApi")
    fun getWindowStatusBarColor(activity: AppCompatActivity, color: Int) {
        val window: Window = activity.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(activity, color)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    /*fun setLocaleLanguage(lang: String?, mContext: Context) {
        val resources = mContext.resources
        val dm: DisplayMetrics = resources.displayMetrics
        val config: Configuration = resources.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            config.setLocale(Locale(lang?.lowercase(Locale.getDefault())))
        } else {
            config.locale = Locale(lang?.lowercase(Locale.getDefault()))
        }
        resources.updateConfiguration(config, dm)

    }*/

    fun Context.redirectToEnrollment() {
        startActivity(Intent(this, EnrolmentPlanPricingNewActivity::class.java))
    }

    fun setLan(context: Context): Context {
        var prefs: SharedPrefManager = SharedPrefManager(context)

        val lan: String = prefs.getUserLanguage().toString()
        val locale: Locale
        locale = if (lan == "hi") {
            Locale("hi")
        }else {
            Locale("en")
        }
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        context.resources.updateConfiguration(
            config,
            context.resources.displayMetrics
        )
        return context
    }

    fun disableSsAndRecording(activity : Activity) {
        //below code will Disable Screenshot and Screen Recording in Android Application
        /*activity.getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE)*/    //TODO:Change this before release
    }

    fun isDebug(): Boolean {
        //return true;
        return BuildConfig.DEBUG
    }

    fun getDeviceId(context: Context): String? {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }


    fun getVersion(context: Context): String? {
        return try {
            val currentVersion =
                context.packageManager.getPackageInfo(context.packageName, 0).versionName
            val currentVersionCode =
                context.packageManager.getPackageInfo(context.packageName, 0).versionCode
            if (isDebug()) Log.e(
                "VERSION",
                currentVersion + "_" + currentVersionCode
            )
            currentVersion
        } catch (e: Exception) {
            e.printStackTrace()
            "0.0"
        }
    }

}