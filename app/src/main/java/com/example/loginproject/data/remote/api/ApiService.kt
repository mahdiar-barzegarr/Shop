package com.example.loginproject.data.remote.api

import com.example.loginproject.data.remote.request.LoginRequest
import com.example.loginproject.data.remote.request.RegisterRequest
import com.example.loginproject.data.remote.response.ProfileResponse
import com.example.loginproject.models.LoginResponseModel
import com.example.loginproject.models.RefreshRequest
import com.example.loginproject.models.RefreshResponseModel
import com.example.loginproject.models.RegisterResponseModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("user_login/")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponseModel>

    @POST("new_access/")
    suspend fun refreshToken(@Body request: RefreshRequest): Response<RefreshResponseModel>


    @POST("user_register/")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponseModel>

    @GET("profile/")
    suspend fun getProfile(): Response<ProfileResponse>


}