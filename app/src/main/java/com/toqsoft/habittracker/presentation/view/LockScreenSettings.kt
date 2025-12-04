package com.toqsoft.habittracker.presentation.view

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.toqsoft.habittracker.R
import com.toqsoft.habittracker.data.model.FingerprintDataStore
import com.toqsoft.habittracker.data.model.PinDataStore
import kotlinx.coroutines.launch

@Composable
fun LockScreenSettings(
    onAuthenticated: (() -> Unit)? = null // optional callback after fingerprint success
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Get saved PIN from DataStore
    val savedPin by PinDataStore.getPin(context).collectAsState(initial = null)

    // Lock PIN switch state
    var lockPinEnabled by remember { mutableStateOf(false) }
    LaunchedEffect(savedPin) {
        lockPinEnabled = savedPin != null
    }

    // FINGERPRINT STATE FROM DATASTORE (read-only)
    val fingerprintEnabled by FingerprintDataStore.isEnabled(context).collectAsState(initial = false)

    // Launch biometric authentication automatically if enabled
    LaunchedEffect(fingerprintEnabled) {
        if (fingerprintEnabled) {
            val activity = context as FragmentActivity
            val promptInfo = createPromptInfo()
            val biometricPrompt = createBiometricPrompt(
                activity,
                onSuccess = {
                    onAuthenticated?.invoke()
                },
                onError = {
                    Log.d("BiometricAuth", "Error: $it")
                }
            )
            biometricPrompt.authenticate(promptInfo)
        }
    }

    // Dialog states
    var showPinDialog by remember { mutableStateOf(false) }
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(WindowInsets.safeContent.asPaddingValues())
    ) {

        // Top bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.back),
                contentDescription = "Back",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text("Lock Screen", fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ---------------------------
        // LOCK PIN SWITCH
        // ---------------------------
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.lock),
                contentDescription = "Lock PIN",
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "Lock PIN",
                modifier = Modifier.weight(1f),
                fontSize = 16.sp
            )

            Switch(
                checked = lockPinEnabled,
                onCheckedChange = { newState ->
                    if (newState) {
                        showPinDialog = true
                    } else {
                        lockPinEnabled = false
                        scope.launch { PinDataStore.clearPin(context) }
                    }
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    uncheckedThumbColor = Color.White,
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                    uncheckedTrackColor = Color.LightGray
                )
            )
        }

        // ---------------------------
        // FINGERPRINT SWITCH
        // ---------------------------
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.fingerprint),
                contentDescription = "Fingerprint",
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(Modifier.weight(1f)) {
                Text("Enable fingerprint support", fontSize = 16.sp)
                Text("Only for premium users", fontSize = 12.sp, color = Color.Gray)
            }

            Switch(
                checked = fingerprintEnabled,
                onCheckedChange = { newValue ->
                    scope.launch { FingerprintDataStore.save(context, newValue) }
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    uncheckedThumbColor = Color.White,
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                    uncheckedTrackColor = Color.LightGray
                )
            )
        }
    }

    // ======================
    // PIN CREATION DIALOG
    // ======================
    if (showPinDialog) {
        AlertDialog(
            onDismissRequest = {
                showPinDialog = false
                lockPinEnabled = savedPin != null
            },
            confirmButton = {},
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Text("Create a lock PIN", fontSize = 20.sp)

                    Spacer(Modifier.height(20.dp))

                    OutlinedTextField(
                        value = pin,
                        onValueChange = { if (it.length <= 4) pin = it },
                        placeholder = { Text("Enter your PIN") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true
                    )

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = confirmPin,
                        onValueChange = { if (it.length <= 4) confirmPin = it },
                        placeholder = { Text("Re-enter your PIN") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true
                    )

                    Spacer(Modifier.height(20.dp))

                    Text(
                        "If you lose your PIN you will have to reset the application data.",
                        color = Color.Red,
                        fontSize = 12.sp
                    )

                    if (errorMessage.isNotEmpty()) {
                        Spacer(Modifier.height(12.dp))
                        Text(errorMessage, color = Color.Red, fontSize = 14.sp)
                    }

                    Spacer(Modifier.height(24.dp))

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {

                        TextButton(onClick = {
                            showPinDialog = false
                            lockPinEnabled = savedPin != null
                            pin = ""
                            confirmPin = ""
                            errorMessage = ""
                        }) {
                            Text("CANCEL")
                        }

                        TextButton(onClick = {
                            when {
                                pin.isEmpty() || confirmPin.isEmpty() ->
                                    errorMessage = "PIN fields cannot be empty"

                                pin != confirmPin ->
                                    errorMessage = "PINs do not match"

                                else -> {
                                    val pinToSave = pin
                                    scope.launch {
                                        Log.d("PIN_TEST", "Saving PIN: $pinToSave")
                                        PinDataStore.savePin(context, pinToSave)
                                    }

                                    lockPinEnabled = true
                                    showPinDialog = false
                                    pin = ""
                                    confirmPin = ""
                                    errorMessage = ""
                                }
                            }
                        }) {
                            Text("CONFIRM")
                        }
                    }
                }
            }
        )
    }
}
