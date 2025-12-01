package com.toqsoft.habittracker.presentation.view

import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults

import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.toqsoft.habittracker.R
import com.toqsoft.habittracker.domain.model.Category
import com.toqsoft.habittracker.presentation.viewmodel.CategoryViewModel
import com.toqsoft.habittracker.ui.theme.MeronSoft
import com.toqsoft.habittracker.ui.theme.MeronWarm
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    viewModel: CategoryViewModel = viewModel()  // <-- Shared ViewModel
) {

    val scope = rememberCoroutineScope()

    // Bottom sheet visibility
    var showBottomSheet by remember { mutableStateOf(false) }

    // Dialogs
    var showNameDialog by remember { mutableStateOf(false) }
    var showIconDialog by remember { mutableStateOf(false) }
    var showColorDialog by remember { mutableStateOf(false) }

    // Bottom sheet state
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Input state for new category
    var categoryName by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf<Int?>(null) }
    var selectedColor by remember { mutableStateOf<Color?>(null) }

    // --- SHARED ViewModel State ---
    val customCategories = viewModel.customCategories
    val defaultCategories = viewModel.defaultCategories
    // ------------------------------

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)

            .padding(WindowInsets.safeContent.asPaddingValues())
    ) {

        // HEADER
        Row(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.back),
                contentDescription = "back",
                modifier = Modifier.size(20.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
            Text(
                text = "Categories",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 10.dp)
            )
        }

        // CUSTOM CATEGORIES
        Text(
            text = "Custom categories",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 20.dp, start = 10.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (customCategories.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(customCategories) { category ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(80.dp)
                            .clickable { /* handle click */ }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(category.color ?: MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = category.icon ?: R.drawable.category),
                                contentDescription = null,
                                modifier = Modifier.size(28.dp),
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.background)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = category.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.background
                        )

                        Text(
                            text = "${category.entries} entries",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        Spacer(modifier = Modifier.height(10.dp))

        // DEFAULT CATEGORIES
        Text(
            text = "Default categories",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 20.dp, start = 10.dp),
            color = MaterialTheme.colorScheme.background
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(defaultCategories) { category ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(80.dp)
                        .clickable { /* handle click */ }
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(category.color),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = category.icon),
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.background)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = category.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.background
                    )

                    Text(
                        text = "${category.entries} entries",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // NEW CATEGORY BUTTON
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp)
                .height(55.dp)
                .background(MeronWarm, RoundedCornerShape(12.dp))
                .clickable {
                    showBottomSheet = true
                    scope.launch { sheetState.show() }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "New Category",
                color = MaterialTheme.colorScheme.background,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

    // ---------------- BOTTOM SHEET -----------------
    if (showBottomSheet) {
        BottomSheetCategory(
            sheetState = sheetState,
            categoryName = categoryName,
            selectedIcon = selectedIcon,
            selectedColor = selectedColor,
            categories = customCategories,
            onAddCategory = { newCategory ->
                viewModel.addCustomCategory(newCategory)    // <-- SAVE TO VIEWMODEL
            },
            onDismiss = { showBottomSheet = false },
            onOpenCateName = { showNameDialog = true },
            onOpenCateIcon = { showIconDialog = true },
            onOpenCateColor = { showColorDialog = true }
        )
    }

    // ---------------- DIALOGS -----------------
    if (showNameDialog) {
        CategoryNameDialog(
            name = categoryName,
            onNameChange = { categoryName = it },
            onCancel = {
                showNameDialog = false
                showBottomSheet = true
                scope.launch { sheetState.show() }
            },
            onConfirm = {
                showNameDialog = false
                showBottomSheet = true
                scope.launch { sheetState.show() }
            }
        )
    }

    if (showIconDialog) {
        CategoryIconDialog(
            selectedIcon = selectedIcon,
            onIconSelected = {
                selectedIcon = it
                showIconDialog = false
                showBottomSheet = true
                scope.launch { sheetState.show() }
            },
            onCancel = {
                showIconDialog = false
                showBottomSheet = true
                scope.launch { sheetState.show() }
            }
        )
    }

    if (showColorDialog) {
        CategoryColorDialog(
            selectedColor = selectedColor,
            onColorSelected = {
                selectedColor = it
                showColorDialog = false
                showBottomSheet = true
                scope.launch { sheetState.show() }
            },
            onCancel = {
                showColorDialog = false
                showBottomSheet = true
                scope.launch { sheetState.show() }
            }
        )
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetCategory(
    sheetState: SheetState,
    categoryName: String,
    selectedIcon: Int?,
    selectedColor: Color?,
    categories: List<Category>,
    onAddCategory: (Category) -> Unit,
    onDismiss: () -> Unit,
    onOpenCateName: () -> Unit,
    onOpenCateIcon: () -> Unit,
    onOpenCateColor: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val circleColor = selectedColor ?: MaterialTheme.colorScheme.primary

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier.padding(
                top = 35.dp, bottom = 35.dp,
                start = 25.dp, end = 25.dp
            )
        ) {
            // HEADER
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(circleColor, CircleShape)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = categoryName.ifEmpty { "New Category" },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.weight(1f))

                Image(
                    painter = painterResource(id = selectedIcon ?: R.drawable.category),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp),
                    colorFilter = ColorFilter.tint(circleColor)
                )
            }

            MildDivider()

            // CATEGORY NAME
            CategoryRow(
                icon = R.drawable.cate_name,
                text = "Category Name",
                onClick = {
                    scope.launch {
                        sheetState.hide()
                        onOpenCateName()
                    }
                }
            )

            MildDivider()

            // CATEGORY ICON
            CategoryRow(
                icon = R.drawable.cate_icon,
                text = "Category Icon",
                onClick = {
                    scope.launch {
                        sheetState.hide()
                        onOpenCateIcon()
                    }
                }
            )

            MildDivider()


            // CATEGORY COLOR
            CategoryRow(
                icon = R.drawable.cate_color,
                text = "Category Color",
                onClick = {
                    scope.launch {
                        sheetState.hide()
                        onOpenCateColor()
                    }
                }
            )

            MildDivider()


            // CREATE CATEGORY BUTTON
            Text(
                text = "Create Category",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (categoryName.isNotEmpty()) {
                            onAddCategory(
                                Category(
                                    name = categoryName,
                                    icon = selectedIcon ?: R.drawable.category,
                                    color = selectedColor ?: MeronSoft,
                                    entries = 0
                                )
                            )
                            // Reset selections
                            onDismiss()
                        }
                    }
                    .padding(vertical = 10.dp)
            )
        }
    }
}


@Composable
fun CategoryRow(
    icon: Int,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(24.dp),

            )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
fun CategoryNameDialog(
    name: String,
    onNameChange: (String) -> Unit,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onCancel,
        containerColor = MaterialTheme.colorScheme.background,
        title = {},
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                singleLine = true,
                label = { Text("Category Name") },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("OK", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("Cancel", fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }
    )
}

@Composable
fun CategoryIconDialog(
    selectedIcon: Int?,                  // currently selected icon
    onIconSelected: (Int) -> Unit,       // callback when user selects
    onCancel: () -> Unit                  // callback when dialog closes
) {

    AlertDialog(
        onDismissRequest = onCancel,
        containerColor = MaterialTheme.colorScheme.background,
        title = {
            Text(
                text = "Category Icon",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {

            // Your 12 icons
            val icons = listOf(
                R.drawable.img1,
                R.drawable.img2,
                R.drawable.img3,
                R.drawable.img4,
                R.drawable.img5,
                R.drawable.img6,
                R.drawable.img7,
                R.drawable.img8,
                R.drawable.img9,
                R.drawable.img10,
                R.drawable.img11,
                R.drawable.img12
            )

            // Grid of icons
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier.height(220.dp)   // adjust height as needed
            ) {
                items(icons) { iconRes ->    // iconRes is Int (drawable id)

                    val isSelected = iconRes == selectedIcon

                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary.copy(0.15f)
                                else Color(0xFFF5F5F5)
                            )
                            .clickable { onIconSelected(iconRes) },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = iconRes),
                            contentDescription = "icon",
                            modifier = Modifier.size(28.dp),
                            colorFilter = if (isSelected)
                                ColorFilter.tint(MaterialTheme.colorScheme.primary)
                            else ColorFilter.tint(Color.Black)
                        )
                    }
                }
            }
        },
        confirmButton = {},   // no confirm button needed, selection is instant
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("Close", fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun CategoryColorDialog(
    selectedColor: Color?,
    onColorSelected: (Color) -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        containerColor = MaterialTheme.colorScheme.background,
        title = {
            Text(
                text = "Choose Category Color",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {

            val categoryColors = listOf(
                Color(0xFF263238), Color(0xFF37474F), Color(0xFF455A64), Color(0xFF546E7A),
                Color(0xFF1B5E20), Color(0xFF2E7D32), Color(0xFF388E3C), Color(0xFF43A047),
                Color(0xFF004D40), Color(0xFF00695C), Color(0xFF00796B), Color(0xFF00897B),
                Color(0xFF311B92), Color(0xFF4527A0), Color(0xFF512DA8), Color(0xFF673AB7),
                Color(0xFFB71C1C), Color(0xFFC62828), Color(0xFFD32F2F), Color(0xFFE53935),
                Color(0xFFAED581), Color(0xFF4FC3F7), Color(0xFFFF8A65), Color(0xFF90A4AE),
                Color(0xFFE57373), Color(0xFFBA68C8), Color(0xFF64B5F6), Color(0xFF4DB6AC),
                Color(0xFFFFD54F), Color(0xFFA1887F), Color(0xFFE1BEE7), Color(0xFFD1C4E9),
                Color(0xFFC5CAE9), Color(0xFFBBDEFB), Color(0xFFB3E5FC), Color(0xFFB2EBF2),
                Color(0xFFB2DFDB), Color(0xFFC8E6C9), Color(0xFFDCEDC8), Color(0xFFF0F4C3),
                Color(0xFFFFF9C4), Color(0xFFFFECB3), Color(0xFFFFE0B2), Color(0xFFFFCCBC),
                Color(0xFFD7CCC8), Color(0xFFCFD8DC), Color(0xFF90A4AE), Color(0xFFB0BEC5),
                Color(0xFFFFAB91), Color(0xFFFF7043)
            )


            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier.height(220.dp)
            ) {
                items(categoryColors) { color ->

                    val isSelected = color == selectedColor

                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (isSelected) 3.dp else 0.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { onColorSelected(color) },

                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("Close", fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun CategoryDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    customCategories: List<Category>,
    defaultCategories: List<Category>,
    onManageCategories: () -> Unit,
    onCategorySelected: (Category) -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {},
            dismissButton = {},
            containerColor = MaterialTheme.colorScheme.background,
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Custom Categories",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    if (customCategories.isNotEmpty()) {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            items(customCategories) { category ->
                                CategoryItem(category) {
                                    onCategorySelected(category)
                                    onDismiss()
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "No custom categories",
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    Text(
                        text = "Default Categories",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(defaultCategories) { category ->
                            CategoryItem(category) {
                                onCategorySelected(category)
                                onDismiss()
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Manage Categories Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFE0E0E0))
                            .clickable {
                                onManageCategories()
                                onDismiss()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Manage Categories",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun CategoryItem(category: Category, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(80.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(category.color ?: Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = category.icon ?: R.drawable.category),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.background)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = category.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )

        Text(
            text = "${category.entries} entries",
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}
