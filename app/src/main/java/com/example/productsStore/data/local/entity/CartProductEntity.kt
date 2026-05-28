package com.example.productsStore.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("cart_products")
data class CartProductEntity(
    @PrimaryKey
    val productId: Int,
    val title: String,
    val price: Double,
    val brand: String?,
    val quantity: Int,
    val reminderEnabled: Boolean = false,
)
