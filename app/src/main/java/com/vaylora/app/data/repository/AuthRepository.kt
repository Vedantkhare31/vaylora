package com.vaylora.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.vaylora.app.core.result.VayloraResult
import kotlinx.coroutines.tasks.await

/**
 * Repository for Firebase Authentication operations.
 * Handles sign-in, sign-up, password reset, and sign-out.
 */
class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    val currentUser: FirebaseUser? get() = auth.currentUser

    val isLoggedIn: Boolean get() = auth.currentUser != null

    /**
     * Sign in with email and password.
     */
    suspend fun signIn(email: String, password: String): VayloraResult<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email.trim(), password).await()
            val user = result.user ?: return VayloraResult.Error("Sign-in failed: no user returned")
            VayloraResult.Success(user)
        } catch (e: Exception) {
            VayloraResult.Error(friendlyAuthError(e), e)
        }
    }

    /**
     * Create a new account with email and password.
     */
    suspend fun signUp(email: String, password: String): VayloraResult<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email.trim(), password).await()
            val user = result.user ?: return VayloraResult.Error("Sign-up failed: no user returned")
            VayloraResult.Success(user)
        } catch (e: Exception) {
            VayloraResult.Error(friendlyAuthError(e), e)
        }
    }

    /**
     * Send a password reset email.
     */
    suspend fun sendPasswordResetEmail(email: String): VayloraResult<Unit> {
        return try {
            auth.sendPasswordResetEmail(email.trim()).await()
            VayloraResult.Success(Unit)
        } catch (e: Exception) {
            VayloraResult.Error(friendlyAuthError(e), e)
        }
    }

    /**
     * Sign out the current user.
     */
    fun signOut() {
        auth.signOut()
    }

    private fun friendlyAuthError(e: Exception): String {
        return when {
            e.message?.contains("email address is already in use") == true ->
                "An account with this email already exists. Please sign in instead."
            e.message?.contains("no user record") == true ->
                "No account found with this email. Please sign up first."
            e.message?.contains("password is invalid") == true ->
                "Incorrect password. Please try again."
            e.message?.contains("network error") == true ->
                "Network error. Please check your internet connection."
            e.message?.contains("too-many-requests") == true ->
                "Too many attempts. Please wait a moment and try again."
            else -> e.message ?: "An unknown error occurred"
        }
    }
}
