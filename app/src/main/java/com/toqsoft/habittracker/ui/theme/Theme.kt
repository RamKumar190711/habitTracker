package com.toqsoft.habittracker.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private fun lightColors(primaryColor: Color) = lightColorScheme(
    primary = primaryColor,
    onPrimary = Color.White,
    background = Color.White,
    surface = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black
)

private fun darkColors(primaryColor: Color) = darkColorScheme(
    primary = primaryColor,
    onPrimary = Color.White,
    background = Color.Black,
    surface = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)


@Composable
fun HabitTrackerTheme(
    darkTheme: Boolean,
    primaryColor: Color,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) darkColors(primaryColor) else lightColors(primaryColor)

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
