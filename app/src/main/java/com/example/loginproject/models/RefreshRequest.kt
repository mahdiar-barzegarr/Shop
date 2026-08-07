package com.example.loginproject.models

data class RefreshRequest(
    val refresh: String,
    val access: Int = 7443
)
