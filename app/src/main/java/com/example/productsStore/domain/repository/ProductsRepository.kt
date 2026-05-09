package com.example.productsStore.domain.repository

import com.example.productsStore.domain.model.ProductDetailsModel
import com.example.productsStore.domain.model.ProductsPageModel

interface ProductsRepository {
    suspend fun getProductsPage(limit: Int, skip: Int) : ProductsPageModel

    suspend fun getProductsDetails(id : Int) : ProductDetailsModel
}