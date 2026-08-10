package com.example.loginproject.data.remote.response

data class ProductListResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<ProductDto>
)

data class ProductDto(
    val id: Int,
    val category: CategoryDto,
    val name: String,
    val image: String,
    val is_available: Boolean,
    val price: String
)

data class CategoryDto(
    val id: Int,
    val name: String
)



