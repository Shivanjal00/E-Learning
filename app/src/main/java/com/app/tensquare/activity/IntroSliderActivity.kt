package com.app.tensquare.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.app.tensquare.R
import com.app.tensquare.adapter.IntroSliderAdapter
import com.app.tensquare.base.AppBaseActivity
import com.app.tensquare.databinding.ActivityIntroSliderBinding
import com.app.tensquare.fragment.Intro1Fragment
import com.app.tensquare.fragment.Intro2Fragment
import com.app.tensquare.fragment.Intro3Fragment
import com.app.tensquare.fragment.Intro4Fragment
import com.app.tensquare.ui.initialsetup.SelectClassActivity
import com.app.tensquare.utils.AppUtills
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroSliderActivity : AppBaseActivity() {
    // private lateinit var vpIntroSlider: ViewPager2
    //private lateinit var indicatorLayout: IndicatorLayout
    private lateinit var tvSkip: TextView
    //private lateinit var tvNext: TextView

    private val fragmentList = ArrayList<Fragment>()

    private lateinit var binding: ActivityIntroSliderBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppUtills.setLan(this)
        AppUtills.getWindowStatusBarColor(this, R.color.app_status_bar_color)
        binding = ActivityIntroSliderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val window: Window = this@IntroSliderActivity.getWindow()
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = this@IntroSliderActivity.getResources().getColor(android.R.color.white)
        }


        var first = getString(com.app.tensquare.R.string.already_have_an_account_with_us);
        var last = getString(com.app.tensquare.R.string.log_in_here);

        var mainStr = first + " " + last;

        val ss = SpannableString(mainStr)
        val fcsred = ForegroundColorSpan(resources.getColor(R.color.loginclr))

        val firstlen = first.length + 1
        val lastlen = firstlen + last.length
        ss.setSpan(fcsred, firstlen, lastlen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.txtLogin.text = ss

        setListsAndAdapters()
        setListeners()
    }

    override fun init() {

    }

    override fun setListsAndAdapters() {
        val adapter = IntroSliderAdapter(this)
        binding.viewPager2IntroSlider.adapter = adapter
        fragmentList.addAll(
            listOf(
                Intro1Fragment(), Intro2Fragment(), Intro3Fragment() ,Intro4Fragment()
            )
        )
        adapter.setFragmentList(fragmentList)
        binding.indicatorLayoutInto.setIndicatorCount(adapter.itemCount)
        binding.indicatorLayoutInto.selectCurrentPosition(0)
    }

    override fun setListeners() {
        binding.viewPager2IntroSlider.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.indicatorLayoutInto.selectCurrentPosition(position)
                if (position < fragmentList.lastIndex) {
                    binding.tvSkip.visibility = View.VISIBLE

//                    binding.tvNext.text = getString(com.app.elearning.R.string.next)
                } else {
                    binding.tvSkip.visibility = View.GONE
//                    binding.tvNext.setTextColor(Color.parseColor("#FAC65B"))
//                    binding.tvNext.text = getString(com.app.elearning.R.string.continue_s)
                }
            }
        })
        binding.tvSkip.setOnClickListener {
//            startActivity(Intent(this, SelectClassActivity::class.java))
//            prefs.setOnBoardingViewed(true)
//            finish()

            try {
                val position = binding.viewPager2IntroSlider.currentItem
                if (position < fragmentList.lastIndex)
                    binding.viewPager2IntroSlider.currentItem = position + 1
            }catch (e : Exception){
                e.printStackTrace()
            }

        }
        binding.tvNext.setOnClickListener {
            val position = binding.viewPager2IntroSlider.currentItem
/*            if (position < fragmentList.lastIndex) {
                binding.viewPager2IntroSlider.currentItem = position + 1
            } else {
                //startActivity(Intent(this, SelectLanguageActivity::class.java))
                startActivity(Intent(this, SelectClassActivity::class.java))
                prefs.setOnBoardingViewed(true)
                finish()
            }   */

            val intent = Intent(this, SelectClassActivity::class.java)
            intent.putExtra("go_to", "signup")
            startActivity(intent)
            prefs.setOnBoardingViewed(true)
            finish()

        }


        binding.txtLogin.setOnClickListener {

            val intent = Intent(this, SelectClassActivity::class.java)
            intent.putExtra("go_to", "login")
            startActivity(intent)
            prefs.setOnBoardingViewed(true)
            finish()

        }

    }

    override fun initObservers() {
        TODO("Not yet implemented")
    }


}