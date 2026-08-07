package com.example.loginproject.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginproject.data.local.TokenManager
import com.example.loginproject.repository.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ApiRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            // اول از کش محلی بخون تا فوری چیزی نشون بدی (بدون منتظر شبکه موندن)
            _userName.value = tokenManager.fullNameFlow.first()

            // بعد از سرور تازه بگیر و کش رو آپدیت کن
            try {
                val response = repository.getProfile()
                if (response.isSuccessful) {
                    val name = response.body()?.fullName
                    if (!name.isNullOrBlank()) {
                        _userName.value = name
                        tokenManager.saveFullName(name)
                    }
                }
            } catch (e: Exception) {
                // بی‌سروصدا نادیده بگیر — همون مقدار کش‌شده روی صفحه می‌مونه
            }
        }
    }
}
