package com.example.productsStore.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductPreviewDto(
    val id : Int,
    val title : String,
    val price : Double,
    val brand : String? = null,
)
