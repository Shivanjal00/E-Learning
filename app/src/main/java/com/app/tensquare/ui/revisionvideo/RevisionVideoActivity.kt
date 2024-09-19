package com.app.tensquare.ui.revisionvideo

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.app.tensquare.R
import com.app.tensquare.activity.FeedbackActivity
import com.app.tensquare.adapter.ChapterVideoAdapter
import com.app.tensquare.base.AppBaseActivity
import com.app.tensquare.customUiExample.CustomUiActivity
import com.app.tensquare.databinding.ActivityRevisionVideoBinding
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.ui.initialsetup.InitialViewModel
import com.app.tensquare.ui.login.LoginActivity
import com.app.tensquare.ui.subscription.EnrolmentPlanPricingNewActivity
import com.app.tensquare.utils.*
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RevisionVideoActivity : AppBaseActivity() {
    private lateinit var binding: ActivityRevisionVideoBinding
    private lateinit var chapterVideoAdapter: ChapterVideoAdapter
    private lateinit var nextChapterVideoList: List<NextVideo>
    private val viewModel: RevisionVideoViewModel by viewModels()
    private val initialViewModel:InitialViewModel by viewModels()
    private var videoId: String? = null
    private lateinit var youTubePlayer: YouTubePlayer
    private var isYoutubeReady: Boolean = false
    private var revisionVideoId: String? = null
    private val youtubeTracker = YouTubePlayerTracker()

    private var isEnable: Boolean = false

    private var requestQueue: RequestQueue? = null

    private var deviceId: String? = ""
    private var isFullscreen = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRevisionVideoBinding.inflate(layoutInflater)
        //viewModel = ViewModelProvider(this)[RevisionVideoViewModel::class.java]
        setContentView(binding.root)

        try {
            deviceId = AppUtills.getDeviceId(this@RevisionVideoActivity)
            Log.e("DeviceId  ==> " , deviceId.toString())
        }catch (e : Exception){
            e.printStackTrace()
        }

        requestQueue = Volley.newRequestQueue(this@RevisionVideoActivity)

        init()
        setListeners()
        initObservers()

//        val frag =
//          supportFragmentManager.findFragmentById(R.id.youtubeFragment) as YouTubePlayerSupportFragment?
//        frag!!.initialize(PLAYER_API_KEY, this)

    }





    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        revisionVideoId = intent?.getStringExtra("video_id")
        if (intent != null) {
            revisionVideoId?.let { getVideoDetail(intent.getIntExtra("module", 0), it) }
        }

    }

    override fun onBackPressed() {
        if (isFullscreen) {
            youTubePlayer?.toggleFullscreen()
        } else {
            super.onBackPressed()
        }
    }


    private fun doLikeVideo(request: LikeVideoRequest) {
        showProgressDialog()
        viewModel.doLikeVideo(request)
    }

    override fun init() {

        revisionVideoId = intent.getStringExtra("video_id").toString()

        lifecycle.addObserver(binding.youtubePlayerView)

        try {

            val paddingDp = 18
            val paddingDp_B = 0
            val density: Float = this@RevisionVideoActivity.resources.displayMetrics.density
            val paddingPixel: Int = (paddingDp.toInt() * density).toInt()
            val paddingPixel_bottom: Int = (paddingDp_B.toInt() * density).toInt()
            binding.loutSub.setPadding(
                paddingPixel,
                paddingPixel,
                paddingPixel,
                paddingPixel_bottom
            )

        }catch (e : Exception){
            e.printStackTrace()
        }

        /*       val iFramePlayerOptions: IFramePlayerOptions = IFramePlayerOptions.Builder()
                   .controls(1)
                   .build() */



        binding.youtubePlayerView.addFullscreenListener(object :FullscreenListener{
//            override fun onYouTubePlayerEnterFullScreen() {
//                binding.toolbarLayout.visibility = View.GONE
//                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
//                binding.youtubePlayerView.enterFullScreen()
//                binding.youtubePlayerView.isFullScreen()
//            }
//
//            override fun onYouTubePlayerExitFullScreen() {
//                binding.toolbarLayout.visibility = View.VISIBLE
//                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//                binding.youtubePlayerView.exitFullScreen()
//            }

            override fun onEnterFullscreen(fullscreenView: View, exitFullscreen: () -> Unit) {
                isFullscreen = true

                // the video will continue playing in fullscreenView
                binding.fullScreenViewContainer.visibility = View.VISIBLE
                binding.fullScreenViewContainer.addView(fullscreenView)

                if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                }
                binding.toolbarLayout.visibility = View.GONE
            }

            override fun onExitFullscreen() {
                isFullscreen = false

                // the video will continue playing in the player
                binding.fullScreenViewContainer.visibility = View.GONE
                binding.fullScreenViewContainer.removeAllViews()

                if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_SENSOR) {
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }
                binding.toolbarLayout.visibility = View.VISIBLE
            }

        })



        /*
                 binding.youtubePlayerView.initialize(object : AbstractYouTubePlayerListener(){
                     override fun onReady(youTubePlayer: YouTubePlayer) {
                         //val videoId = "S0Q4gqBUs7c"
                         this@RevisionVideoActivity.youTubePlayer = youTubePlayer
                         binding.viewPlayerCover.visibility = View.GONE
                         binding.playerProgressBar.visibility = View.GONE


                         /*val defaultPlayerUiController =
                             DefaultPlayerUiController(
                                 binding.youtubePlayerView,
                                 this@RevisionVideoActivity.youTubePlayer
                             )*/

        //                defaultPlayerUiController.showMenuButton(false)
        //                defaultPlayerUiController.showVideoTitle(false)
        //                defaultPlayerUiController.showYouTubeButton(false)
        //                defaultPlayerUiController.showCustomAction1(false)
        //                defaultPlayerUiController.showCustomAction2(false)

        //                binding.youtubePlayerView.setCustomPlayerUi(defaultPlayerUiController.rootView)

                         viewModel.setIsYoutubeReady(true)

                         youTubePlayer.addListener(object : AbstractYouTubePlayerListener() {
                             override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
                                 super.onStateChange(youTubePlayer, state)

        //                        binding.youtubePlayerView.setCustomPlayerUi(defaultPlayerUiController.rootView)

                                 if (state == PlayerConstants.PlayerState.PLAYING) {
        //                            binding.youtubePlayerView.setCustomPlayerUi(defaultPlayerUiController.rootView)
                                 }else if (state == PlayerConstants.PlayerState.PAUSED){
        //                            binding.youtubePlayerView.setCustomPlayerUi(defaultPlayerUiController.rootView)
                                     //binding.youtubePlayerView.setCustomPlayerUi(defaultPlayerUiController.rootView)
                                     com.google.android.youtube.player.YouTubePlayer.PlayerStyle.CHROMELESS

        //                            binding.youtubePlayerView.inflateCustomPlayerUi(R.layout.activity_revision_video)
                                 }

                             }
                         })

                         //youTubePlayer.loadVideo(videoId, 0f)
                     }

                 },true , iFramePlayerOptions)
                 */

        val youtubePlayerListener = object :
            AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                //val videoId = "S0Q4gqBUs7c"
                this@RevisionVideoActivity.youTubePlayer = youTubePlayer
                youTubePlayer.addListener(youtubeTracker)

//                ContextCompat.getDrawable(this@RevisionVideoActivity,R.drawable.ic_forward_10)?.let {
//                    binding.youtubePlayerView.getPlayerUiController().setCustomAction2(
//                        icon = it,
//                        clickListener = {
//                            val duration = youtubeTracker.currentSecond
//                            youTubePlayer.seekTo(duration + 10f)
//                        }
//                    )
//                }
//
//                ContextCompat.getDrawable(this@RevisionVideoActivity,R.drawable.ic_backward_10)?.let {
//                    binding.youtubePlayerView.getPlayerUiController().setCustomAction1(
//                        icon = it,
//                        clickListener = {
//                            val duration = youtubeTracker.currentSecond
//                            youTubePlayer.seekTo(duration - 10f)
//                        }
//                    )
//                }


                binding.viewPlayerCover.visibility = View.GONE
                binding.playerProgressBar.visibility = View.GONE


                /*val defaultPlayerUiController =
                    DefaultPlayerUiController(
                        binding.youtubePlayerView,
                        this@RevisionVideoActivity.youTubePlayer
                    )*/

//                defaultPlayerUiController.showMenuButton(false)
//                defaultPlayerUiController.showVideoTitle(false)
//                defaultPlayerUiController.showYouTubeButton(false)
//                defaultPlayerUiController.showCustomAction1(false)
//                defaultPlayerUiController.showCustomAction2(false)

//                binding.youtubePlayerView.setCustomPlayerUi(defaultPlayerUiController.rootView)

                viewModel.setIsYoutubeReady(true)

                youTubePlayer.addListener(object : AbstractYouTubePlayerListener() {
                    override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
                        super.onStateChange(youTubePlayer, state)

//                        binding.youtubePlayerView.setCustomPlayerUi(defaultPlayerUiController.rootView)

                        if (state == PlayerConstants.PlayerState.PLAYING) {
//                            binding.youtubePlayerView.setCustomPlayerUi(defaultPlayerUiController.rootView)
                        }else if (state == PlayerConstants.PlayerState.PAUSED){
//                            binding.youtubePlayerView.setCustomPlayerUi(defaultPlayerUiController.rootView)
                            //binding.youtubePlayerView.setCustomPlayerUi(defaultPlayerUiController.rootView)
                            com.google.android.youtube.player.YouTubePlayer.PlayerStyle.CHROMELESS


//                            binding.youtubePlayerView.inflateCustomPlayerUi(R.layout.activity_revision_video)
                        }

                    }
                })

                //youTubePlayer.loadVideo(videoId, 0f)
            }

        }
        val iFramePlayerOptions: IFramePlayerOptions = IFramePlayerOptions.Builder()
            .controls(1) // enable full screen button
            .fullscreen(1)
            .build()

        binding.youtubePlayerView.enableAutomaticInitialization = false
        binding.youtubePlayerView.initialize(youtubePlayerListener, iFramePlayerOptions)



        binding.apply {
            txtTitle.text = intent.getStringExtra("title")
            chkLike.isVisible = !prefs.isGuestUser()
            rvNextChapters.apply {
                layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                chapterVideoAdapter =
                    ChapterVideoAdapter(this@RevisionVideoActivity) { nextVideo: NextVideo, position: Int ->
                        try {
                            Intent(context, CustomUiActivity::class.java).also {
                                it.putExtra("subject_name", intent.getStringExtra("subject_name"))
                                it.putExtra("module", intent.getIntExtra("module", 0))
                                it.putExtra("video_id", nextVideo._id)
                                it.putExtra("isContentVisible", true)
                                it.putExtra("title", nextVideo.title)
                                startActivity(it)
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

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (!isFullscreen) {
                youTubePlayer?.toggleFullscreen()
            }
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (isFullscreen) {
                youTubePlayer?.toggleFullscreen()
            }
        }

//        // Checks the orientation of the screen
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
////            binding.youtubePlayerView.enterFullScreen()
//
//        /*    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                this@RevisionVideoActivity.getWindow().setFlags(
//                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                    WindowManager.LayoutParams.FLAG_FULLSCREEN
//                )
//            }   */
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                this@RevisionVideoActivity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or
//                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
//                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
//                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
//                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//            }
//
//            try {
//
//                val paddingDp = 0
//                val density: Float = this@RevisionVideoActivity.resources.displayMetrics.density
//                val paddingPixel: Int = (paddingDp.toInt() * density).toInt()
//                binding.loutSub.setPadding(
//                    paddingPixel,
//                    paddingPixel,
//                    paddingPixel,
//                    paddingPixel
//                )
//
//            }catch (e : Exception){
//                e.printStackTrace()
//            }
//
//
//        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
////            binding.youtubePlayerView.exitFullScreen()
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                this@RevisionVideoActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
//                this@RevisionVideoActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
//                this@RevisionVideoActivity.getWindow().decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
//            }
//
//            try {
//
//                val paddingDp = 18
//                val paddingDp_B = 0
//                val density: Float = this@RevisionVideoActivity.resources.displayMetrics.density
//                val paddingPixel: Int = (paddingDp.toInt() * density).toInt()
//                val paddingPixel_bottom: Int = (paddingDp_B.toInt() * density).toInt()
//                binding.loutSub.setPadding(
//                    paddingPixel,
//                    paddingPixel,
//                    paddingPixel,
//                    paddingPixel_bottom
//                )
//
//            }catch (e : Exception){
//                e.printStackTrace()
//            }
//
//        }
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
            /* intent.getStringExtra("video_id")?.let {*/ viewModel.getRevisionVideoDetail(video_id) //}
        }
    }


    override fun setListsAndAdapters() {
        TODO("Not yet implemented")
    }

    override fun setListeners() {
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
            Intent(this@RevisionVideoActivity, FeedbackActivity::class.java).also {
                it.putExtra("screen", SCREEN_FEEDBACK)
                it.putExtra("video_id", revisionVideoId)
                startActivity(it)
            }
        }

        //binding.youtubePlayerView.addFullScreenListener(this)


    }


    override fun initObservers() {

        viewModel.isYoutubeReady().observe(this) { isReady ->
            isYoutubeReady = isReady
            if (isYoutubeReady) {
                videoId?.let {
//                    Log.e("PrintisYoutubeReady","isYoutubeReady = $it")
                    youTubePlayer.loadVideo(it, 0f) }

            }
        }

        viewModel.getVideoId().observe(this) {
            if (isYoutubeReady) youTubePlayer.loadVideo(it, 0f)
        }

        viewModel.revVideoDetailResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    val data = response.data

                    try {
//                        showToast(data!!.videoDetails.enrollmentPlanStatus.toString())
                        isEnable = data!!.videoDetails.enrollmentPlanStatus
                        if (isEnable){

                            binding.apply {
                                try {
                                    Log.e("PrinttxtSummeryText","txtSummery revVideoDetailResponse = ${data?.videoDetails?.description}")
                                    llMain.visibility = View.VISIBLE
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

                                    videoId = data?.videoDetails?.videoURL?.let { getYouTubeLinkId(it) }

                                    if (isYoutubeReady)
                                        viewModel.setVideoId(videoId!!)

                                    data?.let { nextChapterVideoList = it.nextVideos }

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
                                    this@RevisionVideoActivity,
                                    EnrolmentPlanPricingNewActivity::class.java
                                ).also {
                                    startActivity(it)
                                }
                                finish()

                            }else{

                                binding.apply {
                                    try {
                                        llMain.visibility = View.VISIBLE
                                        Log.e("PrinttxtSummeryText","txtSummery else revVideoDetailResponse = ${data?.videoDetails?.description}")
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

                                        videoId = data?.videoDetails?.videoURL?.let { getYouTubeLinkId(it) }

                                        if (isYoutubeReady)
                                            viewModel.setVideoId(videoId!!)

                                        data?.let { nextChapterVideoList = it.nextVideos }

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

        viewModel.searchedVideoDetailResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    val data = response.data
                    binding.apply {
                        try {
                            llMain.visibility = View.VISIBLE
                            Log.e("PrinttxtSummeryText","txtSummery searchedVideoDetailResponse = ${data?.description}")
                            txtSummery.text = data?.description
                            txtTitle.text = data?.title

                            /*chkLike.setOnCheckedChangeListener(null)
                            chkLike.isChecked = data?.like == 1
                            setListeners()*/


                            videoId = data?.videoURL?.let { getYouTubeLinkId(it) }

                            if (isYoutubeReady)
                                viewModel.setVideoId(videoId!!)

                            chkLike.visibility = View.GONE
//                            txtNextVideo.visibility = View.GONE
                            rvNextChapters.visibility = View.GONE
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                    /*revisionVideoInSubjectAdapter.submitList(data)*/

                }
                is NetworkResult.Error -> {
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
        viewModel.latestVideoDetailResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {

                    val data = response.data
                    binding.apply {
                        try {
                            llMain.visibility = View.VISIBLE
                            Log.e("PrinttxtSummeryText","txtSummery latestVideoDetailResponse = ${data?.videoDetails?.description}")
                            txtSummery.text = data?.videoDetails?.description
                            txtTitle.text = data?.videoDetails?.name

                            chkLike.setOnCheckedChangeListener(null)
                            chkLike.isChecked = data?.like == 1
                            setListeners()

                            videoId = data?.videoDetails?.videoURL?.let { getYouTubeLinkId(it) }

                            if (isYoutubeReady)
                                viewModel.setVideoId(videoId!!)

                            /*data?.let { nextChapterVideoList = it.nextVideos }

                            chapterVideoAdapter.submitList(nextChapterVideoList)*/

//                            txtNextVideo.visibility = View.GONE
                            rvNextChapters.visibility = View.GONE

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                }
                is NetworkResult.Error -> {
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

        viewModel.videoLikeResponse.observe(this) { response ->
            dismissProgressDialog()

            when (response) {
                is NetworkResult.Success -> {
                    showToast(response.message.toString())
                }
                is NetworkResult.Error -> {
                    when (response.message) {
                        ACCESS_TOKEN_EXPIRED -> {
                            generateNewToken()
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


    /*override fun onInitializationSuccess(
        p0: YouTubePlayer.Provider?,
        player: YouTubePlayer?,
        wasRestored: Boolean
    ) {
        if (!wasRestored) {
            //I assume the below String value is your video id
            player?.cueVideo("nCgQDjiotG0")
        }
    }

    override fun onInitializationFailure(
        p0: YouTubePlayer.Provider?,
        errorReason: YouTubeInitializationResult?
    ) {
        if (errorReason != null) {
            if (errorReason.isUserRecoverableError) {
                errorReason.getErrorDialog(this, 1).show()
            } else {
                //val errorMessage = String.format("Error Player"), errorReason.toString())
                Toast.makeText(this, "Error Player", Toast.LENGTH_LONG).show()
            }
        }
    }*/

    /*private fun getDataFromServer() {
        for (i in 0..5) {
            val subject = Subject()
            chapterVideoList.add(subject)
        }

        chapterVideoAdapter.submitList(chapterVideoList)
    }*/

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

    /*override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        // Checks the orientation of the screen
        if (newConfig.orientation === Configuration.ORIENTATION_LANDSCAPE) {
            binding.youtubePlayerView.enterFullScreen()
        } else if (newConfig.orientation === Configuration.ORIENTATION_PORTRAIT) {
            binding.youtubePlayerView.exitFullScreen()
        }
    }*/

    override fun onDestroy() {
        super.onDestroy()
        // binding.youtubePlayerView.release();
    }

    private fun generateNewToken() {
        initialViewModel.getRefreshToken(prefs.getRefreshToken().toString())
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


}