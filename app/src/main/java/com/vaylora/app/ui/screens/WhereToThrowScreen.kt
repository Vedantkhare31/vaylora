package com.vaylora.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vaylora.app.rag.RuleEntity

@Composable
fun WhereToThrowScreen(
    rules: List<RuleEntity>,
    isDarkTheme: Boolean = true,
    onSelectUpcycling: (RuleEntity) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = remember(rules) {
        listOf("All") + rules.map { it.category }.distinct()
    }

    val filteredRules = remember(searchQuery, selectedCategory, rules) {
        rules.filter { rule ->
            val matchesCategory = selectedCategory == "All" || rule.category == selectedCategory
            val matchesQuery = searchQuery.isBlank() ||
                    rule.material.contains(searchQuery, ignoreCase = true) ||
                    rule.instruction.contains(searchQuery, ignoreCase = true) ||
                    rule.binType.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }

    val bgColor = if (isDarkTheme) Color(0xFF0C1410) else Color(0xFFF6FBF4)
    val cardBg = if (isDarkTheme) Color(0xFF14241B) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color(0xFF1B1B1B)
    val subtitleColor = if (isDarkTheme) Color(0xFF88A090) else Color(0xFF6B7280)
    val limeAccent = Color(0xFF8BC34A)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Title Header
        Text(
            text = "Where To Throw",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = textColor
        )
        Text(
            text = "Comprehensive local disposal & recycling guidelines",
            fontSize = 12.sp,
            color = subtitleColor
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search materials, bins, or keywords...", fontSize = 13.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = limeAccent
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = subtitleColor
                        )
                    }
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = cardBg,
                unfocusedContainerColor = cardBg,
                focusedBorderColor = limeAccent,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = textColor,
                unfocusedTextColor = textColor
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Category Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->
                val isSelected = category == selectedCategory
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategory = category },
                    label = {
                        Text(
                            text = category,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = limeAccent,
                        selectedLabelColor = Color(0xFF0C1410),
                        containerColor = cardBg,
                        labelColor = textColor
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Rule Item List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredRules) { rule ->
                RuleCard(
                    rule = rule,
                    cardBg = cardBg,
                    textColor = textColor,
                    subtitleColor = subtitleColor,
                    onUpcycle = { onSelectUpcycling(rule) }
                )
            }

            if (filteredRules.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.SearchOff,
                                contentDescription = null,
                                tint = subtitleColor,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No rules match \"$searchQuery\"",
                                color = subtitleColor,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RuleCard(
    rule: RuleEntity,
    cardBg: Color,
    textColor: Color,
    subtitleColor: Color,
    onUpcycle: () -> Unit
) {
    val binColor = try {
        Color(android.graphics.Color.parseColor(rule.binColorHex))
    } catch (_: Exception) {
        Color(0xFF2196F3)
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Material Title + Bin Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = rule.material.replaceFirstChar { it.uppercase() },
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = binColor
                ) {
                    Text(
                        text = rule.binType,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Instruction
            Text(
                text = rule.instruction,
                fontSize = 13.sp,
                color = subtitleColor,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Preparation Step Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CheckCircleOutline,
                    contentDescription = null,
                    tint = Color(0xFF8BC34A),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = rule.preparationSteps,
                    fontSize = 11.sp,
                    color = subtitleColor,
                    maxLines = 2
                )
            }

            // Upcycling Trigger Button (if available)
            if (rule.isDisposable && rule.upcyclingIdea.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onUpcycle),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFF009688),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Upcycling Idea Available",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF009688)
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color(0xFF009688),
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}
