package com.example.loginproject.data.remote.api

import com.example.loginproject.data.remote.request.LoginRequest
import com.example.loginproject.data.remote.request.RegisterRequest
import com.example.loginproject.data.remote.response.CategoryDto
import com.example.loginproject.data.remote.response.ProductDetailResponse
import com.example.loginproject.data.remote.response.ProductListResponse
import com.example.loginproject.data.remote.response.ProfileResponse
import com.example.loginproject.models.LoginResponseModel
import com.example.loginproject.models.RefreshRequest
import com.example.loginproject.models.RefreshResponseModel
import com.example.loginproject.models.RegisterResponseModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface ApiService {

    @POST("user_login/")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponseModel>

    @POST("new_access/")
    suspend fun refreshToken(@Body request: RefreshRequest): Response<RefreshResponseModel>


    @POST("user_register/")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponseModel>

    @GET("profile/")
    suspend fun getProfile(): Response<ProfileResponse>


    companion object {
        const val PRODUCTS_URL = "http://192.168.1.102:8000/products/api/v1/get_products/"
        const val CATEGORIES_URL = "http://192.168.1.102:8000/products/api/v1/get_categories/"
        const val PRODUCT_DETAIL_BASE = "http://192.168.1.102:8000/products/api/v1/get_product_detail/"
    }

    @GET
    suspend fun getProductDetail(@Url url: String): Response<ProductDetailResponse>

    @GET
    suspend fun getCategories(@Url url: String = CATEGORIES_URL): Response<List<CategoryDto>>

    @GET
    suspend fun getProducts(@Url url: String = PRODUCTS_URL): Response<ProductListResponse>

}