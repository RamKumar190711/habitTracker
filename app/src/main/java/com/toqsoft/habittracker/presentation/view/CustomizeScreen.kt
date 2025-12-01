package com.toqsoft.habittracker.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.toqsoft.habittracker.R
import com.toqsoft.habittracker.presentation.viewmodel.ThemeViewModel

// -----------------------------------------------------------------------------
// Data models
// -----------------------------------------------------------------------------
data class IconDetail(val resourceId: Int, val color: Color)

// -----------------------------------------------------------------------------
// Accent colors
// -----------------------------------------------------------------------------
val freeAccentColors = listOf(
    Color(0xFFE5396C), Color(0xFFFF9800), Color(0xFF00BCD4), Color(0xFF9C27B0)
)

val premiumAccentColors = listOf(
    Color(0xFFE53935), Color(0xFFFF8A65), Color(0xFFFFC107), Color(0xFF4CAF50),
    Color(0xFF00BCD4), Color(0xFF9C27B0), Color(0xFFD81B60), Color(0xFFFF5252)
)

// -----------------------------------------------------------------------------
// Icon styles
// -----------------------------------------------------------------------------
val classicIconsDetails = listOf(
    IconDetail(R.drawable.notification, Color(0xFF9C27B0)),
    IconDetail(R.drawable.timer, Color(0xFF4CAF50)),
    IconDetail(R.drawable.add, Color(0xFF03A9F4))
)

val simpleIconsDetails = classicIconsDetails

// -----------------------------------------------------------------------------
// MAIN COMPOSABLE
// -----------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizeScreen(themeViewModel: ThemeViewModel = viewModel()) {
    val isDarkThemeEnabled by themeViewModel.isDarkTheme.collectAsState()
    val primaryColor by themeViewModel.primaryColor.collectAsState()
    var selectedIconStyle by remember { mutableStateOf("CLASSIC") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Customize") },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = "Back",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = primaryColor
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            ThemeSection(
                isDarkThemeEnabled = isDarkThemeEnabled,
                themeViewModel = themeViewModel
            )

            Spacer(modifier = Modifier.height(24.dp))
            Divider()
            Spacer(modifier = Modifier.height(24.dp))

            IconStyleSection(
                selectedStyle = selectedIconStyle,
                onStyleSelected = { selectedIconStyle = it },
                primaryColor = primaryColor
            )

            Spacer(modifier = Modifier.height(24.dp))
            Divider()
            Spacer(modifier = Modifier.height(24.dp))

            AccentColorSection(
                title = "Free accent colors",
                colors = freeAccentColors,
                themeViewModel = themeViewModel
            )

            Spacer(modifier = Modifier.height(32.dp))

            AccentColorSection(
                title = "Premium accent colors",
                colors = premiumAccentColors,
                themeViewModel = themeViewModel
            )
        }
    }
}

// -----------------------------------------------------------------------------
// THEME SECTION
// -----------------------------------------------------------------------------
@Composable
fun ThemeSection(
    isDarkThemeEnabled: Boolean,
    themeViewModel: ThemeViewModel
) {
    val primaryColor by themeViewModel.primaryColor.collectAsState()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("Theme", style = MaterialTheme.typography.titleMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Use Ultra Dark Theme",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(end = 8.dp, top = 10.dp)
                )
                Text(
                    "Only for premium users",
                    color = Color(0xFFE91E63),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(end = 8.dp, top = 10.dp)
                )
            }
        }

        if (isDarkThemeEnabled) {
            Icon(
                painter = painterResource(R.drawable.light),
                contentDescription = "Switch to Light",
                modifier = Modifier
                    .size(32.dp)
                    .clickable { themeViewModel.toggleDarkTheme(false) },
                tint = primaryColor
            )
        } else {
            Icon(
                painter = painterResource(R.drawable.moon),
                contentDescription = "Switch to Dark",
                modifier = Modifier
                    .size(32.dp)
                    .clickable { themeViewModel.toggleDarkTheme(true) },
                tint = primaryColor
            )
        }
    }
}

// -----------------------------------------------------------------------------
// ICON STYLE SECTION
// -----------------------------------------------------------------------------
@Composable
fun IconStyleSection(
    selectedStyle: String,
    onStyleSelected: (String) -> Unit,
    primaryColor: Color
) {
    Column {
        Text("Category icon style", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            IconStyleOption("CLASSIC", classicIconsDetails, selectedStyle == "CLASSIC", primaryColor) {
                onStyleSelected("CLASSIC")
            }
            IconStyleOption("SIMPLE", simpleIconsDetails, selectedStyle == "SIMPLE", primaryColor) {
                onStyleSelected("SIMPLE")
            }
        }
    }
}

@Composable
fun IconStyleOption(
    name: String,
    iconDetails: List<IconDetail>,
    isSelected: Boolean,
    primaryColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(vertical = 8.dp)) {
            iconDetails.forEach { detail ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF7F2F6))
                ) {
                    Image(
                        painter = painterResource(detail.resourceId),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(detail.color),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        if (isSelected) {
            Card(
                colors = CardDefaults.cardColors(containerColor = primaryColor),
                modifier = Modifier.padding(top = 4.dp),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    name,
                    color = Color.White,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        } else {
            Text(name, color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

// -----------------------------------------------------------------------------
// ACCENT COLOR SECTION
// -----------------------------------------------------------------------------
@Composable
fun AccentColorSection(
    title: String,
    colors: List<Color>,
    themeViewModel: ThemeViewModel
) {
    val selectedColor by themeViewModel.primaryColor.collectAsState()
    Column {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(6),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.height(if (colors.size > 6) 200.dp else 40.dp)
        ) {
            items(colors.size) { index ->
                val color = colors[index]
                ColorSwatchItem(
                    color = color,
                    onClick = { themeViewModel.setPrimaryColor(color) },
                    isSelected = color == selectedColor
                )
            }
        }
    }
}

@Composable
fun ColorSwatchItem(color: Color, onClick: () -> Unit, isSelected: Boolean = false) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .size(36.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(color)
            .clickable { onClick() }
            .border(
                width = if (isSelected) 3.dp else 0.dp,
                color = if (isSelected) Color.Black else Color.Transparent,
                shape = RoundedCornerShape(30.dp)
            )
    )
}


