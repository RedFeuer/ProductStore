package com.example.productsStore.domain.repository.cart

import com.example.productsStore.domain.model.CartProductModel
import com.example.productsStore.domain.model.ProductDetailsModel
import kotlinx.coroutines.flow.Flow

interface CartProductsRepository {
    suspend fun getProductsWithEnabledReminders(): List<CartProductModel>
    suspend fun setProductReminderEnabled(productId: Int, enabled: Boolean)
    fun observeCartProducts() : Flow<List<CartProductModel>>
    suspend fun addProductToCart(product: ProductDetailsModel)
    suspend fun clearCart()
}