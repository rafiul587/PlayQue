package com.qubartech.playque.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.qubartech.playque.R
import com.qubartech.playque.models.BottomNavItem
import com.qubartech.playque.navigation.MenuItem
import com.qubartech.playque.navigation.NavigationController
import com.qubartech.playque.navigation.Screens
import com.qubartech.playque.ui.screens.home.HomeViewModel
import com.qubartech.playque.ui.screens.home.UrlInputLayout
import com.qubartech.playque.ui.theme.YoutubeApiTestingTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<HomeViewModel>()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YoutubeApiTestingTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    val navController = rememberNavController()

                    val (url, onUrlChange) = remember { mutableStateOf("") }
                    var showAddDialogue by remember {
                        mutableStateOf(false)
                    }

                    val currentBackStack by navController.currentBackStackEntryAsState()
                    Log.d("TAG", "onCreate: ${currentBackStack?.destination?.route}")
                    val title = remember(currentBackStack?.destination?.route) {
                        when (currentBackStack?.destination?.route) {
                            Screens.SearchGraph.id -> "Search"
                            Screens.Trash.id -> "Trash"
                            Screens.SearchVideoListScreen.id + "/{playlist}" -> "Searched Videos"
                            Screens.AddPlaylistScreen.id + "/{channel}" -> "Searched Playlists"
                            Screens.AboutDeveloper.id -> "About Developer"
                            else -> getString(R.string.app_name)
                        }
                    }

                    val isCurrentScreenHome =
                        currentBackStack?.destination?.route == Screens.Home.id

                    if (showAddDialogue) {
                        AlertDialog(
                            modifier = Modifier
                                .padding(15.dp)
                                .clip(RoundedCornerShape(15)), onDismissRequest = {
                                showAddDialogue = false
                            }, properties = DialogProperties(usePlatformDefaultWidth = false)
                        ) {
                            Surface {
                                UrlInputLayout(modifier = Modifier
                                    .padding(30.dp)
                                    .wrapContentSize(),
                                    url = url,
                                    onUrlChange = onUrlChange,
                                    onAddClick = {
                                        viewModel.addNewPlaylist(it)
                                        showAddDialogue = false
                                    })
                            }
                        }
                    }

                    Scaffold(topBar = {
                        TopAppBar(
                            modifier = Modifier.shadow(4.dp, RectangleShape),
                            title = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    if (navController.currentDestination != null && navController.currentDestination?.route != Screens.Home.id && navController.currentDestination?.route != Screens.Search.id &&
                                        navController.currentDestination?.route != Screens.Trash.id
                                    ) {
                                        IconButton(onClick = {
                                            navController.popBackStack()
                                        }) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                contentDescription = "Back"
                                            )
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(
                                            painter = painterResource(id = R.drawable.baseline_ondemand_video_24),
                                            contentDescription = "Logo"
                                        )

                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        text = title,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            },
                            actions = {
                                if (currentBackStack?.destination?.route != Screens.AboutDeveloper.id) {
                                    ActionMenuDropDown(navigateToId = {
                                        navController.navigate(it)
                                    }
                                    )
                                }
                            }
                        )
                    },
                        content = {
                            NavigationController(
                                modifier = Modifier.padding(it),
                                navController = navController
                            ) {}
                        }, bottomBar = {
                            val items = remember {
                                listOf(
                                    BottomNavItem(
                                        "Home",
                                        Screens.Home.id,
                                        R.drawable.round_home_24
                                    ),
                                    BottomNavItem(
                                        "Search Channel",
                                        Screens.SearchGraph.id,
                                        R.drawable.baseline_search_24
                                    ),
                                    BottomNavItem(
                                        "Trash",
                                        Screens.Trash.id,
                                        R.drawable.baseline_archive_24
                                    )
                                )
                            }
                            NavigationBar {
                                items.forEachIndexed { index, item ->
                                    NavigationBarItem(icon = {
                                        Icon(
                                            painter = painterResource(id = item.icon),
                                            contentDescription = "${item.name} Icon"
                                        )
                                    },
                                        label = { Text(item.name) },
                                        selected = item.route == currentBackStack?.destination?.route || item.route == currentBackStack?.destination?.parent?.route,
                                        onClick = {
                                            navController.navigate(item.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                // Avoid multiple copies of the same destination when
                                                // reselecting the same item
                                                launchSingleTop = true
                                                // Restore state when reselecting a previously selected item
                                                restoreState = true
                                            }
                                        })
                                }
                            }
                        },


                        floatingActionButton = {
                            AnimatedVisibility(
                                visible = isCurrentScreenHome,
                                enter = scaleIn(),
                                exit = scaleOut(),
                            ) {
                                FloatingActionButton(
                                    onClick = {
                                        showAddDialogue = true
                                    },
                                    containerColor = MaterialTheme.colorScheme.onBackground,
                                ) {
                                    Icon(
                                        modifier = Modifier.size(28.dp),
                                        imageVector = Icons.Rounded.Add,
                                        contentDescription = "Add",
                                        tint = MaterialTheme.colorScheme.background,
                                    )
                                }
                            }
                        }, floatingActionButtonPosition = FabPosition.End
                    )
                }

                val context = LocalContext.current
                val rationalPermissionOpenDialog = remember { mutableStateOf(false) }

                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = { isGranted ->
                        if (!isGranted) {
                            if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                                rationalPermissionOpenDialog.value = true
                            }
                        }
                    }
                )

                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if(ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED){
                            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                            rationalPermissionOpenDialog.value = true
                        }
                    }
                }

                if (rationalPermissionOpenDialog.value) {
                    ShowRationalPermissionDialog(openDialog = rationalPermissionOpenDialog) {
                        rationalPermissionOpenDialog.value = false
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                }
            }
        }
    }

}


@Composable
fun ShowRationalPermissionDialog(openDialog: MutableState<Boolean>, onclick: () -> Unit) {
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                Text(text = "Alert")
            },
            confirmButton = {
                TextButton(
                    onClick = onclick
                ) {
                    Text("Ok")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                    }
                ) {
                    Text("Cancel")
                }
            },
            text = {
                Text("Notification permission is required to show notification")
            },
        )
    }
}

@Composable
fun ActionMenuDropDown(
    navigateToId: (String) -> Unit
) {
    val listItems = listOf(
        MenuItem(Screens.AboutDeveloper.id, "About Developer", Icons.Default.Person)
    )

    var expanded by remember {
        mutableStateOf(false)
    }
    var selectedItem by remember {
        mutableStateOf(-1)
    }

    Box(
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = {
            expanded = true
        }) {
            Icon(
                imageVector = Icons.Default.MoreVert, contentDescription = "Action Menu"
            )
        }

        // drop down menu
        DropdownMenu(expanded = expanded, onDismissRequest = {
            expanded = false
        }) {
            // adding items
            listItems.forEachIndexed { index, item ->
                DropdownMenuItem(onClick = {
                    selectedItem = index
                    expanded = false
                    navigateToId(item.id)
                }, leadingIcon = {
                    Icon(imageVector = item.icon, contentDescription = "${item.title} Icon")
                }, text = {
                    Text(text = item.title)
                })
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    YoutubeApiTestingTheme {
        Greeting("Android")
    }
}