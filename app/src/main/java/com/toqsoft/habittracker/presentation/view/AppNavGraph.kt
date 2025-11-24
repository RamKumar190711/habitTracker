package com.toqsoft.habittracker.presentation.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.toqsoft.habittracker.data.database.TaskDatabase
import com.toqsoft.habittracker.presentation.viewmodel.CategoryViewModel
import com.toqsoft.habittracker.presentation.viewmodel.TaskViewModel
import com.toqsoft.habittracker.presentation.viewmodel.TaskViewModelFactory

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraph(navController: NavHostController,
                categoryViewModel: CategoryViewModel
) {

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") { HomeScreen(navController) }
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
                navController =navController,
                viewModel = categoryViewModel,
                taskViewModel = taskViewModel

            )
        }
        composable("category") { CategoryScreen(
            viewModel = categoryViewModel
        ) }
    }
}
