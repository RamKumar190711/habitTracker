package com.toqsoft.habittracker.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.toqsoft.habittracker.presentation.viewmodel.CategoryViewModel

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
            TaskBottom(

                onCancel = { navController.popBackStack() },
                onConfirm = { navController.popBackStack() },
                navController =navController,
                viewModel = categoryViewModel
            )
        }
        composable("category") { CategoryScreen(
            viewModel = categoryViewModel
        ) }
    }
}
