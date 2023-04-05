package com.example.youtubeapitesting

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun AppBar(
    drawerState: DrawerState,
    scope: CoroutineScope,
    content: @Composable () -> Unit
) {

    val items = listOf(
        "Home" to R.drawable.round_home_24,
        "Search Channel" to R.drawable.baseline_video_library_24,
        "Trash" to R.drawable.baseline_archive_24,
        "About" to R.drawable.baseline_account_circle_24
    )
    val selectedItem = remember { mutableStateOf(items[0]) }
    ModalNavigationDrawer(
        drawerState = drawerState,
        scrimColor = Color.Transparent,
        drawerContent = {
            ModalDrawerSheet(
            ) {
                DrawerHeader() {
                    scope.launch { drawerState.close() }
                }
                Spacer(Modifier.height(12.dp))
                items.forEach { item ->
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                painter = painterResource(id = item.second),
                                contentDescription = null
                            )
                        },
                        label = { Text(item.first) },
                        selected = item == selectedItem.value,
                        onClick = {
                            scope.launch { drawerState.close() }
                            selectedItem.value = item
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        },
        content = content
    )
}

@Composable
fun DrawerHeader(onClose: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 20.dp, horizontal = 16.dp),
    ) {
        IconButton(
            modifier = Modifier
                .align(Alignment.TopEnd),
            onClick = { onClose() }) {
            Icon(

                imageVector = Icons.Default.Clear, contentDescription = "Close"
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.size(64.dp),
                painter = painterResource(id = R.drawable.baseline_ondemand_video_24),
                contentDescription = "Logo",
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "StudyTube",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
