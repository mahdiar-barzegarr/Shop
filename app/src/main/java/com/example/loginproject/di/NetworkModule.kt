package com.example.loginproject.di

import com.example.loginproject.data.remote.api.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {


    private const val BASE_URL = "http://192.168.1.102:8000/accounts/api/v1/"
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {


        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()

    }


    @Provides
    @Singleton
    fun provideAuthApi(
        retrofit: Retrofit
    ): ApiService {

        return retrofit.create(ApiService::class.java)

    }

}