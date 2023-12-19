package com.qubartech.playque.ui.screens.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.qubartech.playque.R

@Composable
fun LoadingContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            modifier = Modifier
                .padding(8.dp),
            text = "Loading"
        )
        CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
fun ErrorScreen(modifier: Modifier = Modifier, onRetry: () -> Unit) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            modifier = Modifier.size(60.dp),
            painter = painterResource(id = R.drawable.baseline_error_outline_24),
            contentDescription = "Error Icon",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Something went wrong!",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                onRetry()
            },
            contentPadding = PaddingValues(horizontal = 30.dp)
        ) {
            Text(text = "Retry")
        }
    }
}

@Composable
fun AppendErrorScreen(modifier: Modifier = Modifier, onRetry: () -> Unit) {
    Row(
        modifier = modifier.clickable {
            onRetry()
        },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.baseline_error_outline_24),
            contentDescription = "Error Icon",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "Failed!",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.width(20.dp))
        IconButton(
            onClick = {
                onRetry()
            },
        ) {
            Icon(
                painterResource(id = R.drawable.baseline_retry_24),
                contentDescription = "Retry Icon"
            )
        }
    }
}

@Composable
fun EmptyScreen(modifier: Modifier = Modifier, message: String) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier.size(100.dp),
            painter = painterResource(id = R.drawable.baseline_hourglass_empty_24),
            contentDescription = "Empty Icon",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
        )
        Spacer(modifier = Modifier.height(15.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.titleMedium
        )
    }
}


fun <T : Any> LazyListScope.listStateHandler(
    items: LazyPagingItems<T>,
    onSuccess: () -> Unit,
    onError: () -> Unit,
    onAppendError: () -> Unit,
    emptyMessage: String
) {
    when (items.loadState.refresh) { //FIRST LOAD
        is LoadState.Error -> {
            item {
                ErrorScreen(
                    modifier = Modifier.fillParentMaxSize(),
                    onRetry = { onError() })
            }
        }

        is LoadState.Loading -> { // Loading UI
            item {
                LoadingContent(modifier = Modifier.fillParentMaxSize())
            }
        }

        else -> {
            if (items.isListEmpty()) {
                item {
                    EmptyScreen(
                        modifier = Modifier.fillParentMaxSize(),
                        message = emptyMessage
                    )
                }
                return
            }
            onSuccess()
        }
    }
    when (items.loadState.append) { // Pagination
        is LoadState.Error -> {
            item {
                AppendErrorScreen(
                    modifier = Modifier.fillParentMaxWidth(),
                    onRetry = {
                        onAppendError()
                    })
            }

        }

        is LoadState.Loading -> { // Pagination Loading UI
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground)
                }
            }
        }

        else -> {}
    }
}


fun <T : Any> LazyPagingItems<T>.isListEmpty(): Boolean {
    return loadState.source.refresh is LoadState.NotLoading &&
            loadState.append.endOfPaginationReached && itemSnapshotList.isEmpty()
}

@Preview
@Composable
fun previewErrorScreen() {
    //ErrorScreen()
}