package com.example.youtubeapitesting.ui.screens.search

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import com.example.youtubeapitesting.models.Channel
import com.example.youtubeapitesting.navigation.Screens
import com.example.youtubeapitesting.ui.screens.SearchResultPlaylistRow
import com.example.youtubeapitesting.utils.Constants.CHANNEL

@Composable
fun SearchScreen(navController: NavController, viewModel: SearchViewModel) {
/*    val playLists by viewModel.playlists.collectAsState()
    val channelLists by viewModel.channelLists.collectAsState()*/
    val channelResults = viewModel.channelLists.collectAsLazyPagingItems()
    val playlistResults = viewModel.playlists.collectAsLazyPagingItems()

    Column(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)
    ) {
        val typeList = listOf("Channel", "Playlist")

        SearchBoxLayout(query = viewModel.query, onQueryChange = {viewModel.onQueryChange(it)}, onSearch = {
            if(viewModel.query.isNotEmpty()) {
               viewModel.search(viewModel.selectedType)
            }
        })

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            typeList.forEachIndexed { index, item ->
                RadioButton(selected = viewModel.selectedType == index, onClick = {
                    viewModel.search(index)
                })
                Text(text = item)
                if (index != typeList.lastIndex) {
                    Spacer(modifier = Modifier.width(15.dp))
                }
            }
        }
        if (viewModel.selectedType == CHANNEL) {
            SearchedChannels(
                modifier = Modifier
                    .fillMaxSize(),
                playLists = channelResults,
                onPlayListClick = {
                    navController.navigate("${Screens.AddPlaylistScreen.id}/$it")
                },
                viewModel = viewModel
            )
        } else {
            SearchResultPlaylistRow(playLists = playlistResults, viewModel = viewModel, navController = navController)
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
            placeholder = { Text(fontSize = 18.sp, text = "Search channels or playlists..") })
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
fun SearchedChannels(
    modifier: Modifier = Modifier,
    playLists: LazyPagingItems<Channel>,
    onPlayListClick: (String) -> Unit,
    viewModel: SearchViewModel
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(playLists) {
            it?.let { it1 -> ChannelRow(channel = it1, onPlaylistClick = onPlayListClick, viewModel = viewModel) }
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
