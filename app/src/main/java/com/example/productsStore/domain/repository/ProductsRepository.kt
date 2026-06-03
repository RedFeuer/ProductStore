package com.example.productsStore.domain.repository

import com.example.productsStore.domain.model.CachedProductDetailsModel
import com.example.productsStore.domain.model.CartProductModel
import com.example.productsStore.domain.model.ProductDetailsModel
import com.example.productsStore.domain.model.ProductPreviewModel
import com.example.productsStore.domain.model.ProductsPageModel
import kotlinx.coroutines.flow.Flow

interface ProductsRepository {
    suspend fun getProductsWithEnabledReminders(): List<CartProductModel>
    suspend fun setProductReminderEnabled(productId: Int, enabled: Boolean)

    fun observeCartProducts() : Flow<List<CartProductModel>>

    suspend fun addProductToCart(product: ProductDetailsModel)

    suspend fun clearCart()
}