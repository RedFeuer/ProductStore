package com.example.productsStore.presentation.elm.productDetails

sealed interface ProductDetailsNews {
    data object NavigateBack : ProductDetailsNews

    data class ShowMessage(
        val message: String,
    ) : ProductDetailsNews
}