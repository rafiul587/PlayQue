package com.example.youtubeapitesting.ui.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.youtubeapitesting.SearchViewModel
import com.example.youtubeapitesting.models.Playlist
import com.example.youtubeapitesting.navigation.Screens


@Composable
fun AddPlaylistScreen(navController: NavController, viewModel: SearchViewModel, id: String) {
    val playLists = viewModel.playlists.collectAsState()
    LaunchedEffect(key1 = Unit){
        viewModel.getPlaylistByChannelId(id)
    }
    AddPlaylistRow(
        modifier = Modifier
            .fillMaxSize(),
        playLists = playLists.value,
        viewModel = viewModel,
        navController = navController
    )
}

@Composable
fun AddPlaylistRow(
    modifier: Modifier = Modifier,
    playLists: List<Playlist>,
    viewModel: SearchViewModel,
    navController: NavController
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(playLists) {
            AddPlaylistRow(playlist = it, onPlaylistClick = {
                Log.d("TAG", "AddPlaylistRow: $it")
                navController.navigate("${Screens.SearchVideoListScreen.id}/$it")
            }){
               viewModel.addNewPlaylist(it)
            }
        }
    }
}

@Composable
fun AddPlaylistRow(
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
                            .height(90.dp),
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