package com.example.loginproject.models

data class LoginResponseModel(
    val user_data: User,

    val refresh: String,

    val access: String
)
