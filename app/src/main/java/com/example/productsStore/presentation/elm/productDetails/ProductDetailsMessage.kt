package com.example.productsStore.presentation.elm.productDetails

sealed interface ProductDetailsMessage {
    data object ProductAddedToCart : ProductDetailsMessage

    data object ProductNotLoadedYet : ProductDetailsMessage

    data class Raw(
        val message: String,
    ) : ProductDetailsMessage
}