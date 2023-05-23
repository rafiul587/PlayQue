package com.example.youtubeapitesting.ui

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import com.example.youtubeapitesting.ui.screens.videoplayer.PlayerViewModel
import com.example.youtubeapitesting.R
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.floor

@AndroidEntryPoint
class YoutubePlayerActivity : ComponentActivity() {
    val viewModel by viewModels<PlayerViewModel>()
    var videoId: String? = null
    private var isFullscreen = false
    private lateinit var youTubePlayer: YouTubePlayer
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (isFullscreen) {
                // if the player is in fullscreen, exit fullscreen
                youTubePlayer.toggleFullscreen()
            } else {
                finish()
            }
        }
    }

    lateinit var youTubePlayerView: YouTubePlayerView
    lateinit var fullscreenViewContainer: FrameLayout

    // a list of videos not available in some countries, to test if they're handled gracefully.
    // private String[] nonPlayableVideoIds = { "sop2V_MREEI" };
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basic_example)
        videoId = intent.getStringExtra("videoId")
        youTubePlayerView = findViewById(R.id.youtube_player_view)
        videoId?.let { initYouTubePlayerView(it) }
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
        youTubePlayerView = findViewById(R.id.youtube_player_view)
        fullscreenViewContainer = findViewById(R.id.full_screen_view_container)
        youTubePlayerView.addFullscreenListener(object : FullscreenListener {
            override fun onEnterFullscreen(fullscreenView: View, exitFullscreen: () -> Unit) {
                isFullscreen = true

                // the video will continue playing in fullscreenView
                youTubePlayerView.visibility = View.GONE
                fullscreenViewContainer.visibility = View.VISIBLE
                fullscreenViewContainer.addView(fullscreenView)

                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }

            override fun onExitFullscreen() {
                isFullscreen = false

                // the video will continue playing in the player
                youTubePlayerView.visibility = View.VISIBLE
                fullscreenViewContainer.visibility = View.GONE
                fullscreenViewContainer.removeAllViews()
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        })
    }


    private fun initYouTubePlayerView(videoId: String) {
        val iFramePlayerOptions = IFramePlayerOptions.Builder()
            .controls(1)
            .fullscreen(1) // enable full screen button
            .build()
        // The player will automatically release itself when the activity is destroyed.
        // The player will automatically pause when the activity is stopped
        // If you don't add YouTubePlayerView as a lifecycle observer, you will have to release it manually.

        youTubePlayerView.initialize(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                this@YoutubePlayerActivity.youTubePlayer = youTubePlayer
                youTubePlayer.loadVideo(videoId.split("_split_")[0], 0f)
            }

            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                super.onCurrentSecond(youTubePlayer, second)
                Log.d("TAG", "onCurrentSecond: $second, ${floor(second.toDouble()).toLong()}")
                viewModel.setProgress(floor(second.toDouble()).toLong())
            }
        }, iFramePlayerOptions)

        youTubePlayerView.let { lifecycle.addObserver(it) }
    }

    override fun onPause() {
        super.onPause()
        videoId?.let { viewModel.updateVideo(it) }
    }
}