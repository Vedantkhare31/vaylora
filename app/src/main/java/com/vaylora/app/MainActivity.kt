package com.vaylora.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vaylora.app.data.local.prefs.AppPreferences
import com.vaylora.app.rag.RAGEngine
import com.vaylora.app.ui.auth.AuthViewModel
import com.vaylora.app.ui.auth.ForgotPasswordScreen
import com.vaylora.app.ui.auth.LoginScreen
import com.vaylora.app.ui.auth.SignUpScreen
import com.vaylora.app.ui.main.MainApp
import com.vaylora.app.ui.theme.VayloraTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

/** Navigation route constants used across the app. */
object Routes {
    const val LOGIN = "login"
    const val SIGN_UP = "sign_up"
    const val FORGOT_PASSWORD = "forgot_password"
    const val MAIN = "main"
}

class MainActivity : ComponentActivity() {

    private lateinit var ragEngine: RAGEngine
    private lateinit var appPreferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        ragEngine = RAGEngine(this)
        appPreferences = AppPreferences(this)

        // Read initial theme synchronously to avoid flash
        val initialDark = runBlocking { appPreferences.isDarkTheme.first() }

        setContent {
            val isDarkTheme by appPreferences.isDarkTheme.collectAsStateWithLifecycle(initialValue = initialDark)

            VayloraTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = if (isDarkTheme) Color(0xFF0C1410) else Color(0xFFF1F8E9)
                ) {
                    VayloraRoot(
                        ragEngine = ragEngine,
                        appPreferences = appPreferences,
                        isDarkTheme = isDarkTheme
                    )
                }
            }
        }
    }
}

@Composable
private fun VayloraRoot(
    ragEngine: RAGEngine,
    appPreferences: AppPreferences,
    isDarkTheme: Boolean
) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()

    // Determine start destination based on login state
    val startDestination = if (authState.isLoggedIn) Routes.MAIN else Routes.LOGIN

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Routes.LOGIN) {
            LoginScreen(
                uiState = authState,
                onSignIn = { email, password -> authViewModel.signIn(email, password) },
                onNavigateToSignUp = { navController.navigate(Routes.SIGN_UP) },
                onNavigateToForgotPassword = { navController.navigate(Routes.FORGOT_PASSWORD) },
                onClearError = { authViewModel.clearError() }
            )
        }

        composable(Routes.SIGN_UP) {
            SignUpScreen(
                uiState = authState,
                onSignUp = { name, email, password -> authViewModel.signUp(name, email, password) },
                onNavigateToSignIn = { navController.popBackStack() },
                onClearError = { authViewModel.clearError() }
            )
        }

        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                uiState = authState,
                onSendReset = { email -> authViewModel.sendPasswordReset(email) },
                onNavigateBack = { navController.popBackStack() },
                onClearError = { authViewModel.clearError() }
            )
        }

        composable(Routes.MAIN) {
            MainApp(
                ragEngine = ragEngine,
                appPreferences = appPreferences,
                isDarkTheme = isDarkTheme,
                currentUser = authState.currentUser,
                userProfile = authState.userProfile,
                onSignOut = {
                    authViewModel.signOut()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.MAIN) { inclusive = true }
                    }
                }
            )
        }
    }

    // Auto-navigate after login/signup
    LaunchedEffect(authState.isLoggedIn) {
        if (authState.isLoggedIn && navController.currentDestination?.route != Routes.MAIN) {
            navController.navigate(Routes.MAIN) {
                popUpTo(0) { inclusive = true }
            }
        } else if (!authState.isLoggedIn && navController.currentDestination?.route == Routes.MAIN) {
            navController.navigate(Routes.LOGIN) {
                popUpTo(0) { inclusive = true }
            }
        }
    }
}
