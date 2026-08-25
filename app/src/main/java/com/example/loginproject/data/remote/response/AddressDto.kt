package com.example.loginproject.data.remote.response

import com.google.gson.annotations.SerializedName

data class AddressDto(
    val id: Int,
    val title: String,
    val province: String,
    val city: String,
    val address: String,
    @SerializedName("post_code")
    val postCode: String,
    val user: Int? = null
)
