package com.example.productsStore.domain.model

data class ProductsPage(
    val products: List<ProductPreview>,
    val total: Int,
    val skip: Int,
    val limit: Int,
)
