package com.example.loginproject.data.remote.request

data class AddCartItemRequest(
    val variant_id: Int,
    val quantity: Int
)

data class CartItemDto(
    val id: Int,
    val product: String,
    val color: String,
    val price: Long,
    val stock: Int,
    val quantity: Int
)

data class UserCartResponse(
    val id: Int,
    val items: List<CartItemDto>,
    val total_price: Double,
    val items_count: Int
)
