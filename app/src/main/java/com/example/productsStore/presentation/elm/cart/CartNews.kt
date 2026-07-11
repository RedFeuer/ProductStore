package com.example.productsStore.presentation.elm.cart

sealed interface CartNews {
    data object NavigateBack : CartNews

    data class OpenProductDetails(
        val productId: Int,
    ) : CartNews

    data class ShowMessage(
        val message: String,
    ) : CartNews
}