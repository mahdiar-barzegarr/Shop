package com.example.loginproject.ui.password


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginproject.data.local.TokenManager
import com.example.loginproject.repository.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChangePasswordUiState(
    val oldPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val repository: ApiRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChangePasswordUiState())
    val uiState: StateFlow<ChangePasswordUiState> = _uiState.asStateFlow()

    private val _successEvent = MutableSharedFlow<Unit>()
    val successEvent = _successEvent.asSharedFlow()

    fun onOldPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(oldPassword = value, errorMessage = null)
    }

    fun onNewPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(newPassword = value, errorMessage = null)
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = value, errorMessage = null)
    }

    fun submit() {
        val state = _uiState.value

        if (state.oldPassword.isBlank() || state.newPassword.isBlank() || state.confirmPassword.isBlank()) {
            _uiState.value = state.copy(errorMessage = "لطفاً همه فیلدها را پر کنید")
            return
        }
        if (state.newPassword != state.confirmPassword) {
            _uiState.value = state.copy(errorMessage = "رمز عبور جدید و تکرار آن یکسان نیستند")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, errorMessage = null)
            try {
                val response = repository.changePassword(
                    oldPassword = state.oldPassword,
                    newPassword = state.newPassword,
                    confirmPassword = state.confirmPassword
                )
                if (response.isSuccessful) {
                    tokenManager.clearSession()
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    _successEvent.emit(Unit)
                }else {
                    val errorMsg = response.errorBody()?.string()
                    android.util.Log.e("ChangePassword", "Error: ${response.code()} - $errorMsg")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "خطا: ${response.code()} - $errorMsg"
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("ChangePassword", "Exception", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "خطا در ارتباط با سرور: ${e.message}"
                )
            }
        }
    }
}