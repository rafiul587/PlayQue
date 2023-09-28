package com.example.youtubeapitesting.navigation

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.youtubeapitesting.models.Channel
import com.example.youtubeapitesting.models.Playlist
import com.example.youtubeapitesting.ui.screens.search.SearchedPlaylistScreen
import com.example.youtubeapitesting.ui.screens.trash.TrashScreen
import com.example.youtubeapitesting.ui.screens.about.AboutDeveloperScreen
import com.example.youtubeapitesting.ui.screens.home.HomeScreen
import com.example.youtubeapitesting.ui.screens.home.HomeViewModel
import com.example.youtubeapitesting.ui.screens.search.SearchScreen
import com.example.youtubeapitesting.ui.screens.search.SearchedVideoListScreen
import com.example.youtubeapitesting.ui.screens.search.SearchViewModel
import com.example.youtubeapitesting.ui.screens.trash.TrashViewModel
import com.example.youtubeapitesting.ui.screens.videoplayer.VideoPlayerScreen
import com.example.youtubeapitesting.ui.screens.videos.VideoListScreen
import com.example.youtubeapitesting.ui.screens.videos.VideosViewModel
import com.google.gson.Gson

sealed class Screens(val id: String) {
    data object Home : Screens("home")
    data object Search : Screens("search")
    data object SearchGraph : Screens("Search_nav_graph")
    data object Trash : Screens("trash")
    data object VideoListScreen : Screens("video_list_screen")
    data object SearchVideoListScreen : Screens("search_video_list_screen")
    data object AddPlaylistScreen : Screens("add_play_list")
    data object VideoPlayer : Screens("video_player")
    data object AboutDeveloper : Screens("about_developer")
}


@Composable
fun NavigationController(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: Screens = Screens.Home,
    onFinish: () -> Unit
) {
    Box {
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
            navigation(
                startDestination = Screens.Search.id,
                route = "Search_nav_graph"
            ){
                composable(route = Screens.Search.id) {
                    val viewModel = hiltViewModel<SearchViewModel>()
                    SearchScreen(navController = navController, viewModel)
                }
                composable(
                    route = "${Screens.SearchVideoListScreen.id}/{playlist}",
                    arguments = listOf(
                        navArgument("playlist") {
                            type = NavType.StringType
                        }
                    )
                ) {
                    val playlist = remember {
                        Gson().fromJson(Uri.decode(it.arguments?.getString("playlist")), Playlist::class.java)
                    }
                    val viewModel = hiltViewModel<SearchViewModel>()
                    playlist?.let { SearchedVideoListScreen(viewModel, it) }
                }
                composable(
                    route = "${Screens.AddPlaylistScreen.id}/{channel}",
                    arguments = listOf(
                        navArgument("channel") {
                            type = NavType.StringType
                        }
                    )) {
                    val channel = remember {
                        Gson().fromJson(Uri.decode(it.arguments?.getString("channel")), Channel::class.java)
                    }
                    val viewModel = hiltViewModel<SearchViewModel>()
                    channel?.let {
                        SearchedPlaylistScreen(
                            navController = navController, viewModel,
                            it
                        )
                    }
                }
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

            composable(route = Screens.VideoPlayer.id) {
                VideoPlayerScreen(navController = navController)
            }
            composable(route = Screens.AboutDeveloper.id) {
                AboutDeveloperScreen(navController = navController)
            }
        }
    }
}