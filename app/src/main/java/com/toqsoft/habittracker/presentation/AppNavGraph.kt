package com.toqsoft.habittracker.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") { HomeScreen(navController) }
        composable("task") {
            TaskBottom(

                onCancel = { navController.popBackStack() },
                onConfirm = { navController.popBackStack() },
                navController =navController
            )
        }
        composable("category") { CategoryScreen() }
    }
}
