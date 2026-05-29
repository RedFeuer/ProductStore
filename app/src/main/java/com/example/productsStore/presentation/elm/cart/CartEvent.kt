package com.example.productsStore.presentation.elm.cart

import com.example.productsStore.domain.model.CartProductModel

sealed interface CartEvent {
    data class UserIntent(
        val intent: CartIntent,
    ) : CartEvent

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

    data object ProductReminderChanged : CartEvent

    data class ProductReminderChangingFailed(
        val message: String,
    ) : CartEvent
}