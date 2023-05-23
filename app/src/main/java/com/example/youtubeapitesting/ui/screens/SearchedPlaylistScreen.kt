package com.example.youtubeapitesting.ui.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import com.example.youtubeapitesting.models.Playlist
import com.example.youtubeapitesting.navigation.Screens
import com.example.youtubeapitesting.ui.screens.search.SearchViewModel


@Composable
fun SearchedPlaylistScreen(navController: NavController, viewModel: SearchViewModel, id: String) {
    val playLists = viewModel.playlists.collectAsLazyPagingItems()
    LaunchedEffect(key1 = Unit){
        viewModel.getPlaylistByChannelId(id)
    }
    SearchResultPlaylistRow(
        modifier = Modifier
            .fillMaxSize(),
        playLists = playLists,
        viewModel = viewModel,
        navController = navController
    )
}

@Composable
fun SearchResultPlaylistRow(
    modifier: Modifier = Modifier,
    playLists: LazyPagingItems<Playlist>,
    viewModel: SearchViewModel,
    navController: NavController
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(playLists) {
            it?.let { it1 ->
                SearchedPlaylistRow(playlist = it1, onPlaylistClick = {
                    Log.d("TAG", "AddPlaylistRow: $it")
                    navController.navigate("${Screens.SearchVideoListScreen.id}/$it")
                }){
                    viewModel.addNewPlaylist(it)
                }
            }
        }
    }
}

@Composable
fun SearchedPlaylistRow(
    playlist: Playlist,
    onPlaylistClick: (String) -> Unit,
    onAdd: () -> Unit,
) {
    ElevatedCard(
        modifier = Modifier.clickable {
            onPlaylistClick(playlist.id)
        }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
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
                Column(modifier = Modifier
                    .padding(16.dp)
                    .weight(1f)) {
                    Text(
                        text = playlist.title, fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = playlist.channelTitle, fontSize = 12.sp, color = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${playlist.itemCount} videos",
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )
                }
                IconButton(onClick = { onAdd() }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                }
            }
        }
    }
}