package com.example.loginproject.data.remote.request

import com.google.gson.annotations.SerializedName

data class ChangePasswordRequest(
    @SerializedName("old_password") val oldPassword: String,
    @SerializedName("password") val password: String,
    @SerializedName("confirm_password") val confirmPassword: String
)
