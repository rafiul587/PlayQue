package com.example.youtubeapitesting.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.youtubeapitesting.TrashViewModel
import com.example.youtubeapitesting.models.Playlist


@Composable
fun TrashScreen(navController: NavController, viewModel: TrashViewModel) {
    val playLists = viewModel.trashedPlaylists.collectAsState()
    TrashLists(
        modifier = Modifier
            .fillMaxSize(),
        playLists = playLists.value,
        onPlayListClick = { },
        viewModel = viewModel
    )
}


@Composable
fun TrashLists(
    modifier: Modifier = Modifier,
    playLists: List<Playlist>,
    onPlayListClick: (String) -> Unit,
    viewModel: TrashViewModel
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 56.dp)
    ) {
        items(playLists) {
            TrashListRow(playlist = it, onPlaylistClick = onPlayListClick, viewModel = viewModel)
        }
    }
}

@Composable
fun TrashListRow(
    playlist: Playlist, onPlaylistClick: (String) -> Unit, viewModel: TrashViewModel
) {
    ElevatedCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clickable { onPlaylistClick(playlist.id ?: "") }) {
                if (playlist.thumbnail.isNotEmpty()) {
                    AsyncImage(
                        model = playlist.thumbnail,
                        contentDescription = "",
                        modifier = Modifier
                            .width(120.dp)
                            .height(90.dp),
                    )
                }
                Column(modifier = Modifier.padding(16.dp)) {
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
                TrashOptionsDropDown(onRestore ={
                    viewModel.restoreFromTrash(
                        playlist.copy(isTrash = false)
                    )
                }, onDelete =  {
                    viewModel.deletePlaylistPermanently(
                        playlist
                    )
                })
            }
        }
    }
}

@Composable
fun TrashOptionsDropDown(
    onRestore: () -> Unit,
    onDelete: () -> Unit
) {
    val listItems = listOf(
        Pair("Restore", Icons.Default.Refresh), Pair("Delete Permanently", Icons.Default.Delete)
    )

    var expanded by remember {
        mutableStateOf(false)
    }
    var selectedItem by remember {
        mutableStateOf(-1)
    }
    when (selectedItem) {
        0 -> onRestore()
        1 -> {
            PermanentlyDeleteDialog(onDismiss = { selectedItem = -1 }) {
                onDelete()
            }
        }
    }
    Box(
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = {
            expanded = true
        }) {
            Icon(
                imageVector = Icons.Default.MoreVert, contentDescription = "Open Options"
            )
        }

        // drop down menu
        DropdownMenu(expanded = expanded, onDismissRequest = {
            expanded = false
        }) {
            // adding items
            listItems.forEachIndexed { index, value ->
                DropdownMenuItem(onClick = {
                    selectedItem = index
                    expanded = false
                }, leadingIcon = {
                    Icon(imageVector = value.second, contentDescription = "Item Icon")
                }, text = {
                    Text(text = value.first)
                })
            }
        }
    }
}

@Composable
fun PermanentlyDeleteDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(onDismissRequest = { onDismiss() }, confirmButton = {
        TextButton(onClick = { onConfirm() }) {
            Text(text = "Delete")
        }
    }, dismissButton = {
        TextButton(onClick = { onDismiss() }) {
            Text(text = "Cancel")
        }
    }, title = {
        Text(text = "Permanently Delete?")
    }, text = {
        Text(text = "Are you sure to delete the playlist permanently? The action can't be undone!")
    })
}