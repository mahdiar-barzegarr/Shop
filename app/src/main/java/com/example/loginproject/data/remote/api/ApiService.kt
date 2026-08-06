package com.example.loginproject.data.remote.api

import com.example.loginproject.data.remote.request.RegisterRequest
import com.example.loginproject.models.LoginModel
import com.example.loginproject.models.LoginResponseModel
import com.example.loginproject.models.RegisterResponseModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("login/")
    suspend fun login(

        @Body request: LoginModel

    ): Response<LoginResponseModel>


    @POST("register/")
    suspend fun register(

        @Body request: RegisterRequest

    ): Response<RegisterResponseModel>

}