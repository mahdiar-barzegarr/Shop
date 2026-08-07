package com.example.loginproject.data.remote.request

import com.google.gson.annotations.SerializedName

data class LoginRequest(

    @SerializedName("phone_number")
    val phoneNumber: String,

    val password: String

)
