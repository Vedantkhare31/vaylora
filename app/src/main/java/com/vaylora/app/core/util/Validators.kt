package com.vaylora.app.core.util

object Validators {
    fun isValidEmail(email: String): Boolean =
        android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()

    fun isValidPassword(password: String): Boolean =
        password.length >= 8

    fun isValidName(name: String): Boolean =
        name.trim().length >= 2

    fun emailError(email: String): String? = when {
        email.isBlank() -> "Email is required"
        !isValidEmail(email) -> "Enter a valid email address"
        else -> null
    }

    fun passwordError(password: String): String? = when {
        password.isBlank() -> "Password is required"
        password.length < 8 -> "Password must be at least 8 characters"
        else -> null
    }

    fun nameError(name: String): String? = when {
        name.isBlank() -> "Name is required"
        name.trim().length < 2 -> "Name must be at least 2 characters"
        else -> null
    }
}
