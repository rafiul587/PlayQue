package com.example.youtubeapitesting.ui.screens.home

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.youtubeapitesting.R
import com.example.youtubeapitesting.models.Playlist
import com.example.youtubeapitesting.models.PlaylistWithReminder
import com.example.youtubeapitesting.models.Reminder
import com.example.youtubeapitesting.navigation.Screens
import com.example.youtubeapitesting.utils.AlarmReceiver
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow


@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel) {
    val playLists by viewModel.playlistInfo.collectAsState(emptyList())

    PlayLists(
        modifier = Modifier.fillMaxSize(),
        playLists = playLists,
        onPlayListClick = { navController.navigate("${Screens.VideoListScreen.id}/$it") },
        viewModel = viewModel
    )

}

@Composable
fun UrlInputLayout(
    modifier: Modifier = Modifier,
    url: String,
    onUrlChange: (String) -> Unit,
    onAddClick: (String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LaunchedEffect(key1 = Unit) {
            focusRequester.requestFocus()
        }
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .focusRequester(focusRequester),
            value = url,
            onValueChange = onUrlChange,
            placeholder = { Text(text = "Paste your playlist url here..") })
        Button(
            modifier = Modifier
                .wrapContentSize()
                .padding(horizontal = 10.dp), onClick = {
                val playListId = "(?:youtube\\.com.*&?list=)([a-zA-Z\\d_-]*)".toRegex()
                    .find(url)?.groupValues?.get(1)
                if (playListId != null) {
                    onAddClick(playListId)
                } else {
                    Log.d("TAG", "UrlInputLayout: Link is not correct")
                }
            }, shape = RoundedCornerShape(10)
        ) {
            Text(text = "Add")
        }
    }
}

@Composable
fun PlayLists(
    modifier: Modifier = Modifier,
    playLists: List<PlaylistWithReminder>,
    onPlayListClick: (String) -> Unit,
    viewModel: HomeViewModel
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 56.dp)
    ) {
        items(playLists) {
            PlayListWithReminderRow(
                playlistWithRem = it, onPlaylistClick = onPlayListClick, viewModel = viewModel
            )
        }
    }
}

@Composable
fun PlayListWithReminderRow(
    playlistWithRem: PlaylistWithReminder,
    onPlaylistClick: (String) -> Unit,
    viewModel: HomeViewModel
) {
    ElevatedCard {
        Column(modifier = Modifier.padding(16.dp)) {
            PlayListRow(playlist = playlistWithRem.list, onPlaylistClick = onPlaylistClick)
            Divider(
                modifier = Modifier.padding(top = 10.dp), color = Color.DarkGray, thickness = 1.dp
            )
            PlayListOption(playlistWithReminder = playlistWithRem, viewModel = viewModel)
        }
    }
}

@Composable
fun PlayListRow(playlist: Playlist, onPlaylistClick: (String) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onPlaylistClick(playlist.id) }) {
        if (playlist.thumbnail.isNotEmpty()) {
            AsyncImage(
                model = playlist.thumbnail,
                contentDescription = "",
                modifier = Modifier
                    .width(120.dp)
                    .height(80.dp)
                    .clip(RoundedCornerShape(5.dp)),
                contentScale = ContentScale.FillBounds
            )
        }
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = playlist.title, fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Log.d("TAG", "PlayListRow: ${MaterialTheme.colorScheme.onTertiary.toArgb()}")
            Text(
                text = playlist.channelTitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onTertiary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${playlist.itemCount} videos",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onTertiary
            )
        }
    }
}


@Composable
fun PlayListOption(
    playlistWithReminder: PlaylistWithReminder, viewModel: HomeViewModel
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val context = LocalContext.current
        val list by rememberUpdatedState(
            newValue = generateWeekdays(
                playlistWithReminder.rem?.daysMask ?: 0
            )
        )
        val dateFormatter = remember {
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        }

        Box(
            modifier = Modifier.size(42.dp), contentAlignment = Alignment.Center
        ) {
            val progress =
                if (playlistWithReminder.list.itemCount > 0) (playlistWithReminder.list.itemComplete / playlistWithReminder.list.itemCount.toFloat()) else 0f
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(),
                progress = progress,
                strokeWidth = 3.dp,
                trackColor = Color.DarkGray,
                strokeCap = StrokeCap.Round
            )
            Text(
                modifier = Modifier.padding(7.dp),
                text = "${playlistWithReminder.list.itemComplete}/${playlistWithReminder.list.itemCount}",
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }

        if (playlistWithReminder.rem == null || !playlistWithReminder.rem.isEnabled) {
            val addedDate by rememberUpdatedState(dateFormatter.format(playlistWithReminder.list.addedTime))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row {
                    Text(text = "Added: $addedDate")
                }
                CompositionLocalProvider(
                    LocalTextStyle provides TextStyle.Default.copy(
                        color = MaterialTheme.colorScheme.onTertiary, fontSize = 13.sp
                    )
                ) {
                    Text(text = "No Reminder Set")
                }
            }
        } else {
            val timeFormatter = remember {
                SimpleDateFormat("hh:mm a", Locale.getDefault())
            }
            val startDate by rememberUpdatedState(
                dateFormatter.format(
                    playlistWithReminder.rem.startDate
                )
            )
            val endDate by rememberUpdatedState(
                dateFormatter.format(
                    playlistWithReminder.rem.endDate
                )
            )
            val time by rememberUpdatedState(
                timeFormatter.format(
                    playlistWithReminder.rem.time
                )
            )
            val mapOfDays = mapOf(
                0 to "Sun", 1 to "Mon", 2 to "Tue", 3 to "Wed", 4 to "Thu", 5 to "Fri", 6 to "Sat"
            )
            val repetition by rememberUpdatedState {
                if (list.size == 7) {
                    "Everyday"
                } else {
                    list.map { mapOfDays[it] }.joinToString(
                        ","
                    )
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row {

                    Text(text = startDate)
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp), text = "-"
                    )
                    Text(text = endDate)

                }
                Row {
                    CompositionLocalProvider(
                        LocalTextStyle provides TextStyle.Default.copy(
                            color = MaterialTheme.colorScheme.onTertiary, fontSize = 13.sp
                        )
                    ) {
                        Text(text = repetition())
                        Text(text = " - $time")
                    }
                }
            }
        }

        PlaylistOptionsDropDown(
            playlistWithReminder = playlistWithReminder,
            onRemindSet = { startDate, endDate, time, timeInMilisec, daysMask ->
                viewModel.saveReminder(
                    Reminder(
                        playlistId = playlistWithReminder.list.id,
                        startDate = startDate,
                        endDate = endDate,
                        time = timeInMilisec,
                        isEnabled = true,
                        daysMask = daysMask
                    )
                )
                scheduleAlarm(
                    context = context,
                    playlist = playlistWithReminder.list,
                    days = list,
                    time = time,
                    startDate = startDate,
                    endDate = endDate
                )
            },
            onDelete = {
                viewModel.moveToTrash(
                    playlistWithReminder.list.copy(isTrash = true)
                )
            })
    }
}

@Composable
fun ReminderDialog(
    playlistWithReminder: PlaylistWithReminder,
    onRemindSet: (Long, Long, String, Long, Int) -> Unit,
    onDismiss: (Boolean) -> Unit
) {

    Dialog(
        onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onDismissRequest.
            onDismiss(false)
        }, properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        ReminderDialogContent(
            playlistWithReminder = playlistWithReminder,
            onRemindSet = onRemindSet,
            onDismiss = onDismiss
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderDialogContent(
    playlistWithReminder: PlaylistWithReminder,
    onRemindSet: (Long, Long, String, Long, Int) -> Unit,
    onDismiss: (Boolean) -> Unit
) {
    var showPicker by remember { mutableStateOf(-1) }

    val cal = remember {
        Calendar.getInstance()
    }

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    val selectedItems = remember {
        if (playlistWithReminder.rem == null) mutableStateListOf() else {
            generateWeekdays(
                playlistWithReminder.rem.daysMask
            ).toMutableStateList()
        }
    }
    var selectedItem by remember {
        mutableStateOf(playlistWithReminder.rem?.let { if (selectedItems.size == 7) 0 else 1 } ?: 0)
    }
    var date by remember {
        mutableStateOf(
            if (playlistWithReminder.rem == null) "" else {
                "${dateFormatter.format(playlistWithReminder.rem.startDate)} - ${
                    dateFormatter.format(
                        playlistWithReminder.rem.endDate
                    )
                }"
            }
        )
    }
    var time by remember {
        mutableStateOf(
            if (playlistWithReminder.rem == null) "" else timeFormatter.format(
                playlistWithReminder.rem.time
            )
        )
    }


    val timePickerState = if (playlistWithReminder.rem == null) rememberTimePickerState() else {
        val timeSplit by rememberUpdatedState(time.split(" "))

        val hour by rememberUpdatedState(
            if (timeSplit[1] == "PM") {
                timeSplit[0].split(":")[0].toInt() + 12
            } else timeSplit[0].split(":")[0].toInt()
        )
        val minute by rememberUpdatedState(
            time.split(" ")[0].split(":")[1].toInt()
        )
        rememberTimePickerState(
            initialHour = hour, initialMinute = minute
        )
    }

    val datePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = playlistWithReminder.rem?.startDate
            ?: playlistWithReminder.list.addedTime,
        initialSelectedEndDateMillis = playlistWithReminder.rem?.endDate
    )

    val onDateChange: (String) -> Unit = {
        date = it
    }

    val onTimeChange: (String) -> Unit = {
        time = it
    }

    when (showPicker) {
        0 -> DatePickerDialog(datePickerState = datePickerState, onShowDatePickerChange = {
            showPicker = it
        }, onConfirm = { start, end ->
            date = "${dateFormatter.format(start)} - ${dateFormatter.format(end)}"
            showPicker = -1
        })

        1 -> {
            TimePickerDialog(timePickerState, onCancel = { showPicker = -1 }) {
                cal.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                cal.set(Calendar.MINUTE, timePickerState.minute)
                cal.isLenient = false
                time = timeFormatter.format(cal.time)
                showPicker = -1
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        shape = RectangleShape,
    ) {
        var dateError by remember {
            mutableStateOf("")
        }
        var timeError by remember {
            mutableStateOf("")
        }
        Column(
            Modifier
                .padding(8.dp)
                .fillMaxSize()
        ) {
            DialogTitle(
                onDismiss = onDismiss,
                onSave = {
                    val dateSplit = date.split("-")
                    if (dateSplit.size > 1) {

                        val start = dateToMilliseconds(dateSplit[0].trim())
                        val end = dateToMilliseconds(dateSplit[1].trim())
                        val timeMilliSec = timeToMilliseconds(time)
                        if (start != null && end != null) {
                            val daysMask = if (selectedItem == 0) {
                                127
                            } else {
                                selectedItems.sumOf { 2.0.pow(it.toDouble()) }.toInt()
                            }
                            Log.d("TAG", "ReminderDialogContent: $start, $end, $daysMask")
                            if (timeMilliSec != null) {
                                onRemindSet(start, end, time, timeMilliSec, daysMask)
                            } else timeError = "Wrong format! Use format hh:mm a"
                        } else dateError = "Wrong format! Use dd/MM/yyyy - dd/MM/yyyy"
                    } else {
                        dateError = "Wrong format! Use dd/MM/yyyy - dd/MM/yyyy"
                    }

                }
            )
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showPicker = 0 },
                value = date,
                onValueChange = {
                    dateError = ""
                    onDateChange(it)
                },
                label = {
                    Text(text = "Select Date")
                },
                placeholder = {
                    Text(text = "dd/MM/yyyy - dd/MM/yyyy")
                },
                trailingIcon = {
                    IconButton(onClick = { showPicker = 0 }) {
                        Icon(imageVector = Icons.Default.DateRange, contentDescription = "Date")
                    }
                },
                isError = dateError.isNotEmpty(),
                readOnly = true
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showPicker = 1 },
                value = time,
                onValueChange = onTimeChange,
                label = {
                    Text(text = "Select Time")
                },
                placeholder = {
                    Text(text = "00:00 AM/PM")
                },
                trailingIcon = {
                    IconButton(onClick = { showPicker = 1 }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_time),
                            contentDescription = "Time"
                        )
                    }
                },
                readOnly = true
            )

            NotificationRepeatContainer(
                selectedItem = selectedItem, selectedItems = selectedItems
            ) { selectedItem = it }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NotificationRepeatContainer(
    selectedItem: Int, selectedItems: SnapshotStateList<Int>, onSelectItem: (Int) -> Unit
) {
    val list = listOf("Everyday", "Custom")
    val listOfDays = listOf("S", "M", "T", "W", "T", "F", "S")

    TabRow(modifier = Modifier
        .padding(vertical = 15.dp)
        .clip(RoundedCornerShape(50)),
        selectedTabIndex = selectedItem,
        indicator = { },
        divider = { }) {
        list.forEachIndexed { index, item ->
            Tab(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .fillMaxWidth()
                    .background(
                        color = if (index == selectedItem) MaterialTheme.colorScheme.primary else Color.White,
                        shape = RoundedCornerShape(50)
                    )
                    .clip(RoundedCornerShape(50)),
                selected = index == selectedItem,
                onClick = { onSelectItem(index) },
                selectedContentColor = MaterialTheme.colorScheme.onPrimary,
                unselectedContentColor = Color.Gray
            ) {
                Text(
                    modifier = Modifier.padding(10.dp), text = item, textAlign = TextAlign.Center
                )
            }
        }
    }
    if (selectedItem == 1) {
        FlowRow(modifier = Modifier.fillMaxWidth()) {
            listOfDays.forEachIndexed { index, item ->
                FilterChip(selected = selectedItems.contains(index), onClick = {
                    when (selectedItems.contains(index)) {
                        true -> selectedItems.remove(index)
                        false -> selectedItems.add(index)
                    }
                }, label = {
                    Text(text = item)
                }, colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    datePickerState: DateRangePickerState,
    onShowDatePickerChange: (Int) -> Unit,
    onConfirm: (Long, Long) -> Unit,
) {
    val confirmEnabled = remember {
        derivedStateOf {
            datePickerState.selectedStartDateMillis != null && datePickerState.selectedEndDateMillis != null
        }
    }
    DatePickerDialog(onDismissRequest = {
        onShowDatePickerChange(-1)
    }, confirmButton = {
        /* TextButton(
             onClick = {
                 onConfirm(
                     datePickerState.selectedStartDateMillis ?: 0L,
                     datePickerState.selectedEndDateMillis ?: 0L
                 )
             },
             enabled = confirmEnabled.value
         ) {
             Text(
                 fontSize = 18.sp,
                 text = "Set"
             )
         }*/
    }, dismissButton = {
        /*TextButton(
            onClick = {
                onShowDatePickerChange(-1)
            }
        ) {
            Text(
                fontSize = 18.sp,
                text = "Cancel"
            )
        }*/
    }) {
        DateRangePicker(state = datePickerState, title = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    onShowDatePickerChange(-1)
                }) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Cancel")
                }
                Button(
                    onClick = {
                        onConfirm(
                            datePickerState.selectedStartDateMillis ?: 0L,
                            datePickerState.selectedEndDateMillis ?: 0L
                        )
                    },
                    contentPadding = PaddingValues(horizontal = 30.dp),
                    enabled = confirmEnabled.value
                ) {
                    Text(
                        text = "SET"
                    )
                }
            }
        })
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    state: TimePickerState, onCancel: () -> Unit = {}, onConfirm: () -> Unit = {}
) {
    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(328.dp)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                ),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = "Select Time",
                    style = MaterialTheme.typography.labelMedium
                )
                TimePicker(state)
                Row(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onCancel) { Text("Cancel") }
                    TextButton(onClick = onConfirm) { Text("OK") }
                }
            }
        }
    }
}


@Composable
fun DialogTitle(
    onDismiss: (Boolean) -> Unit, onSave: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { onDismiss(false) }) {
            Icon(imageVector = Icons.Default.Clear, contentDescription = "Cancel")
        }
        Text(
            text = "Add Reminder", fontWeight = FontWeight.Black, fontSize = 18.sp
        )
        Button(
            onClick = {
                onSave()
                onDismiss(false)
            }, shape = CircleShape
        ) {
            Text(
                text = "Save", fontSize = 16.sp
            )
        }
    }
}

@Composable
fun PlaylistOptionsDropDown(
    playlistWithReminder: PlaylistWithReminder,
    onRemindSet: (Long, Long, String, Long, Int) -> Unit,
    onDelete: () -> Unit
) {
    val listItems = listOf(
        Pair("Add Reminder", Icons.Default.Notifications), Pair("Delete", Icons.Default.Delete)
    )

    var expanded by remember {
        mutableStateOf(false)
    }
    var selectedItem by remember {
        mutableStateOf(-1)
    }

    val (isReminderOn, onReminderChange) = remember {
        mutableStateOf(false)
    }
    when (selectedItem) {
        0 -> ReminderDialog(
            playlistWithReminder = playlistWithReminder, onRemindSet = onRemindSet
        ) {
            selectedItem = -1
        }

        1 -> {
            MoveToTrashDialog(onDismiss = { selectedItem = -1 }) {
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
                }, trailingIcon = {
                    if (index == 0) {
                        Spacer(modifier = Modifier.width(10.dp))
                        Switch(
                            modifier = Modifier.scale(.7f),
                            checked = isReminderOn,
                            onCheckedChange = onReminderChange
                        )
                    }
                }, text = {
                    Text(text = value.first)
                })
            }
        }
    }
}

@Composable
fun MoveToTrashDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(onDismissRequest = { onDismiss() }, confirmButton = {
        TextButton(onClick = {
            onConfirm()
            onDismiss()
        }) {
            Text(text = "Move to Trash")
        }
    }, dismissButton = {
        TextButton(onClick = { onDismiss() }) {
            Text(text = "Cancel")
        }
    }, title = {
        Text(text = "Move to Trash?")
    }, text = {
        Text(text = "This playlist will be moved to trash. You will not lose any data and will be able to restore it anytime from Trash.")
    })
}

fun dateToMilliseconds(date: String): Long? {
    Log.d("TAG", "dateToMilliseconds: $date")
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return try {
        val mDate = sdf.parse(date)
        val timeInMilliseconds = mDate?.time
        timeInMilliseconds
    } catch (e: ParseException) {
        null
    }
}

fun timeToMilliseconds(timeString: String): Long? {
    val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return try {
        val time = formatter.parse(timeString)
        Log.d("TAG", "timeToMilliseconds: $time")
        val timeInMilliseconds = time?.time
        timeInMilliseconds
    } catch (e: ParseException) {
        null
    }
}

fun generateWeekdays(mask: Int): List<Int> {
    val list = mutableListOf<Int>()
    val nBits = 7
    val x = 1
    for (i in 0 until nBits) {
        if ((mask and (x shl i)) > 0) {
            list.add(i)
        }
    }
    return list
}

private fun scheduleAlarm(
    context: Context,
    playlist: Playlist,
    days: List<Int>,
    time: String,
    startDate: Long,
    endDate: Long
) {
    val alarmIntent = Intent(context, AlarmReceiver::class.java)

    alarmIntent.putExtra("playlistId", playlist.id)
    alarmIntent.putExtra("title", playlist.title)
    alarmIntent.putExtra("channelTitle", playlist.channelTitle)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        alarmIntent.identifier = playlist.id
    } else alarmIntent.addCategory(playlist.id)
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
    Log.d("TAG", "scheduleAlarm: ${days},,,$time")
    val timeSplit = time.split(":", " ")

    if (days.size == 7) {
        val alarmPI =
            PendingIntent.getBroadcast(context, 7, alarmIntent, PendingIntent.FLAG_IMMUTABLE)
        val calendar = setTimeInCalendar(0, startDate, timeSplit)
        val alarmSchedule = calendar.timeInMillis
        if (alarmSchedule > endDate) {
            return
        }
        alarmManager?.setRepeating(
            AlarmManager.RTC_WAKEUP,
            alarmSchedule,
            AlarmManager.INTERVAL_DAY,
            alarmPI
        )
    }

    days.forEach { day ->
        val alarmPI =
            PendingIntent.getBroadcast(context, day, alarmIntent, PendingIntent.FLAG_IMMUTABLE)

        if (timeSplit.size > 2) {
            val calendar = setTimeInCalendar(day, startDate, timeSplit)

            val alarmSchedule = calendar.timeInMillis
            if (alarmSchedule > endDate) {
                return@forEach
            }
            alarmManager?.setRepeating(
                AlarmManager.RTC_WAKEUP,
                alarmSchedule,
                AlarmManager.INTERVAL_DAY * 7,
                alarmPI
            )
        }
    }
}

// Function to set the time in a Calendar instance
fun setTimeInCalendar(day: Int, startDate: Long, timeSplit: List<String>): Calendar {
    val calendar = Calendar.getInstance(Locale.getDefault())
    calendar.timeInMillis = startDate
    //Setting Sunday as the first day of the week so that it stays consistent for any locale.
    //So now for sunday always DAY_OF_WEEK = 1
    calendar.firstDayOfWeek = Calendar.SUNDAY
    //day+1 because when select days from the list the index start from 0. i.e. 0 -> Sunday
    //but the days count in calendar start from 1.
    calendar[Calendar.DAY_OF_WEEK] = day + 1
    calendar[Calendar.HOUR_OF_DAY] = if (timeSplit[2] == "PM") {
        timeSplit[0].toInt() + 12
    } else timeSplit[0].toInt()
    calendar[Calendar.MINUTE] = timeSplit[1].toInt()
    calendar[Calendar.SECOND] = 0
    // Check we aren't setting it in the past which would trigger it to fire instantly
    if (calendar.timeInMillis < System.currentTimeMillis()) {
        calendar.add(Calendar.DAY_OF_YEAR, 7)
    }
    return calendar
}


@Preview
@Composable
fun PlayListOptionPrev() {
}