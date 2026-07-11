package com.example.productsStore.domain.repository.productDetails

import com.example.productsStore.domain.model.CachedProductDetailsModel
import kotlinx.coroutines.flow.Flow

interface ProductDetailsRepository {
    fun observeProductDetails(id: Int) : Flow<CachedProductDetailsModel?>
    suspend fun refreshProductDetailsIfNeeded(id : Int)
}