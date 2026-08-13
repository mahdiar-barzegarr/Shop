package com.example.loginproject.data.remote.response

data class ProductDetailResponse(
    val id: Int,
    val colors: List<ColorDto>,
    val images: List<ProductImageDto>,
    val name: String,
    val description: String,
    val price: String,
    val sell_count: Int,
    val is_available: Boolean,
    val created_at: String,
    val updated_at: String,
    val category: Int
)

data class ColorDto(
    val id: Int,
    val stock: Int,
    val product: Int,
    val name: String,
    val code: String
)

data class ProductImageDto(
    val id: Int,
    val image: String
)

data class AddCartItemResponse(
    val detail: String
)
