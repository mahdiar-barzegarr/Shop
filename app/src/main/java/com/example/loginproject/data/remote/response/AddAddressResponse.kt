package com.example.loginproject.data.remote.response

import com.google.gson.annotations.SerializedName

data class AddAddressResponse(
    val title: String? = null,
    val province: String? = null,
    val city: String? = null,
    val address: String? = null,
    @SerializedName("post_code")
    val postCode: String? = null
)
