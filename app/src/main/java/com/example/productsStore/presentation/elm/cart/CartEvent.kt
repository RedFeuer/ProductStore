package com.example.productsStore.presentation.elm.cart

import com.example.productsStore.domain.model.CartProductModel

sealed interface CartEvent {
    data class UserIntent(
        val intent: CartIntent,
    )

    data class CartProductsLoaded(
        val products: List<CartProductModel>,
    ) : CartEvent

    data class CartProductsLoadingFailed(
        val message: String,
    ) : CartEvent

    data object CartCleared : CartEvent

    data class CartClearingFailed(
        val message: String,
    ) : CartEvent
}