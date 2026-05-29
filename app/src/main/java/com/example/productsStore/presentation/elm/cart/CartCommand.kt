package com.example.productsStore.presentation.elm.cart

sealed interface CartCommand {
    data object ObserveCartProducts : CartCommand

    data object ClearCart : CartCommand

    data class SetProductReminderEnabled(
        val productId: Int,
        val enabled: Boolean,
    ) : CartCommand
}