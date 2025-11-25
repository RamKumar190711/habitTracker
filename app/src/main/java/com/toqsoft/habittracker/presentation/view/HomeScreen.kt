package com.toqsoft.habittracker.presentation.view

import android.graphics.Insets.add
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.toqsoft.habittracker.R
import com.toqsoft.habittracker.data.database.TaskDatabase
import com.toqsoft.habittracker.data.model.TaskEntity
import com.toqsoft.habittracker.domain.model.Category
import com.toqsoft.habittracker.domain.model.DrawerItem
import com.toqsoft.habittracker.domain.model.ReminderData
import com.toqsoft.habittracker.presentation.viewmodel.CategoryViewModel
import com.toqsoft.habittracker.presentation.viewmodel.ReminderViewModel
import com.toqsoft.habittracker.presentation.viewmodel.TaskViewModel
import com.toqsoft.habittracker.presentation.viewmodel.TaskViewModelFactory
import com.toqsoft.habittracker.ui.theme.MeronLight
import com.toqsoft.habittracker.ui.theme.MeronSoft
import com.toqsoft.habittracker.ui.theme.MeronUltraLight
import com.toqsoft.habittracker.ui.theme.MeronWarm
import kotlinx.coroutines.launch

import kotlinx.datetime.toKotlinLocalDate
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(navController: NavHostController) {
    var selectedRoute by remember { mutableStateOf(BottomNavItem.Today.route) }
    var isClick by remember { mutableStateOf(false) }
    val today = LocalDate.now().toKotlinLocalDate()
    val selectedDay = remember { mutableStateOf(today.dayOfMonth) }

    // Drawer state and coroutine scope
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val showScaffoldBars = selectedRoute != BottomNavItem.Timer.route
    var isSearchMode by remember { mutableStateOf(false) }

    val drawerItems = listOf(
        DrawerItem("News and events", R.drawable.newspaper),
        DrawerItem("Categories", R.drawable.category),
        DrawerItem("Timer", R.drawable.timer),
        DrawerItem("Customize", R.drawable.customize),
        DrawerItem("Settings", R.drawable.settings),
        DrawerItem("Account and Backups", R.drawable.account),
        DrawerItem("Get premium", R.drawable.premimum),
        DrawerItem("Rate this app", R.drawable.rate),
        DrawerItem("Contact us", R.drawable.contact)
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.fillMaxHeight(),
                drawerContainerColor  = Color.White
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "HabitNow",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Color(0xFFE91E63)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tuesday\n25 November 2025", // Make dynamic if needed
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    drawerItems.forEachIndexed { index, item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { /* Handle drawer item click */ }
                                .padding(vertical = 8.dp)
                        ) {
                            item.iconRes?.let { icon ->
                                Icon(
                                    painter = painterResource(id = icon),
                                    contentDescription = item.title,
                                    modifier = Modifier.size(24.dp),
                                    tint = Color.Gray
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                            }
                            Text(text = item.title, fontSize = 16.sp)
                        }

                        // Add mild divider after specific items
                        if (index == 1 || index == 3 || index == 6) {
                            Divider(
                                color = Color.LightGray,
                                thickness = 0.5.dp,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        },
        scrimColor = Color.Black.copy(alpha = 0.32f) // Optional semi-transparent scrim
    ) {
        Scaffold(
            topBar = {
                if (showScaffoldBars) {
                    if (isSearchMode) {
                        // Show the Search Bar Composable when in search mode
                        ActivitySearchTopBar(
                            onCloseClick = { isSearchMode = false }
                        )
                    } else {
                        // Show the default Top Bar
                        HabitTrackerTopBar(
                            currentDate = formatDate(
                                kotlinx.datetime.LocalDate(today.year, today.monthNumber, selectedDay.value)
                            ),
                            onMenuClick = {
                                scope.launch { drawerState.open() }
                            },
                            // Add a new action handler for the search icon
                            onAction1 = { isSearchMode = true }
                        )
                    }
                }
            },
            floatingActionButton = {
                if (showScaffoldBars) {
                    GradientFAB(
                        onClick = { isClick = !isClick },
                        iconRes = R.drawable.add
                    )
                }
            },
            bottomBar = {
                if (showScaffoldBars) {
                    BottomNavigationBar(
                        items = listOf(
                            BottomNavItem.Today,
                            BottomNavItem.Habits,
                            BottomNavItem.Tasks,
                            BottomNavItem.Category,
                            BottomNavItem.Timer
                        ),
                        selectedRoute = selectedRoute,
                        onItemSelected = { selectedRoute = it.route }
                    )
                }
            },
            containerColor = Color.White
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                when (selectedRoute) {
                    BottomNavItem.Today.route -> TodayScreen()
                    BottomNavItem.Timer.route -> TimerScreen()
                    // Add other screens as needed
                }

                if (isClick && showScaffoldBars) {
                    ModalBottomSheetExample(
                        onDismiss = { isClick = false },
                        onTaskSelected = { navController.navigate("task") }
                    )
                }
            }
        }
    }
}




val PlaceholderBackground = Color(0xFFF7F0F3)
val PlaceholderPink = Color(0xFFE91E63)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivitySearchTopBar(
    onCloseClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White) // White background
            .statusBarsPadding() // Push below status bar
    ) {
        // --- 1. Category Selection Row ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp), // Remove PlaceholderBackground
            verticalAlignment = Alignment.CenterVertically
        ) {
            // "All" dropdown simulation
            Row(
                modifier = Modifier
                    .weight(0.3f)
                    .clickable { /* Handle "All" dropdown click */ }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "All",
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Icon(
                    painter = painterResource(id = R.drawable.down),
                    contentDescription = "Category Dropdown",
                    modifier = Modifier.size(18.dp),
                    tint = Color.Gray
                )
            }

            Text(
                text = "Select a category",
                modifier = Modifier
                    .weight(0.7f)
                    .padding(start = 14.dp),
                fontSize = 16.sp,
                color = Color.Gray
            )
        }

        MildDivider() // Optional divider

        // --- 2. Search Input and Actions Row ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp), // Remove PlaceholderBackground
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.search),
                    contentDescription = "Search",
                    modifier = Modifier.size(20.dp),
                    tint = Color.Gray
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Activity name",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            IconButton(onClick = { /* Handle Pin Click */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.pin),
                    contentDescription = "Pin",
                    modifier = Modifier.size(20.dp),
                    tint = PlaceholderPink
                )
            }

            IconButton(onClick = { /* Handle Delete Click */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.delete),
                    contentDescription = "Delete",
                    modifier = Modifier.size(20.dp),
                    tint = PlaceholderPink
                )
            }

            IconButton(onClick = onCloseClick) {
                Icon(
                    painter = painterResource(id = R.drawable.up),
                    contentDescription = "Close Search",
                    modifier = Modifier.size(20.dp),
                    tint = PlaceholderPink
                )
            }
        }

        Divider(color = Color.LightGray, thickness = 0.5.dp)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TodayScreen() {
    val today = LocalDate.now().toKotlinLocalDate()
    val selectedDay = remember { mutableStateOf(today.dayOfMonth) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Calendar at the top with fixed height
        Calendar(
            today = today,
            onDateSelected = { newDate ->
                selectedDay.value = newDate.dayOfMonth
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp) // fixed height
                .align(Alignment.TopCenter) // top of the screen
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Image(
                painter = painterResource(id = R.drawable.cal_back),
                contentDescription = "Today",
                modifier = Modifier.size(70.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "There is nothing scheduled",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold, // bold
                color = Color.Black // black
            )

            Text(
                text = "Try adding a new activity",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Normal,
                color = Color.Gray ,
                modifier = Modifier.padding(top = 10.dp)
            )
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalBottomSheetExample(
    onDismiss: () -> Unit,
    onTaskSelected: () -> Unit       // <-- NEW callback
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        containerColor = Color.White
    ) {

        Column(
            modifier = Modifier.padding(
                top = 35.dp, bottom = 35.dp,
                start = 25.dp, end = 25.dp
            )
        ) {

            val items = listOf(
                Triple("Habit", "Activity that repeats over time. it has detailed tracking and statistics", R.drawable.habit_bottom),
                Triple("Recurring Task", "Activity that repeats over time. without tracking and statistics", R.drawable.recurring),
                Triple("Task", "Single instance activity without tracking over time.", R.drawable.task_bottom)
            )

            items.forEach { (title, desc, icon) ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                        .clickable {
                            if (title == "Task") {
                                onDismiss()
                                onTaskSelected()  // <-- Open TaskBottom
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        modifier = Modifier.size(26.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontSize = 14.sp
                        )
                        Text(
                            text = desc,
                            color = Color.Gray,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 5.dp)
                        )
                    }

                    Image(
                        painter = painterResource(id = R.drawable.detail),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }

            }
        }
    }
}




@Composable
fun TaskBottom(
    taskId: Int? = null,
    selected: Boolean = false,
    onSelect: () -> Unit = {},
    onCancel: () -> Unit = {},
    onConfirm: () -> Unit = {},
    navController: NavHostController,
    reminderViewModel: ReminderViewModel = viewModel(),
    taskViewModel: TaskViewModel = viewModel(),
    viewModel: CategoryViewModel = viewModel()
) {

    // ---------------------------
    // UI States
    // ---------------------------
    var taskName by remember { mutableStateOf("") }
    var showCategoryDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var getSelectedDate by remember { mutableStateOf("Today") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showRemainderDialog by remember { mutableStateOf(false) }
    var showPriority by remember { mutableStateOf(false) }
    var priorityCount by remember { mutableStateOf("Default") }
    var isPendingState by remember { mutableStateOf(selected) }

        // Reminder list
        val reminders by reminderViewModel.reminders.collectAsState()
        val remainderCount by remember { derivedStateOf { reminders.size.toString() } }

        val context = LocalContext.current
        val db = TaskDatabase.getDatabase(context)
        val taskDao = db.taskDao()

        val taskViewModel: TaskViewModel = viewModel(
            factory = TaskViewModelFactory(taskDao)
        )

    val taskFromRoom = taskViewModel.getTaskById(taskId ?: -1).collectAsState(null).value

    LaunchedEffect(taskFromRoom) {
        taskFromRoom?.let { task ->

            taskName = task.taskName

            // Correct: convert stored millis → "12 Jan 2024"
            getSelectedDate = formatMillisToDateString(task.date)

            // Category
            selectedCategory = viewModel.getCategoryByName(task.category)

            // Set Reminder
            reminderViewModel.setReminderFromEntity(task)

            // Priority
            priorityCount = task.priority.toString()

            // Pending Task
            isPendingState = task.isPending
        }
    }


    Box(
        modifier = Modifier.fillMaxSize().background(Color.White)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp, bottom = 20.dp, start = 20.dp, end = 20.dp)
        ) {

            // Header (Add or Edit)
            Card(colors = CardDefaults.cardColors(containerColor = MeronUltraLight)) {
                Text(
                    text = if (taskId == null) "New Task" else "Edit Task",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Task name input
            OutlinedTextField(
                value = taskName,
                onValueChange = { taskName = it },
                label = { Text("Task") },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = MeronWarm,
                    unfocusedIndicatorColor = MeronWarm,
                    focusedLabelColor = MeronWarm,
                    cursorColor = MeronWarm,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            )
            MildDivider()

            // Category Row
            Row(
                modifier = Modifier.fillMaxWidth().clickable { showCategoryDialog = true },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.category),
                    contentDescription = "",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(MeronSoft)
                )

                Text("Category", modifier = Modifier.padding(start = 10.dp))

                Spacer(Modifier.weight(1f))

                Text(
                    text = selectedCategory?.name ?: "Task",
                    color = selectedCategory?.color ?: MeronSoft
                )

                Image(
                    painter = painterResource(id = selectedCategory?.icon ?: R.drawable.timer),
                    contentDescription = "",
                    modifier = Modifier.padding(start = 6.dp).size(24.dp),
                    colorFilter = ColorFilter.tint(selectedCategory?.color ?: MeronSoft)
                )
            }
            MildDivider()

            // Date Row
            Row(
                modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    painter = painterResource(id = R.drawable.calendar),
                    contentDescription = "",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(MeronSoft)
                )

                Text("Date", modifier = Modifier.padding(start = 10.dp))
                Spacer(modifier = Modifier.weight(1f))

                Card(colors = CardDefaults.cardColors(containerColor = MeronUltraLight)) {
                    Text(
                        text = getSelectedDate,
                        color = MeronSoft,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
            MildDivider()

            // Reminder Row
            Row(
                modifier = Modifier.fillMaxWidth().clickable { showRemainderDialog = true },
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    painter = painterResource(id = R.drawable.notification),
                    contentDescription = "",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(MeronSoft)
                )

                Text("Time & Reminder", modifier = Modifier.padding(start = 10.dp))
                Spacer(modifier = Modifier.weight(1f))

                Card(colors = CardDefaults.cardColors(containerColor = MeronUltraLight)) {
                    Text(
                        text = remainderCount,
                        color = MeronSoft,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
            MildDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    painter = painterResource(id = R.drawable.checklist),
                    contentDescription = "Checklist",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(MeronSoft)
                )

                Text(
                    text = "Checklist",
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.padding(start = 10.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Card(colors = CardDefaults.cardColors(containerColor = MeronUltraLight)) {
                    Text(
                        text = "0",
                        fontFamily = FontFamily.SansSerif,
                        color = MeronSoft,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
            MildDivider()

            // Priority Row
            Row(
                modifier = Modifier.fillMaxWidth().clickable { showPriority = true },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.priority),
                    contentDescription = "",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(MeronSoft)
                )
                Text("Priority", modifier = Modifier.padding(start = 10.dp))
                Spacer(modifier = Modifier.weight(1f))
                Card(colors = CardDefaults.cardColors(containerColor = MeronUltraLight)) {
                    Text(
                        text = priorityCount,
                        color = MeronSoft,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
            MildDivider()

            // Pending Task Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.pending_task),
                    contentDescription = "",
                    modifier = Modifier.size(26.dp),
                    colorFilter = ColorFilter.tint(MeronSoft)
                )

                Column {
                    Text("Pending task", modifier = Modifier.padding(start = 10.dp))
                    Text(
                        "Shown every day until completed.",
                        fontSize = 10.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }

                Spacer(Modifier.weight(1f))

                RadioButton(
                    selected = isPendingState,
                    onClick = { isPendingState = !isPendingState },
                    colors = RadioButtonDefaults.colors(selectedColor = MeronSoft)
                )
            }
            MildDivider()

            Spacer(modifier = Modifier.weight(1f))

            // Bottom Buttons
            Row(
                modifier = Modifier.fillMaxWidth().imePadding().padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = "Cancel",
                    modifier = Modifier.clickable { onCancel() },
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )

                Text(
                    text = "Confirm",
                    fontWeight = FontWeight.Bold,
                    color = MeronWarm,
                    modifier = Modifier.clickable {

                        val dateMillis = convertDateToMillis(getSelectedDate)
                        val reminder = reminderViewModel.reminders.value.firstOrNull()

                        val task = TaskEntity(
                            id = taskId ?: 0,
                            taskName = taskName,
                            category = selectedCategory?.name ?: "Task",
                            date = dateMillis,
                            time = reminder?.time ?: "",
                            reminderType = reminder?.reminderType ?: "none",
                            scheduleType = reminder?.scheduleType ?: "always",
                            selectedDays = reminder?.selectedDays?.joinToString(","),
                            daysBefore = reminder?.daysBefore,
                            priority = priorityCount.toIntOrNull() ?: 0,
                            note = "",
                            checklist = "",
                            isPending = isPendingState
                        )

                        if (taskId == null)
                            taskViewModel.addTask(task)
                        else
                            taskViewModel.updateTask(task)   // <-- UPDATE MODE

                        onConfirm()
                    }
                )
            }
        }
    }

    // ---------------------------
    // Dialogs
    // ---------------------------
    if (showCategoryDialog) {
        CategoryDialog(
            showDialog = true,
            customCategories = viewModel.customCategories,
            defaultCategories = viewModel.defaultCategories,
            onDismiss = { showCategoryDialog = false },
            onCategorySelected = {
                selectedCategory = it
                showCategoryDialog = false
            },
            onManageCategories = {
                navController.navigate("category")
                showCategoryDialog = false
            }
        )
    }

    if (showDatePicker) {
        CustomDatePicker(
            onDismiss = { showDatePicker = false },
            onDateSelected = { date ->
                getSelectedDate = formatDate1(date)
            }
        )
    }

    if (showRemainderDialog) {
        ReminderAlertDialog(
            showDialog = true,
            viewModel = reminderViewModel,
            onDismiss = { showRemainderDialog = false }
        )
    }

    if (showPriority) {
        PriorityAlertDialog(
            onDismissRequest = { showPriority = false },
            onConfirm = {
                priorityCount = it.toString()
                showPriority = false
            }
        )
    }
}

@Composable
fun MildDivider() {
    Divider(
        color = Color.LightGray.copy(alpha = 0.3f),
        thickness = 0.8.dp,
        modifier = Modifier.padding(vertical = 6.dp)
    )
}



@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitTrackerTopBar(
    currentDate: String,
    onMenuClick:()-> Unit = {},
    onAction1: () -> Unit = {},
    onAction2: () -> Unit = {},
    onAction3: () -> Unit = {},
    onAction4: () -> Unit = {}
){

    TopAppBar(
        title = {
           Text(currentDate,
               fontSize = 16.sp)
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    painter = painterResource(id = R.drawable.menu),
                    contentDescription = "Menu",
                    modifier = Modifier.size(22.dp),
                    tint = MeronSoft

                )
            }
        },
        actions = {
            IconButton(onClick = onAction1) {
                Icon( painter = painterResource(id = R.drawable.search),
                    contentDescription = "Search",
                    modifier = Modifier.size(20.dp),
                    tint = MeronSoft
                )
            }
            IconButton(onClick = onAction2) {
                Icon( painter = painterResource(id = R.drawable.filter),
                    contentDescription = "Filter",
                    modifier = Modifier.size(20.dp),
                    tint = MeronSoft
                )
            }
            IconButton(onClick = onAction3) {
                Icon( painter = painterResource(id = R.drawable.calendar),
                    contentDescription = "calendar",
                    modifier = Modifier.size(20.dp),
                    tint = MeronSoft
                )
            }
            IconButton(onClick = onAction4) {
                Icon( painter = painterResource(id = R.drawable.information),
                    contentDescription = "info",
                    modifier = Modifier.size(20.dp),
                    tint = MeronSoft
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White, // <- white background
            titleContentColor = MeronSoft,
            actionIconContentColor = MeronSoft,
            navigationIconContentColor = MeronSoft
        )
    )
}

@Composable
fun BottomNavigationBar(
    items: List<BottomNavItem>,
    selectedRoute: String,
    onItemSelected: (BottomNavItem) -> Unit
) {
    NavigationBar(
        containerColor = Color.White, // <- white background
        contentColor = MeronSoft       // <- icon/text color
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = item.iconRes),
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = { Text(item.label) },
                selected = item.route == selectedRoute,
                onClick = { onItemSelected(item) },
                alwaysShowLabel = true
            )
        }
    }
}
@Composable
fun GradientFAB(onClick: () -> Unit, iconRes: Int) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = Color.Transparent,
        contentColor = Color.White,
        modifier = Modifier.size(56.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(
                    brush = Brush.linearGradient(listOf(MeronWarm, MeronLight)),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes), // <-- use painterResource
                contentDescription = "Add",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePicker(
    onDismiss: () -> Unit,
    onDateSelected: (Long) -> Unit
) {
    val state = rememberDatePickerState()

    // Calendar colors
    val datePickerColors = DatePickerDefaults.colors(
        containerColor = Color.White,
        headlineContentColor = MeronSoft,
        titleContentColor = MeronSoft,
        weekdayContentColor = MeronSoft,
        subheadContentColor = MeronSoft,

        selectedDayContainerColor = MeronSoft,
        selectedDayContentColor = Color.White,

        todayDateBorderColor = MeronSoft,
        todayContentColor = MeronSoft,

        dayContentColor = MeronSoft,
        disabledDayContentColor = MeronSoft.copy(alpha = 0.4f)
    )

    // Header colors (Month–Year row)
    val headerTheme = MaterialTheme.colorScheme.copy(
        primary = MeronSoft,
        onPrimary = Color.White,
        surface = Color.White
    )

    MaterialTheme(colorScheme = headerTheme) {

        DatePickerDialog(
            onDismissRequest = onDismiss,

            colors = DatePickerDefaults.colors(
                containerColor = Color.White // ← FULL WHITE DIALOG INCLUDING ACTION ROW
            ),

            confirmButton = {
                TextButton(
                    onClick = {
                        state.selectedDateMillis?.let { onDateSelected(it) }
                        onDismiss()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = Color.White,
                        contentColor = MeronSoft
                    )
                ) {
                    Text("OK")
                }
            },

            dismissButton = {
                TextButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = Color.White,
                        contentColor = MeronSoft
                    )
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = state,
                colors = datePickerColors,
                showModeToggle = false
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewReminderDialog(
    onDismiss: () -> Unit,
    onConfirm: (ReminderData) -> Unit,
    initialData: ReminderData? = null
) {


    var showTimePicker by remember { mutableStateOf(false) }

    val weekDays = listOf("Sun","Mon","Tue","Wed","Thu","Fri","Sat")


    var reminderType by remember { mutableStateOf(initialData?.reminderType ?: "notification") }
    var scheduleType by remember { mutableStateOf(initialData?.scheduleType ?: "always") }
    var selectedTime by remember { mutableStateOf(initialData?.time ?: "12:00 PM") }
    val selectedDays = remember { mutableStateListOf<String>().apply { initialData?.selectedDays?.let { addAll(it) } } }
    var daysBefore by remember { mutableStateOf(initialData?.daysBefore?.toString() ?: "") }


    BasicAlertDialog(
        onDismissRequest = onDismiss
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(26.dp))
                .background(Color.White)
                .padding(horizontal = 12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(Modifier.height(10.dp))

                Text(
                    text = "New reminder",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MeronSoft,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(Modifier.height(20.dp))

                // Time Picker
                Text(
                    text = selectedTime,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3D2C2C),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clickable { showTimePicker = true }
                )

                Text(
                    text = "Reminder time",
                    fontSize = 14.sp,
                    color = MeronSoft,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(Modifier.height(25.dp))
                Divider(color = Color(0xFFF2E9E9))
                Spacer(Modifier.height(16.dp))

                // Reminder type
                Text(
                    text = "Reminder type",
                    fontSize = 14.sp,
                    color = MeronSoft,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF7EDEE)),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ReminderTypeItem(
                        label = "Don't remind",
                        icon = R.drawable.do_not_remaind,
                        selected = reminderType == "none",
                        onClick = { reminderType = "none" }
                    )

                    ReminderTypeItem(
                        label = "Notification",
                        icon = R.drawable.notification,
                        selected = reminderType == "notification",
                        onClick = { reminderType = "notification" }
                    )

                    ReminderTypeItem(
                        label = "Alarm",
                        icon = R.drawable.alarm,
                        selected = reminderType == "alarm",
                        onClick = { reminderType = "alarm" }
                    )
                }

                Spacer(Modifier.height(20.dp))

                // Schedule
                Text(
                    text = "Reminder schedule",
                    fontSize = 14.sp,
                    color = MeronSoft,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(10.dp))

                ScheduleRow(
                    title = "Always enabled",
                    selected = scheduleType == "always",
                    onClick = { scheduleType = "always" }
                )

                ScheduleRow(
                    title = "Specific days of the week",
                    selected = scheduleType == "specific",
                    onClick = { scheduleType = "specific" }
                )

                if (scheduleType == "specific") {
                    Spacer(Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        weekDays.forEach { day ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(
                                        if (selectedDays.contains(day)) MeronSoft else Color(0xFFEDEBEB)
                                    )
                                    .clickable {
                                        if (selectedDays.contains(day))
                                            selectedDays.remove(day)
                                        else
                                            selectedDays.add(day)
                                    }
                                    .padding(horizontal = 10.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = day,
                                    color = if (selectedDays.contains(day)) Color.White else Color.Black,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }

                ScheduleRow(
                    title = "Days before",
                    selected = scheduleType == "before",
                    onClick = { scheduleType = "before" }
                )

                if (scheduleType == "before") {
                    OutlinedTextField(
                        value = daysBefore,
                        onValueChange = { daysBefore = it },
                        label = { Text("Enter days before") },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = MeronSoft,
                            unfocusedIndicatorColor = MeronSoft,
                            focusedLabelColor = MeronSoft,
                            cursorColor = MeronSoft
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }

                Spacer(Modifier.height(20.dp))
                Divider(color = Color(0xFFF2E9E9))
                Spacer(Modifier.height(10.dp))

                // Buttons
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(45.dp)
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("CANCEL", color = MeronSoft, fontWeight = FontWeight.Bold)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(45.dp)
                            .clickable {
                                onConfirm(
                                    ReminderData(
                                        time = selectedTime,
                                        reminderType = reminderType,
                                        scheduleType = scheduleType,
                                        selectedDays = selectedDays.toList(),
                                        daysBefore = daysBefore.toIntOrNull()
                                    )
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("CONFIRM", color = MeronSoft, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showTimePicker) {
        CustomTimePickerDialog(
            onDismiss = { showTimePicker = false },
            onConfirm = { time ->
                selectedTime = time
                showTimePicker = false
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderAlertDialog(
    showDialog: Boolean,
    viewModel: ReminderViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!showDialog) return

    var showNewReminder by remember { mutableStateOf(false) }
    var editingReminder by remember { mutableStateOf<ReminderData?>(null) }

    val reminderList by viewModel.reminders.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {},
        containerColor = Color.White,
        text = {
            Column(modifier = modifier.fillMaxWidth().padding(4.dp)) {

                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.timer),
                        contentDescription = "Reminder icon",
                        modifier = Modifier.size(28.dp),
                        colorFilter = ColorFilter.tint(MeronSoft)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Time and reminders",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (reminderList.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(reminderList) { data ->
                            ReminderCard(
                                reminderData = data,
                                onDelete = {
                                    viewModel.delete(data)
                                },
                                onEdit = {
                                    editingReminder = data
                                    showNewReminder = true
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                } else {
                    // Empty state
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "No reminder for this activity",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "You can add a reminder to be notified.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Image(
                            painter = painterResource(id = R.drawable.add_remainder),
                            contentDescription = "No reminder illustration",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF5F5F5)),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Add/Edit button
                Button(
                    onClick = {
                        editingReminder = null
                        showNewReminder = true
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MeronSoft)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.add),
                            contentDescription = "Add",
                            modifier = Modifier.size(20.dp),
                            colorFilter = ColorFilter.tint(Color.White)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "New Reminder",
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = Color.White,
                        contentColor = MeronSoft
                    )
                ) {
                    Text(text = "Close", fontWeight = FontWeight.Medium)
                }
            }
        }
    )

    // Show Add/Edit dialog
    if (showNewReminder) {
        NewReminderDialog(
            initialData = editingReminder,
            onDismiss = { showNewReminder = false },
            onConfirm = { newData ->
                showNewReminder = false
                viewModel.addOrUpdate(newData)
                editingReminder = null
            }
        )
    }
}

@Composable
fun ReminderCard(
    reminderData: ReminderData,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7EDEE))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val iconRes = when (reminderData.reminderType) {
                    "notification" -> R.drawable.notification
                    "alarm" -> R.drawable.alarm
                    else -> R.drawable.do_not_remaind
                }
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = reminderData.reminderType,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = reminderData.reminderType.replaceFirstChar { it.uppercase() },
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(text = reminderData.time, fontSize = 14.sp, color = Color.Gray)
                    Text(
                        text = when (reminderData.scheduleType) {
                            "always" -> "Always enabled"
                            "specific" -> reminderData.selectedDays.joinToString()
                            "before" -> "Days before: ${reminderData.daysBefore ?: "-"}"
                            else -> ""
                        },
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        painter = painterResource(id = R.drawable.cate_name),
                        contentDescription = "Edit",
                        tint = MeronSoft,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        painter = painterResource(id = R.drawable.delete),
                        contentDescription = "Delete",
                        tint = Color.Red,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ScheduleRow(title: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp)
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = MeronSoft)
        )
        Text(title, fontSize = 14.sp)
    }
}

@Composable
fun ReminderTypeItem(label: String, icon: Int, selected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(vertical = 15.dp)
            .width(90.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) Color(0xFFFFE1E8) else Color.Transparent)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(26.dp),
            colorFilter = ColorFilter.tint(if (selected) MeronSoft else Color.Gray)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (selected) MeronSoft else Color.DarkGray,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var hour by remember { mutableStateOf(12) }
    var minute by remember { mutableStateOf(0) }
    var isAm by remember { mutableStateOf(true) }
    var mode by remember { mutableStateOf(TimeMode.HOUR) }

    BasicAlertDialog(
        onDismissRequest = onDismiss
    ) {

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(26.dp))
                .background(Color.White)
                .padding(16.dp)
        ) {

            Column(modifier = Modifier.fillMaxWidth()) {

                Text(
                    text = "REMINDER TIME",
                    color = Color(0xFFB3888B),
                    letterSpacing = 1.sp,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    TimeNumberBox(
                        value = hour.toString().padStart(2, '0'),
                        selected = mode == TimeMode.HOUR,
                        onClick = { mode = TimeMode.HOUR }
                    )

                    Text(
                        ":",
                        fontSize = 40.sp,
                        color = Color(0xFF6A4A4A),
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )

                    TimeNumberBox(
                        value = minute.toString().padStart(2, '0'),
                        selected = mode == TimeMode.MINUTE,
                        onClick = { mode = TimeMode.MINUTE }
                    )

                    Spacer(Modifier.width(10.dp))

                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFFE2CED0), RoundedCornerShape(8.dp))
                    ) {

                        Box(
                            modifier = Modifier
                                .background(if (isAm) Color(0xFFFFE3E7) else Color.White)
                                .clickable { isAm = true }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) { Text("AM", color = Color(0xFF9C5A63)) }

                        Box(
                            modifier = Modifier
                                .background(if (!isAm) Color(0xFFFFE3E7) else Color.White)
                                .clickable { isAm = false }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) { Text("PM", color = Color(0xFF9C5A63)) }
                    }
                }

                Spacer(Modifier.height(20.dp))

                TimeClockSelector(
                    mode = mode,
                    selectedHour = hour,
                    selectedMinute = minute,
                    onHourSelected = {
                        hour = it
                        mode = TimeMode.MINUTE   // auto-switch
                    },
                    onMinuteSelected = {
                        minute = it  // smooth: updates 0–59
                    },
                    clockSize = 300.dp,
                    numberSize = 40f,
                    selectedCircleSize = 60f
                )

                Spacer(Modifier.height(20.dp))

                Divider(color = Color(0xFFF2E9E9))

                Spacer(Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Text(
                        text = "Cancel",
                        color = Color(0xFFB35B6B),
                        modifier = Modifier
                            .clickable { onDismiss() }
                            .padding(10.dp),
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = "OK",
                        color = Color(0xFFB35B6B),
                        modifier = Modifier
                            .clickable {
                                val t = "%02d:%02d %s".format(
                                    hour, minute, if (isAm) "AM" else "PM"
                                )
                                onConfirm(t)
                            }
                            .padding(10.dp),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}


@Composable
fun TimeNumberBox(
    value: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(80.dp, 70.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(if (selected) Color(0xFFFFE3E7) else Color(0xFFF7F4F4))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = value,
            fontSize = 38.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF9A4A50)
        )
    }
}



@Composable
fun TimeClockSelector(
    mode: TimeMode,
    selectedHour: Int,
    selectedMinute: Int,
    onHourSelected: (Int) -> Unit,
    onMinuteSelected: (Int) -> Unit,
    clockSize: Dp = 300.dp,
    numberSize: Float = 38f,
    selectedCircleSize: Float = 60f
) {
    val haptic = LocalHapticFeedback.current

    var canvasSize by remember { mutableStateOf(IntSize(0, 0)) }

    var isDragging by remember { mutableStateOf(false) }
    var dragAngleDeg by remember { mutableStateOf<Float?>(null) }
    var dragSelectedValue by remember { mutableStateOf<Int?>(null) }

    var lastHapticTick by remember { mutableStateOf<Int?>(null) }

    val labels = if (mode == TimeMode.HOUR) (1..12).toList()
    else listOf(0,5,10,15,20,25,30,35,40,45,50,55)

    val minuteHapticStep = 3
    val hourHapticStep = 1

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(clockSize)
            .clip(CircleShape)
            .background(Color(0xFFF8F4F4))
            .pointerInput(mode, selectedHour, selectedMinute) {
                detectDragGestures(
                    onDragStart = { offset ->
                        isDragging = true

                        val angle = computeAngleDegFromOffset(offset, canvasSize)
                        dragAngleDeg = angle.toFloat()
                        dragSelectedValue = computeValueForMode(angle, mode)

                        lastHapticTick = computeHapticTick(
                            dragSelectedValue,
                            mode,
                            if (mode == TimeMode.MINUTE) minuteHapticStep else hourHapticStep
                        )

                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    },
                    onDrag = { change, _ ->
                        val angle = computeAngleDegFromOffset(change.position, canvasSize)
                        dragAngleDeg = angle.toFloat()

                        val newValue = computeValueForMode(angle, mode)

                        if (newValue != dragSelectedValue) {
                            dragSelectedValue = newValue

                            val newTick = computeHapticTick(
                                newValue,
                                mode,
                                if (mode == TimeMode.MINUTE) minuteHapticStep else hourHapticStep
                            )

                            if (shouldEmitHapticTick(lastHapticTick, newTick)) {
                                lastHapticTick = newTick
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                        }
                    },
                    onDragEnd = {
                        isDragging = false

                        dragSelectedValue?.let { value ->
                            if (mode == TimeMode.HOUR) onHourSelected(value)
                            else onMinuteSelected(value)
                        }

                        dragAngleDeg = null
                        dragSelectedValue = null
                        lastHapticTick = null
                    },
                    onDragCancel = {
                        isDragging = false
                        dragAngleDeg = null
                        dragSelectedValue = null
                        lastHapticTick = null
                    }
                )
            }
            .onGloballyPositioned {
                canvasSize = IntSize(it.size.width, it.size.height)
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.minDimension / 2.5f
            val centerCircleRadius = 18f
            val pointerThickness = 6f
            val pointerColor = Color(0xFFB2394C).copy(alpha = 0.9f)

            val selAngleDegrees = if (isDragging && dragAngleDeg != null) {
                dragAngleDeg!!.toDouble()
            } else {
                if (mode == TimeMode.HOUR)
                    ((selectedHour % 12) * 30.0) - 90.0
                else
                    (selectedMinute * 6.0) - 90.0
            }

            val angleRad = Math.toRadians(selAngleDegrees)
            val pointerX = center.x + radius * cos(angleRad).toFloat()
            val pointerY = center.y + radius * sin(angleRad).toFloat()

            drawLine(
                color = pointerColor,
                start = center,
                end = Offset(pointerX, pointerY),
                strokeWidth = pointerThickness
            )

            drawCircle(color = pointerColor, radius = centerCircleRadius, center = center)
            drawCircle(color = pointerColor, radius = 10f, center = Offset(pointerX, pointerY))

            val currentSelected =
                dragSelectedValue ?: if (mode == TimeMode.HOUR) selectedHour else selectedMinute

            labels.forEach { n ->
                val angleDeg =
                    if (mode == TimeMode.HOUR) ((n % 12) * 30f) - 90f else (n * 6f) - 90f

                val rad = Math.toRadians(angleDeg.toDouble())
                val x = center.x + radius * cos(rad).toFloat()
                val y = center.y + radius * sin(rad).toFloat()

                val isSelected = currentSelected == n

                if (isSelected) {
                    drawCircle(
                        color = Color(0xFFB2394C),
                        radius = selectedCircleSize,
                        center = Offset(x, y)
                    )
                }

                drawContext.canvas.nativeCanvas.apply {
                    val paint = Paint().apply {
                        textSize = numberSize
                        color = android.graphics.Color.parseColor("#5A3A3A")
                        textAlign = Paint.Align.CENTER
                        isAntiAlias = true
                    }

                    val bounds = Rect()
                    paint.getTextBounds(n.toString(), 0, n.toString().length, bounds)
                    val textHeight = bounds.height()

                    drawText(
                        n.toString().padStart(2, '0'),
                        x,
                        y + textHeight / 2,
                        paint
                    )
                }
            }
        }
    }
}


/** Helpers **/

private fun computeAngleDegFromOffset(offset: Offset, size: IntSize): Double {
    if (size.width == 0 || size.height == 0) return 0.0
    val center = size.center
    val dx = offset.x - center.x
    val dy = offset.y - center.y
    return (Math.toDegrees(atan2(dy, dx).toDouble()) + 360.0 + 90.0) % 360.0
}

private fun computeValueForMode(angleDeg: Double, mode: TimeMode): Int {
    return if (mode == TimeMode.HOUR) {
        val hour = ((angleDeg / 30.0).roundToInt() % 12).let { if (it == 0) 12 else it }
        hour
    } else {
        ((angleDeg / 6.0).roundToInt() % 60 + 60) % 60
    }
}

private fun computeHapticTick(value: Int?, mode: TimeMode, step: Int): Int? {
    if (value == null) return null
    return if (mode == TimeMode.HOUR) value else value / step
}

private fun shouldEmitHapticTick(lastTick: Int?, newTick: Int?): Boolean {
    if (lastTick == null && newTick != null) return true
    return lastTick != newTick
}

enum class TimeMode { HOUR, MINUTE }

@Composable
fun PriorityAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var priority by remember { mutableStateOf(1) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        // The title is the "Set a priority" text
        title = {
            Text(
                text = "Set a priority",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                PriorityCounter(
                    currentPriority = priority,
                    onPriorityChange = { newPrio -> priority = newPrio }
                )
                Spacer(modifier = Modifier.height(16.dp))
                PriorityDefaultButton(onDismiss = onDismissRequest)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Higher priority activities will be displayed higher in the list",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(priority) }) {
                Text("OK", color = Color(0xFFE91E63)) // Pink color
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("CLOSE", color = Color.Black)
            }
        },
        shape = RoundedCornerShape(12.dp),
        containerColor = Color.White
    )
}

@Composable
fun PriorityCounter(currentPriority: Int, onPriorityChange: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF7F2F8), RoundedCornerShape(8.dp))
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PriorityCounterButton(symbol = "−") {
            if (currentPriority > 1) onPriorityChange(currentPriority - 1)
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = currentPriority.toString(),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE91E63) // Pink color
                )
            )
        }

        PriorityCounterButton(symbol = "+") {
            onPriorityChange(currentPriority + 1)
        }
    }
}

@Composable
fun PriorityCounterButton(symbol: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clickable(onClick = onClick)
            .border(
                width = 1.dp,
                color = Color.LightGray.copy(alpha = 0.5f)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = symbol,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
    }
}
@Composable
fun PriorityDefaultButton(onDismiss: () -> Unit) {
    Button(
        onClick = { onDismiss() },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFF8BBD0),
            contentColor = Color(0xFFE91E63)
        ),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = "Default - 1",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
        )
        Text(text = " 🚩", color = Color(0xFFE91E63))
    }
}


fun convertDateToMillis(dateString: String): Long {
    return when (dateString) {

        "Today" -> {
            Calendar.getInstance().timeInMillis
        }

        "Tomorrow" -> {
            Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, 1)
            }.timeInMillis
        }

        else -> {
            try {
                val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                formatter.parse(dateString)?.time ?: System.currentTimeMillis()
            } catch (e: Exception) {
                System.currentTimeMillis()
            }
        }
    }
}

fun formatMillisToDateString(millis: Long): String {
    val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

