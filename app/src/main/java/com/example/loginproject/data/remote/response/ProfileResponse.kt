package com.example.loginproject.data.remote.response

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    @SerializedName("full_name")
    val fullName: String? = null,
    @SerializedName("phone_number")
    val phoneNumber: String? = null,
    val email: String? = null
)
