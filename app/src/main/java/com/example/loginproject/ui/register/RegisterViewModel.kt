package com.example.loginproject.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginproject.data.local.TokenManager
import com.example.loginproject.data.remote.api.ApiService
import com.example.loginproject.data.remote.request.RegisterRequest
import com.example.loginproject.di.RepositoryModule
import com.example.loginproject.repository.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val tokenManager: TokenManager
) : ViewModel()
{

    private val _fullName = MutableStateFlow("")
    val fullName: StateFlow<String> = _fullName

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber = _phoneNumber

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

    fun onPhoneNumberChanged(value:String){
        _phoneNumber.value=value
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

        if(validationError != null){

            _errorMessage.value = validationError
            return

        }


        viewModelScope.launch {


            try {

                _isLoading.value=true


                val response = apiRepository.register(

                    RegisterRequest(

                        fullName = _fullName.value,

                        phoneNumber = _phoneNumber.value,

                        password = _password.value

                    )

                )


                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.access != null && body.refresh != null) {
                        tokenManager.saveSession(
                            accessToken = body.access,
                            refreshToken = body.refresh,
                            phoneNumber = body.data?.phone_number ?: _phoneNumber.value.trim()
                        )
                        body.data?.full_name?.let { tokenManager.saveFullName(it) }
                    }
                    _isRegisterSuccess.value = true
                } else {
                    _errorMessage.value = "ثبت‌نام انجام نشد"
                }


            }catch(e:Exception){

                _errorMessage.value =
                    e.message

            }
            finally {

                _isLoading.value=false

            }

        }

    }

    private fun validate(): String? {
        return when {
            _fullName.value.isBlank() -> "لطفاً نام کامل رو وارد کن"
            _phoneNumber.value.isBlank() -> "لطفاً تلفن رو وارد کن"
            _password.value.length < 6 -> "رمز عبور باید حداقل ۶ کاراکتر باشه"
            _password.value != _confirmPassword.value -> "رمز عبور و تکرار اون یکی نیستن"
            else -> null
        }
    }
}
