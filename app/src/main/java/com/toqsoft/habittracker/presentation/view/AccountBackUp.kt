package com.toqsoft.habittracker.presentation.view

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toqsoft.habittracker.R

val PrimaryColor = Color(0xFFE94E6B)
val BackgroundColor = Color(0xFFFFF9FA)

@Composable
fun AccountAndBackupsScreen() {
    Scaffold(
        topBar = { AppToolbar() },
        containerColor = BackgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            CloudBackupSection()
            Spacer(modifier = Modifier.height(24.dp))
            LoginPromptSection()
            Spacer(modifier = Modifier.height(24.dp))
            AutomaticCloudBackupFeature()
            Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.5f))
            BackupOptionsList()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppToolbar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                "Account and Backups",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        },
        navigationIcon = {
            IconButton(onClick = {  }) {
                Icon(
                    painter = painterResource(id = R.drawable.back),
                    contentDescription = "Back",
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        actions = {
            IconButton(onClick = {  }) {
                Icon(
                    painter = painterResource(id = R.drawable.information),
                    contentDescription = "Info",
                    modifier = Modifier.size(20.dp)

                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundColor)
    )
}

@Composable
fun CloudBackupSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.backup),
            contentDescription = "Cloud Upload Icon",
            tint = PrimaryColor,
            modifier = Modifier.size(72.dp)
        )
        Text(
            text = "Last cloud backup",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = "-",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Button(
            onClick = {  },
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("UPLOAD BACKUP", color = Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = {  },
            border = BorderStroke(2.dp, PrimaryColor),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("IMPORT FROM CLOUD", color = PrimaryColor, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun LoginPromptSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.logout),
            contentDescription = "Not Logged In",
            tint = PrimaryColor,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = "You're not logged in",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "Sign in to link your purchases to your account and access cloud backups (requires Premium).",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun AutomaticCloudBackupFeature() {
    var isChecked by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isChecked = !isChecked }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Automatic Cloud Backups",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Normal,
                color = Color.Black
            )
            Text(
                text = "Premium feature",
                style = MaterialTheme.typography.bodySmall,
                color = PrimaryColor,
                fontWeight = FontWeight.SemiBold
            )
        }

        Switch(
            checked = isChecked,
            onCheckedChange = { isChecked = it },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = PrimaryColor,
                uncheckedThumbColor = Color.LightGray,
                uncheckedTrackColor = Color.Gray
            )
        )
    }
}

data class BackupOption(
    val title: String,
    val subtitle: String?,
    @DrawableRes val imageResId: Int // Changed from ImageVector to Int (Drawable resource ID)
)

@Composable
fun BackupOptionsList() {
    val options = listOf(
        // IMPORTANT: Replace android.R.drawable.xyz with your actual R.drawable.your_image_name
        BackupOption("Create backup file", null, android.R.drawable.ic_menu_add),
        BackupOption("Import backup file", null, android.R.drawable.ic_menu_upload), // Using a generic upload icon
        BackupOption("CSV data export", "One free use remaining", android.R.drawable.ic_menu_share) // Using a generic share icon
    )

    Column {
        options.forEach { option ->
            BackupOptionItem(
                title = option.title,
                subtitle = option.subtitle,
                imageResId = option.imageResId
            )
            if (option != options.last()) {
                Divider(color = Color.LightGray.copy(alpha = 0.5f))
            }
        }
    }
}

@Composable
fun BackupOptionItem(title: String, subtitle: String?, @DrawableRes imageResId: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {  }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = null,
            modifier = Modifier
                .size(28.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black
            )
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = PrimaryColor
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAccountAndBackupsScreen() {
    MaterialTheme {
        AccountAndBackupsScreen()
    }
}