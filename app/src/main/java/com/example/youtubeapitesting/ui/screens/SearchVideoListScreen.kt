package com.example.youtubeapitesting.ui.screens

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import com.example.youtubeapitesting.VideosViewModel
import com.example.youtubeapitesting.models.Video
import com.example.youtubeapitesting.navigation.Screens


@Composable
fun SearchVideoListScreen(navController: NavController, viewModel: VideosViewModel, playListId: String) {

    val list = viewModel.videos.collectAsLazyPagingItems()

    LaunchedEffect(key1 = Unit) {
        viewModel.getVideosFromPlaylist(playListId, Screens.SearchVideoListScreen.id)
    }
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 6.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        items(list) {
            if (it != null) {
                VideoRow(video = it)
            }
        }
    }
}

@Composable
fun VideoRow(video: Video) {
    ElevatedCard {
        Row(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp),
        ) {
            val time = remember {
                DateUtils.formatElapsedTime(video.duration)
            }
            if (video.thumbnail.isNotEmpty()) {
                Box() {
                    AsyncImage(
                        model = video.thumbnail,
                        contentDescription = "",
                        modifier = Modifier
                            .width(120.dp)
                            .height(90.dp),
                    )
                    Text(
                        modifier = Modifier
                            .background(Color.Black)
                            .padding(horizontal = 4.dp)
                            .align(Alignment.BottomEnd),
                        color = Color.White,
                        text = time
                    )
                }
            }

            Column(modifier = Modifier.padding(start = 10.dp)) {
                Text(text = video.title)
                Text(text = "${video.viewCount} Views" )
                Text(text = "${video.likeCount} Likes")
            }
        }
    }
}