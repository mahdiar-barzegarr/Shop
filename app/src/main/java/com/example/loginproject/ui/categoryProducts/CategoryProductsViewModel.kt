package com.example.loginproject.ui.categoryProducts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginproject.data.remote.ApiConfig
import com.example.loginproject.repository.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

enum class SortOption(val label: String) {
    DEFAULT("Default"),
    PRICE_LOW_HIGH("Price: Low to High"),
    PRICE_HIGH_LOW("Price: High to Low")
}

data class CategoryProductUi(
    val id: Int,
    val name: String,
    val price: String,
    val rawPrice: Long,
    val imageUrl: String?,
    val isAvailable: Boolean
)

@HiltViewModel
class CategoryProductsViewModel @Inject constructor(
    private val repository: ApiRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val SERVER_HOST = ApiConfig.SERVER_HOST
    }

    private val categoryId: Int = checkNotNull(savedStateHandle["categoryId"])

    val categoryName: String = URLDecoder.decode(
        savedStateHandle.get<String>("categoryName") ?: "",
        "UTF-8"
    )

    private var allProducts: List<CategoryProductUi> = emptyList()

    private val _products = MutableStateFlow<List<CategoryProductUi>>(emptyList())
    val products: StateFlow<List<CategoryProductUi>> = _products

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _isSearchVisible = MutableStateFlow(false)
    val isSearchVisible: StateFlow<Boolean> = _isSearchVisible

    private val _sortOption = MutableStateFlow(SortOption.DEFAULT)
    val sortOption: StateFlow<SortOption> = _sortOption

    private val _favoriteIds = MutableStateFlow<Set<Int>>(emptySet())
    val favoriteIds: StateFlow<Set<Int>> = _favoriteIds

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        loadProducts()
    }

    fun toggleSearchVisibility() {
        _isSearchVisible.value = !_isSearchVisible.value
        if (!_isSearchVisible.value) {
            _searchQuery.value = ""
            applyFilters()
        }
    }

    fun onSearchQueryChanged(value: String) {
        _searchQuery.value = value
        applyFilters()
    }

    fun onSortOptionSelected(option: SortOption) {
        _sortOption.value = option
        applyFilters()
    }

    fun toggleFavorite(productId: Int) {
        _favoriteIds.value = if (productId in _favoriteIds.value) {
            _favoriteIds.value - productId
        } else {
            _favoriteIds.value + productId
        }
        // TODO: وقتی API علاقه‌مندی رو گرفتی، اینجا هم repository رو صدا بزن
    }

    fun retry() = loadProducts()

    fun onAddToCartClick(productId: Int) {
        // TODO: وقتی API سبد خرید رو گرفتی، اینجا repository.addToCart(productId) رو صدا بزن
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = repository.getProducts()
                if (response.isSuccessful) {
                    allProducts = response.body()?.results
                        ?.filter { it.category.id == categoryId && it.is_available }
                        ?.map { dto ->
                            val rawPrice = dto.price.toLongOrNull() ?: 0L
                            CategoryProductUi(
                                id = dto.id,
                                name = dto.name,
                                price = formatPrice(rawPrice),
                                rawPrice = rawPrice,
                                imageUrl = resolveImageUrl(dto.image),
                                isAvailable = dto.is_available
                            )
                        }
                        ?: emptyList()
                    applyFilters()
                } else {
                    _errorMessage.value = "دریافت محصولات ناموفق بود"
                }
            } catch (e: Exception) {
                _errorMessage.value = "اتصال برقرار نشد. اینترنت یا آدرس سرور رو چک کن"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun applyFilters() {
        val query = _searchQuery.value.trim()

        var result = if (query.isBlank()) {
            allProducts
        } else {
            allProducts.filter { it.name.contains(query, ignoreCase = true) }
        }

        result = when (_sortOption.value) {
            SortOption.DEFAULT -> result
            SortOption.PRICE_LOW_HIGH -> result.sortedBy { it.rawPrice }
            SortOption.PRICE_HIGH_LOW -> result.sortedByDescending { it.rawPrice }
        }

        _products.value = result
    }

    private fun resolveImageUrl(raw: String): String? {
        if (raw.isBlank()) return null
        return if (raw.startsWith("http://") || raw.startsWith("https://")) {
            raw
        } else {
            "$SERVER_HOST$raw"
        }
    }

    private fun formatPrice(number: Long): String {
        val formatted = NumberFormat.getNumberInstance(Locale.US).format(number)
        return "$formatted تومان"
    }
}
