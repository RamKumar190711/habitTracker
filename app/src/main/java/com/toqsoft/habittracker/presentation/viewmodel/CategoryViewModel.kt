package com.toqsoft.habittracker.presentation.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.toqsoft.habittracker.R
import com.toqsoft.habittracker.domain.model.Category

class CategoryViewModel : ViewModel() {
    // Categories
    private val _customCategories = mutableStateListOf<Category>()
    val customCategories: List<Category> get() = _customCategories

    private val _defaultCategories = mutableStateListOf<Category>().apply {
        addAll(
            listOf(
                Category("Art", R.drawable.img1, Color(0xFFE57373), 0),
                Category("Meditation", R.drawable.img2, Color(0xFFBA68C8), 0),
                Category("Study", R.drawable.img3, Color(0xFF64B5F6), 0),
                Category("Fitness", R.drawable.img6, Color(0xFF4DB6AC), 0),
                Category("Swim", R.drawable.img5, Color(0xFFFFD54F), 0),
                Category("Others", R.drawable.img5, Color(0xFFAED581), 0)
            )
        )
    }
    val defaultCategories: List<Category> get() = _defaultCategories

    fun addCustomCategory(category: Category) {
        _customCategories.add(category)
    }
}
