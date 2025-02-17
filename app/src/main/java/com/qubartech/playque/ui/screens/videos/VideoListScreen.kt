package com.qubartech.playque.ui.screens.videos

import android.content.Intent
import android.text.format.DateUtils
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.qubartech.playque.R
import com.qubartech.playque.models.Video
import com.qubartech.playque.ui.YoutubePlayerActivity
import com.qubartech.playque.ui.screens.components.listStateHandler

@Composable
fun VideoListScreen(viewModel: VideosViewModel, playListId: String) {

    val videos = viewModel.videos.collectAsLazyPagingItems()

    LaunchedEffect(key1 = Unit) {
        viewModel.getVideosFromPlaylist(playListId)
    }
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        contentPadding = PaddingValues(12.dp)
    ) {
        listStateHandler(
            items = videos,
            onSuccess = {
                items(
                    count = videos.itemCount
                ) { index ->
                    val item = videos[index]
                    item?.let { video ->
                        VideoRow(video = video, viewModel = viewModel, playListId = playListId) {
                            val intent = Intent(context, YoutubePlayerActivity::class.java)
                            intent.putExtra("videoId", item.id)
                            intent.putExtra("progress", item.progress)
                            context.startActivity(intent)
                        }
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
fun VideoRow(
    video: Video,
    viewModel: VideosViewModel,
    playListId: String,
    onClickVideo: () -> Unit
) {
    ElevatedCard {
        Row(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clickable { onClickVideo() }
                .padding(8.dp),
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
                            .height(90.dp)
                            .clip(RoundedCornerShape(10.dp)),
                    )
                    Text(
                        modifier = Modifier
                            .background(Color.Black)
                            .padding(horizontal = 4.dp)
                            .align(Alignment.BottomEnd),
                        color = Color.White,
                        text = time
                    )
                    /*LinearProgressIndicator(
                        modifier = Modifier
                            .width(120.dp)
                            .align(Alignment.BottomCenter)
                            .semantics { contentDescription = "Localized Description" },
                        progress = if (video.duration != 0L) (video.progress / video.duration).toFloat() else .2f,
                        strokeCap = StrokeCap.Square,
                        color = Color.Red
                    )*/
                }
            }

            Column(modifier = Modifier.padding(start = 10.dp)) {
                val isComplete by rememberUpdatedState(
                    video.duration != 0L && video.progress >= video.duration - 1
                )
                Text(text = video.title)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    //Text(text = time)
                    LinearProgressIndicator(
                        modifier = Modifier
                            .weight(1f)
                            .semantics { contentDescription = "Localized Description" },
                        progress = if (video.duration != 0L) (video.progress / video.duration.toFloat()) else 0f,
                        strokeCap = StrokeCap.Round
                    )
                    val context = LocalContext.current
                    IconButton(onClick = {
                        if (isComplete) {
                            viewModel.updateVideo(video.copy(progress = 0))
                            Toast.makeText(context, "Incomplete", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.updateVideo(video.copy(progress = video.duration))
                            Toast.makeText(context, "Complete", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(
                            painter = painterResource(id = if (isComplete) R.drawable.icon_check_circle_rounded else R.drawable.icon_check_circle_outline_rounded),
                            contentDescription = "Done",
                            tint = if (isComplete) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }
                }
            }
        }
    }
}