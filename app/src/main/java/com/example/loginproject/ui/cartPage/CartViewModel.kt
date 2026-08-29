package com.example.loginproject.ui.cartPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginproject.data.remote.ApiConfig
import com.example.loginproject.data.remote.request.CartItemDto
import com.example.loginproject.data.remote.response.ProductDto
import com.example.loginproject.repository.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class CartItemUi(
    val cartItemId: Int,
    val productId: Int?,
    val colorName: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String?,
    val isSyncing: Boolean = false
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
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    private companion object {
        const val DEBOUNCE_MS = 1500L
    }

    private val serverQuantities = mutableMapOf<Int, Int>()
    private val pendingQuantityChanges = mutableMapOf<Int, Int>()
    private val debounceJobs = mutableMapOf<Int, Job>()

    // فقط یه چرخه‌ی delete+add+refresh در هر لحظه اجرا می‌شه — جلوی race بین
    // چند commit همزمان (چند آیتم با هم دست‌کاری بشن) رو می‌گیره
    private val cartMutex = Mutex()

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
                val items = cart.items.map { buildCartItemUi(it, allProducts) }

                serverQuantities.clear()
                items.forEach { serverQuantities[it.cartItemId] = it.quantity }
                pendingQuantityChanges.clear()
                debounceJobs.values.forEach { it.cancel() }
                debounceJobs.clear()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        items = items,
                        subtotal = items.sumOf { i -> i.price * i.quantity },
                        total = items.sumOf { i -> i.price * i.quantity }
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }
        }
    }

    fun deleteItem(cartItemId: Int) {
        debounceJobs.remove(cartItemId)?.cancel()
        pendingQuantityChanges.remove(cartItemId)
        serverQuantities.remove(cartItemId)

        _uiState.update { state ->
            val updatedItems = state.items.filterNot { it.cartItemId == cartItemId }
            state.copy(
                items = updatedItems,
                subtotal = updatedItems.calculateTotal(),
                total = updatedItems.calculateTotal()
            )
        }

        viewModelScope.launch {
            try {
                cartMutex.withLock {
                    repository.deleteCartItem(cartItemId)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.localizedMessage) }
            }
        }
    }

    fun increaseQuantity(item: CartItemUi) = scheduleQuantityChange(item, item.quantity + 1)

    fun decreaseQuantity(item: CartItemUi) {
        if (item.quantity <= 1) {
            deleteItem(item.cartItemId)
        } else {
            scheduleQuantityChange(item, item.quantity - 1)
        }
    }

    private fun scheduleQuantityChange(item: CartItemUi, newQuantity: Int) {
        val cartItemId = item.cartItemId

        _uiState.update { state ->
            val updatedItems = state.items.map {
                if (it.cartItemId == cartItemId) it.copy(quantity = newQuantity) else it
            }
            state.copy(
                items = updatedItems,
                subtotal = updatedItems.calculateTotal(),
                total = updatedItems.calculateTotal()
            )
        }

        val baseline = serverQuantities[cartItemId]
        if (baseline == newQuantity) {
            pendingQuantityChanges.remove(cartItemId)
            debounceJobs.remove(cartItemId)?.cancel()
            setItemSyncing(cartItemId, false)
            return
        }

        pendingQuantityChanges[cartItemId] = newQuantity

        debounceJobs[cartItemId]?.cancel()
        debounceJobs[cartItemId] = viewModelScope.launch {
            delay(DEBOUNCE_MS)
            commitSingleItem(cartItemId)
        }
    }

    private fun setItemSyncing(cartItemId: Int, syncing: Boolean) {
        _uiState.update { state ->
            state.copy(items = state.items.map {
                if (it.cartItemId == cartItemId) it.copy(isSyncing = syncing) else it
            })
        }
    }

    /**
     * چون endpoint مستقیم برای تغییر quantity نداریم، commit یعنی delete+add —
     * که ردیف رو روی سرور با id جدید می‌سازه. برای همین بعد از commit، به‌جای
     * ساختن دوباره‌ی کل لیست از روی سرور (که می‌تونه ترتیب رو بهم بزنه)، فقط
     * همون یه آیتم رو با map سرجای خودش (همون index قبلی) آپدیت می‌کنیم —
     * بقیه‌ی لیست اصلاً دست نمی‌خوره، پس هیچ‌وقت جابجا نمی‌شه.
     */
    private suspend fun commitSingleItem(cartItemId: Int) {
        val newQuantity = pendingQuantityChanges[cartItemId] ?: return
        val item = _uiState.value.items.find { it.cartItemId == cartItemId } ?: return

        setItemSyncing(cartItemId, true)
        try {
            val variantId = resolveVariantId(item)
            if (variantId != null) {
                cartMutex.withLock {
                    repository.deleteCartItem(cartItemId)
                    repository.addItemToCart(variantId, newQuantity)
                    replaceItemInPlace(
                        oldCartItemId = cartItemId,
                        productName = item.name,
                        colorName = item.colorName
                    )
                }
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(error = e.localizedMessage) }
            setItemSyncing(cartItemId, false)
        } finally {
            pendingQuantityChanges.remove(cartItemId)
            debounceJobs.remove(cartItemId)
        }
    }

    /**
     * بعد از delete+add، سبد رو می‌خونه، فقط ردیفی که با (نام محصول، نام رنگ)
     * مطابقت داره پیدا می‌کنه و id/quantity واقعی سرور رو جایگزین همون یک
     * آیتم می‌کنه — بدون این‌که ترتیب یا بقیه‌ی آیتم‌ها دست بخوره.
     */
    private suspend fun replaceItemInPlace(oldCartItemId: Int, productName: String, colorName: String) {
        val cartResponse = repository.getUserCart()
        val cart = cartResponse.body() ?: return
        val matched = cart.items.find { it.product == productName && it.color == colorName } ?: return

        serverQuantities.remove(oldCartItemId)
        serverQuantities[matched.id] = matched.quantity

        _uiState.update { state ->
            val updatedItems = state.items.map { ui ->
                if (ui.cartItemId == oldCartItemId) {
                    ui.copy(
                        cartItemId = matched.id,
                        quantity = matched.quantity,
                        isSyncing = false
                    )
                } else ui
            }
            state.copy(
                items = updatedItems,
                subtotal = updatedItems.sumOf { it.price * it.quantity },
                total = updatedItems.sumOf { it.price * it.quantity }
            )
        }
    }

    suspend fun flushPendingChanges(): Boolean {
        val pendingIds = pendingQuantityChanges.keys.toList()
        if (pendingIds.isEmpty()) return true

        _uiState.update { it.copy(isSyncing = true) }
        return try {
            for (id in pendingIds) {
                debounceJobs.remove(id)?.cancel()
                commitSingleItem(id)
            }
            true
        } catch (e: Exception) {
            _uiState.update { it.copy(error = e.localizedMessage) }
            false
        } finally {
            _uiState.update { it.copy(isSyncing = false) }
        }
    }

    private fun List<CartItemUi>.calculateTotal(): Double = sumOf { it.price * it.quantity }

    private fun buildCartItemUi(item: CartItemDto, allProducts: List<ProductDto>): CartItemUi {
        val matched = allProducts.find { it.name == item.product }
        val effectiveQty = pendingQuantityChanges[item.id] ?: item.quantity
        return CartItemUi(
            cartItemId = item.id,
            productId = matched?.id,
            colorName = item.color,
            name = item.product,
            price = item.price.toDouble(),
            quantity = effectiveQty,
            imageUrl = matched?.image?.let { ApiConfig.SERVER_HOST + it },
            isSyncing = debounceJobs.containsKey(item.id) || pendingQuantityChanges.containsKey(item.id)
        )
    }

    private suspend fun resolveVariantId(item: CartItemUi): Int? {
        val productId = item.productId ?: return null
        val colors = repository.getProductDetail(productId).body()?.colors.orEmpty()
        return colors.find { it.name == item.colorName }?.id
    }
}