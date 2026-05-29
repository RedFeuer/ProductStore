package com.example.productsStore.presentation.elm.cart

sealed interface CartIntent {
    data object BackClicked : CartIntent

    data class ProductClicked(
        val productId: Int,
    ) : CartIntent

    data object ClearCartClicked : CartIntent

    data class ReminderCheckedChanged(
        val productId: Int,
        val enabled: Boolean,
    ) : CartIntent
}