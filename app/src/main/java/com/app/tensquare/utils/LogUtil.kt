package com.app.tensquare.utils

import android.util.Log
import com.app.tensquare.BuildConfig

object LogUtil {

    private val TAG = "E-learningLogs"
    private val ERROR_RECEIVER_NOT_REGISTERED = "Receiver not registered"
    private val DEBUG: Boolean = BuildConfig.DEBUG
    private val DEBUG2: Boolean = BuildConfig.DEBUG
    private val DEBUG_ERROR: Boolean = BuildConfig.DEBUG

    public fun debug(msg: String) {
        if (DEBUG && msg != "") {
            Log.d(TAG, msg)
        }
    }

    public fun debug2(msg: Any?) {
        if (DEBUG2 && msg != null && msg != "") {
            Log.d(TAG, msg.toString())
        }
    }

    public fun error(msg: String) {
        if (DEBUG_ERROR) {
            Log.e(TAG, msg)
        }
    }

    public fun info(msg: String) {
        if (DEBUG) {
            Log.e(TAG, msg)
        }
    }

    public fun error(ex: Exception) {
        val msg = ex.message
        error(msg!!)
        if (msg.contains(ERROR_RECEIVER_NOT_REGISTERED)) {
            return
        }
        if (DEBUG_ERROR) {
            ex.printStackTrace()
        }
    }
}