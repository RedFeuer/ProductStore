package com.example.productsStore.presentation.elm.productsList

sealed interface ProductsListNews {
    data class OpenProductsDetails(
        val productId: Int,
    ) : ProductsListNews

    data object OpenCart : ProductsListNews
}