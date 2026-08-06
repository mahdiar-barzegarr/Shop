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

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {

        return Retrofit.Builder()

            .baseUrl("http://127.0.0.1:8000/accounts/")

            .addConverterFactory(GsonConverterFactory.create())

            .build()

    }


    @Provides
    @Singleton
    fun provideApi(

        retrofit: Retrofit

    ): ApiService {

        return retrofit.create(ApiService::class.java)

    }

}