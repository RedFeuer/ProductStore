package com.example.productsStore.data.local.mapper

import com.example.productsStore.data.local.entity.CartProductEntity
import com.example.productsStore.domain.model.CartProductModel
import javax.inject.Inject

class CartProductEntityMapper @Inject constructor() {
    fun toDomainModel(cartProductEntity: CartProductEntity): CartProductModel {
        return CartProductModel(
            productId = cartProductEntity.productId,
            title = cartProductEntity.title,
            price = cartProductEntity.price,
            brand = cartProductEntity.brand,
            quantity = cartProductEntity.quantity,
            reminderEnabled = cartProductEntity.reminderEnabled,
        )
    }

    fun toEntity(cartProductModel: CartProductModel) : CartProductEntity {
        return CartProductEntity(
            productId = cartProductModel.productId,
            title = cartProductModel.title,
            price = cartProductModel.price,
            brand = cartProductModel.brand,
            quantity = cartProductModel.quantity,
            reminderEnabled = cartProductModel.reminderEnabled,
        )
    }
}