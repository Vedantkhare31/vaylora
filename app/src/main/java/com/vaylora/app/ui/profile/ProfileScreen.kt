package com.vaylora.app.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.auth.FirebaseUser
import com.vaylora.app.domain.model.ScanRecord
import com.vaylora.app.domain.model.UserProfile
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    currentUser: FirebaseUser? = null,
    isDarkTheme: Boolean = true,
    onNavigateBack: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val uid = currentUser?.uid ?: ""
    LaunchedEffect(uid) {
        if (uid.isNotBlank()) {
            viewModel.loadProfile(uid)
        }
    }

    val bgColor = if (isDarkTheme) Color(0xFF0C1410) else Color(0xFFF6FBF4)
    val cardBg = if (isDarkTheme) Color(0xFF14241B) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color(0xFF1B1B1B)
    val textGray = if (isDarkTheme) Color(0xFF88A090) else Color(0xFF6B7280)
    val limeAccent = Color(0xFF8BC34A)
    val tealAccent = Color(0xFF009688)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Sustainability Profile",
                        color = textColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = limeAccent
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bgColor)
            )
        },
        containerColor = bgColor
    ) { innerPadding ->
        when {
            uiState.isLoading && uiState.profile == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = limeAccent)
                }
            }

            else -> {
                val profile = uiState.profile ?: UserProfile(
                    uid = uid,
                    name = currentUser?.displayName ?: "Eco Advocate",
                    email = currentUser?.email ?: ""
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 10.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Profile Header Card
                    item {
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = cardBg),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                val initial = profile.name.trim().take(1).uppercase().ifEmpty {
                                    profile.email.take(1).uppercase().ifEmpty { "V" }
                                }
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(CircleShape)
                                        .background(limeAccent),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = initial,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0C1410)
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = profile.name.ifBlank { "Vaylora Member" },
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                                Text(
                                    text = profile.email.ifBlank { "Local Profile" },
                                    fontSize = 13.sp,
                                    color = textGray
                                )
                            }
                        }
                    }

                    // Eco Metrics Row
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            MetricItem(
                                title = "Items Scanned",
                                value = "${profile.totalItemsScanned}",
                                icon = Icons.Default.Recycling,
                                color = limeAccent,
                                cardBg = cardBg,
                                textColor = textColor,
                                textGray = textGray,
                                modifier = Modifier.weight(1f)
                            )
                            MetricItem(
                                title = "CO₂ Saved",
                                value = String.format(Locale.getDefault(), "%.1f kg", profile.totalCarbonSavedKg),
                                icon = Icons.Default.Eco,
                                color = tealAccent,
                                cardBg = cardBg,
                                textColor = textColor,
                                textGray = textGray,
                                modifier = Modifier.weight(1f)
                            )
                            MetricItem(
                                title = "EcoScore",
                                value = "${profile.ecoScore}",
                                icon = Icons.Default.Star,
                                color = Color(0xFFFFB300),
                                cardBg = cardBg,
                                textColor = textColor,
                                textGray = textGray,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Scan History Title
                    item {
                        Text(
                            text = "RECENT SCAN HISTORY",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = limeAccent,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }

                    // Scan Records
                    if (uiState.scanHistory.isEmpty()) {
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = cardBg),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(28.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Recycling,
                                        contentDescription = null,
                                        tint = textGray,
                                        modifier = Modifier.size(42.dp)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "No scans recorded yet",
                                        fontWeight = FontWeight.SemiBold,
                                        color = textColor,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = "Open the AI Scan tab and point your camera at household items.",
                                        fontSize = 12.sp,
                                        color = textGray,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    } else {
                        items(uiState.scanHistory) { record ->
                            ScanRecordCard(
                                record = record,
                                cardBg = cardBg,
                                textColor = textColor,
                                textGray = textGray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricItem(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    cardBg: Color,
    textColor: Color,
    textGray: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                color = textColor
            )
            Text(
                text = title,
                fontSize = 10.sp,
                color = textGray,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun ScanRecordCard(
    record: ScanRecord,
    cardBg: Color,
    textColor: Color,
    textGray: Color
) {
    val binColor = try {
        Color(android.graphics.Color.parseColor(record.binColorHex))
    } catch (_: Exception) {
        Color(0xFF2196F3)
    }

    val dateStr = remember(record.timestamp) {
        val sdf = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
        sdf.format(Date(record.timestamp))
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = record.materialLabel.replaceFirstChar { it.uppercase() },
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = dateStr,
                    fontSize = 11.sp,
                    color = textGray
                )
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = binColor
            ) {
                Text(
                    text = record.binType,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
