package com.example.loginproject.data.remote.response

import com.example.loginproject.data.remote.request.UserCartResponse

data class UserCartResponse(
    val items: List<CartItemDto> = emptyList(),
    val subtotal: String = "0",
    val delivery: String = "Free",
    val total: String = "0"
)

data class CartItemDto(
    val id: Int,
    val variantId: Int,
    val title: String,
    val subtitle: String?,
    val price: String,
    val image: String,
    val quantity: Int
)

sealed interface CartUiState {
    object Loading : CartUiState
    data class Success(val cart: UserCartResponse) : CartUiState
    data class Error(val message: String) : CartUiState
}
