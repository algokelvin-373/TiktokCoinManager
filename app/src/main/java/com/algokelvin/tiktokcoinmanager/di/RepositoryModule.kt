package com.algokelvin.tiktokcoinmanager.di

import com.algokelvin.tiktokcoinmanager.data.remote.TransactionApiService
import com.algokelvin.tiktokcoinmanager.data.repository.TransactionRepositoryImpl
import com.algokelvin.tiktokcoinmanager.domain.repository.TransactionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideTransactionRepository(apiService: TransactionApiService): TransactionRepository {
        return TransactionRepositoryImpl(apiService)
    }
}

