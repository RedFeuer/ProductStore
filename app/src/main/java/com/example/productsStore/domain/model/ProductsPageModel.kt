package com.example.productsStore.domain.model

data class ProductsPageModel(
    val products: List<ProductPreviewModel>,
    val total: Int,
    val skip: Int,
    val limit: Int,
)
