package com.toqsoft.habittracker.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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

data class ColorSwatch(val color: Color, val isPremium: Boolean = false)
data class IconDetail(val resourceId: Int, val color: Color)

// -----------------------------------------------------------------------------
// Accent colors
// -----------------------------------------------------------------------------

val freeAccentColors = listOf(
    Color(0xFFE5396C), Color(0xFFFF9800), Color(0xFF00BCD4), Color(0xFF9C27B0)
)

val premiumAccentColors = listOf(
    Color(0xFFE53935), Color(0xFFFF8A65), Color(0xFFFFC107), Color(0xFF4CAF50),
    Color(0xFF00BCD4), Color(0xFF9C27B0), Color(0xFFD81B60), Color(0xFFFF5252),
    Color(0xFFFDD835), Color(0xFF66BB6A), Color(0xFF03A9F4), Color(0xFF673AB7),
    Color(0xFFC2185B), Color(0xFFFFCDD2), Color(0xFFCDDC39), Color(0xFF8BC34A),
    Color(0xFF29B6F6), Color(0xFF512DA8), Color(0xFF880E4F), Color(0xFFD50000),
    Color(0xFF795548), Color(0xFF388E3C), Color(0xFF0097A7), Color(0xFF4527A0),
)

// -----------------------------------------------------------------------------
// Icon styles
// -----------------------------------------------------------------------------

val classicIconsDetails = listOf(
    IconDetail(resourceId = R.drawable.notification, color = Color(0xFF9C27B0)),
    IconDetail(resourceId = R.drawable.timer, color = Color(0xFF4CAF50)),
    IconDetail(resourceId = R.drawable.add, color = Color(0xFF03A9F4))
)

val simpleIconsDetails = classicIconsDetails

// -----------------------------------------------------------------------------
// MAIN COMPOSABLE
// -----------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizeScreen(
    themeViewModel: ThemeViewModel = viewModel()
) {
    val isDarkThemeEnabled by themeViewModel.isDarkTheme.collectAsState()
    var selectedIconStyle by remember { mutableStateOf("CLASSIC") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Customize") },
                navigationIcon = {
                    IconButton(onClick = { /* Handle back */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = "Back",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {

            // THEME ICON TOGGLE ---------------------------------------------
            ThemeSection(
                isDarkThemeEnabled = isDarkThemeEnabled,
                onToggle = { themeViewModel.toggleDarkTheme(it) }
            )

            Spacer(modifier = Modifier.height(24.dp))
            Divider()
            Spacer(modifier = Modifier.height(24.dp))

            // ICON STYLE
            IconStyleSection(
                selectedStyle = selectedIconStyle,
                onStyleSelected = { selectedIconStyle = it }
            )

            Spacer(modifier = Modifier.height(24.dp))
            Divider()
            Spacer(modifier = Modifier.height(24.dp))

            // FREE COLORS
            AccentColorSection(
                title = "Free accent colors",
                colors = freeAccentColors,
                isPremium = false
            )

            Spacer(modifier = Modifier.height(32.dp))

            // PREMIUM COLORS
            AccentColorSection(
                title = "Premium accent colors",
                colors = premiumAccentColors,
                isPremium = true
            )
        }
    }
}

// -----------------------------------------------------------------------------
// SECTIONS
// -----------------------------------------------------------------------------

@Composable
fun ThemeSection(
    isDarkThemeEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column {
            Text(text = "Theme", style = MaterialTheme.typography.titleMedium)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Use Ultra Dark Theme",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(end = 8.dp, top = 10.dp)
                )

                Text(
                    text = "Only for premium users",
                    color = Color(0xFFE91E63),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(end = 8.dp, top = 10.dp)
                )
            }
        }

        // ---------------- ICON TOGGLE (NO SWITCH) ----------------

        if (isDarkThemeEnabled) {
            Icon(
                painter = painterResource(id = R.drawable.light),
                contentDescription = "Switch to Light",
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onToggle(false) },
                tint = MaterialTheme.colorScheme.primary
            )
        } else {
            Icon(
                painter = painterResource(id = R.drawable.moon),
                contentDescription = "Switch to Dark",
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onToggle(true) },
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun IconStyleSection(selectedStyle: String, onStyleSelected: (String) -> Unit) {
    Column {
        Text(text = "Category icon style", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            IconStyleOption(
                name = "CLASSIC",
                iconDetails = classicIconsDetails,
                isSelected = selectedStyle == "CLASSIC",
                onClick = { onStyleSelected("CLASSIC") }
            )

            IconStyleOption(
                name = "SIMPLE",
                iconDetails = simpleIconsDetails,
                isSelected = selectedStyle == "SIMPLE",
                onClick = { onStyleSelected("SIMPLE") }
            )
        }
    }
}

@Composable
fun IconStyleOption(
    name: String,
    iconDetails: List<IconDetail>,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
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
                        painter = painterResource(id = detail.resourceId),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(detail.color),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        if (isSelected) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE91E63)),
                modifier = Modifier.padding(top = 4.dp),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = name,
                    color = Color.White,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        } else {
            Text(
                text = name,
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun AccentColorSection(title: String, colors: List<Color>, isPremium: Boolean) {
    Column {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(6),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.height(if (isPremium) 200.dp else 40.dp)
        ) {
            items(colors.size) { index ->
                ColorSwatchItem(color = colors[index], onClick = { })
            }
        }
    }
}

@Composable
fun ColorSwatchItem(color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .size(36.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(color)
            .clickable(onClick = onClick)
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewCustomizeScreen() {
    MaterialTheme {
        CustomizeScreen()
    }
}
