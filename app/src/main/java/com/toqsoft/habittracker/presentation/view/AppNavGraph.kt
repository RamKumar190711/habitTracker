package com.toqsoft.habittracker.presentation.navigation

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.toqsoft.habittracker.data.database.TaskDatabase
import com.toqsoft.habittracker.data.model.FingerprintDataStore
import com.toqsoft.habittracker.data.model.PinDataStore
import com.toqsoft.habittracker.presentation.view.*
import com.toqsoft.habittracker.presentation.viewmodel.CategoryViewModel
import com.toqsoft.habittracker.presentation.viewmodel.TaskViewModel
import com.toqsoft.habittracker.presentation.viewmodel.TaskViewModelFactory
import com.toqsoft.habittracker.presentation.viewmodel.ThemeViewModel
import kotlinx.coroutines.flow.map

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraph(
    navController: NavHostController,
    categoryViewModel: CategoryViewModel,
    themeViewModel: ThemeViewModel

) {
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = "start"
    ) {
        composable("start") {
            val savedPin by PinDataStore.getPin(context)
                .map { it ?: "" }
                .collectAsState(initial = "LOADING")

            val fingerprintEnabled by FingerprintDataStore.isEnabled(context)
                .collectAsState(initial = false)

            // State to prevent auto-navigation before authentication
            var authenticated by remember { mutableStateOf(false) }

            if (savedPin != "LOADING") {

                when {
                    // Fingerprint enabled → show BiometricLoginScreen
                    fingerprintEnabled && !authenticated -> {
                        BiometricLoginScreen(
                            onAuthenticated = {
                                authenticated = true
                                navController.navigate("home") {
                                    popUpTo("start") { inclusive = true }
                                }
                            }
                        )
                    }

                    // PIN exists → ask for PIN if not authenticated
                    savedPin.isNotEmpty() && !authenticated -> {
                        EnterPinScreen(
                            savedPin = savedPin,
                            onUnlock = {
                                authenticated = true
                                navController.navigate("home") {
                                    popUpTo("start") { inclusive = true }
                                }
                            }
                        )
                    }

                    // No PIN or already authenticated → go home
                    else -> {
                        LaunchedEffect(Unit) {
                            navController.navigate("home") {
                                popUpTo("start") { inclusive = true }
                            }
                        }
                    }
                }
            }
        }



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
