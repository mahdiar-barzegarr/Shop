package com.example.loginproject.repository

import com.example.loginproject.data.remote.api.ApiService
import com.example.loginproject.data.remote.request.RegisterRequest
import com.example.loginproject.data.remote.response.RegisterResponse
import com.example.loginproject.models.LoginModel
import com.example.loginproject.models.LoginResponseModel
import com.example.loginproject.models.RegisterResponseModel
import jakarta.inject.Inject
import retrofit2.Response

class ApiRepository @Inject constructor(

    private val api: ApiService

) {

    suspend fun login(

        phone: String,

        password: String

    ): Response<LoginResponseModel> {

        return api.login(

            LoginModel(

                phone,

                password

            )

        )

    }


    suspend fun register(

        request: RegisterRequest

    ): Response<RegisterResponseModel> {

        return api.register(request)

    }

}