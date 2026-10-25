package com.vaylora.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vaylora.app.rag.RAGEngine
import com.vaylora.app.rag.RuleEntity
import com.vaylora.app.ui.screens.CameraScreen
import com.vaylora.app.ui.screens.DashboardScreen
import com.vaylora.app.ui.screens.UpcyclingScreen
import com.vaylora.app.ui.screens.WhereToThrowScreen
import com.vaylora.app.ui.theme.VayloraTheme

enum class AppScreen(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    Dashboard("Home", Icons.Filled.Home, Icons.Outlined.Home),
    Camera("AI Scan", Icons.Filled.CameraAlt, Icons.Outlined.CameraAlt),
    WhereToThrow("Where to Throw", Icons.Filled.Delete, Icons.Outlined.Delete),
    Upcycling("Upcycle", Icons.Filled.Eco, Icons.Outlined.Eco)
}

class MainActivity : ComponentActivity() {

    private lateinit var ragEngine: RAGEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        ragEngine = RAGEngine(this)

        setContent {
            VayloraTheme {
                VayloraApp(ragEngine = ragEngine)
            }
        }
    }
}

@Composable
fun VayloraApp(ragEngine: RAGEngine) {
    var currentScreen by remember { mutableStateOf(AppScreen.Dashboard) }
    var rules by remember { mutableStateOf<List<RuleEntity>>(emptyList()) }

    LaunchedEffect(Unit) {
        ragEngine.initialize()
        rules = ragEngine.getAllRules()
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF0F1C15),
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            ) {
                AppScreen.entries.forEach { screen ->
                    val isSelected = currentScreen == screen
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentScreen = screen },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                contentDescription = screen.title
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF081C10),
                            selectedTextColor = Color(0xFF8BC34A),
                            indicatorColor = Color(0xFF8BC34A),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        },
        containerColor = Color(0xFF0C1410)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.Dashboard -> {
                    DashboardScreen(
                        onNavigateToCamera = { currentScreen = AppScreen.Camera },
                        onNavigateToWhereToThrow = { currentScreen = AppScreen.WhereToThrow },
                        onNavigateToUpcycling = { currentScreen = AppScreen.Upcycling }
                    )
                }

                AppScreen.Camera -> {
                    CameraScreen(
                        ragEngine = ragEngine,
                        onNavigateBack = { currentScreen = AppScreen.Dashboard },
                        onNavigateToUpcycling = { _ ->
                            currentScreen = AppScreen.Upcycling
                        }
                    )
                }

                AppScreen.WhereToThrow -> {
                    WhereToThrowScreen(
                        rules = rules,
                        onSelectUpcycling = { _ ->
                            currentScreen = AppScreen.Upcycling
                        }
                    )
                }

                AppScreen.Upcycling -> {
                    UpcyclingScreen(rules = rules)
                }
            }
        }
    }
}
