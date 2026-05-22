package com.example.productsStore.domain.repository

import com.example.productsStore.domain.model.ProductDetailsModel
import com.example.productsStore.domain.model.ProductPreviewModel
import com.example.productsStore.domain.model.ProductsPageModel
import kotlinx.coroutines.flow.Flow

interface ProductsRepository {
    suspend fun observeProductPreviews(limit: Int) : Flow<List<ProductPreviewModel>>
    suspend fun refreshProductsPage(limit: Int, skip: Int) : ProductsPageModel

    suspend fun observeProductDetails(id: Int) : Flow<ProductDetailsModel?>
    suspend fun refreshProductDetails(id : Int) : ProductDetailsModel
}