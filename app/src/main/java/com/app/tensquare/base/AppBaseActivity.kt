package com.app.tensquare.base

import android.content.Context
import com.app.tensquare.utils.LocaleHelper

abstract class AppBaseActivity : BaseActivity() {
    /*fun updateToolbar(title: String) {
        findViewById<TextView>(R.id.textViewToolbarTitle).text = title
        findViewById<ImageView>(R.id.imageViewBack).setOnClickListener { onBackPressed() }
    }*/

    /*fun visibleSkipButton(title: String) {
        updateToolbar(title)
        findViewById<TextView>(R.id.textViewSkip).visibility = View.VISIBLE
    }*/

    fun getParam(): HashMap<String, String> {
        return HashMap()
    }

    fun getLoginParam(): HashMap<String, String> {
        val param = HashMap<String, String>()
        param["customer_id"] = prefs.getUserId()!!
        param["Authorization"] = "Bearer " + prefs.getUserToken()!!
        return param
    }

    abstract fun init()
    abstract fun setListsAndAdapters()
    abstract fun setListeners()
    abstract fun initObservers()
    /*open fun saveUserDetails(fetchedDetails: UserData) {
        prefs.setUserId(fetchedDetails.id)
        prefs.setUserDetails(fetchedDetails)
        prefs.setUserToken(fetchedDetails.accessToken)
    }*/
}
