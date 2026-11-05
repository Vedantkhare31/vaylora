package com.vaylora.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vaylora.app.data.repository.UserRepository
import com.vaylora.app.domain.model.ScanRecord
import com.vaylora.app.domain.model.UserProfile
import com.vaylora.app.core.result.VayloraResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = false,
    val profile: UserProfile? = null,
    val scanHistory: List<ScanRecord> = emptyList(),
    val errorMessage: String? = null
)

class ProfileViewModel : ViewModel() {
    private val userRepository = UserRepository()
    private val _uiState = MutableStateFlow(ProfileUiState(isLoading = true))
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun loadProfile(uid: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val profileResult = userRepository.getUserProfile(uid)
            val historyResult = userRepository.getScanHistory(uid, 20)
            val profile = if (profileResult is VayloraResult.Success) profileResult.data else null
            val history = if (historyResult is VayloraResult.Success) historyResult.data else emptyList()
            _uiState.value = ProfileUiState(
                isLoading = false,
                profile = profile,
                scanHistory = history
            )
        }
    }
}
