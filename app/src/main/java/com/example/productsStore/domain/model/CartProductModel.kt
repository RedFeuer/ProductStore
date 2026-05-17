package com.example.productsStore.domain.model

/** модель товара в корзине товаров */
data class CartProductModel(
    val productId: Int,
    val title: String,
    val price: Double,
    val brand: String?,
    val quantity: Int,
)
