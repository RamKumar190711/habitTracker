package com.toqsoft.habittracker.domain.model

import androidx.compose.ui.graphics.Color

data class Category(
    val name: String,
    val icon: Int,
    val color: Color, // ✅ Compose Color
    val entries: Int = 0
)