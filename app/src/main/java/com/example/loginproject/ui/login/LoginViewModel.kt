package com.example.loginproject.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(

) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _isPasswordVisible = MutableStateFlow(false)
    val isPasswordVisible: StateFlow<Boolean> = _isPasswordVisible

    private val _rememberMe = MutableStateFlow(false)
    val rememberMe: StateFlow<Boolean> = _rememberMe

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun onEmailChanged(value: String) {
        _email.value = value
        _errorMessage.value = null
    }

    fun onPasswordChanged(value: String) {
        _password.value = value
        _errorMessage.value = null
    }

    fun onTogglePasswordVisibility() {
        _isPasswordVisible.value = !_isPasswordVisible.value
    }

    fun onRememberMeChanged(value: Boolean) {
        _rememberMe.value = value
    }

    fun onLoginClick() {
        val currentEmail = _email.value.trim()
        val currentPassword = _password.value

        if (currentEmail.isEmpty() || currentPassword.isEmpty()) {
            _errorMessage.value = "لطفاً ایمیل و رمز عبور رو وارد کن"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            // TODO: اینجا فراخوانی واقعی لاگین (Repository/UseCase) رو بذار
            delay(1200) // شبیه‌سازی تأخیر شبکه — بعداً حذفش کن

            _isLoading.value = false

            // TODO: بر اساس نتیجه واقعی، یا onLoginSuccess رو صدا بزن یا خطا نشون بده
            // مثال:
            // val result = loginUseCase(currentEmail, currentPassword)
            // if (result.isSuccess) onLoginSuccess() else _errorMessage.value = result.errorText
        }
    }

    fun onForgotPasswordClick() {
        // TODO: نویگیت به صفحه فراموشی رمز عبور
    }

    fun onNeedHelpClick() {
        // TODO: نویگیت به صفحه راهنما/پشتیبانی
    }
}
