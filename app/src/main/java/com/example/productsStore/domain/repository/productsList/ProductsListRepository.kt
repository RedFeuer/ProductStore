package com.example.productsStore.domain.repository.productsList

import com.example.productsStore.domain.model.ProductPreviewModel
import com.example.productsStore.domain.model.ProductsPageModel
import kotlinx.coroutines.flow.Flow

interface ProductsListRepository {
    suspend fun observeProductPreviews(limit: Int) : Flow<List<ProductPreviewModel>>
    suspend fun refreshProductsPage(limit: Int, skip: Int) : ProductsPageModel
}