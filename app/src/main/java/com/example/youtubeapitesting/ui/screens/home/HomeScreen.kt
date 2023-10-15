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
import kotlin.math.max
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
                playlistWithRem = it,
                onPlaylistClick = onPlayListClick,
                viewModel = viewModel
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
            HorizontalDivider(
                modifier = Modifier.padding(top = 10.dp), thickness = 1.dp,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(5.dp))
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
            .clickable { onPlaylistClick(playlist.id) },
        verticalAlignment = Alignment.CenterVertically
    ) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            )
            {
                val progress =
                    if (playlist.itemCount > 0) (playlist.itemComplete / playlist.itemCount.toFloat()) else 0f

                LinearProgressIndicator(
                    modifier = Modifier.weight(1f),
                    progress = progress,
                    trackColor = Color.DarkGray,
                    strokeCap = StrokeCap.Round
                )
                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "${playlist.itemComplete}/${playlist.itemCount} videos",
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )

                /*Box(
                    modifier = Modifier.size(36.dp), contentAlignment = Alignment.Center
                ) {
                   
                    Text(
                        modifier = Modifier.padding(4.dp),
                        text = "${(progress * 100).roundToInt()}%",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }*/

                /*Text(
                    text = "${playlist.itemCount} videos",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onTertiary
                )*/
            }
        }
    }
}


@Composable
fun PlayListOption(
    playlistWithReminder: PlaylistWithReminder, viewModel: HomeViewModel
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        val context = LocalContext.current
        val selectedDaysList by rememberUpdatedState(
            newValue = generateWeekdays(
                playlistWithReminder.rem?.daysMask ?: 0
            )
        )

        val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

        var isReminderOn by remember(playlistWithReminder.rem?.isEnabled) {
            mutableStateOf(playlistWithReminder.rem?.isEnabled ?: false)
        }

        if (playlistWithReminder.rem == null) {
            ReminderAdded(
                modifier = Modifier.weight(1f),
                playlistWithReminder = playlistWithReminder
            )
        } else {
            ReminderColumn(
                modifier = Modifier.weight(1f),
                reminder = playlistWithReminder.rem,
                selectedDaysList = selectedDaysList
            )
            Spacer(modifier = Modifier.width(5.dp))

            Switch(
                modifier = Modifier
                    .scale(.7f)
                    .size(36.dp),
                checked = isReminderOn,
                onCheckedChange = { isChecked ->
                    isReminderOn = !isReminderOn
                    playlistWithReminder.rem.also {
                        viewModel.updateReminderStatus(it.copy(isEnabled = isChecked))
                        if (isChecked) {
                            scheduleAlarm(
                                context = context,
                                playlist = playlistWithReminder.list,
                                daysMask = it.daysMask,
                                oldDays = listOf(),
                                time = timeFormatter.format(it.time),
                                endDate = it.endDate,
                                startDate = it.startDate
                            )
                        } else {
                            val alarmIntent = createAlarmIntent(
                                context = context,
                                playlist = playlistWithReminder.list,
                                daysMask = it.daysMask,
                                endDate = it.endDate
                            )
                            cancelExistingAlarms(
                                context = context,
                                oldDays = generateWeekdays(it.daysMask),
                                alarmIntent = alarmIntent
                            )
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.width(5.dp))
        }

        PlaylistOptionsDropDown(
            modifier = Modifier
                .size(36.dp),
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
                    daysMask = daysMask,
                    oldDays = selectedDaysList,
                    time = time,
                    startDate = startDate,
                    endDate = endDate
                )
            },
            onPlaylistDelete = {
                viewModel.moveToTrash(
                    playlistWithReminder.list.copy(isTrash = true)
                )
            },
            onReminderDelete = {
                playlistWithReminder.rem?.let { viewModel.deleteReminder(it) }
            }
        )

    }
}

@Composable
fun ReminderColumn(
    modifier: Modifier,
    reminder: Reminder,
    selectedDaysList: List<Int>
) {
    val timeFormatter = remember {
        SimpleDateFormat("hh:mm a", Locale.getDefault())
    }

    val dateFormatter = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    }

    val startDate by rememberUpdatedState(
        dateFormatter.format(
            reminder.startDate
        )
    )
    val endDate by rememberUpdatedState(
        dateFormatter.format(
            reminder.endDate
        )
    )
    val time by rememberUpdatedState(
        timeFormatter.format(
            reminder.time
        )
    )
    val mapOfDays = mapOf(
        0 to "Sun", 1 to "Mon", 2 to "Tue", 3 to "Wed", 4 to "Thu", 5 to "Fri", 6 to "Sat"
    )
    val repetition by rememberUpdatedState {
        if (selectedDaysList.size == 7) {
            "Everyday"
        } else {
            selectedDaysList.map { mapOfDays[it] }.joinToString(
                ","
            )
        }
    }
    Column(
        modifier = modifier,
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

@Composable
fun ReminderAdded(
    modifier: Modifier,
    playlistWithReminder: PlaylistWithReminder
) {
    val dateFormatter = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    }

    val addedDate by rememberUpdatedState(dateFormatter.format(playlistWithReminder.list.addedTime))
    Column(
        modifier = modifier,
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
}

@Composable
fun ReminderDialog(
    playlistWithReminder: PlaylistWithReminder,
    onRemindSet: (Long, Long, String, Long, Int) -> Unit,
    onReminderDelete: () -> Unit,
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
            onReminderDelete = onReminderDelete,
            onDismiss = onDismiss
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderDialogContent(
    playlistWithReminder: PlaylistWithReminder,
    onRemindSet: (Long, Long, String, Long, Int) -> Unit,
    onReminderDelete: () -> Unit,
    onDismiss: (Boolean) -> Unit
) {
    var showPicker by remember { mutableIntStateOf(-1) }

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
        mutableIntStateOf(playlistWithReminder.rem?.let { if (selectedItems.size == 7) 0 else 1 }
            ?: 0)
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
        val timeSplit by rememberUpdatedState(convertTo24HourFormat(time).split(":"))

        val hour by rememberUpdatedState(timeSplit[0].toInt())
        val minute by rememberUpdatedState(timeSplit[1].toInt())
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
                onReminderDelete = onReminderDelete,
                onSave = {
                    saveReminder(
                        date = date,
                        time = time,
                        selectedItem = selectedItem,
                        selectedItems = selectedItems,
                        onRemindSet = onRemindSet,
                        onDateError = {
                            dateError = it
                        },
                        onTimeError = {
                            timeError = it
                        }
                    )
                },
                reminder = playlistWithReminder.rem
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

            Spacer(modifier = Modifier.height(20.dp))
            Button(
                modifier = Modifier
                    .wrapContentSize()
                    .align(Alignment.CenterHorizontally),
                onClick = {
                    onReminderDelete()
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE57373))
            ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "Delete Reminder")
            }
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
    }, confirmButton = {}, dismissButton = {}) {
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
    onDismiss: (Boolean) -> Unit,
    onReminderDelete: () -> Unit,
    onSave: () -> Unit,
    reminder: Reminder?,
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

        Log.d("TAG", "DialogTitle: $reminder")

        if (reminder != null) {
            IconButton(
                onClick = {
                    onReminderDelete()
                    onDismiss(false)
                },
                colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFFE57373))
            ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
            }
        }
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
    modifier: Modifier,
    playlistWithReminder: PlaylistWithReminder,
    onRemindSet: (Long, Long, String, Long, Int) -> Unit,
    onPlaylistDelete: () -> Unit,
    onReminderDelete: () -> Unit
) {
    val listItems = listOf(
        Pair(
            if (playlistWithReminder.rem == null) "Add Reminder" else "Edit Reminder",
            Icons.Default.Notifications
        ), Pair("Delete", Icons.Default.Delete)
    )

    var expanded by remember {
        mutableStateOf(false)
    }
    var selectedItem by remember {
        mutableIntStateOf(-1)
    }

    when (selectedItem) {
        0 -> ReminderDialog(
            playlistWithReminder = playlistWithReminder,
            onRemindSet = onRemindSet,
            onReminderDelete = onReminderDelete,
        ) {
            selectedItem = -1
        }

        1 -> {
            MoveToTrashDialog(onDismiss = { selectedItem = -1 }) {
                onPlaylistDelete()
            }
        }
    }
    Box(
        modifier = modifier,
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

fun saveReminder(
    date: String,
    time: String,
    selectedItem: Int,
    selectedItems: SnapshotStateList<Int>,
    onRemindSet: (Long, Long, String, Long, Int) -> Unit,
    onTimeError: (String) -> Unit,
    onDateError: (String) -> Unit
) {
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
            } else onTimeError("Wrong format! Use format hh:mm a")
        } else onDateError("Wrong format! Use dd/MM/yyyy - dd/MM/yyyy")
    } else {
        onDateError("Wrong format! Use dd/MM/yyyy - dd/MM/yyyy")
    }
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
    daysMask: Int,
    oldDays: List<Int>,
    time: String,
    startDate: Long,
    endDate: Long
) {
    val alarmIntent = createAlarmIntent(context, playlist, daysMask, endDate)
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

    val daysList = generateWeekdays(daysMask)
    Log.d("TAG", "scheduleAlarm: $daysMask....$daysList")
    cancelExistingAlarms(
        context = context,
        oldDays = oldDays,
        alarmIntent = alarmIntent,
    )
    //Using 7 to indicate everyday since 0..6 is used for separate days.
    if (daysList.size == 7) {
        val alarmPI =
            PendingIntent.getBroadcast(context, 7, alarmIntent, PendingIntent.FLAG_IMMUTABLE)
        val calendar = setTimeInCalendar(7, startDate, time)
        val alarmSchedule = calendar.timeInMillis
        if (alarmSchedule > (endDate + AlarmManager.INTERVAL_DAY)) {
            return
        }
        alarmManager?.setRepeating(
            AlarmManager.RTC_WAKEUP,
            alarmSchedule,
            AlarmManager.INTERVAL_DAY,
            alarmPI
        )
        return
    }

    daysList.forEach { day ->
        Log.d("TAG", "scheduleAlarm: $startDate, $time")
        alarmIntent.putExtra("day", day)
        val alarmPI =
            PendingIntent.getBroadcast(context, day, alarmIntent, PendingIntent.FLAG_IMMUTABLE)

        val calendar = setTimeInCalendar(day, startDate, time)

        val alarmSchedule = calendar.timeInMillis
        //endDate == 00:00 so but it has to be 11:59. That's why endDate + INTERVAL_DAY
        if (alarmSchedule > (endDate + AlarmManager.INTERVAL_DAY)) {
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

fun cancelExistingAlarms(
    context: Context,
    oldDays: List<Int>,
    alarmIntent: Intent,
) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
    if (oldDays.size == 7) {
        val alarmPI =
            PendingIntent.getBroadcast(context, 7, alarmIntent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager?.cancel(alarmPI)
    } else {
        oldDays.forEach {
            val alarmPI =
                PendingIntent.getBroadcast(context, it, alarmIntent, PendingIntent.FLAG_IMMUTABLE)
            alarmManager?.cancel(alarmPI)
        }
    }
}

fun createAlarmIntent(context: Context, playlist: Playlist, daysMask: Int, endDate: Long): Intent {
    val alarmIntent = Intent(context, AlarmReceiver::class.java)

    alarmIntent.putExtra("playlistId", playlist.id)
    alarmIntent.putExtra("title", playlist.title)
    alarmIntent.putExtra("channelTitle", playlist.channelTitle)
    alarmIntent.putExtra("daysMask", daysMask)
    alarmIntent.putExtra("endDate", endDate)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        alarmIntent.identifier = playlist.id
    } else alarmIntent.addCategory(playlist.id)
    return alarmIntent
}

// Function to set the time in a Calendar instance
fun setTimeInCalendar(day: Int, startDate: Long, time: String): Calendar {
    val calendar = Calendar.getInstance(Locale.getDefault())

    val timeSplit = convertTo24HourFormat(time).split(":")
    calendar.timeInMillis = max(startDate, System.currentTimeMillis())
    //Setting Sunday as the first day of the week so that it stays consistent for any locale.
    //So now for sunday always DAY_OF_WEEK = 1
    calendar.firstDayOfWeek = Calendar.SUNDAY
    //day+1 because when select days from the list the index start from 0. i.e. 0 -> Sunday
    //but the days count in calendar start from 1.
    if (day != 7) {
        calendar[Calendar.DAY_OF_WEEK] = day + 1
    }
    Log.d("TAG", "setTimeInCalendar: ${timeSplit[0]}, ${timeSplit[1]}")
    calendar[Calendar.HOUR_OF_DAY] = timeSplit[0].toInt()
    calendar[Calendar.MINUTE] = timeSplit[1].toInt()
    calendar[Calendar.SECOND] = 0
    // Check we aren't setting it in the past which would trigger it to fire instantly
    if (calendar.timeInMillis < System.currentTimeMillis()) {
        calendar.add(Calendar.DAY_OF_YEAR, if (day == 7) 1 else 7)
    }
    return calendar
}

fun convertTo24HourFormat(time12Hour: String): String {
    val inputFormat = SimpleDateFormat("hh:mm a", Locale.US)
    val outputFormat = SimpleDateFormat("HH:mm", Locale.US)
    try {
        val date = inputFormat.parse(time12Hour)
        return outputFormat.format(date)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return "00:00"
}


@Preview
@Composable
fun PlayListOptionPrev() {
}