package com.example.loginproject.ui.cartPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginproject.data.remote.ApiConfig
import com.example.loginproject.data.remote.api.ApiService
import com.example.loginproject.data.remote.response.CartUiState
import com.example.loginproject.repository.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CartItemUi(
    val cartItemId: Int,
    val productId: Int?,
    val colorName: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String?
)

data class CartUiState(
    val isLoading: Boolean = true,
    val items: List<CartItemUi> = emptyList(),
    val subtotal: Double = 0.0,
    val total: Double = 0.0,
    val error: String? = null
)

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repository: ApiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<com.example.loginproject.ui.cartPage.CartUiState> = _uiState.asStateFlow()

    init { loadCart() }

    fun loadCart() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val cartResponse = repository.getUserCart()
                val cart = cartResponse.body()
                if (!cartResponse.isSuccessful || cart == null) {
                    _uiState.update { it.copy(isLoading = false, error = "خطا در دریافت سبد خرید") }
                    return@launch
                }

                // برای عکس و productId، با لیست کل محصولات match می‌کنیم روی نام
                val allProducts = repository.getProducts().body()?.results.orEmpty()

                val items = cart.items.map { item ->
                    val matched = allProducts.find { it.name == item.product }
                    CartItemUi(
                        cartItemId = item.id,
                        productId = matched?.id,
                        colorName = item.color,
                        name = item.product,
                        price = item.price.toDouble() ?: 0.0,
                        quantity = item.quantity,
                        imageUrl = matched?.image?.let { ApiConfig.SERVER_HOST + it }
                    )
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        items = items,
                        subtotal = cart.total_price.toDouble() ?: 0.0,
                        total = cart.total_price.toDouble() ?: 0.0
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }
        }
    }

    fun deleteItem(cartItemId: Int) {
        viewModelScope.launch {
            if (repository.deleteCartItem(cartItemId).isSuccessful) loadCart()
        }
    }

    fun increaseQuantity(item: CartItemUi) {
        changeQuantity(item, item.quantity + 1)
    }

    fun decreaseQuantity(item: CartItemUi) {
        if (item.quantity <= 1) {
            deleteItem(item.cartItemId)
        } else {
            changeQuantity(item, item.quantity - 1)
        }
    }

    private fun changeQuantity(item: CartItemUi, newQuantity: Int) {
        viewModelScope.launch {
            val variantId = resolveVariantId(item) ?: return@launch
            repository.deleteCartItem(item.cartItemId)
            repository.addItemToCart(variantId, newQuantity)
            loadCart()
        }
    }

    private suspend fun resolveVariantId(item: CartItemUi): Int? {
        val productId = item.productId ?: return null
        val colors = repository.getProductDetail(productId).body()?.colors.orEmpty()
        return colors.find { it.name == item.colorName }?.id
    }
}