package com.example.loginproject.data.remote.response

data class CartCheckoutResponse(
    val addresses: List<Any>, // فعلاً بک‌اند چیزی توش نمی‌فرسته؛ بعداً مدل آدرس واقعی جایگزین می‌شه
    val items: List<CartCheckoutItemDto>,
    val items_price: Double,
    val shipping_price: Double,
    val total_price: Double
)

data class CartCheckoutItemDto(
    val id: Int,
    val product: String,
    val color: String,
    val price: Double,
    val stock: Int,
    val quantity: Int
)
