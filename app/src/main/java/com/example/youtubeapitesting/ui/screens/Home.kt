package com.example.youtubeapitesting.ui.screens

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
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
import com.example.youtubeapitesting.AlarmReceiver
import com.example.youtubeapitesting.HomeViewModel
import com.example.youtubeapitesting.R
import com.example.youtubeapitesting.models.PlaylistWithReminder
import com.example.youtubeapitesting.models.Reminder
import com.example.youtubeapitesting.navigation.Screens
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow


@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel) {
    val (url, onUrlChange) = remember { mutableStateOf("") }
    val playLists = viewModel.playlistInfo.collectAsState()
    /*playLists.forEach {
        Log.d("TAG", "HomeScreen: $it")
    }*/
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        UrlInputLayout(url = url, onUrlChange = onUrlChange) {
            viewModel.getPlaylistInfo(it)
        }
        Spacer(modifier = Modifier.height(20.dp))
        PlayLists(
            playLists = playLists.value,
            onPlayListClick = { navController.navigate("${Screens.PlayListScreen.id}/$it") },
            viewModel = viewModel
        )
    }
}

@Composable
fun UrlInputLayout(url: String, onUrlChange: (String) -> Unit, onAddClick: (String) -> Unit) {
    Column(
        modifier = Modifier.wrapContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = url,
            onValueChange = onUrlChange,
            placeholder = { Text(text = "Paste your playlist url here..") }
        )
        Button(
            modifier = Modifier
                .wrapContentSize()
                .padding(horizontal = 10.dp), onClick = {
                val playListId =
                    "(?:youtube\\.com.*&?list=)([a-zA-Z\\d_-]*)".toRegex()
                        .find(url)?.groupValues?.get(1)
                if (playListId != null) {
                    onAddClick(playListId)
                } else {
                    Log.d("TAG", "UrlInputLayout: Link is not correct")
                }
            },
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(text = "Add")
        }
    }
}

@Composable
fun PlayLists(
    playLists: List<PlaylistWithReminder>,
    onPlayListClick: (String) -> Unit,
    viewModel: HomeViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        items(playLists) {
            PlayListRow(playlist = it, onPlaylistClick = onPlayListClick, viewModel = viewModel)
        }
    }
}

@Composable
fun PlayListRow(
    playlist: PlaylistWithReminder,
    onPlaylistClick: (String) -> Unit,
    viewModel: HomeViewModel
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
                        text = playlist.title,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = playlist.channelTitle,
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${playlist.itemCount} videos",
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )
                }
            }
            val list = generateWeekdays(playlist.daysMask ?: 0)
            val context = LocalContext.current
            PlayListOption(playlist) { startData, endDate, time, daysMask ->
                viewModel.saveReminder(
                    Reminder(
                        playlistId = playlist.id ?: "",
                        startDate = startData,
                        endDate = endDate,
                        time = time,
                        daysMask = daysMask
                    )
                )
                list.forEach {
                    scheduleAlarm(context, it, time)
                }
            }
        }
    }
}


@Composable
fun PlayListOption(
    playlistWithReminder: PlaylistWithReminder,
    onRemindSet: (Long, Long, Long, Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(42.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(),
                progress = (playlistWithReminder.itemComplete/playlistWithReminder.itemCount.toFloat()),
                strokeWidth = 3.dp,
                trackColor = Color.DarkGray,
                strokeCap = StrokeCap.Round
            )
            Text(
                modifier = Modifier.padding(7.dp),
                text = "${playlistWithReminder.itemComplete}/${playlistWithReminder.itemCount}",
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
        val dateFormatter = remember {
            SimpleDateFormat("dd/mm/yyyy", Locale.getDefault())
        }
        val timeFormatter = remember {
            SimpleDateFormat("hh:mm a", Locale.getDefault())
        }
        val startDate by rememberUpdatedState(
            dateFormatter.format(
                playlistWithReminder.startDate ?: 0L
            )
        )
        val endDate by rememberUpdatedState(
            dateFormatter.format(
                playlistWithReminder.endDate ?: 0L
            )
        )
        val time by rememberUpdatedState(timeFormatter.format(playlistWithReminder.time ?: 0L))
        val mapOfDays = mapOf<Int, String>(
            0 to "Sun",
            1 to "Mon",
            2 to "Tue",
            3 to "Wed",
            4 to "Thu",
            5 to "Fri",
            6 to "Sat"
        )
        val repetition by rememberUpdatedState {
            val list = generateWeekdays(playlistWithReminder.daysMask ?: 0)
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
                    modifier = Modifier.padding(horizontal = 8.dp),
                    text = "-"
                )
                Text(text = endDate)

            }
            Row {
                CompositionLocalProvider(
                    LocalTextStyle provides TextStyle.Default.copy(
                        color = Color.LightGray,
                        fontSize = 13.sp
                    )
                ) {
                    Text(text = repetition())
                    Text(text = " - $time")
                }
            }
        }
        VideoOptionsDropDown(playlistWithReminder, onRemindSet)
    }
}

@Composable
fun ReminderDialog(
    playlistWithReminder: PlaylistWithReminder,
    onRemindSet: (Long, Long, Long, Int) -> Unit,
    onDismiss: (Boolean) -> Unit
) {

    Dialog(
        onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onDismissRequest.
            onDismiss(false)
        },
        properties = DialogProperties(
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
    onRemindSet: (Long, Long, Long, Int) -> Unit,
    onDismiss: (Boolean) -> Unit
) {
    var showPicker by remember { mutableStateOf(-1) }

    val cal = remember {
        Calendar.getInstance()
    }

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val formatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val selectedItems = remember {
        generateWeekdays(playlistWithReminder.daysMask ?: 0).toMutableStateList()
    }
    var selectedItem by remember {
        mutableStateOf(if (selectedItems.size == 7) 0 else 1)
    }

    var date by remember {
        mutableStateOf(
            "${dateFormatter.format(playlistWithReminder.startDate?:0)} - ${
                dateFormatter.format(
                    playlistWithReminder.endDate?: 0
                )
            }"
        )
    }

    val onDateChange: (String) -> Unit = {
        date = it
    }
    var time by remember {
        mutableStateOf(formatter.format(playlistWithReminder.time ?: 0))
    }
    val onTimeChange: (String) -> Unit = {
        time = it
    }

    val timeSplit by rememberUpdatedState(time.split(" "))

    val hour by rememberUpdatedState(
        if (timeSplit[1] == "PM") {
            timeSplit[0].split(":")[0].toInt() + 12
        } else timeSplit[0].split(":")[0].toInt()
    )
    val minute by rememberUpdatedState(
        time.split(" ")[0].split(":")[1].toInt()
    )

    val state = rememberTimePickerState(
        initialHour = hour,
        initialMinute = minute
    )

    val datePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = playlistWithReminder.startDate
            ?: playlistWithReminder.addedTime,
        initialSelectedEndDateMillis = playlistWithReminder.endDate,
        yearRange = 2023..2040
    )

    when (showPicker) {
        0 -> DatePickerDialog(
            datePickerState = datePickerState,
            onShowDatePickerChange = {
                showPicker = it
            },
            onConfirm = { start, end ->
                date = "${dateFormatter.format(start)} - ${dateFormatter.format(end)}"
                showPicker = -1
            })
        1 -> {
            TimePickerDialog(state, onCancel = { showPicker = -1 }) {
                cal.set(Calendar.HOUR_OF_DAY, state.hour)
                cal.set(Calendar.MINUTE, state.minute)
                cal.isLenient = false
                time = formatter.format(cal.time)
                showPicker = -1
            }
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize(),
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
            DialogTitle(onDismiss = onDismiss) {
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
                            onRemindSet(start, end, timeMilliSec, daysMask)
                        } else timeError = "Wrong format! Use format hh:mm a"
                    } else dateError = "Wrong format! Use dd/mm/yyyy - dd/mm/yyyy"
                } else {
                    dateError = "Wrong format! Use dd/mm/yyyy - dd/mm/yyyy"
                }

            }
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = date,
                onValueChange = {
                    dateError = ""
                    onDateChange(it)
                },
                label = {
                    Text(text = "Select Date")
                },
                placeholder = {
                    Text(text = "dd/mm/yyyy - dd/mm/yyyy")
                },
                trailingIcon = {
                    IconButton(onClick = { showPicker = 0 }) {
                        Icon(imageVector = Icons.Default.DateRange, contentDescription = "Date")
                    }
                },
                isError = dateError.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
            )
            OutlinedTextField(
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
                modifier = Modifier.fillMaxWidth()
            )

            NotificationRepeatContainer(
                selectedItem = selectedItem,
                selectedItems = selectedItems
            ) { selectedItem = it }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NotificationRepeatContainer(
    selectedItem: Int,
    selectedItems: SnapshotStateList<Int>,
    onSelectItem: (Int) -> Unit
) {
    val list = listOf("Everyday", "Custom")
    val listOfDays = listOf("S", "M", "T", "W", "T", "F", "S")

    TabRow(
        modifier = Modifier
            .padding(vertical = 15.dp)
            .clip(RoundedCornerShape(50)),
        selectedTabIndex = selectedItem,
        indicator = { },
        divider = { }
    ) {
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
                    modifier = Modifier.padding(10.dp),
                    text = item,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
    if (selectedItem == 1) {
        FlowRow(modifier = Modifier.fillMaxWidth()) {
            listOfDays.forEachIndexed { index, item ->
                FilterChip(
                    selected = selectedItems.contains(index),
                    onClick = {
                        when (selectedItems.contains(index)) {
                            true -> selectedItems.remove(index)
                            false -> selectedItems.add(index)
                        }
                    }, label = {
                        Text(text = item)
                    },
                    colors = FilterChipDefaults.filterChipColors(
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
    DatePickerDialog(
        onDismissRequest = {
            onShowDatePickerChange(-1)
        },
        confirmButton = {
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
        },
        dismissButton = {
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
        }
    ) {
        DateRangePicker(
            state = datePickerState,
            title = {
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
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    state: TimePickerState,
    onCancel: () -> Unit = {},
    onConfirm: () -> Unit = {}
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
    onSave: () -> Unit
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
            text = "Add Reminder",
            fontWeight = FontWeight.Black,
            fontSize = 18.sp
        )
        Button(
            onClick = {
                onSave()
                onDismiss(false)
            },
            shape = CircleShape
        ) {
            Text(
                text = "Save",
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun VideoOptionsDropDown(
    playlistWithReminder: PlaylistWithReminder,
    onRemindSet: (Long, Long, Long, Int) -> Unit
) {
    val listItems = listOf(
        Pair("Add Reminder", Icons.Default.Notifications),
        Pair("Delete", Icons.Default.Delete)
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
            playlistWithReminder = playlistWithReminder,
            onRemindSet = onRemindSet
        ) {
            selectedItem = -1
        }
        1 -> {}
    }
    Box(
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = {
            expanded = true
        }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Open Options"
            )
        }

        // drop down menu
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            // adding items
            listItems.forEachIndexed { index, value ->
                DropdownMenuItem(
                    onClick = {
                        selectedItem = index
                        expanded = false
                    },
                    leadingIcon = {
                        Icon(imageVector = value.second, contentDescription = "Item Icon")
                    },
                    trailingIcon = {
                        if (index == 0) {
                            Spacer(modifier = Modifier.width(10.dp))
                            Switch(
                                modifier = Modifier.scale(.7f),
                                checked = isReminderOn,
                                onCheckedChange = onReminderChange
                            )
                        }
                    },
                    text = {
                        Text(text = value.first)
                    }
                )
            }
        }
    }
}

fun dateToMilliseconds(date: String): Long? {
    val sdf = SimpleDateFormat("dd/mm/yyyy", Locale.getDefault())
    return try {
        val mDate = sdf.parse(date)
        val timeInMilliseconds = mDate?.time
        timeInMilliseconds
    } catch (e: ParseException) {
        null
    }
}

fun timeToMilliseconds(date: String): Long? {
    val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return try {
        val time = formatter.parse(date)
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

private fun scheduleAlarm(context: Context, dayOfWeek: Int, time: Long) {
    val calendar = Calendar.getInstance()
    calendar[Calendar.DAY_OF_WEEK] = dayOfWeek
    calendar.timeInMillis = time

    // Check we aren't setting it in the past which would trigger it to fire instantly
    if (calendar.timeInMillis < System.currentTimeMillis()) {
        calendar.add(Calendar.DAY_OF_YEAR, 7)
    }

    val alarmIntent = Intent(context, AlarmReceiver::class.java)

    alarmIntent.putExtra("AlarmID", "Alarm_Id_12345")

    val alarmPI =
        PendingIntent.getBroadcast(context, 1234, alarmIntent, PendingIntent.FLAG_IMMUTABLE)

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
    alarmManager?.setRepeating(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        AlarmManager.INTERVAL_DAY * 7,
        alarmPI
    )
}


@Preview
@Composable
fun PlayListOptionPrev() {
}