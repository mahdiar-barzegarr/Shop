package com.example.loginproject.di

import com.example.loginproject.data.remote.api.ApiService
import com.example.loginproject.repository.ApiRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideRepository(

        api: ApiService

    ): ApiRepository {

        return ApiRepository(api)

    }

}