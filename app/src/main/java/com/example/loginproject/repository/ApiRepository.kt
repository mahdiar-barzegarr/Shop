package com.example.loginproject.repository

import com.example.loginproject.data.remote.api.ApiService
import com.example.loginproject.data.remote.request.LoginRequest
import com.example.loginproject.data.remote.request.RegisterRequest
import com.example.loginproject.data.remote.response.ProductListResponse
import com.example.loginproject.data.remote.response.ProfileResponse
import com.example.loginproject.data.remote.response.RegisterResponse
import com.example.loginproject.models.LoginModel
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


    suspend fun getProducts(): Response<ProductListResponse> = api.getProducts()


    suspend fun getProfile(): Response<ProfileResponse> = api.getProfile()


    suspend fun refreshToken(refresh: String): Response<RefreshResponseModel> =
        api.refreshToken(RefreshRequest(refresh))

}