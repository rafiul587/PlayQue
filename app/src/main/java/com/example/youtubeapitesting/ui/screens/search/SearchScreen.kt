package com.example.youtubeapitesting.ui.screens.search

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.youtubeapitesting.models.Channel
import com.example.youtubeapitesting.models.Playlist
import com.example.youtubeapitesting.navigation.Screens
import com.example.youtubeapitesting.ui.screens.components.EmptyScreen
import com.example.youtubeapitesting.ui.screens.components.listStateHandler
import com.example.youtubeapitesting.utils.Constants.CHANNEL
import com.google.gson.Gson

@Composable
fun SearchScreen(navController: NavController, viewModel: SearchViewModel) {
    val channelResults = viewModel.channelLists.collectAsLazyPagingItems()
    val playlistResults = viewModel.playlists.collectAsLazyPagingItems()

    Log.d("TAG", "SearchScreen: ${channelResults.loadState}")
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)
    ) {
        val typeList = listOf("Channel", "Playlist")

        SearchBoxLayout(
            focusManager = focusManager,
            query = viewModel.query,
            onQueryChange = { viewModel.onQueryChange(it) },
            onSearch = {
                viewModel.search(viewModel.selectedType)
            })

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            typeList.forEachIndexed { index, item ->
                RadioButton(selected = viewModel.selectedType == index, onClick = {
                    viewModel.search(index)
                    focusManager.clearFocus()
                })
                Text(text = item)
                if (index != typeList.lastIndex) {
                    Spacer(modifier = Modifier.width(15.dp))
                }
            }
        }
        if (viewModel.selectedType == CHANNEL) {
            SearchedChannels(
                channels = channelResults
            ) {
                navController.navigate("${Screens.AddPlaylistScreen.id}/$it")
            }
        } else {
            SearchedPlaylists(
                playlists = playlistResults,
                navController = navController,
                viewModel = viewModel
            )
        }
    }

}


@Composable
fun SearchBoxLayout(
    modifier: Modifier = Modifier,
    focusManager: FocusManager,
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit
) {
    Column(
        modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .scale(.9f),
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text(fontSize = 18.sp, text = "Search channels or playlists..") })
        Button(
            modifier = Modifier
                .wrapContentSize()
                .padding(horizontal = 10.dp), onClick = {
                onSearch(query)
                focusManager.clearFocus()
            }, shape = RoundedCornerShape(10)
        ) {
            Text(text = "Search")
        }
    }
}

@Composable
fun SearchedPlaylists(
    modifier: Modifier = Modifier,
    playlists: LazyPagingItems<Playlist>,
    viewModel: SearchViewModel,
    navController: NavController,
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {

        listStateHandler(
            items = playlists,
            onSuccess = {
                items(playlists.itemCount) { index ->
                    val playlist = playlists[index]
                    playlist?.let {
                        var isPlaylistAdded by remember { mutableStateOf(false) }
                        LaunchedEffect(key1 = Unit) {
                            isPlaylistAdded = viewModel.isPlaylistAlreadyAdded(it.id)
                        }
                        SearchedPlaylistRow(
                            playlist = it,
                            onPlaylistClick = {
                                navController.navigate("${Screens.SearchVideoListScreen.id}/$it")
                            },
                            isFromChannel = false,
                            isPlaylistAdded = isPlaylistAdded
                        ) {
                            viewModel.addNewPlaylist(it)
                            isPlaylistAdded = true
                            Toast.makeText(
                                context,
                                "Playlist added successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
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
fun SearchedChannels(
    modifier: Modifier = Modifier,
    channels: LazyPagingItems<Channel>,
    onChannelClick: (String) -> Unit
) {
    Log.d("TAG", "SearchedChannels: ${channels.loadState} ${channels.itemSnapshotList}")
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {

        listStateHandler(
            items = channels,
            onSuccess = {
                items(
                    count = channels.itemCount
                ) { index ->
                    val item = channels[index]
                    item?.let { it1 ->
                        ChannelRow(
                            channel = it1,
                            onChannelClick = onChannelClick
                        )
                    }
                }
            },
            onError = {
                channels.refresh()
            },
            onAppendError = {
                channels.retry()
            },
            emptyMessage = "No channels found with this keyword!"
        )
    }
}

@Composable
fun ChannelRow(
    channel: Channel, onChannelClick: (String) -> Unit
) {
    ElevatedCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clickable {
                        val json = Uri.encode(Gson().toJson(channel))
                        onChannelClick(json)
                    }) {
                if (channel.thumbnail.isNotEmpty()) {
                    AsyncImage(
                        model = channel.thumbnail,
                        contentDescription = "",
                        modifier = Modifier
                            .width(120.dp)
                            .height(90.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        contentScale = ContentScale.FillBounds
                    )
                }
                Column(Modifier.padding(start = 16.dp)) {
                    Text(
                        text = channel.title, fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${channel.numbSub} Subscribers",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${channel.numbVideos} videos",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                }
            }
        }
    }
}
