package com.app.tensquare.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.util.*


fun <A : Activity> Activity.startNewActivity(activity: Class<A>) {
    Intent(this, activity).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
    }
}


fun <A : Activity> Activity.openActivity(activity: Class<A>) {
    Intent(this, activity).also {
        startActivity(it)
    }
}


fun <A : Activity> Fragment.openActivity(activity: Class<A>) {
    Intent(requireContext(), activity).also {
        startActivity(it)
    }
}

fun <T> Activity.openActivity(it: Class<T>, extras: Bundle.() -> Unit = {}) {
    val intent = Intent(this, it)
    intent.putExtras(Bundle().apply(extras))
    startActivity(intent)
}

/*------------------------------------- Display Snackbar ------------------------------*/

fun View.snackbar(message: String?) {
    val snackbar = Snackbar.make(this, message ?: "", Snackbar.LENGTH_LONG)
    snackbar.show()
}

/*------------------------------------- Display Toast ------------------------------*/

fun Context.showToast(message: String?) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

/*fun Activity.showToast(message: String?) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}*/

fun AppCompatActivity.showToast(message: String?) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun AppCompatActivity.showToastCenter(message: String?) {
//    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
    toast.setGravity(Gravity.CENTER, 0, 0)

//    val layout = toast.view as LinearLayout
//    if (layout.childCount > 0) {
//        val tv = layout.getChildAt(0) as TextView
//        tv.SetForegroundGravity(GravityFlags.CenterVertical or GravityFlags.CenterHorizontal)
//        //tv.TextAlignment = Android.Views.TextAlignment.Center;
//    }

    toast.show()
}


fun AppCompatActivity.SnackbarMessage(strMessage: String) {
    val view: View = findViewById(android.R.id.content)
    Snackbar.make(view, strMessage, Snackbar.LENGTH_LONG).show()
}

fun Activity.SnackbarMessage(strMessage: String) {
    val view: View = findViewById(android.R.id.content)
    Snackbar.make(view, strMessage, Snackbar.LENGTH_LONG).show()
}

fun Activity.getFile(mimetype: String, documentUri: Uri): File {
    val inputStream = contentResolver?.openInputStream(documentUri)
    var file: File

    inputStream.use { input ->

        if (mimetype.lowercase(Locale.getDefault()) == "image/jpeg")
            file = File(cacheDir, System.currentTimeMillis().toString() + ".jpeg")
        else if (mimetype.lowercase(Locale.getDefault()) == "image/png")
            file = File(cacheDir, System.currentTimeMillis().toString() + ".png")
        else
            file = File(cacheDir, System.currentTimeMillis().toString() + ".pdf")

        inputStream?.copyTo(file.outputStream())

    }
    return file
}

/*------------------------------------- Translate Error Code ------------------------------*/


/*fun AppCompatActivity.handleApiError(
    failure: NetworkResult.Error,
    view: View? = null,
    errorMessage: String? = null
) {

    when (failure.isNetworkError) {
        true -> {
            showToast(failure.errorBody.toString())
            Log.d("Handle API ERROR", "handleApiError: ${failure.errorBody}")
        }
        else -> {
            errorMessage?.let {
                showToast(it)
            }
            run {
                showToast(
                    translateCode(this, failure.errorCode)
                )
            }
        }
    }

}*/

fun <T> translateCode(instance: T? = null, code: Int?): String {

    return when (code) {

        400 -> "Invalid Parameters"
        404 -> "Not Found"
        409 -> "Account is already Registered"
        401 -> "Invalid Mobile No. or Password"
        403 -> "Account Approval Pending"
        422 -> "Invalid Parameters"
        423 -> "Current Password is Invalid"
        429 -> "Too many requests"
        500 -> "Server Error"
        503 -> "Service Unavailable"
        else -> "Something went wrong"

    }

}