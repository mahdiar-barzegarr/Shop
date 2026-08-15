package com.example.loginproject.ui.checkout


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginproject.repository.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CheckoutItemUi(
    val id: Int,
    val name: String,
    val colorName: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String? = null
)

data class CheckoutUiState(
    val isLoading: Boolean = true,
    val items: List<CheckoutItemUi> = emptyList(),
    val itemsPrice: Double = 0.0,
    val shippingPrice: Double = 0.0,
    val totalPrice: Double = 0.0,
    val error: String? = null
)

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val repository: ApiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    init {
        loadCheckout()
    }

    private fun loadCheckout() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val checkoutResponse = repository.cartCheckout()

                val productsResponse = repository.getProducts()
                val allProducts = productsResponse.body()?.results ?: emptyList()

                val items = checkoutResponse.items.map { item ->
                    val matchedProduct = allProducts.find { it.name == item.product }
                    CheckoutItemUi(
                        id = item.id,
                        name = item.product,
                        colorName = item.color,
                        price = item.price,
                        quantity = item.quantity,
                        imageUrl = matchedProduct?.image
                    )
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    items = items,
                    itemsPrice = checkoutResponse.items_price,
                    shippingPrice = checkoutResponse.shipping_price,
                    totalPrice = checkoutResponse.total_price
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "خطا در دریافت اطلاعات سبد خرید"
                )
            }
        }
    }
}