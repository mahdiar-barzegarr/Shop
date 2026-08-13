package com.example.loginproject.ui.product

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginproject.data.remote.ApiConfig
import com.example.loginproject.data.remote.response.ProductDetailResponse
import com.example.loginproject.repository.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

data class ProductColorUi(
    val id: Int,
    val name: String,
    val hexCode: String,
    val inStock: Boolean
)

data class ProductDetailUi(
    val id: Int,
    val name: String,
    val description: String,
    val price: String,
    val imageUrls: List<String>,
    val isAvailable: Boolean,
    val colors: List<ProductColorUi>
)

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val repository: ApiRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // فرض بر اینه که توی NavGraph آرگومان مسیر اسمش "productId" هست
    private val productId: Int = checkNotNull(savedStateHandle["productId"])

    private val _product = MutableStateFlow<ProductDetailUi?>(null)
    val product: StateFlow<ProductDetailUi?> = _product

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // آیا این محصول همین الان توی سبد خریده؟ (چه از قبل، چه همین الان اد شده)
    private val _isInCart = MutableStateFlow(false)
    val isInCart: StateFlow<Boolean> = _isInCart

    // تعداد فعلیش توی سبد (فقط وقتی isInCart==true معنا داره)
    private val _quantity = MutableStateFlow(1)
    val quantity: StateFlow<Int> = _quantity

    // id ردیف این آیتم توی سبد خرید (برای delete لازمه). چون user_cart فقط
    // اسم محصول رو میده نه id، این با مچ کردن اسم پیدا میشه.
    private var cartItemId: Int? = null

    // موقع add/increase/decrease که داره با سرور سینک میشه، دکمه‌ها قفلن
    private val _isSyncingCart = MutableStateFlow(false)
    val isSyncingCart: StateFlow<Boolean> = _isSyncingCart

    private val _cartErrorMessage = MutableStateFlow<String?>(null)
    val cartErrorMessage: StateFlow<String?> = _cartErrorMessage

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite

    init {
        loadProduct()
    }

    fun toggleFavorite() {
        // TODO: فعلاً فقط ظاهریه (محلیه). وقتی API لایک/علاقه‌مندی رو گرفتی،
        // همینجا repository.addToFavorites(productId) رو صدا بزن.
        _isFavorite.value = !_isFavorite.value
    }

    fun retry() = loadProduct()

    fun onAddToCartClick() {
        if (_isSyncingCart.value) return

        viewModelScope.launch {
            _isSyncingCart.value = true
            _cartErrorMessage.value = null
            try {
                // TODO: وقتی UI انتخاب رنگ اضافه شد، به‌جای productId باید id همون
                // رنگ انتخاب‌شده (ColorDto.id) به‌عنوان variant_id فرستاده بشه
                val response = repository.addItemToCart(variantId = productId, quantity = 1)
                if (response.isSuccessful) {
                    refreshCartStatus() // برای گرفتن id واقعی ردیف سبد از سرور
                } else {
                    _cartErrorMessage.value = "اضافه کردن به سبد خرید ناموفق بود"
                }
            } catch (e: Exception) {
                _cartErrorMessage.value = "اتصال برقرار نشد. اینترنت یا آدرس سرور رو چک کن"
            } finally {
                _isSyncingCart.value = false
            }
        }
    }

    fun onIncreaseQuantity() {
        if (_isSyncingCart.value) return

        viewModelScope.launch {
            _isSyncingCart.value = true
            _cartErrorMessage.value = null
            try {
                // add_item جمع‌شونده‌ست، پس یه واحد دیگه اضافه کردن دقیقاً همینه
                val response = repository.addItemToCart(variantId = productId, quantity = 1)
                if (response.isSuccessful) {
                    refreshCartStatus()
                } else {
                    _cartErrorMessage.value = "بروزرسانی تعداد ناموفق بود"
                }
            } catch (e: Exception) {
                _cartErrorMessage.value = "اتصال برقرار نشد. اینترنت یا آدرس سرور رو چک کن"
            } finally {
                _isSyncingCart.value = false
            }
        }
    }

    fun onDecreaseQuantity() {
        if (_isSyncingCart.value) return
        val currentCartItemId = cartItemId ?: return
        val newQuantity = _quantity.value - 1

        viewModelScope.launch {
            _isSyncingCart.value = true
            _cartErrorMessage.value = null
            try {
                val deleteResponse = repository.deleteCartItem(currentCartItemId)
                if (!deleteResponse.isSuccessful) {
                    _cartErrorMessage.value = "بروزرسانی تعداد ناموفق بود"
                    return@launch
                }

                if (newQuantity <= 0) {
                    // کاملاً از سبد حذف شد
                    _isInCart.value = false
                    _quantity.value = 1
                    cartItemId = null
                } else {
                    // دوباره با تعداد جدید اضافه کن (چون API فقط add/delete داره، نه decrease مستقیم)
                    val addResponse = repository.addItemToCart(variantId = productId, quantity = newQuantity)
                    if (addResponse.isSuccessful) {
                        refreshCartStatus()
                    } else {
                        _cartErrorMessage.value = "بروزرسانی تعداد ناموفق بود"
                    }
                }
            } catch (e: Exception) {
                _cartErrorMessage.value = "اتصال برقرار نشد. اینترنت یا آدرس سرور رو چک کن"
            } finally {
                _isSyncingCart.value = false
            }
        }
    }

    /** سبد خرید رو از سرور می‌گیره و می‌بینه این محصول (بر اساس اسم) توش هست یا نه */
    private fun refreshCartStatus() {
        viewModelScope.launch {
            try {
                val response = repository.getUserCart()
                if (response.isSuccessful) {
                    val currentProductName = _product.value?.name
                    val matchedItem = response.body()?.items?.firstOrNull {
                        it.product.equals(currentProductName, ignoreCase = true)
                    }

                    if (matchedItem != null) {
                        _isInCart.value = true
                        _quantity.value = matchedItem.quantity
                        cartItemId = matchedItem.id
                    } else {
                        _isInCart.value = false
                        _quantity.value = 1
                        cartItemId = null
                    }
                }
            } catch (e: Exception) {
                // بی‌سروصدا نادیده بگیر؛ وضعیت فعلی (محلی) رو نگه دار
            }
        }
    }

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
                    // بعد از لود شدن محصول، چک کن از قبل توی سبد هست یا نه
                    refreshCartStatus()
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
        isAvailable = is_available,
        colors = colors.map { colorDto ->
            ProductColorUi(
                id = colorDto.id,
                name = colorDto.name,
                hexCode = colorDto.code,
                inStock = colorDto.stock > 0
            )
        }
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
