package com.example.youtubeapitesting.ui.screens.search

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import com.example.youtubeapitesting.ui.screens.videos.VideosViewModel
import com.example.youtubeapitesting.models.Video
import com.example.youtubeapitesting.navigation.Screens


@Composable
fun SearchVideoListScreen(
    navController: NavController,
    viewModel: SearchViewModel,
    playListId: String
) {

    val videos = viewModel.videos.collectAsLazyPagingItems()


    LaunchedEffect(key1 = Unit) {
        viewModel.getVideosFromPlaylist(playListId)
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 6.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {

        items(videos) {
            it?.let { it1 -> VideoRow(video = it1) }
        }
        when (videos.loadState.refresh) { //FIRST LOAD
            is LoadState.Error -> {
                //TODO Error Item
                //state.error to get error message
            }
            is LoadState.Loading -> { // Loading UI
                item {
                    Column(
                        modifier = Modifier
                            .fillParentMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(8.dp),
                            text = "Loading"
                        )

                        CircularProgressIndicator(color = Color.Black)
                    }
                }
            }
            else -> {}
        }

        when (videos.loadState.append) { // Pagination
            is LoadState.Error -> {
                //TODO Pagination Error Item
                //state.error to get error message
            }
            is LoadState.Loading -> { // Pagination Loading UI
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(text = "Pagination Loading")

                        CircularProgressIndicator(color = Color.Black)
                    }
                }
            }
            else -> {}
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
                                .width(130.dp)
                                .height(80.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            contentScale = ContentScale.FillBounds
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
                    Text(text = "${video.viewCount} Views")
                    Text(text = "${video.likeCount} Likes")
                }
            }
        }
    }