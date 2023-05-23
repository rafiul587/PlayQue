package com.example.youtubeapitesting.ui.screens.videoplayer

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView


@Composable
fun VideoPlayerScreen(navController: NavController) {
    YouTubePlayerView("_nmm0nZqIIY")
}

@Composable
fun YouTubePlayerView(videoId: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
            val context = LocalContext.current
            val lifecycleOwner = LocalLifecycleOwner.current

            // Create a new instance of YouTubePlayerView
            val playerView = remember {
                val iFramePlayerOptions: IFramePlayerOptions = IFramePlayerOptions.Builder()
                    .controls(1)
                    .build()
                YouTubePlayerView(context).apply {
                    enableAutomaticInitialization = false
                    initialize(object : AbstractYouTubePlayerListener() {
                        override fun onReady(youTubePlayer: YouTubePlayer) {
                            youTubePlayer.loadVideo(videoId, 0F)
                            youTubePlayer.play()
                        }

                        override fun onVideoDuration(
                            youTubePlayer: YouTubePlayer,
                            duration: Float
                        ) {
                        }
                    }, iFramePlayerOptions)
                    lifecycleOwner.lifecycle.addObserver(this)
                }
            }

            DisposableEffect(lifecycleOwner) {
                val observer = object : LifecycleEventObserver {
                    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                        when (event) {
                            Lifecycle.Event.ON_DESTROY -> playerView.release()
                            else -> return
                        }
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }

            AndroidView(
                factory = { playerView },
                modifier = Modifier.fillMaxSize()
            )
        }
    }