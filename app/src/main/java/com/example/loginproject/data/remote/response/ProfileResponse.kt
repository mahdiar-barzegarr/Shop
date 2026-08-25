package com.example.loginproject.data.remote.response

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    @SerializedName("phone_number")
    val phoneNumber: String? = null,
    @SerializedName("full_name")
    val fullName: String? = null,
)
