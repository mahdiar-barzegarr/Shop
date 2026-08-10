package com.example.loginproject.ui.product

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginproject.data.remote.response.ProductDetailResponse
import com.example.loginproject.repository.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

data class ProductDetailUi(
    val id: Int,
    val name: String,
    val description: String,
    val price: String,
    val imageUrls: List<String>,
    val isAvailable: Boolean
)

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val repository: ApiRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val SERVER_HOST = "http://192.168.1.102:8000"
    }

    private val productId: Int = checkNotNull(savedStateHandle["productId"])

    private val _product = MutableStateFlow<ProductDetailUi?>(null)
    val product: StateFlow<ProductDetailUi?> = _product

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _quantity = MutableStateFlow(1)
    val quantity: StateFlow<Int> = _quantity

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite

    init {
        loadProduct()
    }

    fun increaseQuantity() {
        _quantity.value = _quantity.value + 1
    }

    fun decreaseQuantity() {
        if (_quantity.value > 1) {
            _quantity.value = _quantity.value - 1
        }
    }

    fun toggleFavorite() {
        // همینجا repository.addToFavorites(productId) رو صدا بزن.
        _isFavorite.value = !_isFavorite.value
    }

    fun onAddToCartClick() {
        // TODO: call add to cart
    }

    fun retry() = loadProduct()

    private fun loadProduct() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = repository.getProductDetail(productId)
                if (response.isSuccessful) {
                    response.body()?.let { dto ->
                        _product.value = dto.toUi()
                    }
                } else {
                    _errorMessage.value = "دریافت اطلاعات محصول ناموفق بود"
                }
            } catch (e: Exception) {
                _errorMessage.value = "اتصال برقرار نشد. اینترنت یا آدرس سرور رو چک کن"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun ProductDetailResponse.toUi() = ProductDetailUi(
        id = id,
        name = name,
        description = description,
        price = formatPrice(price),
        imageUrls = images.map { resolveImageUrl(it.image) },
        isAvailable = is_available
    )

    private fun resolveImageUrl(raw: String): String {
        return if (raw.startsWith("http://") || raw.startsWith("https://")) {
            raw
        } else {
            "$SERVER_HOST$raw"
        }
    }

    private fun formatPrice(raw: String): String {
        val number = raw.toLongOrNull() ?: return raw
        val formatted = NumberFormat.getNumberInstance(Locale.US).format(number)
        return "$formatted تومان"
    }
}
