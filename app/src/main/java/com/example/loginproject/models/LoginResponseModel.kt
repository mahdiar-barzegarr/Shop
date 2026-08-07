package com.example.loginproject.models

import com.google.gson.annotations.SerializedName

data class LoginResponseModel(
    val full_name: String? = null,
    val access: String? = null,
    val refresh: String? = null
)
