package com.example.youtubeapitesting.navigation

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.youtubeapitesting.HomeViewModel
import com.example.youtubeapitesting.VideosViewModel
import com.example.youtubeapitesting.ui.screens.HomeScreen
import com.example.youtubeapitesting.ui.screens.PlayListScreen
import com.example.youtubeapitesting.ui.screens.VideoPlayerScreen

sealed class Screens(val id: String) {
    object Home : Screens("home")
    object PlayListScreen : Screens("play_list")
    object VideoPlayer : Screens("video_player")
}


@Composable
fun NavigationController(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: Screens = Screens.Home,
    onFinish: () -> Unit
) {
    BoxWithConstraints {
        NavHost(
            navController = navController, startDestination = startDestination.id
        ) {
            composable(
                route = Screens.Home.id,
            ) {
                val viewModel = hiltViewModel<HomeViewModel>()
                HomeScreen(navController = navController, viewModel)
            }
            composable(
                route = "${Screens.PlayListScreen.id}/{id}"
            ) {
                val id = it.arguments?.getString("id") ?: ""
                val viewModel = hiltViewModel<VideosViewModel>()
                PlayListScreen(navController = navController,viewModel,  id)
            }
            composable(route = Screens.VideoPlayer.id) {
                VideoPlayerScreen(navController = navController)
            }
        }
    }
}