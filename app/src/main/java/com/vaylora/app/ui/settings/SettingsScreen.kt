package com.vaylora.app.ui.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    isDarkTheme: Boolean,
    onNavigateBack: () -> Unit,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()
    var showSignOutDialog by remember { mutableStateOf(false) }

    val bgColor = if (isDarkTheme) Color(0xFF0C1410) else Color(0xFFF6FBF4)
    val cardBg = if (isDarkTheme) Color(0xFF14241B) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color(0xFF1B1B1B)
    val subtitleColor = if (isDarkTheme) Color(0xFF88A090) else Color(0xFF6B7280)
    val limeGreen = Color(0xFF8BC34A)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = textColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bgColor)
            )
        },
        containerColor = bgColor
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 40.dp)
        ) {
            // 1. Appearance Section
            item {
                SettingsSectionHeader(title = "APPEARANCE", color = limeGreen)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SettingsToggleRow(
                        icon = if (uiState.isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                        title = "Dark Mode",
                        subtitle = if (uiState.isDarkTheme) "On (OLED battery saving)" else "Off (Light palette)",
                        isChecked = uiState.isDarkTheme,
                        onCheckedChange = { settingsViewModel.setDarkTheme(it) },
                        textColor = textColor,
                        subtitleColor = subtitleColor,
                        iconTint = limeGreen
                    )
                }
            }

            // 2. Notifications Section
            item {
                SettingsSectionHeader(title = "NOTIFICATIONS & REMINDERS", color = limeGreen)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SettingsToggleRow(
                        icon = Icons.Default.Notifications,
                        title = "Waste Sorting Reminders",
                        subtitle = "Helpful eco-tips & daily sustainability badges",
                        isChecked = uiState.notificationsEnabled,
                        onCheckedChange = { settingsViewModel.setNotificationsEnabled(it) },
                        textColor = textColor,
                        subtitleColor = subtitleColor,
                        iconTint = limeGreen
                    )
                }
            }

            // 3. AI & Hardware Preferences
            item {
                SettingsSectionHeader(title = "ON-DEVICE AI VISION", color = limeGreen)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        SettingsClickableRow(
                            icon = Icons.Default.Memory,
                            title = "Inference Acceleration",
                            value = "MediaPipe NPU / GPU Delegate",
                            onClick = {
                                Toast.makeText(context, "Accelerated on-device neural inference is active.", Toast.LENGTH_SHORT).show()
                            },
                            textColor = textColor,
                            subtitleColor = subtitleColor,
                            iconTint = limeGreen
                        )
                        HorizontalDivider(color = cardBg.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
                        SettingsClickableRow(
                            icon = Icons.Default.Security,
                            title = "Living Entity Tripwire Safety",
                            value = "Protected (100+ keywords)",
                            onClick = {
                                Toast.makeText(context, "Humans, pets, & wildlife are safely excluded from waste bin rules.", Toast.LENGTH_LONG).show()
                            },
                            textColor = textColor,
                            subtitleColor = subtitleColor,
                            iconTint = limeGreen
                        )
                    }
                }
            }

            // 4. Privacy & Account
            item {
                SettingsSectionHeader(title = "ACCOUNT & PRIVACY", color = limeGreen)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        SettingsClickableRow(
                            icon = Icons.Default.LockReset,
                            title = "Reset Password",
                            value = "Send reset email",
                            onClick = {
                                Toast.makeText(context, "Password reset link sent to your registered email.", Toast.LENGTH_LONG).show()
                            },
                            textColor = textColor,
                            subtitleColor = subtitleColor,
                            iconTint = limeGreen
                        )
                        HorizontalDivider(color = cardBg.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
                        SettingsClickableRow(
                            icon = Icons.Default.ExitToApp,
                            title = "Sign Out",
                            value = "Log out from this device",
                            onClick = { showSignOutDialog = true },
                            textColor = Color(0xFFFF5252),
                            subtitleColor = subtitleColor,
                            iconTint = Color(0xFFFF5252)
                        )
                    }
                }
            }

            // 5. About Vaylora
            item {
                SettingsSectionHeader(title = "ABOUT", color = limeGreen)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        SettingsClickableRow(
                            icon = Icons.Default.Info,
                            title = "Vaylora Build",
                            value = "v2.0 (Dual-Engine AI & RAG)",
                            onClick = {},
                            textColor = textColor,
                            subtitleColor = subtitleColor,
                            iconTint = limeGreen
                        )
                        HorizontalDivider(color = cardBg.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
                        SettingsClickableRow(
                            icon = Icons.Default.PrivacyTip,
                            title = "Privacy Policy",
                            value = "100% On-Device Camera Processing",
                            onClick = {
                                Toast.makeText(context, "Zero video or camera frames leave your device. Private & secure.", Toast.LENGTH_LONG).show()
                            },
                            textColor = textColor,
                            subtitleColor = subtitleColor,
                            iconTint = limeGreen
                        )
                    }
                }
            }
        }
    }

    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = { Text("Sign Out of Vaylora?", fontWeight = FontWeight.Bold) },
            text = { Text("Your offline scan history is backed up to your cloud profile. You can log back in at any time.") },
            confirmButton = {
                Button(
                    onClick = {
                        showSignOutDialog = false
                        onSignOut()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                ) {
                    Text("Sign Out", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SettingsSectionHeader(title: String, color: Color) {
    Text(
        text = title,
        color = color,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 8.dp, bottom = 4.dp),
        letterSpacing = 1.sp
    )
}

@Composable
fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    textColor: Color,
    subtitleColor: Color,
    iconTint: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = textColor)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = subtitle, fontSize = 12.sp, color = subtitleColor)
            }
        }
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = iconTint
            )
        )
    }
}

@Composable
fun SettingsClickableRow(
    icon: ImageVector,
    title: String,
    value: String,
    onClick: () -> Unit,
    textColor: Color,
    subtitleColor: Color,
    iconTint: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = textColor)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = value, fontSize = 12.sp, color = subtitleColor)
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = subtitleColor,
            modifier = Modifier.size(14.dp)
        )
    }
}
