package com.example.youtubeapitesting.ui.screens.search

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.youtubeapitesting.models.Channel
import com.example.youtubeapitesting.models.Playlist
import com.example.youtubeapitesting.navigation.Screens
import com.example.youtubeapitesting.ui.screens.components.EmptyScreen
import com.example.youtubeapitesting.ui.screens.components.listStateHandler
import com.google.gson.Gson


@Composable
fun SearchedPlaylistScreen(
    navController: NavController,
    viewModel: SearchViewModel,
    channel: Channel
) {
    val playLists = viewModel.playlists.collectAsLazyPagingItems()
    LaunchedEffect(key1 = Unit) {
        viewModel.getPlaylistByChannelId(channel.id)
    }
    SearchedChannelPlaylists(
        modifier = Modifier
            .fillMaxSize(),
        playlists = playLists,
        navController = navController,
        channel = channel,
        viewModel = viewModel
    )
}

@Composable
fun ChannelHeader(
    modifier: Modifier = Modifier,
    channel: Channel
) {
    Column(
        modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = channel.thumbnail,
            contentDescription = "",
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape),
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(text = channel.title)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "${channel.numbSub} subscribers")
            Text(text = "  -  ")
            Text(text = "${channel.numbVideos} videos")

        }
    }
}

@Composable
fun SearchedChannelPlaylists(
    modifier: Modifier = Modifier,
    playlists: LazyPagingItems<Playlist>,
    navController: NavController,
    channel: Channel,
    viewModel: SearchViewModel
) {

    val context = LocalContext.current

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            ChannelHeader(channel = channel)
        }

        listStateHandler(
            items = playlists,
            onSuccess = {

                if (playlists.itemSnapshotList.isEmpty()) {
                    item {
                        EmptyScreen(message = "No playlists found with this keyword!")
                    }
                    return@listStateHandler
                }

                items(playlists.itemCount) { index ->
                    val playlist = playlists[index]
                    playlist?.let {
                        var isPlaylistAdded by remember { mutableStateOf(false) }
                        LaunchedEffect(key1 = Unit) {
                            isPlaylistAdded = viewModel.isPlaylistAlreadyAdded(it.id)
                        }
                        SearchedPlaylistRow(
                            playlist = it.copy(channelTitle = channel.title),
                            onPlaylistClick = {
                                Log.d("TAG", "AddPlaylistRow: $it")
                                navController.navigate("${Screens.SearchVideoListScreen.id}/$it")
                            },
                            isFromChannel = true,
                            isPlaylistAdded = isPlaylistAdded,
                            onAddPlaylist = {
                                viewModel.addNewPlaylist(playlist = it.copy(channelTitle = channel.title))
                                isPlaylistAdded = true
                                Toast.makeText(
                                    context,
                                    "Playlist added successfully!",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            },

                            )
                    }
                }
            },
            onError = {
                playlists.refresh()
            },
            onAppendError = {
                playlists.retry()
            },
            emptyMessage = "No playlists found with this keyword!"
        )
    }
}

@Composable
fun SearchedPlaylistRow(
    playlist: Playlist,
    onPlaylistClick: (String) -> Unit,
    isFromChannel: Boolean,
    isPlaylistAdded: Boolean,
    onAddPlaylist: (Playlist) -> Unit,
) {
    ElevatedCard(
        modifier = Modifier.clickable {
            val json = Uri.encode(Gson().toJson(playlist))
            onPlaylistClick(json)
        }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (playlist.thumbnail.isNotEmpty()) {
                    AsyncImage(
                        model = playlist.thumbnail,
                        contentDescription = "",
                        modifier = Modifier
                            .width(120.dp)
                            .height(90.dp)
                            .clip(RoundedCornerShape(10.dp)),
                    )
                }
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .weight(1f)
                ) {
                    Text(
                        text = playlist.title, fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    if (!isFromChannel) {
                        Text(
                            text = playlist.channelTitle, fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onTertiary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                    }
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
                    IconButton(onClick = { onAddPlaylist(playlist) }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                    }
                }
            }
        }
    }
}