package com.example.youtubeapitesting.ui.screens.search

import android.text.format.DateUtils
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.youtubeapitesting.models.Playlist
import com.example.youtubeapitesting.models.Video
import com.example.youtubeapitesting.ui.screens.components.listStateHandler


@Composable
fun SearchedVideoListScreen(
    viewModel: SearchViewModel, playlist: Playlist
) {

    val videos = viewModel.videos.collectAsLazyPagingItems()
    val context = LocalContext.current
    var isPlaylistAdded by remember { mutableStateOf(false) }
    //var privateVideoCount by rememberUpdatedState(newValue =  videos.itemSnapshotList.count { it. } })

    LaunchedEffect(key1 = Unit) {
        viewModel.getVideosFromPlaylist(playlist.id)
        isPlaylistAdded = viewModel.isPlaylistAlreadyAdded(playlist.id)
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 6.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        item {
            PlaylistHeader(playlist = playlist, isPlaylistAdded = isPlaylistAdded, onAddPlaylist = {
                viewModel.addNewPlaylist(playlist)
                isPlaylistAdded = true
                Toast.makeText(context, "Playlist added successfully!", Toast.LENGTH_SHORT).show()
            })
        }

        listStateHandler(
            items = videos,
            onSuccess = {
                items(count = videos.itemCount) { index ->
                    val video = videos[index]
                    video?.let {
                        VideoRow(video = it)
                    }
                }
            },
            onError = {
                videos.refresh()
            },
            onAppendError = {
                videos.retry()
            },
            emptyMessage = "No public videos available"
        )
    }
}

@Composable
fun PlaylistHeader(
    playlist: Playlist, isPlaylistAdded: Boolean, onAddPlaylist: () -> Unit
) {

    ElevatedCard() {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(

                        text = playlist.title,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = playlist.channelTitle, fontSize = 12.sp, color = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${playlist.itemCount} videos",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                }
                if (isPlaylistAdded) {
                    Text(
                        modifier = Modifier.padding(16.dp),
                        fontSize = 16.sp,
                        text = "Added",
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                } else {
                    IconButton(onClick = { onAddPlaylist() }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                    }
                }
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
                            .width(130.dp)
                            .height(80.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        contentScale = ContentScale.FillBounds
                    )
                    Text(
                        modifier = Modifier
                            .background(Color.Black)
                            .padding(horizontal = 4.dp)
                            .align(Alignment.BottomEnd), color = Color.White, text = time
                    )
                }
            }
            Column(
                modifier = Modifier.padding(start = 10.dp)
            ) {
                Text(text = video.title)
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = "${video.viewCount} Views • ${video.likeCount} Likes",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                }
                Text(
                    text = getTimeAgo(LocalContext.current, video.videoPublishedAt),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onTertiary
                )
            }
        }
    }
}

@Preview
@Composable
fun previewPlaylistHeader() {
    PlaylistHeader(
        playlist = Playlist(
            id = "esse",
            title = "Career with ehsan",
            channelTitle = "Hablu programmer",
            thumbnail = "solet",
            itemCount = 1209,
            itemComplete = 100,
            isTrash = false,
            addedTime = 8106
        ), isPlaylistAdded = true
    ) {

    }
}