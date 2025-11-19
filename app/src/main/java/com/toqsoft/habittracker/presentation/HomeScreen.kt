package com.toqsoft.habittracker.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.toqsoft.habittracker.R
import com.toqsoft.habittracker.ui.theme.MeronLight
import com.toqsoft.habittracker.ui.theme.MeronSoft
import com.toqsoft.habittracker.ui.theme.MeronUltraLight
import com.toqsoft.habittracker.ui.theme.MeronWarm

import kotlinx.datetime.toKotlinLocalDate
import java.time.LocalDate



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(navController: NavHostController) {
    var selectedRoute by remember { mutableStateOf(BottomNavItem.Today.route) }

    var isClick by remember { mutableStateOf(false) }

    val today = LocalDate.now().toKotlinLocalDate()
    val selectedDay = remember { mutableStateOf(today.dayOfMonth) }

    Scaffold(
        topBar = {
            HabitTrackerTopBar(
                currentDate = formatDate(
                    kotlinx.datetime.LocalDate(today.year, today.monthNumber, selectedDay.value)
                )
            )
        },
        floatingActionButton = {
            GradientFAB(
                onClick = {  isClick = !isClick },
                iconRes = R.drawable.add // pass PNG resource here
            )
        },
        bottomBar = {
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
        },
        containerColor = Color.White

    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedRoute) {
                BottomNavItem.Today.route -> TodayScreen() // Show TodayScreen with image
//                BottomNavItem.Habits.route -> HabitsScreen()
//                BottomNavItem.Tasks.route -> TasksScreen()
//                BottomNavItem.Category.route -> CategoryScreen()
//                BottomNavItem.Timer.route -> TimerScreen()
            }
        }

        if (isClick) {
            ModalBottomSheetExample(
                onDismiss = { isClick = false },
                onTaskSelected = {
                    navController.navigate("task")   // Navigate to Task Screen

                }
            )
        }


    }
}
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
    selected: Boolean = false,
    onSelect: () -> Unit = {},
    onCancel: () -> Unit = {},
    onConfirm: () -> Unit = {},
    navController: NavHostController
) {

    var taskName by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp, bottom = 20.dp, start = 20.dp, end = 20.dp)
                .background(Color.White)
        ) {

            // Header card
            Card(colors = CardDefaults.cardColors(containerColor = MeronUltraLight)) {
                Text(
                    text = "New task",
                    fontFamily = FontFamily.SansSerif,
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
                modifier = Modifier.fillMaxWidth()
                    .clickable{
                        navController.navigate("category")
                    },
                verticalAlignment = Alignment.CenterVertically

            ) {
                Image(
                    painter = painterResource(id = R.drawable.category),
                    contentDescription = "Category",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(MeronSoft)
                )

                Text(
                    text = "Category",
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.padding(start = 10.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "Task",
                    fontFamily = FontFamily.SansSerif,
                    color = MeronSoft
                )

                Image(
                    painter = painterResource(id = R.drawable.timer),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 6.dp)
                        .size(24.dp),
                    colorFilter = ColorFilter.tint(MeronSoft)
                )
            }
            MildDivider()

            // Date Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    painter = painterResource(id = R.drawable.calendar),
                    contentDescription = "Date",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(MeronSoft)
                )

                Text(
                    text = "Date",
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.padding(start = 10.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Card(colors = CardDefaults.cardColors(containerColor = MeronUltraLight)) {
                    Text(
                        text = "Today",
                        fontFamily = FontFamily.SansSerif,
                        color = MeronSoft,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
            MildDivider()

            // Notification Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    painter = painterResource(id = R.drawable.notification),
                    contentDescription = "Notification",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(MeronSoft)
                )

                Text(
                    text = "Time and remainder",
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

            // Checklist Row
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
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    painter = painterResource(id = R.drawable.priority),
                    contentDescription = "Priority",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(MeronSoft)
                )

                Text(
                    text = "Priority",
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.padding(start = 10.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MeronUltraLight
                    )
                ) {
                    Text(
                        text = "Default",
                        fontFamily = FontFamily.SansSerif,
                        color = MeronSoft,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
            MildDivider()

            // Note Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    painter = painterResource(id = R.drawable.note),
                    contentDescription = "Note",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(MeronSoft)
                )

                Text(
                    text = "Note",
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
            MildDivider()

            // Pending Task Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    painter = painterResource(id = R.drawable.pending_task),
                    contentDescription = "Pending Task",
                    modifier = Modifier.size(26.dp),
                    colorFilter = ColorFilter.tint(MeronSoft)
                )

                Column {
                    Text(
                        text = "Pending task",
                        fontFamily = FontFamily.SansSerif,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                    Text(
                        text = "It will be shown each day until completed.",
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 10.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                RadioButton(
                    selected = selected,
                    onClick = { onSelect() },
                    colors = RadioButtonDefaults.colors(selectedColor = MeronSoft)
                )
            }
            MildDivider()

            Spacer(modifier = Modifier.weight(1f))

            // ⬇️ Bottom Buttons (move up above keyboard automatically)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    text = "Cancel",
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    fontSize = 16.sp,
                    modifier = Modifier.clickable { onCancel() }
                )

                Text(
                    text = "Confirm",
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    color = MeronWarm,
                    fontSize = 16.sp,
                    modifier = Modifier.clickable { onConfirm() }
                )
            }

        }
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

