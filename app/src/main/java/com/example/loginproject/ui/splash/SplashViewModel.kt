package com.example.loginproject.ui.splash

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginproject.utilities.isNetworkAvailable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val app: Application
) : ViewModel() {

    private val _isConnected = MutableStateFlow<Boolean?>(null)
    val isConnected: StateFlow<Boolean?> = _isConnected

    init {
        checkInternetWithDelay()
    }

    enum class SimState { LOADING, CONNECTED, OFFLINE }


    fun checkInternetWithDelay() {
        viewModelScope.launch {
            delay(1500.milliseconds)
            _isConnected.value = isNetworkAvailable(app)
        }
    }

    fun checkInternet() {
        _isConnected.value = isNetworkAvailable(app)
    }


}