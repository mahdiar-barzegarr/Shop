package com.example.loginproject.models

data class RegisterResponseModel(
    val data: RegisteredUserData?,
    val access: String?,
    val refresh: String?
)

data class RegisteredUserData(
    val full_name: String?,
    val phone_number: String?
)
