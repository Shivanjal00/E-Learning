package com.app.tensquare.ui.pdf

import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.app.tensquare.R
import com.app.tensquare.adapter.QuestionBankPaperAdapter
import com.app.tensquare.base.AppBaseActivity
import com.app.tensquare.base.SharedPrefManager
import com.app.tensquare.constants.NetworkConstants
import com.app.tensquare.databinding.ActivityPdfViewerBinding
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.ui.customview.CustomProgressBar
import com.app.tensquare.ui.home.HomeViewModel
import com.app.tensquare.ui.initialsetup.InitialViewModel
import com.app.tensquare.ui.login.LoginActivity
import com.app.tensquare.ui.questionbankpaper.QuestionBankPaperViewModel
import com.app.tensquare.ui.questionbankpaper.questionList
import com.app.tensquare.ui.subscription.EnrolmentPlanPricingNewActivity
import com.app.tensquare.utils.*
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

@AndroidEntryPoint
class PdfViewerActivity : AppBaseActivity() {
    lateinit var binding: ActivityPdfViewerBinding

    private val viewModel: PdfViewerViewModel by viewModels()
    private val initialViewModel:InitialViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    private lateinit var fileAbsPath: String
    private var requestQueue: RequestQueue? = null
    private var requestQueue_2: RequestQueue? = null
    private var urlSubDom : String? = "";

    private var deviceId: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfViewerBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        try {
            deviceId = AppUtills.getDeviceId(this@PdfViewerActivity)
            Log.e("DeviceId  ==> " , deviceId.toString())
        }catch (e : Exception){
            e.printStackTrace()
        }

        prefs = SharedPrefManager(this@PdfViewerActivity)
        requestQueue = Volley.newRequestQueue(this@PdfViewerActivity)
        requestQueue_2 = Volley.newRequestQueue(this@PdfViewerActivity)

        AppUtills.disableSsAndRecording(this@PdfViewerActivity)

        init()
//        setListeners()
        initObservers()




        binding.imgBack.setOnClickListener {
            finish()
        }

//        showProgressDialog()

    }
    override fun init() {

        fileAbsPath = intent.getStringExtra("file").toString()
        Log.e("FileName ==> " , fileAbsPath);

        if (getIntent().hasExtra("deepLink")){
            if (intent.getStringExtra("deepLink").toString().isNotEmpty() &&
                intent.getStringExtra("urlfetch").toString().isNotEmpty()){
                showProgressDialog()
//                checkEnrolled(intent.getStringExtra("deepLink").toString() , intent.getStringExtra("urlfetch").toString())

                viewModel.getVerifyUserEnrollmentPlanResponse(intent.getStringExtra("deepLink").toString() ,
                    intent.getStringExtra("urlfetch").toString())

            }
        }else{

            if (intent.getIntExtra("VIA", 0) == PDF_VIA_URL) {
                RetrivePDFfromUrl().execute(fileAbsPath)

                showProgressDialog()
            } else {
                val file = File(fileAbsPath)
                val uri = Uri.fromFile(file)
                binding.pdfView.fromUri(uri).load()
            }

        }

    }

    override fun setListeners() {
        TODO("Not yet implemented")
    }

    override fun initObservers() {
        urlSubDom = ""
        viewModel.getVerifyUserEnrollmentPlanResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    val data = response.data

                    if (data!!.enrollmentPlanStatus){


                        if (data.url.isNotEmpty()){
                            urlSubDom = data.url;
                            val urlPdf : String =
                                "https://bhanulearning.s3.ap-south-1.amazonaws.com/" + data.url
//                                "https://bhanulearning.s3.ap-south-1.amazonaws.com/pdf/" + data.url
                            RetrivePDFfromUrl().execute(urlPdf)

                        }else{

                            dismissProgressDialog()
//
//                            showToastCenter(getString(com.app.tensquare.R.string.please_enrol))
//                            Intent(
//                                this@PdfViewerActivity,
//                                EnrolmentPlanPricingNewActivity::class.java
//                            ).also {
//                                startActivity(it)
//                            }
//                            finish()

                        }


                    }else{


                        dismissProgressDialog()

                        showToastCenter(getString(com.app.tensquare.R.string.please_enrol))
                        Intent(
                            this@PdfViewerActivity,
                            EnrolmentPlanPricingNewActivity::class.java
                        ).also {
                            startActivity(it)
                        }
                        finish()

                    }

                }
                is NetworkResult.Error -> {
//                    showToast(response.message)
                    when (response.message) {
                        ACCESS_TOKEN_EXPIRED -> {
                            generateNewToken()
                        }
                        else->{
                            if (response.message != OTHER_DEVISE_LOGIN) {
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
                        }

                    }

                }
                is NetworkResult.Loading -> {
                    // show a progress bar
                }
            }
        }

        initialViewModel.refreshTokenResponse.observe(this){
            when (it){
                is NetworkResult.Loading -> {}
                is NetworkResult.Success -> {
                    dismissProgressDialog()
                    it.data?.let { data ->

                        Log.e("token_Home = >" , data.accessToken.toString())
                        prefs.setUserToken(data.accessToken.toString())
                        prefs.setRefreshToken(data.refreshToken.toString())
                        showProgressDialog()
//                        checkEnrolled(intent.getStringExtra("deepLink").toString() ,
//                            intent.getStringExtra("urlfetch").toString())
                        viewModel.getVerifyUserEnrollmentPlanResponse(intent.getStringExtra("deepLink").toString() ,
                            intent.getStringExtra("urlfetch").toString())
                    }
                }
                is NetworkResult.Error -> {
                    dismissProgressDialog()
                    if (it.message == REFRESH_TOKEN_EXPIRED ) {
//                            requireActivity().showToast("2")
                        val intent =  Intent(this, LoginActivity::class.java)
                        intent.putExtra("Expired", "Expired")
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        val broadcast = Intent(LOG_OUT)
                        sendBroadcast(broadcast)
                        startActivity(intent)
                        finish()
                    }else if (it.message == OTHER_DEVISE_LOGIN) {

                        val intent =  Intent(this, LoginActivity::class.java)
                        intent.putExtra("Expired", "Expired")
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        val broadcast = Intent(LOG_OUT)
                        sendBroadcast(broadcast)
                        startActivity(intent)
                        finish()

                    } else showToast(it.message)

//                            requireActivity().showToast("3")

                }
            }
        }

    }



    private fun checkEnrolled(id: String , urlSend: String) {

        Log.e("id & url => " , id + " " + urlSend)

//        val url =  NetworkConstants.BASE_URL +  "api/notesResources/verifyUserEnrollmentPlan/" + id;
        val url =  NetworkConstants.BASE_URL +  "api/notesResources/verifyUserEnrollmentPlan"
        Log.e("Url = > " ,url.toString())
        val strReq: StringRequest = object : StringRequest(
            Request.Method.GET,url,
            Response.Listener { response ->

                // Handle Server response here
                try {
                    val responseObj = JSONObject(response)
//                    val isSuccess = responseObj.getBoolean("isSuccess")
                    Log.e("TOKEN API RESPOSE = >" , response.toString())
                    if (responseObj.getString("status") == STATUS_SUCCESS){

                        val objData = responseObj.getJSONObject("data")

                        if (objData.getBoolean("enrollmentPlanStatus")){

                            if (intent.getIntExtra("VIA", 0) == PDF_VIA_URL) {
                                RetrivePDFfromUrl().execute(fileAbsPath)
                            } else {
                                val file = File(fileAbsPath)
                                val uri = Uri.fromFile(file)
                                binding.pdfView.fromUri(uri).load()
                            }

                        }else{

                            dismissProgressDialog()

                            showToastCenter(getString(com.app.tensquare.R.string.please_enrol))
                            Intent(
                                this@PdfViewerActivity,
                                EnrolmentPlanPricingNewActivity::class.java
                            ).also {
                                startActivity(it)
                            }
                            finish()

                        }

                    }else{

                        dismissProgressDialog()

                        if (responseObj.getString("status") == ACCESS_TOKEN_EXPIRED){
                            generateNewToken()
                        }

                    }


                } catch (e: Exception) { // caught while parsing the response
                    e.printStackTrace()
                    dismissProgressDialog()
                }
            },
            Response.ErrorListener { volleyError -> // error occurred
                dismissProgressDialog()
            }) {

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers: MutableMap<String, String> = HashMap()
                headers.put("token" , prefs.getUserToken().toString())
                Log.e("get Token = >" , headers.toString())
                return headers
            }

            override fun getParams(): MutableMap<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["subjectId"] = id
                params["url"] = urlSend
                return params
            }

//                    @Throws(AuthFailureError::class)
//            override fun getParams(): Map<String, String> {
//                val params: MutableMap<String, String> = HashMap()
//                params["subjectId"] = id
//                params["url"] = urlSend
//                return params
//            }

        }
        // Adding request to request queue
        requestQueue?.add(strReq)

    }


    private fun generateNewToken() {
        initialViewModel.getRefreshToken(prefs.getRefreshToken().toString())
    }


    inner class RetrivePDFfromUrl : AsyncTask<String?, Void?, InputStream?>() {
        override fun doInBackground(vararg p0: String?): InputStream? {
            // we are using inputstream
            // for getting out PDF.
            var inputStream: InputStream? = null
            try {
                val url = URL(p0[0])
                // below is the step where we are
                // creating our connection.
                val urlConnection: HttpURLConnection = url.openConnection() as HttpsURLConnection
                if (urlConnection.responseCode === 200) {
                    // response is success.
                    // we are getting input stream from url
                    // and storing it in our variable.
                    inputStream = BufferedInputStream(urlConnection.inputStream)
                }
            } catch (e: Exception) {
                // this is the method
                // to handle errors.
                e.printStackTrace()

//                val urlPdf : String =
//                    "https://bhanulearning.s3.ap-south-1.amazonaws.com/latestUpdate/" + urlSubDom
//                RetrivePDFfromUrl().execute(urlPdf)

                return null
            }
            return inputStream
        }

        override fun onPostExecute(inputStream: InputStream?) {
            // after the execution of our async
            // task we are loading our pdf in our pdf view.
//            dismissProgressDialog()
//            binding.pdfView.fromStream(inputStream).load()
//            binding.pdfView.fromStream(inputStream).load()

            binding.pdfView.fromStream(inputStream).onLoad(object : OnLoadCompleteListener {
                override fun loadComplete(nbPages: Int) {
                    dismissProgressDialog()
                }
            }).load()

        }
    }


    override fun setListsAndAdapters() {
        TODO("Not yet implemented")
    }


}