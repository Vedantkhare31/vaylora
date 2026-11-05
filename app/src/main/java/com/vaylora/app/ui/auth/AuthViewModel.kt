package com.vaylora.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.vaylora.app.core.result.VayloraResult
import com.vaylora.app.core.util.Validators
import com.vaylora.app.data.repository.AuthRepository
import com.vaylora.app.data.repository.UserRepository
import com.vaylora.app.domain.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val currentUser: FirebaseUser? = null,
    val userProfile: UserProfile? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class AuthViewModel : ViewModel() {

    private val authRepository = AuthRepository()
    private val userRepository = UserRepository()

    private val _uiState = MutableStateFlow(AuthUiState(isLoggedIn = authRepository.isLoggedIn, currentUser = authRepository.currentUser))
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signIn(email: String, password: String) {
        val emailErr = Validators.emailError(email)
        val passErr = Validators.passwordError(password)
        if (emailErr != null) { _uiState.value = _uiState.value.copy(errorMessage = emailErr); return }
        if (passErr != null) { _uiState.value = _uiState.value.copy(errorMessage = passErr); return }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val result = authRepository.signIn(email, password)) {
                is VayloraResult.Success -> {
                    val user = result.data
                    loadUserProfile(user)
                }
                is VayloraResult.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = result.message)
                }
                else -> {}
            }
        }
    }

    fun signUp(name: String, email: String, password: String) {
        val nameErr = Validators.nameError(name)
        val emailErr = Validators.emailError(email)
        val passErr = Validators.passwordError(password)
        if (nameErr != null) { _uiState.value = _uiState.value.copy(errorMessage = nameErr); return }
        if (emailErr != null) { _uiState.value = _uiState.value.copy(errorMessage = emailErr); return }
        if (passErr != null) { _uiState.value = _uiState.value.copy(errorMessage = passErr); return }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val result = authRepository.signUp(email, password)) {
                is VayloraResult.Success -> {
                    val user = result.data
                    val profile = UserProfile(uid = user.uid, name = name, email = email)
                    
                    // Don't block sign-up if Firestore is not enabled or fails
                    viewModelScope.launch {
                        try {
                            userRepository.saveUserProfile(profile)
                        } catch (_: Exception) {}
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        currentUser = user,
                        userProfile = profile
                    )
                }
                is VayloraResult.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = result.message)
                }
                else -> {}
            }
        }
    }

    fun sendPasswordReset(email: String) {
        val emailErr = Validators.emailError(email)
        if (emailErr != null) { _uiState.value = _uiState.value.copy(errorMessage = emailErr); return }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, successMessage = null)
            when (val result = authRepository.sendPasswordResetEmail(email)) {
                is VayloraResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Password reset email sent to $email. Check your inbox."
                    )
                }
                is VayloraResult.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = result.message)
                }
                else -> {}
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
        _uiState.value = AuthUiState(isLoggedIn = false, currentUser = null)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    private suspend fun loadUserProfile(user: FirebaseUser) {
        val defaultProfile = UserProfile(uid = user.uid, email = user.email ?: "", name = user.displayName ?: "")
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            isLoggedIn = true,
            currentUser = user,
            userProfile = defaultProfile
        )

        // Asynchronously fetch/sync cloud profile without blocking auth state
        viewModelScope.launch {
            try {
                when (val result = userRepository.getUserProfile(user.uid)) {
                    is VayloraResult.Success -> {
                        _uiState.value = _uiState.value.copy(userProfile = result.data)
                    }
                    is VayloraResult.Error -> {
                        userRepository.saveUserProfile(defaultProfile)
                    }
                    else -> {}
                }
            } catch (_: Exception) {}
        }
    }
}
