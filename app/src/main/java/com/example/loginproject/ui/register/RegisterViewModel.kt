package com.example.loginproject.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject



sealed class RegisterResult {
    object Success : RegisterResult()
    data class Error(val message: String) : RegisterResult()
}

interface RegisterRepository {
    suspend fun register(
        fullName: String,
        username: String,
        email: String,
        password: String
    ): RegisterResult
}

/*
 * ==========================================================================
 * ViewModel
 * ==========================================================================
 */

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: RegisterRepository
) : ViewModel() {

    private val _fullName = MutableStateFlow("")
    val fullName: StateFlow<String> = _fullName

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isRegisterSuccess = MutableStateFlow(false)
    val isRegisterSuccess: StateFlow<Boolean> = _isRegisterSuccess

    fun onFullNameChanged(value: String) {
        _fullName.value = value
        _errorMessage.value = null
    }

    fun onUsernameChanged(value: String) {
        _username.value = value
        _errorMessage.value = null
    }

    fun onEmailChanged(value: String) {
        _email.value = value
        _errorMessage.value = null
    }

    fun onPasswordChanged(value: String) {
        _password.value = value
        _errorMessage.value = null
    }

    fun onConfirmPasswordChanged(value: String) {
        _confirmPassword.value = value
        _errorMessage.value = null
    }

    fun onCreateAccountClick() {
        val validationError = validate()
        if (validationError != null) {
            _errorMessage.value = validationError
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            when (val result = repository.register(
                fullName = _fullName.value.trim(),
                username = _username.value.trim(),
                email = _email.value.trim(),
                password = _password.value
            )) {
                is RegisterResult.Success -> {
                    _isLoading.value = false
                    _isRegisterSuccess.value = true
                }
                is RegisterResult.Error -> {
                    _isLoading.value = false
                    _errorMessage.value = result.message
                }
            }
        }
    }

    private fun validate(): String? {
        return when {
            _fullName.value.isBlank() -> "لطفاً نام کامل رو وارد کن"
            _username.value.isBlank() -> "لطفاً یوزرنیم رو وارد کن"
            _email.value.isBlank() -> "لطفاً ایمیل رو وارد کن"
            _password.value.length < 6 -> "رمز عبور باید حداقل ۶ کاراکتر باشه"
            _password.value != _confirmPassword.value -> "رمز عبور و تکرار اون یکی نیستن"
            else -> null
        }
    }
}
