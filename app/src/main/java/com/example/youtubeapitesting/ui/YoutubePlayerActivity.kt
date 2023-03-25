package com.example.youtubeapitesting.ui

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.youtubeapitesting.FullScreenHelper
import com.example.youtubeapitesting.R
import com.example.youtubeapitesting.utils.VideoIdsProvider
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlaybackRate
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.loadOrCueVideo
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView


class YoutubePlayerActivity : ComponentActivity() {
    private var youTubePlayerView: YouTubePlayerView? = null
    private val fullScreenHelper = FullScreenHelper(this)

    // a list of videos not available in some countries, to test if they're handled gracefully.
    // private String[] nonPlayableVideoIds = { "sop2V_MREEI" };
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basic_example)
        val videoId = intent.getStringExtra("videoId")
        youTubePlayerView = findViewById(R.id.youtube_player_view)
        videoId?.let { initYouTubePlayerView(it) }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            youTubePlayerView!!.enterFullScreen()
            fullScreenHelper.enterFullScreen()
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            youTubePlayerView!!.exitFullScreen()
            fullScreenHelper.exitFullScreen()
        }
    }

    override fun onBackPressed() {
        if (youTubePlayerView!!.isFullScreen()) youTubePlayerView!!.exitFullScreen() else super.onBackPressed()
    }

    private fun initYouTubePlayerView(videoId: String) {
        // The player will automatically release itself when the activity is destroyed.
        // The player will automatically pause when the activity is stopped
        // If you don't add YouTubePlayerView as a lifecycle observer, you will have to release it manually.
        val iFrameOptions = IFramePlayerOptions.Builder().controls(1).build()
        youTubePlayerView?.let { lifecycle.addObserver(it) }
        youTubePlayerView!!.initialize(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youTubePlayer.loadVideo(videoId, 0f)
                setPlayNextVideoButtonClickListener(youTubePlayer)
                setPlaybackSpeedButtonsClickListeners(youTubePlayer)
            }

            override fun onPlaybackRateChange(
                youTubePlayer: YouTubePlayer,
                playbackRate: PlaybackRate
            ) {
                val playbackSpeedTextView: TextView = findViewById(R.id.playback_speed_text_view)
                val playbackSpeed = "Playback speed: "
                playbackSpeedTextView.text = playbackSpeed + playbackRate
            }
        }, iFrameOptions)
        youTubePlayerView?.addFullScreenListener(object : YouTubePlayerFullScreenListener{
            override fun onYouTubePlayerEnterFullScreen() {
                youTubePlayerView?.enterFullScreen()
            }

            override fun onYouTubePlayerExitFullScreen() {
                youTubePlayerView?.exitFullScreen()
            }

        });
    }


    /**
     * Set a click listener on the "Play next video" button
     */
    private fun setPlayNextVideoButtonClickListener(youTubePlayer: YouTubePlayer) {
        val playNextVideoButton: Button = findViewById(R.id.next_video_button)
        playNextVideoButton.setOnClickListener { view: View? ->
            youTubePlayer.loadOrCueVideo(
                lifecycle,
                VideoIdsProvider.getNextVideoId(),
                0f
            )
        }
    }

    /**
     * Set the click listeners for the "playback speed" buttons
     */
    private fun setPlaybackSpeedButtonsClickListeners(youTubePlayer: YouTubePlayer) {
        val playbackSpeed_0_25: Button = findViewById(R.id.playback_speed_0_25)
        val playbackSpeed_1: Button = findViewById(R.id.playback_speed_1)
        val playbackSpeed_2: Button = findViewById(R.id.playback_speed_2)
        playbackSpeed_0_25.setOnClickListener { view: View? ->
            youTubePlayer.setPlaybackRate(
                PlaybackRate.RATE_0_25
            )
        }
        playbackSpeed_1.setOnClickListener { view: View? ->
            youTubePlayer.setPlaybackRate(
                PlaybackRate.RATE_1
            )
        }
        playbackSpeed_2.setOnClickListener { view: View? ->
            youTubePlayer.setPlaybackRate(
                PlaybackRate.RATE_2
            )
        }
    }

    /**
     * This method is here just for reference, it is not being used because the IFrame player already shows the title of the video.
     *
     * This method is called every time a new video is being loaded/cued.
     * It uses the YouTube Data APIs to fetch the video title from the video ID.
     * The YouTube Data APIs are nothing more then a wrapper over the YouTube REST API.
     * You can learn more at the following urls:
     * https://developers.google.com/youtube/v3/docs/videos/list
     * https://developers.google.com/apis-explorer/#p/youtube/v3/youtube.videos.list?part=snippet&id=6JYIGclVQdw&fields=items(snippet(title))&_h=9&
     *
     * This method does network operations, therefore it cannot be executed on the main thread.
     * It's up to you to make sure that it does not run on the UI thread, you can use whatever you want: Threads, AsyncTask, Coroutines, RxJava etc.
     */
    @SuppressLint("CheckResult")
    private fun setVideoTitle(videoId: String) {
        //val videoInfo: VideoInfo = YouTubeDataEndpoint.getVideoInfoFromYouTubeDataAPIs(videoId)
    }
}