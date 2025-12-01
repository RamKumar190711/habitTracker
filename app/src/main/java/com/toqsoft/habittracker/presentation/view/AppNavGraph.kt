package com.toqsoft.habittracker.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.toqsoft.habittracker.data.database.TaskDatabase
import com.toqsoft.habittracker.presentation.view.*
import com.toqsoft.habittracker.presentation.viewmodel.CategoryViewModel
import com.toqsoft.habittracker.presentation.viewmodel.TaskViewModel
import com.toqsoft.habittracker.presentation.viewmodel.TaskViewModelFactory
import com.toqsoft.habittracker.presentation.viewmodel.ThemeViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraph(
    navController: NavHostController,
    categoryViewModel: CategoryViewModel,
    themeViewModel: ThemeViewModel

) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        // Home screen with drawer and bottom navigation
        composable("home") { HomeScreen(navController) }

        // Task screen
        composable("task") {
            val context = LocalContext.current
            val db = TaskDatabase.getDatabase(context)
            val taskDao = db.taskDao()
            val taskViewModel: TaskViewModel = viewModel(
                factory = TaskViewModelFactory(taskDao)
            )

            TaskBottom(
                onCancel = { navController.popBackStack() },
                onConfirm = { navController.popBackStack() },
                navController = navController,
                viewModel = categoryViewModel,
                taskViewModel = taskViewModel
            )
        }

        // Category screen
        composable("category") {
            CategoryScreen(viewModel = categoryViewModel)
        }

        // Drawer destinations
        composable("news") { PlaceholderScreen("News and Events") }
        composable("timer") { TimerScreen() }
        composable("customize") {CustomizeScreen(themeViewModel = themeViewModel) }
        composable("settings") { SettingsScreen() }
        composable("account_backup") { AccountAndBackupsScreen() }
        composable("premium") { PlaceholderScreen("Get Premium") }
        composable("rate") { PlaceholderScreen("Rate this App") }
        composable("contact") { PlaceholderScreen("Contact Us") }
    }
}
