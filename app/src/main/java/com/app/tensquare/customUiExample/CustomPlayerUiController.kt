import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.app.tensquare.R
import com.app.tensquare.customUiExample.FullScreenCallback
import com.app.tensquare.customUiExample.views.YouTubePlayerSeekBar
import com.app.tensquare.customUiExample.views.YouTubePlayerSeekBarListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerState
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class CustomPlayerUiController(
    private val context: Context,
    customPlayerUi: View,
    private val youTubePlayer: YouTubePlayer,
    private val youTubePlayerView: YouTubePlayerView?,
    private val fullScreenCallback: FullScreenCallback
) : AbstractYouTubePlayerListener() {
    private val playerTracker: YouTubePlayerTracker

    private var panel: View? = null
    private var progressbar: View? = null
    private var fullscreen = false

    init {
        playerTracker = YouTubePlayerTracker()
        youTubePlayer.addListener(playerTracker)
        initViews(customPlayerUi)
    }

    private fun initViews(playerUi: View) {
        panel = playerUi.findViewById(R.id.panel)
        progressbar = playerUi.findViewById(R.id.progressbar)
        val ivFullScreen = playerUi.findViewById<ImageView>(R.id.ivFullScreen)
        val playPauseButton = playerUi.findViewById<ImageView>(R.id.ivPlayPause)
        val youtubePlayerSeekBar = playerUi.findViewById<YouTubePlayerSeekBar>(R.id.youtube_player_seekbar)

        playPauseButton.setOnClickListener { view: View? ->
            if (playerTracker.state == PlayerState.PLAYING) {
                playPauseButton.setImageResource(R.drawable.ic_play_youtube)
                youTubePlayer.pause()
            } else {
                playPauseButton.setImageResource(R.drawable.ic_pause_youtube)
                youTubePlayer.play()
            }
        }

        panel?.setOnClickListener { view: View? ->
            if (playerTracker.state == PlayerState.PLAYING) {
                playPauseButton.setImageResource(R.drawable.ic_play_youtube)
                youTubePlayer.pause()
            } else {
                playPauseButton.setImageResource(R.drawable.ic_pause_youtube)
                youTubePlayer.play()
            }
        }

        ivFullScreen.setOnClickListener {
            if (fullscreen) {
                // Exit fullscreen
                youTubePlayerView?.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                ivFullScreen.setImageResource(R.drawable.ic_fullscreen)
            } else {
                // Enter fullscreen
                youTubePlayerView?.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                ivFullScreen.setImageResource(R.drawable.ic_fullscreen_exit)
            }
            fullscreen = !fullscreen
            fullScreenCallback.onFullScreen(fullscreen)
        }

        youTubePlayer.addListener(youtubePlayerSeekBar)
        youtubePlayerSeekBar.youtubePlayerSeekBarListener = object : YouTubePlayerSeekBarListener {
            override fun seekTo(time: Float) = youTubePlayer.seekTo(time)
        }
    }

    override fun onReady(youTubePlayer: YouTubePlayer) {
        progressbar?.visibility = View.GONE
    }

    override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerState) {
        if (state == PlayerState.PLAYING || state == PlayerState.PAUSED || state == PlayerState.VIDEO_CUED) {
            panel?.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
        } else if (state == PlayerState.BUFFERING) {
            panel?.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
        // Handle current second
    }

    @SuppressLint("SetTextI18n")
    override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
        // Handle video duration
    }
}
