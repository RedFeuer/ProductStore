package com.example.productsStore.di

import com.example.productsStore.data.provider.time.SystemCurrentTimeProvider
import com.example.productsStore.domain.provider.time.CurrentTimeProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TimeModule {
    @Binds
    @Singleton
    abstract fun bindCurrentTimeProvider(impl: SystemCurrentTimeProvider) : CurrentTimeProvider
}