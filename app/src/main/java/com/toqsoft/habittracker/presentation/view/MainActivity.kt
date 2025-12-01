package com.toqsoft.habittracker.presentation.view

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.toqsoft.habittracker.presentation.navigation.AppNavGraph
import com.toqsoft.habittracker.presentation.viewmodel.CategoryViewModel
import com.toqsoft.habittracker.presentation.viewmodel.ThemeViewModel
import com.toqsoft.habittracker.ui.theme.HabitTrackerTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
         val categoryViewModel: CategoryViewModel by viewModels()
        val themeViewModel: ThemeViewModel by viewModels()


        setContent {
            val isDarkThemeEnabled by themeViewModel.isDarkTheme.collectAsState()
            val primaryColor by themeViewModel.primaryColor.collectAsState()

            val navController = rememberNavController()
            HabitTrackerTheme(darkTheme = isDarkThemeEnabled,
                primaryColor = primaryColor) {
            AppNavGraph(
                    navController,
                    categoryViewModel = categoryViewModel,
                themeViewModel = themeViewModel
                )
            }
        }
    }
}