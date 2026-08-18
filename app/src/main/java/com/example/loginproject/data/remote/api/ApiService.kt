package com.example.loginproject.data.remote.api

import com.example.loginproject.data.remote.ApiConfig
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
import com.example.loginproject.data.remote.response.ProductListResponse
import com.example.loginproject.data.remote.response.ProfileResponse
import com.example.loginproject.models.LoginResponseModel
import com.example.loginproject.models.RefreshRequest
import com.example.loginproject.models.RefreshResponseModel
import com.example.loginproject.models.RegisterResponseModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Url

interface ApiService {

    companion object {
        val PRODUCTS_URL = "${ApiConfig.PRODUCTS_BASE}get_products/"
        val CATEGORIES_URL = "${ApiConfig.PRODUCTS_BASE}get_categories/"
        val PRODUCT_DETAIL_BASE = "${ApiConfig.PRODUCTS_BASE}get_product_detail/"
        val ADD_ITEM_URL = "${ApiConfig.CART_BASE}add_item/"
        val CART_URL = "${ApiConfig.CART_BASE}user_cart/"
        val DELETE_ITEM_BASE = "${ApiConfig.CART_BASE}delete_item/"
        val CART_CHECKOUT_URL = "${ApiConfig.CART_BASE}cart_checkout/"
    }


    @GET
    suspend fun getUserCart(@Url url: String = CART_URL): Response<UserCartResponse>

    @DELETE
    suspend fun deleteCartItem(@Url url: String): Response<Unit>
    @POST
    suspend fun addItemToCart(
        @Url url: String = ADD_ITEM_URL,
        @Body request: AddCartItemRequest
    ): Response<AddCartItemResponse>

    @POST("user_login/")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponseModel>

    @POST("new_access/")
    suspend fun refreshToken(@Body request: RefreshRequest): Response<RefreshResponseModel>

    @POST("user_register/")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponseModel>

    @GET("user_profile/")
    suspend fun getProfile(): Response<ProfileResponse>

    @GET("user_favorites/")
    suspend fun getFavorites(): Response<List<FavoriteItemDto>>

    @POST("add_favorite/{productId}/")
    suspend fun addFavorite(
        @Path("productId") productId: Int
    ): Response<FavoriteResponse>

    @DELETE("remove_favorite/{favoriteEntryId}/")
    suspend fun removeFavorite(
        @Path("favoriteEntryId") favoriteEntryId: Int
    ): Response<Unit>

    @GET
    suspend fun cartCheckout(@Url url: String): CartCheckoutResponse

    @GET
    suspend fun getProductDetail(@Url url: String): Response<ProductDetailResponse>

    @GET
    suspend fun getCategories(@Url url: String = CATEGORIES_URL): Response<List<CategoryDto>>

    @GET
    suspend fun getProducts(@Url url: String = PRODUCTS_URL): Response<ProductListResponse>
}