package com.example.loginproject.ui.splash

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginproject.data.local.TokenManager
import com.example.loginproject.repository.ApiRepository
import com.example.loginproject.utilities.isNetworkAvailable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val app: Application,
    private val repository: ApiRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _isConnected = MutableStateFlow<Boolean?>(null)
    val isConnected: StateFlow<Boolean?> = _isConnected

    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination: StateFlow<String?> = _startDestination

    init {
        checkInternetWithDelay()
    }

    fun checkInternetWithDelay() {
        viewModelScope.launch {
            delay(1500.milliseconds)
            val connected = isNetworkAvailable(app)
            _isConnected.value = connected

            if (connected) {
                checkSession()
            }
        }
    }

    fun checkInternet() {
        viewModelScope.launch {
            val connected = isNetworkAvailable(app)
            _isConnected.value = connected

            if (connected) {
                checkSession()
            }
        }
    }

    private fun checkSession() {
        viewModelScope.launch {
            val refreshToken = tokenManager.refreshTokenFlow.first()

            if (refreshToken.isNullOrEmpty()) {
                _startDestination.value = "login"
                return@launch
            }

            try {
                val response = repository.refreshToken(refreshToken)

                if (response.isSuccessful) {
                    val body = response.body()
                    body?.access?.let { tokenManager.updateAccessTokenBlocking(it) }
                    body?.refresh?.let { tokenManager.updateRefreshTokenBlocking(it) }
                    _startDestination.value = "home"
                } else {
                    tokenManager.clearSession()
                    _startDestination.value = "login"
                }
            } catch (e: Exception) {
                _startDestination.value = "login"
            }
        }
    }
}
