package com.app.tensquare.customUiExample

import CustomPlayerUiController
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.tensquare.R
import com.app.tensquare.activity.FeedbackActivity
import com.app.tensquare.adapter.ChapterVideoAdapter
import com.app.tensquare.base.AppBaseActivity
import com.app.tensquare.databinding.ActivityCustomUiExampleBinding
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.ui.initialsetup.InitialViewModel
import com.app.tensquare.ui.login.LoginActivity
import com.app.tensquare.ui.revisionvideo.LikeVideoRequest
import com.app.tensquare.ui.revisionvideo.NextVideo
import com.app.tensquare.ui.revisionvideo.RevisionVideoViewModel
import com.app.tensquare.ui.subscription.EnrolmentPlanPricingNewActivity
import com.app.tensquare.utils.ACCESS_TOKEN_EXPIRED
import com.app.tensquare.utils.LOG_OUT
import com.app.tensquare.utils.MODULE_LATEST_UPDATE
import com.app.tensquare.utils.MODULE_SEARCH
import com.app.tensquare.utils.OTHER_DEVISE_LOGIN
import com.app.tensquare.utils.REFRESH_TOKEN_EXPIRED
import com.app.tensquare.utils.SCREEN_FEEDBACK
import com.app.tensquare.utils.showToast
import com.app.tensquare.utils.showToastCenter
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.loadOrCueVideo
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CustomUiActivity : AppBaseActivity(), FullScreenCallback {

    private lateinit var binding: ActivityCustomUiExampleBinding
    private var revisionVideoId: String? = null
    private var videoId: String? = null
    private var currentVideoId = ""
    private lateinit var chapterVideoAdapter: ChapterVideoAdapter
    private val nextChapterVideoList = ArrayList<NextVideo>()
    private val viewModel: RevisionVideoViewModel by viewModels()
    private val initialViewModel: InitialViewModel by viewModels()
    private var isEnable: Boolean = false

    override fun init() {
        revisionVideoId = intent.getStringExtra("video_id")

        intent.getBooleanExtra("isContentVisible",false).let {
            if (!it) {
                binding.apply {
                    scrollView.visibility = View.GONE
                    rlFeedback.visibility = View.GONE
                }
            }
        }

        try {
            val paddingDp = 18
            val paddingDp_B = 0
            val density: Float = this.resources.displayMetrics.density
            val paddingPixel: Int = (paddingDp.toInt() * density).toInt()
            val paddingPixel_bottom: Int = (paddingDp_B.toInt() * density).toInt()
            binding.youtubePlayerView.setPadding(
                paddingPixel,
                paddingPixel,
                paddingPixel,
                paddingPixel_bottom
            )
        }catch (e : Exception){
            e.printStackTrace()
        }

        binding.apply {
            txtTitle.text = intent.getStringExtra("title")
            chkLike.isVisible = !prefs.isGuestUser()
            rvNextChapters.apply {
                layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                chapterVideoAdapter =
                    ChapterVideoAdapter(this@CustomUiActivity) { nextVideo: NextVideo, position: Int ->
                        try {
                            Intent(context, CustomUiActivity::class.java).also {
                                it.putExtra("subject_name", intent.getStringExtra("subject_name"))
                                it.putExtra("module", intent.getIntExtra("module", 0))
                                it.putExtra("video_id", nextVideo._id)
                                it.putExtra("title", nextVideo.title)
                                it.putExtra("isContentVisible", intent.getBooleanExtra("isContentVisible", false))
                                startActivity(it)
                                finish()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                adapter = chapterVideoAdapter
            }
        }

        revisionVideoId?.let { getVideoDetail(intent.getIntExtra("module", 0), it) }
    }

    override fun setListsAndAdapters() {

    }

    override fun setListeners() {
        binding.txtSummery.movementMethod = ScrollingMovementMethod()
        binding.scrollView.setOnTouchListener(OnTouchListener { v, event ->
            binding.txtSummery.getParent().requestDisallowInterceptTouchEvent(false)
            false
        })

        binding.txtSummery.setOnTouchListener(OnTouchListener { v, event ->
            binding.txtSummery.getParent().requestDisallowInterceptTouchEvent(true)
            false
        })

        binding.imgShare.setOnClickListener { shareVideo() }

        binding.chkLike.setOnCheckedChangeListener { compoundButton, check ->

            val request = revisionVideoId?.let { videoId ->
                prefs.getUserId()?.let { userId ->
                    LikeVideoRequest(
                        videoId = videoId,
                        userId = userId,
                        status = if (check) 1 else 0

                    )
                }
            }
            if (request != null) {
                doLikeVideo(request)
            }
        }

        binding.imgBack.setOnClickListener { finish() }

        binding.llFeedback.setOnClickListener {
            Intent(this, FeedbackActivity::class.java).also {
                it.putExtra("screen", SCREEN_FEEDBACK)
                it.putExtra("video_id", revisionVideoId)
                startActivity(it)
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        revisionVideoId = intent?.getStringExtra("video_id")
        if (intent != null) {
            revisionVideoId?.let { getVideoDetail(intent.getIntExtra("module", 0), it) }
        }
    }

    private fun getVideoDetail(module: Int, video_id: String) {
        if (module == MODULE_LATEST_UPDATE) {
            showProgressDialog()
            /*intent.getStringExtra("video_id")?.let { */viewModel.getLatestUpdateDetail(video_id) //}
        } else if (intent.getIntExtra("module", 0) == MODULE_SEARCH) {
            showProgressDialog()
            viewModel.geSearchedtRevisionVideoDetail(video_id)
        } else {
            showProgressDialog()
            Log.e("Printvideo_id","video_id = $video_id")
            /* intent.getStringExtra("video_id")?.let {*/ viewModel.getRevisionVideoDetail(video_id) //}
        }
    }

    override fun initObservers() {
        viewModel.isYoutubeReady().observe(this) { isReady ->
//            isYoutubeReady = isReady
//            if (isYoutubeReady) {
//                videoId?.let {
////                    Log.e("PrintisYoutubeReady","isYoutubeReady = $it")
//                    youTubePlayer.loadVideo(it, 0f) }
//
//            }
        }

        viewModel.getVideoId().observe(this) {
            initYoutubePlayer(it)
        }

        viewModel.revVideoDetailResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    val data = response.data

                    Log.e("enrollmentPlanStatus","enrollmentPlanStatus = ${data?.videoDetails?.enrollmentPlanStatus} - ${data?.videoDetails?.lock}")
                    try {
//                        showToast(data!!.videoDetails.enrollmentPlanStatus.toString())
                        isEnable = data!!.videoDetails.enrollmentPlanStatus
                        if (isEnable){

                            binding.apply {
                                try {
//                                    llMain.visibility = View.VISIBLE
                                    txtSummery.text = data?.videoDetails?.description
                                    txtTitle.text = data?.videoDetails?.title

                                    chkLike.setOnCheckedChangeListener(null)
                                    chkLike.isChecked = data?.like == 1
                                    setListeners()
                                    /*val url = URL(data.data.videoDetails.thumbnail)
                                    val bitmap = BitmapFactory.decodeStream(
                                        url.openConnection().getInputStream()
                                    )
                                    val background = BitmapDrawable(resources, bitmap)*/
                                    //llThumbnail.setBackgroundDrawable(background)

                                    /*val listener: YouTubePlayerListener =
                                        object : AbstractYouTubePlayerListener() {
                                            fun onReady(youTubePlayer: YouTubePlayer) {
                                                val videoId = "S0Q4gqBUs7c"
                                                youTubePlayer.loadVideo(videoId, 0)

                                            }
                                        }
                                    youtubePlayerView.initialize(listener)*/

//                                    videoId = data?.videoDetails?.videoURL?.let { getYouTubeLinkId(it) }
//
//                                    if (isYoutubeReady)
//                                        viewModel.setVideoId(videoId!!)

                                    currentVideoId = data.videoDetails._id

                                    for (i in 0 until data.nextVideos.size) {
                                        if (data.nextVideos[i]._id == currentVideoId) {
                                            val firstList = data.nextVideos.subList(0, i + 1)
                                            val secondList = data.nextVideos.subList(i + 1, data.nextVideos.size)
                                            val mergeVideo = secondList + firstList
                                            nextChapterVideoList.clear()
                                            if (mergeVideo.size > 1) {
                                                mergeVideo.forEach {
                                                    if (nextChapterVideoList.size < 2) {
                                                        if (nextChapterVideoList.isNotEmpty()) {
                                                            nextChapterVideoList.forEach { nectVideo ->
                                                                if (it._id != currentVideoId) {
                                                                    nextChapterVideoList.add(it)
                                                                }
                                                            }
                                                        }else {
                                                            nextChapterVideoList.add(it)
                                                        }
                                                    }
                                                }
                                            }
                                            break
                                        }
                                    }

                                    Log.e("PrintnextChapter","nextChapterVideoList = ${nextChapterVideoList.size}")
//                                    data?.let { nextChapterVideoList = it.nextVideos }

                                    Log.e("PrintNextVideo","PrintNextVideo = ${data.nextVideos.size}")

                                    videoId = data?.videoDetails?.videoURL?.let { getYouTubeLinkId(it) }
                                    videoId?.let { viewModel.setVideoId(it) }

                                    chapterVideoAdapter.submitList(nextChapterVideoList)

//                            txtNextVideo.visibility = View.VISIBLE
                                    rvNextChapters.visibility = View.VISIBLE
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                            }

                        }else{

                            if (data!!.videoDetails.lock == 0){

                                showToastCenter(getString(R.string.please_enrol))
                                Intent(
                                    this,
                                    EnrolmentPlanPricingNewActivity::class.java
                                ).also {
                                    startActivity(it)
                                }
                                finish()

                            }else{

                                binding.apply {
                                    try {
//                                        llMain.visibility = View.VISIBLE
                                        txtSummery.text = data?.videoDetails?.description
                                        txtTitle.text = data?.videoDetails?.title

                                        chkLike.setOnCheckedChangeListener(null)
                                        chkLike.isChecked = data?.like == 1
                                        setListeners()


//                                        videoId = data?.videoDetails?.videoURL?.let { getYouTubeLinkId(it) }
//
//                                        if (isYoutubeReady)
//                                            viewModel.setVideoId(videoId!!)

//                                        data.let { nextChapterVideoList = it.nextVideos }

                                        currentVideoId = data.videoDetails._id

                                        for (i in 0 until data.nextVideos.size) {
                                            if (data.nextVideos[i]._id == currentVideoId) {
                                                val firstList = data.nextVideos.subList(0, i + 1)
                                                val secondList = data.nextVideos.subList(i + 1, data.nextVideos.size)
                                                val mergeVideo = secondList + firstList
                                                nextChapterVideoList.clear()
                                                if (mergeVideo.size > 1) {
                                                    mergeVideo.forEach {
                                                        if (nextChapterVideoList.size < 2) {
                                                            if (nextChapterVideoList.isNotEmpty()) {
                                                                nextChapterVideoList.forEach { nectVideo ->
                                                                    if (it._id != currentVideoId) {
                                                                        nextChapterVideoList.add(it)
                                                                    }
                                                                }
                                                            }else {
                                                                nextChapterVideoList.add(it)
                                                            }
                                                        }
                                                    }
                                                }
                                                break
                                            }
                                        }

                                        Log.e("PrintnextChapter","nextChapterVideoList else = ${nextChapterVideoList.size} - ${data.nextVideos.size}")

                                        videoId = data?.videoDetails?.videoURL?.let { getYouTubeLinkId(it) }

                                        viewModel.setVideoId(videoId!!)

                                        chapterVideoAdapter.submitList(nextChapterVideoList)

//                            txtNextVideo.visibility = View.VISIBLE
                                        rvNextChapters.visibility = View.VISIBLE
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }

                                }

                            }



                        }

                    }catch (e: Exception){
                        e.printStackTrace()
                    }

                    /*revisionVideoInSubjectAdapter.submitList(data)*/

                }
                is NetworkResult.Error -> {
//                    showToast(response.message)
                    when (response.message) {
                        ACCESS_TOKEN_EXPIRED -> {
                            initialViewModel.getRefreshToken(prefs.getRefreshToken().toString())
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

        viewModel.searchedVideoDetailResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    val data = response.data
                    binding.apply {
                        try {
//                            llMain.visibility = View.VISIBLE
                            txtSummery.text = data?.description
                            if (txtSummery.lineCount <= 3) {
                                txtSummery.updateLayoutParams<LinearLayout.LayoutParams> {
                                    height = LinearLayout.LayoutParams.WRAP_CONTENT
                                    weight = 0F
                                }
                            }else {
                                txtSummery.updateLayoutParams<LinearLayout.LayoutParams> {
                                    weight = 1F
                                }
                            }
                            txtTitle.text = data?.title



                            videoId = data?.videoURL?.let { getYouTubeLinkId(it) }

                            videoId?.let { viewModel.setVideoId(it) }

                            chkLike.visibility = View.GONE
                            rvNextChapters.visibility = View.GONE
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }

                }
                is NetworkResult.Error -> {
                    when (response.message) {
                        ACCESS_TOKEN_EXPIRED -> {
                            initialViewModel.getRefreshToken(prefs.getRefreshToken().toString())
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
        viewModel.latestVideoDetailResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {

                    val data = response.data
                    binding.apply {
                        try {
//                            llMain.visibility = View.VISIBLE
                            txtSummery.text = data?.videoDetails?.description
                            if (txtSummery.lineCount <= 3) {
                                txtSummery.updateLayoutParams<ConstraintLayout.LayoutParams> {
                                    height = ConstraintLayout.LayoutParams.WRAP_CONTENT
//                                    weight = 0F
                                }
                            }else {
                                txtSummery.updateLayoutParams<ConstraintLayout.LayoutParams> {
//                                    weight = 1F
                                }
                            }
                            txtTitle.text = data?.videoDetails?.name

                            chkLike.setOnCheckedChangeListener(null)
                            chkLike.isChecked = data?.like == 1
                            setListeners()

                            videoId = data?.videoDetails?.videoURL?.let { getYouTubeLinkId(it) }
                            videoId?.let { viewModel.setVideoId(it) }

                            rvNextChapters.visibility = View.GONE

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                }
                is NetworkResult.Error -> {
                    when (response.message) {
                        ACCESS_TOKEN_EXPIRED -> {
                            initialViewModel.getRefreshToken(prefs.getRefreshToken().toString())
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

        viewModel.videoLikeResponse.observe(this) { response ->
            dismissProgressDialog()

            when (response) {
                is NetworkResult.Success -> {
                    showToast(response.message.toString())
                }
                is NetworkResult.Error -> {
                    when (response.message) {
                        ACCESS_TOKEN_EXPIRED -> {
                            initialViewModel.getRefreshToken(prefs.getRefreshToken().toString())
                        }
                        else->{
                            if (response.message != OTHER_DEVISE_LOGIN) {
                                showToast(response.message)
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
                        // goto
                        init()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomUiExampleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        setListeners()
        initObservers()
    }

    private fun getYouTubeLinkId(link: String): String {

        val parts = link.split("/")

        if (link.contains("https://youtu.be/")) {
            return parts[parts.size - 1]
        }

        if (link.contains("https://www.youtube.com/") && link.contains("watch?v=")) {
            return (parts[parts.size - 1]).replace("watch?v=", "")
        }

        return "";
    }


    private fun initYoutubePlayer(revisionVideoId: String) {
        lifecycle.addObserver(binding.youtubePlayerView)
        val customPlayerUi = binding.youtubePlayerView.inflateCustomPlayerUi(R.layout.custom_player_ui)
        val listener: YouTubePlayerListener = object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                val customPlayerUiController = CustomPlayerUiController(
                    this@CustomUiActivity,
                    customPlayerUi,
                    youTubePlayer,
                    binding.youtubePlayerView,
                    this@CustomUiActivity
                )
                youTubePlayer.addListener(customPlayerUiController)
                youTubePlayer.loadOrCueVideo(lifecycle, revisionVideoId, 0f)
            }
        }

        // disable web ui
        val options: IFramePlayerOptions = IFramePlayerOptions.Builder().controls(0).build()
        binding.youtubePlayerView.initialize(listener, options)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        // Checks the orientation of the screen
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            Log.e("PrintFullScreen","onConfigurationChanged = in ORIENTATION_LANDSCAPE")
//            binding.youtubePlayerView.wrapContent()
//            binding.toolbarLayout.visibility = View.VISIBLE
//            binding.scrollContent.visibility = View.VISIBLE
//            try {
//                val paddingDp = 18
//                val paddingDp_B = 0
//                val density: Float = this.resources.displayMetrics.density
//                val paddingPixel: Int = (paddingDp.toInt() * density).toInt()
//                val paddingPixel_bottom: Int = (paddingDp_B.toInt() * density).toInt()
//                binding.youtubePlayerView.setPadding(
//                    paddingPixel,
//                    paddingPixel,
//                    paddingPixel,
//                    paddingPixel_bottom
//                )
//            }catch (e : Exception){
//                e.printStackTrace()
//            }
//        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            Log.e("PrintFullScreen","onConfigurationChanged = in ORIENTATION_PORTRAIT")
//            binding.youtubePlayerView.matchParent()
//            binding.toolbarLayout.visibility = View.GONE
//            binding.scrollContent.visibility = View.GONE
//            try {
//                binding.youtubePlayerView.setPadding(
//                    0,
//                    0,
//                    0,
//                    0
//                )
//            }catch (e : Exception){
//                e.printStackTrace()
//            }
//        }
    }

    private fun shareVideo() {
        val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse("https://elearningdynamic.page.link/$revisionVideoId?&name=resource"))
            .setDomainUriPrefix("elearningdynamic.page.link")
            .setAndroidParameters(DynamicLink.AndroidParameters.Builder(packageName).build())
            .buildDynamicLink()
        val dynamicLinkUri = dynamicLink.uri
        FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLongLink(dynamicLinkUri)
            .buildShortDynamicLink()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val shortLink = task.result.shortLink.toString()
                    Log.d("TASK", "=====>>>>success$shortLink")
                    val intent = Intent()
                    intent.action = Intent.ACTION_SEND
                    intent.putExtra(Intent.EXTRA_TEXT, shortLink)
                    intent.type = "text/plain"
                    startActivity(intent)
                } else {
                    val intent = Intent()
                    intent.action = Intent.ACTION_SEND
                    intent.putExtra(Intent.EXTRA_TEXT, dynamicLinkUri.toString())
                    intent.type = "text/plain"
                    startActivity(intent)
                }
            }
    }
    private fun doLikeVideo(request: LikeVideoRequest) {
        showProgressDialog()
        viewModel.doLikeVideo(request)
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onFullScreen(isFullScreen: Boolean) {
        Log.e("PrintFullScreen","PrintFullScreen = $isFullScreen")
        try {
            if (isFullScreen) {
                // Set youtubePlayerView to match parent
                val params = binding.youtubePlayerView.layoutParams
                params.width = ViewGroup.LayoutParams.MATCH_PARENT
                params.height = ViewGroup.LayoutParams.MATCH_PARENT
                binding.youtubePlayerView.layoutParams = params
                binding.youtubePlayerView.setPadding(0, 0, 0, 0)

                @Suppress("DEPRECATION")
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                )
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                binding.toolbarLayout.visibility = View.GONE
            } else {
                // Reset padding and layout parameters
                val paddingDp = 18
                val paddingDp_B = 0
                val density: Float = this.resources.displayMetrics.density
                val paddingPixel: Int = (paddingDp.toInt() * density).toInt()
                val paddingPixel_bottom: Int = (paddingDp_B.toInt() * density).toInt()
                binding.youtubePlayerView.setPadding(paddingPixel, paddingPixel, paddingPixel, 0)

                val params = binding.youtubePlayerView.layoutParams
                params.width = ViewGroup.LayoutParams.MATCH_PARENT
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT // Or specify a fixed height if needed
                binding.youtubePlayerView.layoutParams = params

                @Suppress("DEPRECATION")
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                binding.toolbarLayout.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}
