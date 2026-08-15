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
    val isSyncing: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repository: ApiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<com.example.loginproject.ui.cartPage.CartUiState> = _uiState.asStateFlow()

    // آخرین quantity‌ای که سرور تأیید کرده (baseline) — برای تشخیص اینکه واقعاً چی تغییر کرده
    private val serverQuantities = mutableMapOf<Int, Int>()

    // تغییرات quantity که هنوز به سرور فرستاده نشدن (cartItemId -> quantity نهایی)
    private val pendingQuantityChanges = mutableMapOf<Int, Int>()

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

                serverQuantities.clear()
                items.forEach { serverQuantities[it.cartItemId] = it.quantity }
                pendingQuantityChanges.clear()

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

    /**
     * حذف همچنان فوریه — چون یه اکشن مجزاست، نه چیزی که سریع پشت‌سرهم تپ بشه.
     */
    fun deleteItem(cartItemId: Int) {
        _uiState.update { state ->
            val updatedItems = state.items.filterNot { it.cartItemId == cartItemId }
            state.copy(
                items = updatedItems,
                subtotal = updatedItems.calculateTotal(),
                total = updatedItems.calculateTotal()
            )
        }
        serverQuantities.remove(cartItemId)
        pendingQuantityChanges.remove(cartItemId)

        viewModelScope.launch {
            repository.deleteCartItem(cartItemId)
            syncCartSilently()
        }
    }

    /**
     * فقط local state رو آپدیت می‌کنه و توی pendingQuantityChanges ثبت می‌کنه.
     * هیچ network call ای اینجا نمی‌ره — فقط موقع Checkout همه با هم commit می‌شن.
     */
    fun increaseQuantity(item: CartItemUi) = updateQuantityLocal(item, item.quantity + 1)

    fun decreaseQuantity(item: CartItemUi) {
        if (item.quantity <= 1) deleteItem(item.cartItemId)
        else updateQuantityLocal(item, item.quantity - 1)
    }

    private fun List<CartItemUi>.calculateTotal(): Double = sumOf { it.price * it.quantity }

    private fun updateQuantityLocal(item: CartItemUi, newQuantity: Int) {
        _uiState.update { state ->
            val updatedItems = state.items.map {
                if (it.cartItemId == item.cartItemId) it.copy(quantity = newQuantity) else it
            }
            state.copy(
                items = updatedItems,
                subtotal = updatedItems.calculateTotal(),
                total = updatedItems.calculateTotal()
            )
        }

        val baseline = serverQuantities[item.cartItemId]
        if (baseline == newQuantity) {
            pendingQuantityChanges.remove(item.cartItemId)
        } else {
            pendingQuantityChanges[item.cartItemId] = newQuantity
        }
    }

    /**
     * موقع زدن Checkout صدا زده می‌شه. همه‌ی تغییرات pending رو یکی‌یکی
     * (نه همزمان) به سرور می‌فرسته تا هیچ race ای پیش نیاد، بعد cart رو
     * از سرور رفرش می‌کنه. خروجی true یعنی می‌تونی خیالت راحت باشه و
     * بری صفحه Checkout.
     */
    suspend fun commitPendingChanges(): Boolean {
        if (pendingQuantityChanges.isEmpty()) return true

        _uiState.update { it.copy(isSyncing = true) }

        val currentItems = _uiState.value.items
        val changesSnapshot = pendingQuantityChanges.toMap()

        try {
            for ((cartItemId, newQuantity) in changesSnapshot) {
                val item = currentItems.find { it.cartItemId == cartItemId } ?: continue
                val variantId = resolveVariantId(item) ?: continue

                repository.deleteCartItem(cartItemId)
                repository.addItemToCart(variantId, newQuantity)
            }

            pendingQuantityChanges.clear()
            syncCartSilently()
            return true
        } catch (e: Exception) {
            _uiState.update { it.copy(error = e.localizedMessage) }
            return false
        } finally {
            _uiState.update { it.copy(isSyncing = false) }
        }
    }

    private suspend fun syncCartSilently() {
        val cartResponse = repository.getUserCart()
        val cart = cartResponse.body() ?: return
        val allProducts = repository.getProducts().body()?.results.orEmpty()

        val freshItems = cart.items.map { item ->
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

        serverQuantities.clear()
        freshItems.forEach { serverQuantities[it.cartItemId] = it.quantity }

        _uiState.update { state ->
            val oldOrderKeys = state.items.map { it.name to it.colorName }
            val reordered = freshItems.sortedBy { fi ->
                oldOrderKeys.indexOf(fi.name to fi.colorName).let { if (it == -1) Int.MAX_VALUE else it }
            }
            state.copy(
                items = reordered,
                subtotal = cart.total_price.toDouble() ?: 0.0,
                total = cart.total_price.toDouble() ?: 0.0
            )
        }
    }

    private suspend fun resolveVariantId(item: CartItemUi): Int? {
        val productId = item.productId ?: return null
        val colors = repository.getProductDetail(productId).body()?.colors.orEmpty()
        return colors.find { it.name == item.colorName }?.id
    }
}