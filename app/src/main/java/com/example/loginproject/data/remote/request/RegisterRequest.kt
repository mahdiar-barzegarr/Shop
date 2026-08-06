package com.example.loginproject.data.remote.request

import com.google.gson.annotations.SerializedName

data class RegisterRequest(

    @SerializedName("full_name")
    val fullName: String,


    @SerializedName("phone_number")
    val phoneNumber: String,


    @SerializedName("email")
    val email: String,


    @SerializedName("password")
    val password: String

)