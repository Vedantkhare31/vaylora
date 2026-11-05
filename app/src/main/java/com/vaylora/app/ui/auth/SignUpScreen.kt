package com.vaylora.app.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SignUpScreen(
    uiState: AuthUiState,
    onSignUp: (name: String, email: String, password: String) -> Unit,
    onNavigateToSignIn: () -> Unit,
    onClearError: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Local validation for confirm password mismatch
    val passwordMismatch = confirmPassword.isNotEmpty() && password != confirmPassword

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
                .verticalScroll(scrollState)
                .padding(horizontal = 28.dp)
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo + Title
            Text(
                text = "🌿",
                fontSize = 48.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "VAYLORA",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 3.sp
            )
            Text(
                text = "Create your account",
                fontSize = 14.sp,
                color = Color(0xFF8BC34A),
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(32.dp))

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

            // Name field
            OutlinedTextField(
                value = name,
                onValueChange = { name = it; onClearError() },
                label = { Text("Full Name") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = Color(0xFF8BC34A)
                    )
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
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
            Spacer(modifier = Modifier.height(14.dp))

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
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
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
            Spacer(modifier = Modifier.height(14.dp))

            // Password field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; onClearError() },
                label = { Text("Password") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color(0xFF8BC34A)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = Color(0xFF8BC34A)
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
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
            Spacer(modifier = Modifier.height(14.dp))

            // Confirm Password field
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = if (passwordMismatch) Color(0xFFFF6B6B) else Color(0xFF8BC34A)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                            tint = if (passwordMismatch) Color(0xFFFF6B6B) else Color(0xFF8BC34A)
                        )
                    }
                },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = passwordMismatch,
                supportingText = if (passwordMismatch) {
                    { Text("Passwords do not match", color = Color(0xFFFF6B6B), fontSize = 12.sp) }
                } else null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                    if (!uiState.isLoading && !passwordMismatch) {
                        onSignUp(name, email, password)
                    }
                }),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (passwordMismatch) Color(0xFFFF6B6B) else Color(0xFF8BC34A),
                    unfocusedBorderColor = if (passwordMismatch) Color(0xFFFF6B6B) else Color(0xFF2E4A32),
                    focusedLabelColor = if (passwordMismatch) Color(0xFFFF6B6B) else Color(0xFF8BC34A),
                    cursorColor = Color(0xFF8BC34A),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    errorBorderColor = Color(0xFFFF6B6B),
                    errorLabelColor = Color(0xFFFF6B6B)
                ),
                shape = RoundedCornerShape(14.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Create Account button
            Button(
                onClick = {
                    focusManager.clearFocus()
                    if (!passwordMismatch) {
                        onSignUp(name, email, password)
                    }
                },
                enabled = !uiState.isLoading && !passwordMismatch,
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
                        "Create Account",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0C1410)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = Color(0xFF2E4A32))
            Spacer(modifier = Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Already have an account?", color = Color.Gray, fontSize = 14.sp)
                TextButton(onClick = onNavigateToSignIn) {
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
