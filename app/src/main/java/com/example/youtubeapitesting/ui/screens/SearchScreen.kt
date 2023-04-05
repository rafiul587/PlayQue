package com.example.youtubeapitesting.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.youtubeapitesting.SearchViewModel
import com.example.youtubeapitesting.models.Channel
import com.example.youtubeapitesting.models.Playlist
import com.example.youtubeapitesting.navigation.Screens

@Composable
fun SearchScreen(navController: NavController, viewModel: SearchViewModel) {
    val playLists by viewModel.playlists.collectAsState()
    val channelLists by viewModel.channelLists.collectAsState()
    val (query, onQueryChange) = remember {
        mutableStateOf("")
    }
    Column(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)
    ) {
        val typeList = listOf("Playlist", "Channel")

        SearchBoxLayout(query = query, onQueryChange = onQueryChange, onSearch = {
            if(query.isNotEmpty()) {
                viewModel.search(query, typeList[viewModel.selectedType].lowercase())
            }
        })

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            typeList.forEachIndexed { index, item ->
                RadioButton(selected = viewModel.selectedType == index, onClick = {
                    viewModel.selectType(index)
                    if(query.isNotEmpty()) {
                        viewModel.search(query, item.lowercase())
                    }
                })
                Text(text = item)
                if (index != typeList.lastIndex) {
                    Spacer(modifier = Modifier.width(15.dp))
                }
            }
        }
        if (viewModel.selectedType == 1) {
            SearchResults(
                modifier = Modifier
                    .fillMaxSize(),
                playLists = channelLists,
                onPlayListClick = {
                    navController.navigate("${Screens.AddPlaylistScreen.id}/$it")
                },
                viewModel = viewModel
            )
        } else {
            AddPlaylistRow(playLists = playLists, viewModel = viewModel, navController = navController)
        }
    }

}

@Composable
fun SearchBoxLayout(
    modifier: Modifier = Modifier,
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
            placeholder = { Text(fontSize = 18.sp, text = "Search any youtube channel..") })
        Button(
            modifier = Modifier
                .wrapContentSize()
                .padding(horizontal = 10.dp), onClick = {
                onSearch(query)
            }, shape = RoundedCornerShape(10)
        ) {
            Text(text = "Search")
        }
    }
}

@Composable
fun SearchResults(
    modifier: Modifier = Modifier,
    playLists: List<Channel>,
    onPlayListClick: (String) -> Unit,
    viewModel: SearchViewModel
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(playLists) {
            ChannelRow(channel = it, onPlaylistClick = onPlayListClick, viewModel = viewModel)
        }
    }
}

@Composable
fun ChannelRow(
    channel: Channel, onPlaylistClick: (String) -> Unit, viewModel: SearchViewModel
) {
    ElevatedCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clickable { onPlaylistClick(channel.id) }) {
                if (channel.thumbnail.isNotEmpty()) {
                    AsyncImage(
                        model = channel.thumbnail,
                        contentDescription = "",
                        modifier = Modifier
                            .width(120.dp)
                            .height(90.dp),
                    )
                }
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = channel.title, fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${channel.numbSub} Subscribers",
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${channel.numbVideos} videos",
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )
                }
            }
        }
    }
}
