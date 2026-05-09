package com.example.productsStore.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductDetailsDto(
    val id: Int,
    val title: String,
    val price: Double,
    val brand: String? = null,

    val description: String,
    val rating: Double,
    val weight: Int,
    val availabilityStatus: String,
    val warrantyInformation: String,
)
