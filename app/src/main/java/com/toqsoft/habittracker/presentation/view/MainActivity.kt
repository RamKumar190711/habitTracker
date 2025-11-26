package com.toqsoft.habittracker.presentation.view

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.navigation.compose.rememberNavController
import com.toqsoft.habittracker.presentation.navigation.AppNavGraph
import com.toqsoft.habittracker.presentation.viewmodel.CategoryViewModel

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
         val categoryViewModel: CategoryViewModel by viewModels()

        setContent {
            val navController = rememberNavController()
            AppNavGraph(navController,
                categoryViewModel = categoryViewModel)
        }
    }
}