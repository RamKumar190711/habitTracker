package com.toqsoft.habittracker.presentation.view

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toqsoft.habittracker.R
import kotlinx.coroutines.delay
import kotlin.math.abs

// ----------------------------------------------------------
//  MAIN TIMER SCREEN
// ----------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen() {
    var currentTime by remember { mutableStateOf(0L) }
    var timerState by remember { mutableStateOf<TimerState>(TimerState.Idle) }
    var selectedTab by remember { mutableStateOf("Stopwatch") }
    var lastTime by remember { mutableStateOf<String?>  (null) }
    val showCount = selectedTab == "Countdown"

    // Stopwatch ticking
    LaunchedEffect(timerState) {
        if (timerState is TimerState.Running) {
            while (timerState is TimerState.Running) {
                delay(100L)
                currentTime += 100L
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Timer") },
            colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = MaterialTheme.colorScheme.background)
        ) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(color = MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .width(350.dp)
                    .height(300.dp),
                contentAlignment = Alignment.Center
            ) {
                when (selectedTab) {
                    "Stopwatch" -> TimerDisplay(currentTime = currentTime, timerState = timerState)
                    "Countdown" -> CountdownMainScreen(
                        showCount = showCount
                    )
                    "Intervals" -> Text("Intervals Setup", color = MaterialTheme.colorScheme.onBackground)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            if(!showCount) {
                ActionButtons(
                    timerState = timerState,
                    onStart = { timerState = TimerState.Running },
                    onPause = { timerState = TimerState.Paused },
                    onResume = { timerState = TimerState.Running },
                    onStop = {
                        val minutes = (currentTime / 1000) / 60
                        val seconds = (currentTime / 1000) % 60
                        lastTime = String.format("%02d:%02d", minutes, seconds)
                        timerState = TimerState.Idle
                        currentTime = 0L
                    }
                )
            }

            Spacer(modifier = Modifier.height(64.dp))

            TimerTabs(selectedTab = selectedTab, onTabSelected = { selectedTab = it })

            Spacer(modifier = Modifier.height(16.dp))

            LastRecordCard(lastTime = lastTime)
        }
    }
}

// ----------------------------------------------------------
//  ACTION BUTTONS
// ----------------------------------------------------------
@Composable
fun ActionButtons(
    timerState: TimerState,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStop: () -> Unit
) {
    when (timerState) {
        is TimerState.Idle -> {
            Button(
                onClick = onStart,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD81B60),
                    contentColor = Color.White
                ),
                modifier = Modifier.width(200.dp)
            ) {
                Text("▶ START", fontWeight = FontWeight.Bold)
            }
        }

        is TimerState.Running, is TimerState.Paused -> {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Button(
                    onClick = if (timerState is TimerState.Running) onPause else onResume,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (timerState is TimerState.Running) Color(0xFFD81B60) else Color(0xFFF06292),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (timerState is TimerState.Running) "⏸ PAUSE" else "▶ RESUME",
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = onStop,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF8BBD0),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("⏹ STOP", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ----------------------------------------------------------
//  STOPWATCH DISPLAY
// ----------------------------------------------------------
@Composable
fun TimerDisplay(currentTime: Long, timerState: TimerState) {
    val seconds = (currentTime / 1000) % 60
    val minutes = (currentTime / 1000) / 60
    val timeString = String.format("%02d:%02d", minutes, seconds)

    val circleColor by if (timerState is TimerState.Running) {
        rememberInfiniteTransition().animateColor(
            initialValue = Color(0xFFD81B60),
            targetValue = Color(0xFFF06292),
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 500, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    } else {
        mutableStateOf(if (timerState is TimerState.Paused) Color(0xFFF8BBD0) else Color(0xFFD81B60))
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(200.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = circleColor,
                style = Stroke(width = 25f)
            )
        }

        Text(
            text = timeString,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

// ----------------------------------------------------------
//  TABS
// ----------------------------------------------------------
@Composable
fun TimerTabs(selectedTab: String, onTabSelected: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFFEBEE))
    ) {
        val tabs = listOf("Stopwatch", "Countdown", "Intervals")
        tabs.forEach { tab ->
            val isSelected = selectedTab == tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTabSelected(tab) }
                    .background(if (isSelected) Color(0xFFFFCDD2) else Color.Transparent)
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                TimerTabButton(text = tab, isSelected = isSelected)
            }
        }
    }
}

@Composable
fun TimerTabButton(text: String, isSelected: Boolean) {
    val contentColor = if (isSelected) Color(0xFFD81B60) else Color(0xFF6E6E6E)
    val imageResId = when (text) {
        "Stopwatch" -> R.drawable.timer
        "Countdown" -> R.drawable.countdown
        "Intervals" -> R.drawable.interval
        else -> null
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        imageResId?.let {
            Image(
                painter = painterResource(id = it),
                contentDescription = text,
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(contentColor),
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = text,
            color = contentColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 12.sp
        )
    }
}

// ----------------------------------------------------------
//  LAST RECORD CARD
// ----------------------------------------------------------
@Composable
fun LastRecordCard(lastTime: String? = null) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (lastTime != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.timer),
                        contentDescription = "Last record",
                        tint = Color(0xFFD81B60),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Last record $lastTime",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6E6E6E)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = { /* Clear action */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.close),
                            contentDescription = "Clear",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "No activity selected",
                color = Color(0xFF6E6E6E)
            )
        }
    }
}


val HOURS_RANGE = 0..23
val MINUTES_SECONDS_RANGE = 0..59
val ITEM_HEIGHT = 60.dp

@Composable
fun CountdownMainScreen(showCount: Boolean= false) {
    var showTimer by remember { mutableStateOf(false) }
    var timerValueMs by remember { mutableStateOf(0L) }
    var isRunning by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (showTimer) {
            CircularCountdownTimer(
                totalTime = timerValueMs,
                isRunning = isRunning,
                onFinished = {
                    showTimer = false
                    isRunning = false
                },
                onPause = { isRunning = !isRunning } // toggle running state
            )
        } else {
            CountdownTimerSetupScreen { millis ->
                timerValueMs = millis
                showTimer = true
                isRunning = true
            }
        }
    }
}


@Composable
fun CountdownTimerSetupScreen(onStart: (Long) -> Unit = {}) {
    val selectedHours = remember { mutableStateOf(0) }
    val selectedMinutes = remember { mutableStateOf(0) }
    val selectedSeconds = remember { mutableStateOf(0) }

    // Compute total time directly from the values
    val totalTimeMillis = remember(selectedHours.value, selectedMinutes.value, selectedSeconds.value) {
        (selectedHours.value * 3600L +
                selectedMinutes.value * 60L +
                selectedSeconds.value) * 1000L
    }

    val isStartEnabled = totalTimeMillis > 0L

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            TimePickerColumn(
                value = selectedHours.value,
                range = HOURS_RANGE,
                label = "hours",
                onValueChange = { selectedHours.value = it }
            )
            Text(":", fontSize = 48.sp, color = Color.Black, modifier = Modifier.height(ITEM_HEIGHT * 3))
            TimePickerColumn(
                value = selectedMinutes.value,
                range = MINUTES_SECONDS_RANGE,
                label = "minutes",
                onValueChange = { selectedMinutes.value = it }
            )
            Text(":", fontSize = 48.sp, color = Color.Black, modifier = Modifier.height(ITEM_HEIGHT * 3))
            TimePickerColumn(
                value = selectedSeconds.value,
                range = MINUTES_SECONDS_RANGE,
                label = "seconds",
                onValueChange = { selectedSeconds.value = it }
            )
        }

        Button(
            onClick = { onStart(totalTimeMillis) },
            enabled = isStartEnabled, // will now enable automatically after setting time
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFD81B60),
                contentColor =MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier
                .padding(top = 24.dp)
                .width(200.dp)
        ) {
            Text("▶ START", fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground)
        }
    }
}

@Composable
fun TimePickerColumn(
    value: Int,
    range: IntRange,
    label: String,
    onValueChange: (Int) -> Unit
) {
    val loopMultiplier = 1000
    val rangeList = remember { List(loopMultiplier) { range.toList() }.flatten() }
    val initialIndex = rangeList.size / 2 + value
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val snapBehavior = rememberSnapFlingBehavior(listState)
    var centeredIndex by remember { mutableStateOf(initialIndex) }

    // ✅ Call onValueChange immediately for the visual center
    LaunchedEffect(Unit) {
        onValueChange(rangeList[centeredIndex] % range.count())
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                val layoutInfo = listState.layoutInfo
                if (visibleItems.isEmpty()) return@collect

                val centerOffset = layoutInfo.viewportStartOffset +
                        (layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset) / 2

                centeredIndex = visibleItems.minByOrNull { item ->
                    val itemCenter = item.offset + item.size / 2
                    abs(itemCenter - centerOffset)
                }?.index ?: centeredIndex
            }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .height(ITEM_HEIGHT * 3)
            .width(90.dp)
            .clipToBounds()
    ) {
        Box(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(vertical = ITEM_HEIGHT),
                horizontalAlignment = Alignment.CenterHorizontally,
                flingBehavior = snapBehavior
            ) {
                itemsIndexed(rangeList) { index, itemValue ->
                    val isCentered = index == centeredIndex
                    Text(
                        text = itemValue.toString().padStart(2, '0'),
                        fontSize = if (isCentered) 50.sp else 32.sp,
                        fontWeight = if (isCentered) FontWeight.Bold else FontWeight.Normal,
                        color = if (isCentered) Color.Black else Color.Gray,
                        modifier = Modifier
                            .height(ITEM_HEIGHT)
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .clickable { onValueChange(itemValue % range.count()) }
                    )
                }
            }

            Divider(
                color = Color.Gray,
                thickness = 2.dp,
                modifier = Modifier
                    .width(90.dp)
                    .align(Alignment.TopCenter)
                    .offset(y = ITEM_HEIGHT)
            )
            Divider(
                color = Color.Gray,
                thickness = 2.dp,
                modifier = Modifier
                    .width(90.dp)
                    .align(Alignment.TopCenter)
                    .offset(y = ITEM_HEIGHT * 2)
            )
        }

        Text(
            text = label,
            fontSize = 14.sp,
            color =MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )
    }

    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            delay(60)
            listState.animateScrollToItem(centeredIndex)
            val newValue = rangeList[centeredIndex] % range.count()
            if (newValue != value) onValueChange(newValue)
        }
    }
}

@Composable
fun CircularCountdownTimer(
    totalTime: Long,
    isRunning: Boolean,
    onFinished: () -> Unit = {},
    onPause: () -> Unit
) {
    var timeLeft by remember { mutableStateOf(totalTime) }

    val secondsLeft = (timeLeft / 1000).toInt()
    val progress = timeLeft / totalTime.toFloat().coerceAtLeast(1f)

    // Timer logic reacts to `isRunning`
    LaunchedEffect(isRunning) {
        while (timeLeft > 0 && isRunning) {
            delay(1000)
            timeLeft -= 1000
        }
        if (timeLeft <= 0) onFinished()
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(280.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawArc(
                color = Color(0xFFE5D9DD),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 22f, cap = StrokeCap.Round)
            )

            drawArc(
                color = Color(0xFFE14A63),
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(width = 22f, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = String.format(
                    "%02d:%02d:%02d",
                    secondsLeft / 3600,
                    (secondsLeft % 3600) / 60,
                    secondsLeft % 60
                ),
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3B3235)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Pause / Resume button
            Button(
                onClick = { onPause() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD81B60),
                    contentColor = Color.White
                )
            ) {
                Text(if (isRunning) "⏸ PAUSE" else "▶ RESUME", fontWeight = FontWeight.Bold)
            }
        }
    }
}


sealed class TimerState {
    object Idle : TimerState()
    object Running : TimerState()
    object Paused : TimerState()
}
