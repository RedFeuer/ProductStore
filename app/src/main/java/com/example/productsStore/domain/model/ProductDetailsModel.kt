package com.example.productsStore.domain.model

data class ProductDetailsModel(
    /** общее с preview из списка товаров */
    val id: Int,
    val title: String,
    val price: Double,
    val brand: String?,

    val description: String,
    val rating: Double,
    val weight: Int,
    val availabilityStatus: String, // можно потом отдельным классом сделать, в API два состояния всего
    val warrantyInformation: String,
)
