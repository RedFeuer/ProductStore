package com.example.productsStore.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductsPageDto(
    val products: List<ProductPreviewDto>,
    val total: Int,
    val skip: Int,
    val limit: Int,
) {
}