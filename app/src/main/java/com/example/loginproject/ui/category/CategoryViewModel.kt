package com.example.loginproject.ui.category

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginproject.repository.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoryCardItem(
    val id: Int,
    val name: String,
    val productCount: Int,
    val icon: ImageVector,
    val backgroundColor: Color
)

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val repository: ApiRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private var allCategories: List<CategoryCardItem> = emptyList()

    private val _categories = MutableStateFlow<List<CategoryCardItem>>(emptyList())
    val categories: StateFlow<List<CategoryCardItem>> = _categories

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        loadCategories()
    }

    fun onSearchQueryChanged(value: String) {
        _searchQuery.value = value
        applyFilter()
    }

    fun refresh() = loadCategories()

    private fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val categoriesResponse = repository.getCategories()
                val productsResponse = repository.getProducts()

                if (categoriesResponse.isSuccessful) {
                    val categoryList = categoriesResponse.body().orEmpty()
                    val products = if (productsResponse.isSuccessful) {
                        productsResponse.body()?.results.orEmpty()
                    } else {
                        emptyList()
                    }

                    val countByCategoryId = products
                        .groupingBy { it.category.id }
                        .eachCount()

                    allCategories = categoryList.mapIndexed { index, category ->
                        CategoryCardItem(
                            id = category.id,
                            name = category.name,
                            productCount = countByCategoryId[category.id] ?: 0,
                            icon = iconForIndex(index),
                            backgroundColor = colorForIndex(index)
                        )
                    }

                    applyFilter()
                } else {
                    _errorMessage.value = "دریافت دسته‌بندی‌ها ناموفق بود"
                }
            } catch (e: Exception) {
                _errorMessage.value = "اتصال برقرار نشد. اینترنت یا آدرس سرور رو چک کن"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun applyFilter() {
        val query = _searchQuery.value.trim()
        _categories.value = if (query.isBlank()) {
            allCategories
        } else {
            allCategories.filter { it.name.contains(query, ignoreCase = true) }
        }
    }

    private fun iconForIndex(index: Int): ImageVector {
        val icons = listOf(
            Icons.Outlined.ShoppingBag,
            Icons.Filled.DirectionsRun,
            Icons.Filled.Spa,
            Icons.Filled.Headphones,
            Icons.Filled.Checkroom,
            Icons.Filled.Home
        )
        return icons[index % icons.size]
    }

    private fun colorForIndex(index: Int): Color {
        val colors = listOf(
            Color(0xFFE3DEFA),
            Color(0xFFFBDCE0),
            Color(0xFFDCEEDB),
            Color(0xFFEDEDED),
            Color(0xFFFBDCE0),
            Color(0xFFE3DEFA)
        )
        return colors[index % colors.size]
    }
}

