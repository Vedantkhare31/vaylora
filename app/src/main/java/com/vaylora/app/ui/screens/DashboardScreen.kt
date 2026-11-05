package com.vaylora.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vaylora.app.R
import com.vaylora.app.domain.model.UserProfile

@Composable
fun DashboardScreen(
    userProfile: UserProfile? = null,
    isDarkTheme: Boolean = true,
    onNavigateToCamera: () -> Unit,
    onNavigateToWhereToThrow: () -> Unit,
    onNavigateToUpcycling: () -> Unit,
    onNavigateToSettings: () -> Unit = {}
) {
    val emeraldDark = if (isDarkTheme) Color(0xFF0F2B1D) else Color(0xFFE8F5E9)
    val limeAccent = Color(0xFF8BC34A)
    val tealAccent = Color(0xFF009688)
    val bgColor = if (isDarkTheme) Color(0xFF0C1410) else Color(0xFFF6FBF4)
    val textColor = if (isDarkTheme) Color.White else Color(0xFF1B1B1B)
    val cardBg = if (isDarkTheme) Color(0xFF14241B) else Color.White
    val subtitleColor = if (isDarkTheme) Color(0xFF88A090) else Color(0xFF6B7280)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 20.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Header with Logo & Profile Quick Access
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.vaylora_logo),
                        contentDescription = "Vaylora Logo",
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "VAYLORA",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = textColor,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = if (userProfile != null && userProfile.name.isNotBlank()) {
                                "Hello, ${userProfile.name} • On-Device AI"
                            } else {
                                "On-Device AI Waste Intelligence"
                            },
                            fontSize = 12.sp,
                            color = limeAccent,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                IconButton(
                    onClick = onNavigateToSettings,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(cardBg)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = limeAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Live Hero Scan Banner (Call to Action)
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToCamera() },
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF1B5E20),
                                    Color(0xFF004D40)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = limeAccent.copy(alpha = 0.25f)
                            ) {
                                Text(
                                    text = "REAL-TIME CAMERA SCAN",
                                    color = Color(0xFFAED581),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Point & Identify Waste",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Front & back cameras supported • 1,000+ materials recognized automatically.",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.85f),
                                lineHeight = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(limeAccent),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Scan",
                                tint = Color(0xFF0C1410),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }

        // Personal Eco Stats Counter
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Items Scanned",
                    value = "${userProfile?.totalItemsScanned ?: 0}",
                    icon = Icons.Default.QrCodeScanner,
                    accentColor = limeAccent,
                    cardBg = cardBg,
                    textColor = textColor,
                    subtitleColor = subtitleColor,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "CO₂ Saved",
                    value = String.format("%.1f kg", userProfile?.totalCarbonSavedKg ?: 0f),
                    icon = Icons.Default.Eco,
                    accentColor = tealAccent,
                    cardBg = cardBg,
                    textColor = textColor,
                    subtitleColor = subtitleColor,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Eco Score",
                    value = "${userProfile?.ecoScore ?: 0}",
                    icon = Icons.Default.Star,
                    accentColor = Color(0xFFFFB300),
                    cardBg = cardBg,
                    textColor = textColor,
                    subtitleColor = subtitleColor,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Quick Navigation Cards
        item {
            Text(
                text = "EXPLORE FEATURES",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = limeAccent,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        item {
            QuickActionTile(
                title = "Where To Throw Directory",
                subtitle = "Search 30+ comprehensive waste rules, bin color hex codes, & prep steps",
                icon = Icons.Default.Delete,
                color = Color(0xFF2196F3),
                cardBg = cardBg,
                textColor = textColor,
                subtitleColor = subtitleColor,
                onClick = onNavigateToWhereToThrow
            )
        }

        item {
            QuickActionTile(
                title = "Upcycling Workshop",
                subtitle = "Step-by-step DIY projects: turn plastic bottles and glass jars into planters & crafts",
                icon = Icons.Default.AutoAwesome,
                color = Color(0xFF9C27B0),
                cardBg = cardBg,
                textColor = textColor,
                subtitleColor = subtitleColor,
                onClick = onNavigateToUpcycling
            )
        }

        // Safety & Ethics Notice
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2E7D32).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = "Safe AI",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Ethical On-Device Safety Active",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "100+ living-entity filters prevent people and pets from ever being mapped to waste categories.",
                            fontSize = 11.sp,
                            color = subtitleColor,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    cardBg: Color,
    textColor: Color,
    subtitleColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = textColor
            )
            Text(
                text = title,
                fontSize = 10.sp,
                color = subtitleColor,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun QuickActionTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    cardBg: Color,
    textColor: Color,
    subtitleColor: Color,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = subtitleColor,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
