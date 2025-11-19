package com.toqsoft.habittracker.presentation



import androidx.annotation.DrawableRes
import com.toqsoft.habittracker.R

sealed class BottomNavItem(
    val label: String,
    @DrawableRes val iconRes: Int,
    val route: String
) {
    object Today : BottomNavItem("Today", R.drawable.today, "today")

    object Habits : BottomNavItem("Habits", R.drawable.habits, "habits")

    object Tasks : BottomNavItem("Tasks", R.drawable.tasks, "tasks")

    object Category : BottomNavItem("Category", R.drawable.category, "category")

    object Timer : BottomNavItem("Timer", R.drawable.timer, "timer")
}
