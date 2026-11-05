package com.vaylora.app.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseUser
import com.vaylora.app.data.local.prefs.AppPreferences
import com.vaylora.app.domain.model.UserProfile
import com.vaylora.app.rag.RAGEngine
import com.vaylora.app.rag.RuleEntity
import com.vaylora.app.ui.profile.ProfileViewModel
import com.vaylora.app.ui.profile.ProfileScreen
import com.vaylora.app.ui.screens.CameraScreen
import com.vaylora.app.ui.screens.DashboardScreen
import com.vaylora.app.ui.screens.UpcyclingScreen
import com.vaylora.app.ui.screens.WhereToThrowScreen
import com.vaylora.app.ui.settings.SettingsScreen
import com.vaylora.app.ui.settings.SettingsViewModel

/** Bottom navigation tabs for the main authenticated shell. */
enum class MainTab(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    Home("Home", Icons.Filled.Home, Icons.Outlined.Home),
    Camera("Scan", Icons.Filled.CameraAlt, Icons.Outlined.CameraAlt),
    WhereTo("Where?", Icons.Filled.Delete, Icons.Outlined.Delete),
    Upcycle("Upcycle", Icons.Filled.Eco, Icons.Outlined.Eco),
    Profile("Profile", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun MainApp(
    ragEngine: RAGEngine,
    appPreferences: AppPreferences,
    isDarkTheme: Boolean,
    currentUser: FirebaseUser?,
    userProfile: UserProfile?,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(MainTab.Home) }
    var showSettings by remember { mutableStateOf(false) }
    var rules by remember { mutableStateOf<List<RuleEntity>>(emptyList()) }

    val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory(context))
    val settingsState by settingsViewModel.uiState.collectAsStateWithLifecycle()

    val profileViewModel: ProfileViewModel = viewModel()

    LaunchedEffect(Unit) {
        ragEngine.initialize()
        rules = ragEngine.getAllRules()
    }

    // Load profile data when user is available
    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let { uid -> profileViewModel.loadProfile(uid) }
    }

    val bgColor = if (isDarkTheme) Color(0xFF0C1410) else Color(0xFFF1F8E9)
    val navBarColor = if (isDarkTheme) Color(0xFF0F1C15) else Color(0xFFE8F5E9)
    val accentColor = Color(0xFF8BC34A)

    // Settings overlay
    if (showSettings) {
        SettingsScreen(
            settingsViewModel = settingsViewModel,
            isDarkTheme = isDarkTheme,
            onNavigateBack = { showSettings = false },
            onSignOut = onSignOut
        )
        return
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = navBarColor,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            ) {
                MainTab.entries.forEach { tab ->
                    val isSelected = selectedTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.label
                            )
                        },
                        label = { Text(text = tab.label, fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF081C10),
                            selectedTextColor = accentColor,
                            indicatorColor = accentColor,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        },
        containerColor = bgColor
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                MainTab.Home -> DashboardScreen(
                    userProfile = userProfile,
                    isDarkTheme = isDarkTheme,
                    onNavigateToCamera = { selectedTab = MainTab.Camera },
                    onNavigateToWhereToThrow = { selectedTab = MainTab.WhereTo },
                    onNavigateToUpcycling = { selectedTab = MainTab.Upcycle },
                    onNavigateToSettings = { showSettings = true }
                )

                MainTab.Camera -> CameraScreen(
                    ragEngine = ragEngine,
                    currentUser = currentUser,
                    isDarkTheme = isDarkTheme,
                    onNavigateBack = { selectedTab = MainTab.Home },
                    onNavigateToUpcycling = { selectedTab = MainTab.Upcycle }
                )

                MainTab.WhereTo -> WhereToThrowScreen(
                    rules = rules,
                    isDarkTheme = isDarkTheme,
                    onSelectUpcycling = { selectedTab = MainTab.Upcycle }
                )

                MainTab.Upcycle -> UpcyclingScreen(
                    rules = rules,
                    isDarkTheme = isDarkTheme
                )

                MainTab.Profile -> ProfileScreen(
                    viewModel = profileViewModel,
                    currentUser = currentUser,
                    isDarkTheme = isDarkTheme,
                    onNavigateToSettings = { showSettings = true }
                )
            }
        }
    }
}
