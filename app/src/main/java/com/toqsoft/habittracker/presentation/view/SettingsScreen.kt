package com.toqsoft.habittracker.presentation.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.toqsoft.habittracker.R

data class SettingsItem(
    val title: String,
    val icon: Int,
    val contentDescription: String,
    val onClick: () -> Unit = {},

)

@Composable
fun SettingsScreen() {
    val iconColor = MaterialTheme.colorScheme.primary

    var onclick by remember { mutableStateOf(false) }


    val settingsItems = listOf(
        SettingsItem(
            title = "To-do and Habit lists",
            icon = R.drawable.to_do_list,
            contentDescription = "To-do and Habit lists settings",

        ),
        SettingsItem(
            title = "Notifications and Alarms",
            icon = R.drawable.notification,
            contentDescription = "Notifications and Alarms settings"
        ),
        SettingsItem(
            title = "Customize",
            icon = R.drawable.customize,
            contentDescription = "Customize settings"
        ),
        SettingsItem(
            title = "Lock Screen",
            icon = R.drawable.lock_phone,
            contentDescription = "Lock Screen settings",
            onClick = {onclick = !onclick }
        ),
        SettingsItem(
            title = "Backups",
            icon = R.drawable.back_up,
            contentDescription = "Backups settings"
        ),
        SettingsItem(
            title = "Language",
            icon = R.drawable.language,
            contentDescription = "Language settings"
        ),
        SettingsItem(
            title = "Legals",
            icon = R.drawable.legal,
            contentDescription = "Legal information"
        )
    )

    Scaffold(
        topBar = {
            SettingsTopAppBar(iconColor = iconColor)
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                settingsItems.forEach { item ->
                    SettingsListItem(item = item, iconColor = iconColor)
                }
            }
        }
    )

    if(onclick){
        LockScreenSettings()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopAppBar(iconColor: Color) {
    TopAppBar(
        title = {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.titleMedium
            )
        },
        navigationIcon = {
            IconButton(onClick = { }) {
                Icon(
                    painter = painterResource(id = R.drawable.back),
                    contentDescription = "Back",
                    modifier = Modifier.size(18.dp),
                    tint = iconColor
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface,
        )
    )
}

@Composable
fun SettingsListItem(item: SettingsItem, iconColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = item.onClick)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = item.icon),
            contentDescription = item.contentDescription,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = item.title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSettingsScreen() {
    Surface {
        SettingsScreen()
    }
}