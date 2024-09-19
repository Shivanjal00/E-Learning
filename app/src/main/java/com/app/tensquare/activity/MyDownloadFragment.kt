package com.app.tensquare.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.app.tensquare.R
import com.app.tensquare.adapter.HomeSubjectAdapter
import com.app.tensquare.databinding.FragmentMyDownloadBinding
import com.app.tensquare.response.Subject
import com.app.tensquare.ui.home.HomeFragment
import com.app.tensquare.ui.home.HomeViewModel
import java.text.SimpleDateFormat

class MyDownloadFragment : Fragment() {

    private lateinit var binding: FragmentMyDownloadBinding
    private lateinit var homeSubjectAdapter: HomeSubjectAdapter
    private var subjectList = mutableListOf<Subject>()

    private val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private val outputFormat = SimpleDateFormat("dd MMM, yyyy")

    /*override fun initializeAllView(view: View) {
        TODO("Not yet implemented")
    }

    override fun setListsAndAdapters() {
        TODO("Not yet implemented")
    }

    override fun clickListeners() {
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
        binding = FragmentMyDownloadBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.llNotesResources.setOnClickListener {
            showMyDownloads("NOTES_RESOURCES")
        }
        binding.llQueBankPaper.setOnClickListener {
            showMyDownloads("QUESTION_BANK_PAPER")
        }
        binding.llModelPaper.setOnClickListener {
            showMyDownloads("MODEL_PAPER")
        }
        binding.llPreviousYearPaper.setOnClickListener {
            showMyDownloads("PREVIOUS_PAPER")
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

    private fun showMyDownloads(downloadType: String) {
        Intent(activity, MyDownloadsActivity::class.java).also {
            it.putExtra("downloadType", downloadType)
            startActivity(it)
        }
    }


}
