package com.example.loginproject.data.remote.request

import com.google.gson.annotations.SerializedName

data class AddAddressRequest(
    val title: String,
    val province: String,
    val city: String,
    val address: String,
    @SerializedName("post_code")
    val postCode: String
)
