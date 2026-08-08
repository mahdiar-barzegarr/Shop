package com.example.loginproject.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginproject.data.local.TokenManager
import com.example.loginproject.data.remote.response.ProductDto
import com.example.loginproject.repository.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.LocalTime
import java.util.Locale
import javax.inject.Inject

data class CategoryItem(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val category: Int
)

data class ProductItem(
    val id: String,
    val name: String,
    val price: String,
    val imageUrl: String? = null
)

data class NewArrivalItem(
    val id: String,
    val name: String,
    val description: String,
    val price: String,
    val icon: ImageVector,
    val imageUrl: String? = null
)


data class PromoBanner(
    val title: String,
    val subtitle: String,
    val buttonText: String
)

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ApiRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    companion object {
        private const val SERVER_HOST = "http://192.168.1.102:8000"
    }

    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName

    private val _greeting = MutableStateFlow("Good morning")
    val greeting: StateFlow<String> = _greeting

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _banner = MutableStateFlow(
        PromoBanner(
            title = "New season, softer choices",
            subtitle = "Made for your daily edit",
            buttonText = "Explore collection"
        )
    )
    val banner: StateFlow<PromoBanner> = _banner


    private val _categories = MutableStateFlow(
        listOf(
            CategoryItem("bags", "Bags", Icons.Outlined.ShoppingBag,0),
            CategoryItem("shoes", "Shoes", Icons.Filled.DirectionsRun,0),
            CategoryItem("beauty", "Beauty", Icons.Filled.Spa,0),
            CategoryItem("tech", "Tech", Icons.Filled.Headphones,0)
        )
    )
    val categories: StateFlow<List<CategoryItem>> = _categories

    private val _popularProducts = MutableStateFlow<List<ProductItem>>(emptyList())
    val popularProducts: StateFlow<List<ProductItem>> = _popularProducts

    private val _newArrivals = MutableStateFlow<List<NewArrivalItem>>(emptyList())
    val newArrivals: StateFlow<List<NewArrivalItem>> = _newArrivals

    private val _isLoadingProducts = MutableStateFlow(false)
    val isLoadingProducts: StateFlow<Boolean> = _isLoadingProducts

    init {
        _greeting.value = computeGreeting()
        loadProfile()
        loadProducts()
    }

    fun onSearchQueryChanged(value: String) {
        _searchQuery.value = value
    }

    fun refreshProducts() = loadProducts()

    private fun computeGreeting(): String {
        val hour = LocalTime.now().hour
        return when (hour) {
            in 5..11 -> "Good morning"
            in 12..17 -> "Good afternoon"
            else -> "Good evening"
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _userName.value = tokenManager.fullNameFlow.first()
            try {
                val response = repository.getProfile()
                if (response.isSuccessful) {
                    response.body()?.fullName?.let { name ->
                        _userName.value = name
                        tokenManager.saveFullName(name)
                    }
                }
            } catch (e: Exception) {
                // بی‌سروصدا نادیده بگیر
            }
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _isLoadingProducts.value = true
            try {
                val response = repository.getProducts()
                if (response.isSuccessful) {
                    val allProducts = response.body()?.results
                        ?.filter { it.is_available } // فقط چیزی که موجوده نشون بده
                        ?: emptyList()

                    // فعلاً بک‌اند فرقی بین «محبوب» و «جدید» نمی‌ذاره،
                    // پس یه تقسیم ساده انجام می‌دیم. وقتی بک‌اند فیلدهایی
                    // مثل is_popular / is_new اضافه کرد، اینجا رو دقیق‌تر کن.
                    _popularProducts.value = allProducts.take(4).map { it.toProductItem() }
                    _newArrivals.value = allProducts.takeLast(4).map { it.toNewArrivalItem() }
                }
            } catch (e: Exception) {
                // TODO: می‌تونی یه پیام خطا هم نگه داری و توی UI نشون بدی
            } finally {
                _isLoadingProducts.value = false
            }
        }
    }

    private fun ProductDto.toProductItem() = ProductItem(
        id = id.toString(),
        name = name,
        price = formatPrice(price),
        imageUrl = resolveImageUrl(image)
    )

    private fun ProductDto.toNewArrivalItem() = NewArrivalItem(
        id = id.toString(),
        name = name,
        description = category.name,
        price = formatPrice(price),
        icon = Icons.Outlined.ShoppingBag, // اگه imageUrl نال بود، این آیکون fallback نشون داده میشه
        imageUrl = resolveImageUrl(image)
    )

    private fun resolveImageUrl(raw: String): String? {
        if (raw.isBlank()) return null
        return if (raw.startsWith("http://") || raw.startsWith("https://")) {
            raw
        } else {
            // TODO: اگه بک‌اند واقعی (نه لوکال) شد، این آدرس رو با آدرس سرور اصلی عوض کن
            "$SERVER_HOST$raw"
        }
    }

    private fun formatPrice(raw: String): String {
        val number = raw.toLongOrNull() ?: return raw
        val formatted = NumberFormat.getNumberInstance(Locale.US).format(number)
        return "$formatted تومان" // TODO: اگه واحد پول فرق داره، این پسوند رو عوض کن
    }
}


