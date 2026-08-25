package com.example.loginproject.ui.favorites



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginproject.data.remote.ApiConfig
import com.example.loginproject.data.remote.response.FavoriteItemDto
import com.example.loginproject.repository.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject
enum class FavoriteSortOption(val label: String) {
    NEWEST("Newest"),
    PRICE_LOW_TO_HIGH("Price: Low to High"),
    PRICE_HIGH_TO_LOW("Price: High to Low"),
    NAME_A_TO_Z("Name: A to Z")
}

data class FavoriteProductUi(
    val id: Int,
    val name: String,
    val price: String,
    val rawPrice: Long,
    val imageUrl: String?
)

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: ApiRepository
) : ViewModel() {

    private val _rawProducts = MutableStateFlow<List<FavoriteProductUi>>(emptyList())

    private val _sortOption = MutableStateFlow(FavoriteSortOption.NEWEST)
    val sortOption: StateFlow<FavoriteSortOption> = _sortOption

    val products: StateFlow<List<FavoriteProductUi>> =
        combine(_rawProducts, _sortOption) { list, sort ->
            when (sort) {
                FavoriteSortOption.NEWEST -> list
                FavoriteSortOption.PRICE_LOW_TO_HIGH -> list.sortedBy { it.rawPrice }
                FavoriteSortOption.PRICE_HIGH_TO_LOW -> list.sortedByDescending { it.rawPrice }
                FavoriteSortOption.NAME_A_TO_Z -> list.sortedBy { it.name }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        loadFavorites()
    }

    fun retry() = loadFavorites()

    fun onSortOptionSelected(option: FavoriteSortOption) {
        _sortOption.value = option
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = repository.getFavorites()
                if (response.isSuccessful) {
                    _rawProducts.value = response.body()?.map { it.toUi() } ?: emptyList()
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

    private fun FavoriteItemDto.toUi(): FavoriteProductUi {
        val rawPrice = product.price.toLongOrNull() ?: 0L
        return FavoriteProductUi(
            id = product.id,
            name = product.name,
            price = formatPrice(product.price),
            rawPrice = rawPrice,
            imageUrl = product.image?.let { resolveImageUrl(it) }
        )
    }

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