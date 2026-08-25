package com.example.loginproject.ui.address

import com.example.loginproject.data.remote.request.AddAddressRequest


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginproject.repository.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AddAddressUiState {
    object Idle : AddAddressUiState()
    object Loading : AddAddressUiState()
    object Success : AddAddressUiState()
    data class Error(val message: String) : AddAddressUiState()
}

@HiltViewModel
class AddAddressViewModel @Inject constructor(
    private val repository: ApiRepository
) : ViewModel() {

    var title by mutableStateOf(""); private set
    var province by mutableStateOf(""); private set
    var city by mutableStateOf(""); private set
    var address by mutableStateOf(""); private set
    var postCode by mutableStateOf(""); private set

    fun onTitleChange(v: String) { title = v }
    fun onProvinceChange(v: String) { province = v }
    fun onCityChange(v: String) { city = v }
    fun onAddressChange(v: String) { address = v }
    fun onPostCodeChange(v: String) { postCode = v }

    private val _uiState = MutableStateFlow<AddAddressUiState>(AddAddressUiState.Idle)
    val uiState = _uiState.asStateFlow()

    val isFormValid: Boolean
        get() = title.isNotBlank() && province.isNotBlank() && city.isNotBlank() &&
                address.isNotBlank() && postCode.isNotBlank()

    fun submit() {
        if (!isFormValid) return
        viewModelScope.launch {
            _uiState.value = AddAddressUiState.Loading
            try {
                val response = repository.addAddress(
                    AddAddressRequest(title, province, city, address, postCode)
                )
                _uiState.value = if (response.isSuccessful)
                    AddAddressUiState.Success
                else
                    AddAddressUiState.Error("ثبت آدرس ناموفق بود")
            } catch (e: Exception) {
                _uiState.value = AddAddressUiState.Error("خطا در ارتباط با سرور")
            }
        }
    }
}