package com.toqsoft.habittracker.presentation.view

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toqsoft.habittracker.R
import kotlinx.coroutines.delay
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen() {
    var currentTime by remember { mutableStateOf(0L) }
    var timerState by remember { mutableStateOf<TimerState>(TimerState.Idle) }
    var selectedTab by remember { mutableStateOf("Stopwatch") }
    var lastTime by remember { mutableStateOf<String?>(null) }


    LaunchedEffect(timerState) {
        if (timerState is TimerState.Running) {
            while (timerState is TimerState.Running) {
                delay(100L)
                currentTime += 100L
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Timer") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Show timer display depending on selected tab
            when (selectedTab) {
                "Stopwatch" -> TimerDisplay(currentTime = currentTime, timerState = timerState)
                "Countdown" -> CountdownTimerSetupScreen(onStart = { currentTime = it })
                "Intervals" -> {
                    // You can add Interval UI here later
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action Buttons (same for all tabs)
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

            Spacer(modifier = Modifier.height(64.dp))

            // Tabs
            TimerTabs(selectedTab = selectedTab, onTabSelected = { selectedTab = it })

            Spacer(modifier = Modifier.height(16.dp))

            // Last record card
            LastRecordCard(lastTime = lastTime)
        }

    }
}

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
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
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

@Composable
fun TimerDisplay(currentTime: Long, timerState: TimerState) {
    val seconds = (currentTime / 1000) % 60
    val minutes = (currentTime / 1000) / 60
    val timeString = String.format("%02d:%02d", minutes, seconds)

    val circleColor by if (timerState is TimerState.Running) {
        // Animate only if running
        rememberInfiniteTransition().animateColor(
            initialValue = Color(0xFFD81B60),
            targetValue = Color(0xFFF06292),
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 500, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    } else {
        // Static color if paused/idle
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
            color = Color.Black
        )
    }
}


@Composable
fun TimerTabs(selectedTab: String, onTabSelected: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFFEBEE)) // light pink background for the tab bar
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

// --- Constants ---
val HOURS_RANGE = 0..23
val MINUTES_SECONDS_RANGE = 0..59
val ITEM_HEIGHT = 55.dp
val DIVIDER_COLOR = Color(0xFFE0E0E0) // Light gray horizontal divider
val HORIZONTAL_DIVIDER_WIDTH = 30.dp

// --- Countdown Timer Setup Composable ---
@Composable
fun CountdownTimerSetupScreen(onStart: (Long) -> Unit = {}) {
    val selectedHours = remember { mutableStateOf(0) }
    val selectedMinutes = remember { mutableStateOf(0) }
    val selectedSeconds = remember { mutableStateOf(0) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))

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

            Box(
                modifier = Modifier
                    .height(ITEM_HEIGHT * 3)
                    .wrapContentHeight(Alignment.CenterVertically)
            ) {
                Text(":", fontSize = 48.sp, color = Color.Black)
            }

            TimePickerColumn(
                value = selectedMinutes.value,
                range = MINUTES_SECONDS_RANGE,
                label = "minutes",
                onValueChange = { selectedMinutes.value = it }
            )

            Box(
                modifier = Modifier
                    .height(ITEM_HEIGHT * 3)
                    .wrapContentHeight(Alignment.CenterVertically)
            ) {
                Text(":", fontSize = 48.sp, color = Color.Black)
            }

            TimePickerColumn(
                value = selectedSeconds.value,
                range = MINUTES_SECONDS_RANGE,
                label = "seconds",
                onValueChange = { selectedSeconds.value = it }
            )
        }

        val totalTimeMillis = remember(selectedHours.value, selectedMinutes.value, selectedSeconds.value) {
            (selectedHours.value * 3600L + selectedMinutes.value * 60L + selectedSeconds.value) * 1000L
        }

        LaunchedEffect(totalTimeMillis) {
            onStart(totalTimeMillis)
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
    val density = LocalDensity.current
    val loopMultiplier = 1000 // simulate infinite scroll
    val rangeList = remember(range) { List(loopMultiplier) { range.toList() }.flatten() }

    // Start in the middle of the large list
    val initialValue = if (value in range) value else range.first
    val initialIndex = rangeList.indexOf(initialValue) + (rangeList.size / 2)

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialIndex,
        initialFirstVisibleItemScrollOffset = 0
    )

    // Snap behavior for LazyColumn
    val snapBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .height(ITEM_HEIGHT * 3)
            .width(80.dp)
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
                modifier = Modifier.fillMaxHeight(),
                flingBehavior = snapBehavior
            ) {
                itemsIndexed(rangeList) { index, itemValue ->
                    // Determine which item is visually centered
                    val middleIndex = listState.layoutInfo.visibleItemsInfo.minByOrNull { itemInfo ->
                        val centerOfItem = itemInfo.offset + itemInfo.size / 2
                        val centerOfColumn = with(density) { ITEM_HEIGHT.roundToPx() * 1.5f }
                        abs(centerOfItem - centerOfColumn)
                    }?.index ?: initialIndex

                    val isCenteredVisually = index == middleIndex

                    Text(
                        text = itemValue.toString().padStart(2, '0'),
                        fontSize = if (isCenteredVisually) 48.sp else 32.sp,
                        fontWeight = if (isCenteredVisually) FontWeight.Bold else FontWeight.Normal,
                        color = if (itemValue == value) Color.Black else Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .height(ITEM_HEIGHT)
                            .fillMaxWidth()
                            .wrapContentHeight(Alignment.CenterVertically)
                            .clickable {
                                // Adjust value with modulo for the range
                                val newValue = range.first + (itemValue - range.first) % range.count()
                                onValueChange(newValue)
                            },
                        softWrap = false
                    )
                }
            }

            // Dividers
            Divider(
                color = DIVIDER_COLOR,
                thickness = 1.dp,
                modifier = Modifier
                    .width(HORIZONTAL_DIVIDER_WIDTH)
                    .align(Alignment.TopCenter)
                    .offset(y = ITEM_HEIGHT)
            )
            Divider(
                color = DIVIDER_COLOR,
                thickness = 1.dp,
                modifier = Modifier
                    .width(HORIZONTAL_DIVIDER_WIDTH)
                    .align(Alignment.TopCenter)
                    .offset(y = ITEM_HEIGHT * 2)
            )
        }

        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier.padding(top = 8.dp)
        )
    }

    // Snap to nearest item and update value
    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            delay(50)
            val layoutInfo = listState.layoutInfo
            if (layoutInfo.visibleItemsInfo.isEmpty()) return@LaunchedEffect

            val centerOffset = with(density) { ITEM_HEIGHT.roundToPx() * 1.5f }

            val closestItem = layoutInfo.visibleItemsInfo.minByOrNull { itemInfo ->
                val centerOfItem = itemInfo.offset + itemInfo.size / 2
                abs(centerOfItem - centerOffset)
            } ?: layoutInfo.visibleItemsInfo.first()

            listState.animateScrollToItem(closestItem.index, 0)

            val newValue = range.first + (rangeList[closestItem.index] - range.first) % range.count()
            if (newValue != value) {
                onValueChange(newValue)
            }
        }
    }
}


sealed class TimerState {
    object Idle : TimerState()
    object Running : TimerState()
    object Paused : TimerState()
}
