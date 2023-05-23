package com.example.youtubeapitesting.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.youtubeapitesting.navigation.MenuItem
import com.example.youtubeapitesting.R
import com.example.youtubeapitesting.models.BottomNavItem
import com.example.youtubeapitesting.navigation.NavigationController
import com.example.youtubeapitesting.navigation.Screens
import com.example.youtubeapitesting.ui.screens.home.UrlInputLayout
import com.example.youtubeapitesting.ui.screens.home.HomeViewModel
import com.example.youtubeapitesting.ui.theme.YoutubeApiTestingTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<HomeViewModel>()

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_ondemand_video_24),
                                        contentDescription = "Logo"
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        "StudyTube",
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            },
                            actions = {
                                ActionMenuDropDown(navigateToId = {
                                    navController.navigate(it)
                                }
                                )
                            }
                        )
                    },
                        content = {
                            NavigationController(
                                modifier = Modifier.padding(it),
                                navController = navController
                            ) {}
                        }, bottomBar = {
                            var selectedItem by remember { mutableStateOf(0) }
                            val items = remember {
                                listOf(
                                    BottomNavItem(
                                        "Home",
                                        Screens.Home.id,
                                        R.drawable.round_home_24
                                    ),
                                    BottomNavItem(
                                        "Search Channel",
                                        Screens.Search.id,
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
                                        selected = item.route == currentBackStack?.destination?.route,
                                        onClick = {
                                            navController.navigate(item.route)
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
            }

        }
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