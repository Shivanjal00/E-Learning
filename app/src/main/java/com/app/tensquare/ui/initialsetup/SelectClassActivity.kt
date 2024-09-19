package com.app.tensquare.ui.initialsetup

import android.app.ActionBar.LayoutParams
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.PopupWindow
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.app.tensquare.R
import com.app.tensquare.base.AppBaseActivity
import com.app.tensquare.databinding.ActivitySelectClassBinding
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.ui.login.LoginActivity
import com.app.tensquare.ui.login.SignUpActivity
import com.app.tensquare.utils.ACCESS_TOKEN_EXPIRED
import com.app.tensquare.utils.AppUtills
import com.app.tensquare.utils.JUMP_TO_HOME_BROADCAST
import com.app.tensquare.utils.LOG_OUT
import com.app.tensquare.utils.OTHER_DEVISE_LOGIN
import com.app.tensquare.utils.REFRESH_TOKEN_EXPIRED
import com.app.tensquare.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
//import kotlinx.android.synthetic.main.class_list_popup_layout.view.list_view

private interface OnClassClickItemListener {
    fun onClassListItemClick(position: Int, data: ClassData)
}

@AndroidEntryPoint
class SelectClassActivity : AppBaseActivity() {

    private val viewModel: InitialViewModel by viewModels()
    private lateinit var binding: ActivitySelectClassBinding
    private var closingReceiver: BroadcastReceiver = MyBroadcastReceiver()
    private lateinit var classList: List<ClassData>
    private var classListPopup: PopupWindow? = null

    private var go_to = "login"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectClassBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        initObservers()
        setListeners()

    }

    private fun classListPopup(
        classList: List<ClassData>,
        onClassListItemClickListener: OnClassClickItemListener
    ): PopupWindow {

        val popupWindow = PopupWindow(this)
        val popupView: View = layoutInflater.inflate(R.layout.class_list_popup_layout, null)
        val adapter = ArrayAdapter<ClassData>(
            this,
            R.layout.row_class_list_item,
            R.id.class_name,
            classList
        )
        val listView: ListView = popupView.findViewById(R.id.list_view) // Get the ListView from the popupView
        listView.adapter = adapter
        popupWindow.contentView = popupView
        // Set the width and height of the PopupWindow
        popupWindow.width = LayoutParams.MATCH_PARENT
        popupWindow.height = LayoutParams.WRAP_CONTENT

        // Set outside touchable to dismiss the PopupWindow when touched outside
        popupWindow.isOutsideTouchable = false;
        popupWindow.setBackgroundDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.tranparent_popup_background
            )
        )
        listView.setOnItemClickListener { _, _, position, _ ->
            if (position != 0) {
                onClassListItemClickListener.onClassListItemClick(position, classList[position])
            }
            popupWindow.dismiss()
        }
        return popupWindow
    }

    override fun init() {
        Log.d("USER_LANGUAGE", prefs.getSelectedLanguageId().toString())
        getClassList()
        if (intent.hasExtra("go_to")) {
            go_to = intent.getStringExtra("go_to").toString()
        }
        val filter = IntentFilter(JUMP_TO_HOME_BROADCAST)
        this.registerReceiver(closingReceiver, filter)

    }

    private fun getClassList() {
        showProgressDialog()
        viewModel.getClassList(prefs.getSelectedLanguageId().toString())
    }

    override fun setListsAndAdapters() {

    }

    override fun setListeners() {

        binding.txtHighSchool.setOnClickListener {
            classListPopup?.let {
                if (it.isShowing) it.dismiss()
                else it.showAsDropDown(binding.txtHighSchool)
            }
        }
        binding.btnBack.setOnClickListener { finish() }
        /*
        binding.txtHighSchool.setOnClickListener {
            //prefs.setSelectedClassId(classList.find { it.name.uppercase() == "10TH" }?._id)
            prefs.setSelectedClassId(classList[0]._id)
            val i = Intent(this@SelectClassActivity, SelectUserActivity::class.java)
            startActivity(i)
        }

        binding.txtIntermediate.setOnClickListener {
            //prefs.setSelectedClassId(classList.find { it.name.uppercase() == "12TH" }?._id)
            prefs.setSelectedClassId(classList[1]._id)
            val i = Intent(this@SelectClassActivity, SelectUserActivity::class.java)
            startActivity(i)
        }*/
    }

    override fun initObservers() {
        viewModel.classListResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    val customClassList = arrayListOf<ClassData>(ClassData("", 0, "", 0, LanguageData("",""), "", getString(R.string.select_class), 0))
                    response.data?.classList?.let { customClassList.addAll(it) }
                    classList = customClassList
                    binding.txtHighSchool.text = if (classList.isNotEmpty()) classList[0].name else ""
                    binding.llButtons.visibility = View.VISIBLE
                    classListPopup = classListPopup(classList, object : OnClassClickItemListener {
                        override fun onClassListItemClick(position: Int, data: ClassData) {
                            prefs.setSelectedClassId(data._id)
                            prefs.setSelectedClassName(data.name)
                            if (go_to.equals("login")) {
                                val i = Intent(this@SelectClassActivity, LoginActivity::class.java)
                                startActivity(i)
                                finish()
                            } else {
                                val i = Intent(this@SelectClassActivity, SignUpActivity::class.java)
                                startActivity(i)
                                finish()
                            }
                        }
                    })
                }
                is NetworkResult.Error -> {
                    when (response.message) {
                        ACCESS_TOKEN_EXPIRED -> {
                            prefs.setUserToken(prefs.getRefreshToken())
                            viewModel.getRefreshToken(prefs.getUserToken().toString())

                        }
                    }
                }

                is NetworkResult.Loading -> {
                    // show a progress bar
                }
            }
        }

        viewModel.refreshTokenResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    prefs.setUserToken(response.data?.accessToken)
                    prefs.setRefreshToken(response.data?.refreshToken)
                    getClassList()
                }

                is NetworkResult.Error -> {
                   showToast(response.message.toString())
                }

                is NetworkResult.Loading -> {
                    // show a progress bar
                }
            }
        }
    }

    private inner class MyBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(this.closingReceiver)
    }

    override fun onResume() {
        super.onResume()
        getClassList()
    }
}