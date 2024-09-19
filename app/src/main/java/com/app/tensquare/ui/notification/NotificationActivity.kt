package com.app.tensquare.ui.notification

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.tensquare.adapter.NotificationAdapter
import com.app.tensquare.base.AppBaseActivity
import com.app.tensquare.databinding.ActivityNotificationBinding
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.ui.chapter.ChaptersInSubjectActivity
import com.app.tensquare.ui.home.HomeActivity
import com.app.tensquare.ui.home.HomeViewModel
import com.app.tensquare.ui.login.LoginActivity
import com.app.tensquare.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationActivity : AppBaseActivity() {
    private lateinit var binding: ActivityNotificationBinding
    private lateinit var notificationAdapter: NotificationAdapter
    private var notificationList = mutableListOf<NotificationData>()
    private val viewModel: NotificationViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        initObservers()
        setListeners()
        //getDataFromServer()


    }

    override fun init() {
        binding.rvModelPaper.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            notificationAdapter =
                NotificationAdapter(this@NotificationActivity) { list: MutableList<NotificationData>, position: Int, notification: NotificationData ->
                    val deleteNotificationReq = NotificationDeleteRequest(
                        id = notification.id,
                        userId = prefs.getUserId().toString()
                    )
                    showProgressDialog()
                    viewModel.deleteNotification(deleteNotificationReq)

                    list.removeAt(position)

                    (this.adapter as NotificationAdapter).submitList(list)

                    if (list.isEmpty())
                        binding.txtNoData.visibility = View.VISIBLE
                }

            adapter = notificationAdapter
        }

        showProgressDialog()
        viewModel.getNotificationList("1")
    }

    override fun setListsAndAdapters() {
        TODO("Not yet implemented")
    }

    override fun setListeners() {
        binding.imgBack.setOnClickListener { finish() }
    }

    override fun initObservers() {
        homeViewModel.refreshTokenResponse.observe(this){
            when (it){
                is NetworkResult.Loading -> {}
                is NetworkResult.Success -> {
                    dismissProgressDialog()
                    it.data?.let {data ->

                        Log.e("token_Home = >" , data.accessToken.toString())
                        prefs.setUserToken(data.accessToken)
                        prefs.setRefreshToken(data.refreshToken)
                        finish()
                        startActivity(Intent(this, ChaptersInSubjectActivity::class.java))
                    }
                }
                is NetworkResult.Error -> {
                    dismissProgressDialog()
                    if (it.message == REFRESH_TOKEN_EXPIRED ) {
//                            requireActivity().showToast("2")
                        Intent(this, LoginActivity::class.java).also {
                            startActivity(it)
                        }
                        finish()
                    }else if (it.message == OTHER_DEVISE_LOGIN) {

                        val intent =  Intent(this, LoginActivity::class.java)
                        intent.putExtra("Expired", "Expired")
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()

                    } else showToast(it.message)

//                            requireActivity().showToast("3")

                }
            }
        }

        viewModel.notificationList.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    val data = response.data
                    notificationList = data?.list as MutableList<NotificationData>
                    notificationAdapter.submitList(notificationList)
                    if (notificationList.isNotEmpty())
                        notificationAdapter.submitList(notificationList)
                    else
                        binding.txtNoData.visibility = View.VISIBLE

                    AppUtills.notificationCountListener?.notificationCountListener(0)
//                    HomeActivity.ivNotifyAlert!!.visibility = View.GONE

                }
                is NetworkResult.Error -> {
                    //handleApiError(response, binding.root, "Invalid Mobile No. or Password")
                    /* if (response.message == ACCESS_TOKEN_EXPIRED)
                     else if (response.message == REFRESH_TOKEN_EXPIRED) {
                         Intent(this, LoginActivity::class.java).also {
                             startActivity(it)
                         }
                         finish()
                     } else*/

                    when (response.message) {
                        ACCESS_TOKEN_EXPIRED -> {
//                            prefs.setUserToken(prefs.getRefreshToken())
//                            viewModel.getRefreshToken(prefs.getRefreshToken().toString())
                            homeViewModel.getRefreshToken(prefs.getRefreshToken().toString())
//                            showMessage("1")
                        }
                        REFRESH_TOKEN_EXPIRED -> {
//                           Intent(requireActivity(), LoginActivity::class.java).also {
//                                startActivity(it)
//                            }
//                            requireActivity().finish()
//                            generateNewToken()    // JACK
                        }
                        OTHER_DEVISE_LOGIN ->{
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
                        else->{
//                            requireActivity().showToast(response.message)
//                            prefs.setUserToken(prefs.getRefreshToken())
//                            viewModel.getRefreshToken()
//                            viewModel.getRefreshToken(prefs.getUserToken().toString())
//                            generateNewToken()
//                            showMessage("2")
                        }

                    }
                }
                is NetworkResult.Loading -> {
                    // show a progress bar
                }
                else -> {}
            }
        }

        viewModel.deleteNotification.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    showToast(response.message)
                    /*notificationList = response.data?.list as MutableList<NotificationData>
                    notificationAdapter.submitList(notificationList)*/
                }
                is NetworkResult.Error -> {
                    when (response.message) {
                        ACCESS_TOKEN_EXPIRED -> {
//                            prefs.setUserToken(prefs.getRefreshToken())
//                            viewModel.getRefreshToken(prefs.getRefreshToken().toString())
                            homeViewModel.getRefreshToken(prefs.getRefreshToken().toString())
//                            showMessage("1")
                        }
                        REFRESH_TOKEN_EXPIRED -> {
//                           Intent(requireActivity(), LoginActivity::class.java).also {
//                                startActivity(it)
//                            }
//                            requireActivity().finish()
//                            generateNewToken()    // JACK
                        }
                        OTHER_DEVISE_LOGIN ->{
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
                        else->{
//                            requireActivity().showToast(response.message)
//                            prefs.setUserToken(prefs.getRefreshToken())
//                            viewModel.getRefreshToken()
//                            viewModel.getRefreshToken(prefs.getUserToken().toString())
//                            generateNewToken()
//                            showMessage("2")
                        }

                    }
                }
                is NetworkResult.Loading -> {
                    // show a progress bar
                }
            }
        }
    }

    /*private fun getDataFromServer() {
        for (i in 0..5) {
            val subject = PracticeSession()
            sessionList.add(subject)
        }
        notificationAdapter.submitList(sessionList)
    }*/


}


