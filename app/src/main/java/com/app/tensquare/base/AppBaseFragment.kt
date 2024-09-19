package com.app.tensquare.base

import android.view.View

abstract class AppBaseFragment : BaseFragment() {
    abstract fun init(view: View)
    abstract fun setListsAndAdapters()
    abstract fun setListeners()
    abstract fun initObservers()
    fun getParam(): HashMap<String, String> {
        return HashMap()
    }

    fun getLoginParam(): HashMap<String, String> {
        val param = HashMap<String, String>()
        param["customer_id"] = prefs.getUserId()!!
        param["Authorization"] = "Bearer " + prefs.getUserToken()!!
        return param
    }
}