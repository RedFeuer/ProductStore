package com.example.productsStore.di

import com.example.productsStore.data.repositoryImpl.ProductsRepositoryImpl
import com.example.productsStore.data.repositoryImpl.productsList.ProductsListRepositoryImpl
import com.example.productsStore.domain.repository.ProductsRepository
import com.example.productsStore.domain.repository.productsList.ProductsListRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProductModule {
    @Binds
    @Singleton
    abstract fun bindProductsRepository(impl: ProductsRepositoryImpl) : ProductsRepository

    @Binds
    @Singleton
    abstract fun bindProductsListRepository(impl: ProductsListRepositoryImpl) : ProductsListRepository
}