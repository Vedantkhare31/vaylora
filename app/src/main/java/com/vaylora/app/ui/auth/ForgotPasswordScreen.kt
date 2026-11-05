package com.vaylora.app.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    uiState: AuthUiState,
    onSendReset: (email: String) -> Unit,
    onNavigateBack: () -> Unit,
    onClearError: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    var email by remember { mutableStateOf("") }

    // Derive success state from successMessage being non-null
    val emailSentSuccessfully = uiState.successMessage != null

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0A1A0F), Color(0xFF0C1410), Color(0xFF0F1E14))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Top bar with back arrow
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF8BC34A)
                    )
                }
            }

            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Icon
                Text(
                    text = if (emailSentSuccessfully) "✅" else "🔑",
                    fontSize = 52.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Title
                Text(
                    text = "Reset Password",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Subtitle
                Text(
                    text = if (emailSentSuccessfully)
                        "Check your inbox for the reset link."
                    else
                        "Enter your email and we'll send you a reset link.",
                    fontSize = 14.sp,
                    color = Color(0xFF8BC34A),
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Spacer(modifier = Modifier.height(36.dp))

                // Success banner
                if (emailSentSuccessfully) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A3A1A)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "✔ Email Sent!",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF8BC34A)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = uiState.successMessage ?: "",
                                fontSize = 13.sp,
                                color = Color(0xFFB0D890),
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    // Back to Sign In button (shown after success)
                    OutlinedButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF8BC34A)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF8BC34A)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            "Back to Sign In",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                } else {
                    // Error banner
                    if (uiState.errorMessage != null) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF4A1515)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("⚠️ ", fontSize = 16.sp)
                                Text(
                                    text = uiState.errorMessage,
                                    color = Color(0xFFFF6B6B),
                                    fontSize = 13.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                TextButton(onClick = onClearError) {
                                    Text("✕", color = Color(0xFFFF6B6B))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Email field
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; onClearError() },
                        label = { Text("Email Address") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = null,
                                tint = Color(0xFF8BC34A)
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = {
                            focusManager.clearFocus()
                            if (!uiState.isLoading) onSendReset(email)
                        }),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF8BC34A),
                            unfocusedBorderColor = Color(0xFF2E4A32),
                            focusedLabelColor = Color(0xFF8BC34A),
                            cursorColor = Color(0xFF8BC34A),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    // Send Reset Email button
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            onSendReset(email)
                        },
                        enabled = !uiState.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8BC34A),
                            disabledContainerColor = Color(0xFF4A6B2A)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                color = Color(0xFF0C1410),
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Text(
                                "Send Reset Email",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0C1410)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Back to sign in link
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Remember your password?", color = Color.Gray, fontSize = 14.sp)
                        TextButton(onClick = onNavigateBack) {
                            Text(
                                "Sign In",
                                color = Color(0xFF8BC34A),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
