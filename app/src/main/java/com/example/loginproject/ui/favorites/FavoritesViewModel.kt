package com.example.loginproject.ui.favorites



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginproject.data.remote.ApiConfig
import com.example.loginproject.data.remote.response.FavoriteItemDto
import com.example.loginproject.repository.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

data class FavoriteProductUi(
    val id: Int,
    val name: String,
    val price: String,
    val imageUrl: String?
)

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: ApiRepository
) : ViewModel() {

    private val _products = MutableStateFlow<List<FavoriteProductUi>>(emptyList())
    val products: StateFlow<List<FavoriteProductUi>> = _products

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        loadFavorites()
    }

    fun retry() = loadFavorites()

    private fun loadFavorites() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = repository.getFavorites()
                if (response.isSuccessful) {
                    _products.value = response.body()?.map { it.toUi() } ?: emptyList()
                } else {
                    _errorMessage.value = "دریافت لیست علاقه‌مندی‌ها ناموفق بود"
                }
            } catch (e: Exception) {
                _errorMessage.value = "اتصال برقرار نشد. اینترنت یا آدرس سرور رو چک کن"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun FavoriteItemDto.toUi() = FavoriteProductUi(
        id = product.id,
        name = product.name,
        price = formatPrice(product.price),
        imageUrl = product.image?.let { resolveImageUrl(it) }
    )

    private fun resolveImageUrl(raw: String): String {
        return if (raw.startsWith("http://") || raw.startsWith("https://")) {
            raw
        } else {
            "${ApiConfig.SERVER_HOST}$raw"
        }
    }

    private fun formatPrice(raw: String): String {
        val number = raw.toLongOrNull() ?: return raw
        val formatted = NumberFormat.getNumberInstance(Locale.US).format(number)
        return "$formatted تومان"
    }
}