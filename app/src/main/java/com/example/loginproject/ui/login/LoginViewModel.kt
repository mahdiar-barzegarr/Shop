package com.example.loginproject.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginproject.data.local.TokenManager
import com.example.loginproject.data.remote.request.LoginRequest
import com.example.loginproject.repository.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: ApiRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber

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

    // این چیزیه که اضافه شد؛ وقتی true بشه، Screen نویگیت می‌کنه
    private val _isLoginSuccess = MutableStateFlow(false)
    val isLoginSuccess: StateFlow<Boolean> = _isLoginSuccess

    fun onPhoneNumberChanged(value: String) {
        _phoneNumber.value = value
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
        val currentPhone = _phoneNumber.value.trim()
        val currentPassword = _password.value

        if (currentPhone.isEmpty() || currentPassword.isEmpty()) {
            _errorMessage.value = "لطفاً شماره موبایل و رمز عبور رو وارد کن"
            return
        }

        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val response = repository.login(
                    LoginRequest(
                        currentPhone,
                        currentPassword
                    )
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.access != null && body.refresh != null) {
                        tokenManager.saveSession(
                            accessToken = body.access,
                            refreshToken = body.refresh,
                            phoneNumber = currentPhone
                        )
                        body.full_name?.let { tokenManager.saveFullName(it) }
                    }
                    _isLoginSuccess.value = true
                } else {
                    _errorMessage.value = when (response.code()) {
                        400, 401 -> "شماره موبایل یا رمز عبور اشتباهه"
                        else -> "خطا در ورود (کد ${response.code()})"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "اتصال برقرار نشد. اینترنت یا آدرس سرور رو چک کن"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onForgotPasswordClick() {
        // TODO
    }

    fun onNeedHelpClick() {
        // TODO
    }
}

