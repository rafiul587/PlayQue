package com.example.youtubeapitesting.navigation

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.youtubeapitesting.HomeViewModel
import com.example.youtubeapitesting.SearchViewModel
import com.example.youtubeapitesting.TrashViewModel
import com.example.youtubeapitesting.VideosViewModel
import com.example.youtubeapitesting.ui.screens.*

sealed class Screens(val id: String) {
    object Home : Screens("home")
    object Search : Screens("search")
    object Trash : Screens("trash")
    object VideoListScreen : Screens("video_list_screen")
    object SearchVideoListScreen : Screens("search_video_list_screen")
    object AddPlaylistScreen : Screens("add_play_list")
    object VideoPlayer : Screens("video_player")
    object AboutDeveloper: Screens("about_developer")
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
            modifier = modifier,
            navController = navController, startDestination = startDestination.id
        ) {
            composable(
                route = Screens.Home.id,
            ) {
                val viewModel = hiltViewModel<HomeViewModel>()
                HomeScreen(navController = navController, viewModel)
            }
            composable(route = Screens.Search.id) {
                val viewModel = hiltViewModel<SearchViewModel>()
                SearchScreen(navController = navController, viewModel)
            }
            composable(route = Screens.Trash.id) {
                val viewModel = hiltViewModel<TrashViewModel>()
                TrashScreen(navController = navController, viewModel)
            }
            composable(
                route = "${Screens.VideoListScreen.id}/{id}"
            ) {
                val id = it.arguments?.getString("id") ?: ""
                val viewModel = hiltViewModel<VideosViewModel>()
                VideoListScreen(navController = navController,viewModel,  id)
            }
            composable(
                route = "${Screens.SearchVideoListScreen.id}/{id}"
            ) {
                val id = it.arguments?.getString("id") ?: ""
                val viewModel = hiltViewModel<VideosViewModel>()
                SearchVideoListScreen(navController = navController,viewModel,  id)
            }
            composable(
                route = "${Screens.AddPlaylistScreen.id}/{channelId}"
            ) {
                val id = it.arguments?.getString("channelId") ?: ""
                val viewModel = hiltViewModel<SearchViewModel>()
                AddPlaylistScreen(navController = navController, viewModel,  id)
            }
            composable(route = Screens.VideoPlayer.id) {
                VideoPlayerScreen(navController = navController)
            }
            composable(route = Screens.AboutDeveloper.id) {
                AboutDeveloperScreen(navController = navController)
            }
        }
    }
}