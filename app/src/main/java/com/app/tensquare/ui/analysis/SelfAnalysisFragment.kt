package com.app.tensquare.ui.analysis

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.tensquare.R
import com.app.tensquare.activity.MyDownloadsActivity
import com.app.tensquare.adapter.SelfAnalysisSubjectAdapter
import com.app.tensquare.base.AppBaseFragment
import com.app.tensquare.databinding.FragmentSelfAnalysisBinding
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.ui.home.HomeFragment
import com.app.tensquare.ui.home.HomeViewModel
import com.app.tensquare.ui.initialsetup.InitialViewModel
import com.app.tensquare.ui.initialsetup.SubjectData
import com.app.tensquare.ui.login.LoginActivity
import com.app.tensquare.utils.LOG_OUT
import com.app.tensquare.utils.OTHER_DEVISE_LOGIN
import com.app.tensquare.utils.REFRESH_TOKEN_EXPIRED
import com.app.tensquare.utils.STATUS_SUCCESS
import com.app.tensquare.utils.SUBJECT_BIOLOGY
import com.app.tensquare.utils.SUBJECT_CHEMISTRY
import com.app.tensquare.utils.SUBJECT_MATHS
import com.app.tensquare.utils.SUBJECT_PHYSICS
import com.app.tensquare.utils.SUBJECT_SCIENCE
import com.app.tensquare.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat

@AndroidEntryPoint
class SelfAnalysisFragment : AppBaseFragment() {

    private lateinit var binding: FragmentSelfAnalysisBinding
    private lateinit var selfAnalysisSubjectAdapter: SelfAnalysisSubjectAdapter
    private var subjectList = mutableListOf<SubjectData>()

    private val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private val outputFormat = SimpleDateFormat("dd MMM, yyyy")
    private val viewModel: AnalysisViewModel by viewModels()
    private val viewModel1: InitialViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private var selectedSubjectData: SubjectData? = null


    /*override fun clickListeners() {
        binding.apply {
            txtCheckDetails.setOnClickListener {
                requireActivity().openActivity(QualityCheckActivity::class.java)
            }
        }
    }*/

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelfAnalysisBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        setListeners()
        setListsAndAdapters()
        initObservers()

        //binding.progressScore.progress = 65


        //getDataFromServer()


    }

    override fun init(view: View) {
        //ObjectAnimator.ofInt(binding.progressScore, "progress", 65).start()
        getSubjectList()
    }

    override fun setListeners() {
        binding.txtDetailedAnalysis.setOnClickListener {
            Intent(activity, AnalysisListActivity::class.java).also {
                it.putExtra("subjectId", selectedSubjectData?._id)
                startActivity(it)
            }
        }

        binding.imgBack.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayoutHome, HomeFragment(), "").commit()

            requireActivity().findViewById<LinearLayout>(R.id.llHome)
                .setBackgroundResource(R.drawable.bg_circle_home_enabled)
            requireActivity().findViewById<ImageView>(R.id.imgMyDownload)
                .setImageResource(R.drawable.ic_my_download_off)
            requireActivity().findViewById<TextView>(R.id.txtMyDownload).setTextColor(
                ContextCompat.getColor(requireActivity(), R.color.gray_170)
            )

            requireActivity().findViewById<ImageView>(R.id.imgSelfAnalysis)
                .setImageResource(R.drawable.ic_home_self_analysis_off)
            requireActivity().findViewById<TextView>(R.id.txtSelfAnalysis).setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.gray_170
                )
            )
        }

    }

    override fun initObservers() {
        homeViewModel.refreshTokenResponse.observe(this) {
            when (it) {
                is NetworkResult.Loading -> {}
                is NetworkResult.Success -> {
                    dismissProgressDialog()
                    it.data?.let { data ->

                        Log.e("token_Home = >", data.accessToken.toString())
                        prefs.setUserToken(data.accessToken)
                        prefs.setRefreshToken(data.refreshToken)
                        getSubjectList()
                    }
                }

                is NetworkResult.Error -> {
                    dismissProgressDialog()
                    if (it.message == REFRESH_TOKEN_EXPIRED) {
//                            requireActivity().showToast("2")
                        Intent(requireActivity(), LoginActivity::class.java).also {
                            startActivity(it)
                        }
                        requireActivity().finish()
                    } else if (it.message == OTHER_DEVISE_LOGIN) {

                        val intent = Intent(requireActivity(), LoginActivity::class.java)
                        intent.putExtra("Expired", "Expired")
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        requireActivity().finish()

                    } else requireActivity().showToast(it.message)

//                            requireActivity().showToast("3")

                }
            }
        }


        viewModel1.subjectListResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {

                    subjectList.clear()

//                    subjectList = response.data!! as MutableList
//                    subjectList[0].isSelected = true
//                    selectedSubjectData = subjectList[0]

                    val subjects: ArrayList<SubjectData> = ArrayList()
                    val className = prefs.getSelectedClassName()

                    for (subject in response.data!! as MutableList) {
                        if (className.toString() != "12th") {
                            if (subject.value == SUBJECT_MATHS || subject.value == SUBJECT_SCIENCE) {
                                subjectList.add(subject)
                            }
                        } else {
                            if (subject.value == SUBJECT_MATHS || subject.value == SUBJECT_PHYSICS || subject.value == SUBJECT_CHEMISTRY || subject.value == SUBJECT_BIOLOGY) {
                                subjectList.add(subject)
                            }
                        }
                    }
//                    subjectList = response.data!! as MutableList
                    subjectList[0].isSelected = true
                    selectedSubjectData = subjectList[0]

                    selfAnalysisSubjectAdapter.submitList(subjectList)
                    //binding.llMain.visibility = View.VISIBLE

                    showProgressDialog()
                    viewModel.getSubjectWiseAnalysis(subjectList[0]._id)
                }

                is NetworkResult.Error -> {
                    dismissProgressDialog()
                    if (response.message != OTHER_DEVISE_LOGIN) {
                        val intent = Intent(requireActivity(), LoginActivity::class.java)
                        intent.putExtra("Expired", "Expired")
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        val broadcast = Intent(LOG_OUT)
                        requireActivity().sendBroadcast(broadcast)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                }

                is NetworkResult.Loading -> {
                    dismissProgressDialog()
                    // show a progress bar
                }
            }
        }

        viewModel.subjectAnalysisResponse.observe(viewLifecycleOwner) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {

                    if (response.data?.status == STATUS_SUCCESS) {

                        val data = response.data?.data!!
                        binding.apply {
                            ObjectAnimator.ofInt(
                                binding.progressScore,
                                "progress",
                                data.percentOfCorrectAnswer
                            ).start()
                            txtAvgScore.text = "${data.percentOfCorrectAnswer}%"
                            txtTotalTests.text = data.totalTestAttemped
                            timeSpendOnTest.text = data.userSpendTime
                            txtAvgTimePerQues.text = data.averagePerQuestion
                            txtTimeSpendOnPractice.text = data.timeSpendOnPractice
                        }

                    } else {

                        showMessage(response.data?.message)
                        if (response.data?.message == OTHER_DEVISE_LOGIN) {

                            val intent = Intent(requireActivity(), LoginActivity::class.java)
                            intent.putExtra("Expired", "Expired")
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                            val broadcast = Intent(LOG_OUT)
//                            requireActivity().sendBroadcast(broadcast)
                            startActivity(intent)
                            requireActivity().finish()

                        }

                    }

                }

                is NetworkResult.Error -> {
                    requireActivity().showToast(response.message)
                }

                is NetworkResult.Loading -> {
                    // show a progress bar
                }
            }
        }
    }

    override fun setListsAndAdapters() {
        binding.rvSubject.apply {
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
            selfAnalysisSubjectAdapter =
                SelfAnalysisSubjectAdapter { items: MutableList<SubjectData>, checked: Boolean, subject: SubjectData ->
                    var preSelectedItem = items.find { it.isSelected }
                    val preSelectedItemIndex = items.indexOf(preSelectedItem)
                    if (preSelectedItem != null) {
                        preSelectedItem = preSelectedItem.copy(isSelected = false)
                        items[preSelectedItemIndex] = preSelectedItem
                    }

                    var element = items.first {
                        it._id == subject._id
                    }
                    val index = items.indexOf(element)

                    //items[position].isSelected = !items[position].isSelected
                    if (element != null) {
                        element = element.copy(isSelected = true)
                    }

                    if (element != null) {
                        items[index] = element
                    }

                    selectedSubjectData = element

                    (binding.rvSubject.adapter as SelfAnalysisSubjectAdapter).submitList(items)

                    showProgressDialog()
                    viewModel.getSubjectWiseAnalysis(subject._id)

                    /*if (flag == 1) {
                        val newList = mutableListOf<SelfAnalysisSubject>()
                        subjectList.forEach {
                            if (it.isSelected) {
                                newList.add(
                                    SelfAnalysisSubject(
                                        id = it.id,
                                        name = it.name,
                                        image = it.image,
                                        isSelected = false
                                    )
                                )
                            } else
                                newList.add(
                                    SelfAnalysisSubject(
                                        id = it.id,
                                        name = it.name,
                                        image = it.image,
                                        isSelected = it.isSelected
                                    )
                                )
                        }
                        newList[position].isSelected = true
                        selfAnalysisSubjectAdapter.submitList(newList)
                    }*/
                }
            adapter = selfAnalysisSubjectAdapter
        }

    }

    private fun getSubjectList() {
        showProgressDialog()
        viewModel1.getSubjectList(prefs.getSelectedLanguageId().toString())
    }

    /*
        private fun getDataFromServer() {
            subjectList.add(
                SelfAnalysisSubject(
                    id = 1,
                    name = "Maths",
                    image = R.drawable.img_physics,
                    isSelected = true
                )
            )

            subjectList.add(
                SelfAnalysisSubject(
                    id = 2,
                    name = "Biology",
                    image = R.drawable.img_biology
                )
            )

            subjectList.add(
                SelfAnalysisSubject(
                    id = 3,
                    name = "Physics",
                    image = R.drawable.img_physics
                )
            )

            subjectList.add(
                SelfAnalysisSubject(
                    id = 4,
                    name = "Chemistry",
                    image = R.drawable.img_chemistry
                )
            )

            selfAnalysisSubjectAdapter.submitList(subjectList)
        }
    */


    private fun showMyDownloads() {
        Intent(activity, MyDownloadsActivity::class.java).also {
            startActivity(it)
        }
    }


}
