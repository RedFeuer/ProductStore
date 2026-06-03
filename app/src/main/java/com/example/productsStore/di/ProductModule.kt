package com.example.productsStore.di

import com.example.productsStore.data.repositoryImpl.cart.CartProductsRepositoryImpl
import com.example.productsStore.data.repositoryImpl.productDetails.ProductDetailsRepositoryImpl
import com.example.productsStore.data.repositoryImpl.productsList.ProductsListRepositoryImpl
import com.example.productsStore.domain.repository.cart.CartProductsRepository
import com.example.productsStore.domain.repository.productDetails.ProductDetailsRepository
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
    abstract fun bindCartProductsRepository(impl: CartProductsRepositoryImpl) : CartProductsRepository

    @Binds
    @Singleton
    abstract fun bindProductsListRepository(impl: ProductsListRepositoryImpl) : ProductsListRepository

    @Binds
    @Singleton
    abstract fun bindProductDetailsRepository(impl: ProductDetailsRepositoryImpl) : ProductDetailsRepository
}