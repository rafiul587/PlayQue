package com.example.youtubeapitesting.navigation

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.youtubeapitesting.ui.screens.SearchedPlaylistScreen
import com.example.youtubeapitesting.ui.screens.trash.TrashScreen
import com.example.youtubeapitesting.ui.screens.about.AboutDeveloperScreen
import com.example.youtubeapitesting.ui.screens.home.HomeScreen
import com.example.youtubeapitesting.ui.screens.home.HomeViewModel
import com.example.youtubeapitesting.ui.screens.search.SearchScreen
import com.example.youtubeapitesting.ui.screens.search.SearchVideoListScreen
import com.example.youtubeapitesting.ui.screens.search.SearchViewModel
import com.example.youtubeapitesting.ui.screens.trash.TrashViewModel
import com.example.youtubeapitesting.ui.screens.videoplayer.VideoPlayerScreen
import com.example.youtubeapitesting.ui.screens.videos.VideoListScreen
import com.example.youtubeapitesting.ui.screens.videos.VideosViewModel

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
                VideoListScreen(viewModel, id)
            }
            composable(
                route = "${Screens.SearchVideoListScreen.id}/{id}"
            ) {
                val id = it.arguments?.getString("id") ?: ""
                val viewModel = hiltViewModel<SearchViewModel>()
                SearchVideoListScreen(navController = navController,viewModel,  id)
            }
            composable(
                route = "${Screens.AddPlaylistScreen.id}/{channelId}"
            ) {
                val id = it.arguments?.getString("channelId") ?: ""
                val viewModel = hiltViewModel<SearchViewModel>()
                SearchedPlaylistScreen(navController = navController, viewModel,  id)
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