package com.example.loginproject.repository

import com.example.loginproject.data.remote.ApiConfig
import com.example.loginproject.data.remote.api.ApiService
import com.example.loginproject.data.remote.request.AddCartItemRequest
import com.example.loginproject.data.remote.request.LoginRequest
import com.example.loginproject.data.remote.request.RegisterRequest
import com.example.loginproject.data.remote.request.UserCartResponse
import com.example.loginproject.data.remote.response.AddCartItemResponse
import com.example.loginproject.data.remote.response.CartCheckoutResponse
import com.example.loginproject.data.remote.response.CategoryDto
import com.example.loginproject.data.remote.response.FavoriteItemDto
import com.example.loginproject.data.remote.response.FavoriteResponse
import com.example.loginproject.data.remote.response.ProductDetailResponse
import com.example.loginproject.data.remote.response.ProductDto
import com.example.loginproject.data.remote.response.ProductListResponse
import com.example.loginproject.data.remote.response.ProfileResponse
import com.example.loginproject.models.LoginResponseModel
import com.example.loginproject.models.RefreshRequest
import com.example.loginproject.models.RefreshResponseModel
import com.example.loginproject.models.RegisterResponseModel
import jakarta.inject.Inject
import retrofit2.Response

class ApiRepository @Inject constructor(

    private val api: ApiService

) {

    suspend fun register(

        request: RegisterRequest

    ): Response<RegisterResponseModel> {

        return api.register(request)

    }


    suspend fun login(

        request: LoginRequest

    ): Response<LoginResponseModel> {

        return api.login(request)

    }


    suspend fun getProducts(): Response<ProductListResponse> {
        val allResults = mutableListOf<ProductDto>()
        var nextUrl: String? = ApiService.PRODUCTS_URL

        while (nextUrl != null) {
            val response = api.getProducts(nextUrl)
            if (!response.isSuccessful || response.body() == null) {
                return response
            }
            val body = response.body()!!
            allResults.addAll(body.results)
            nextUrl = body.next
        }

        return Response.success(
            ProductListResponse(
                count = allResults.size,
                next = null,
                previous = null,
                results = allResults
            )
        )
    }


    suspend fun addItemToCart(variantId: Int, quantity: Int): Response<AddCartItemResponse> {
        return api.addItemToCart(
            request = AddCartItemRequest(
                variant_id = variantId,
                quantity = quantity
            )
        )
    }

    suspend fun getFavorites(): Response<List<FavoriteItemDto>> {
        return api.getFavorites()
    }

    suspend fun addFavorite(productId: Int): Response<FavoriteResponse> {
        return api.addFavorite(productId)
    }

    suspend fun removeFavorite(favoriteEntryId: Int): Response<Unit> {
        return api.removeFavorite(favoriteEntryId)
    }


    suspend fun getUserCart(): Response<UserCartResponse> = api.getUserCart()

    suspend fun deleteCartItem(cartItemId: Int): Response<Unit> =
        api.deleteCartItem(url = "${ApiService.DELETE_ITEM_BASE}$cartItemId/")

    suspend fun getProductDetail(productId: Int): Response<ProductDetailResponse> =
        api.getProductDetail(url = "${ApiService.PRODUCT_DETAIL_BASE}$productId/?id=$productId")


    suspend fun getProfile(): Response<ProfileResponse> = api.getProfile()


    suspend fun refreshToken(refresh: String): Response<RefreshResponseModel> =
        api.refreshToken(RefreshRequest(refresh))


    suspend fun getCategories(): Response<List<CategoryDto>> = api.getCategories()


    suspend fun cartCheckout(): CartCheckoutResponse {
        return api.cartCheckout(url = ApiService.CART_CHECKOUT_URL)
    }
}