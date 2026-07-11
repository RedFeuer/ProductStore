package com.example.productsStore.presentation.elm.cart

import com.example.productsStore.domain.model.CartProductModel

sealed interface CartCommand {
    data object ObserveCartProducts : CartCommand

    data object ClearCart : CartCommand

    data class SetProductReminderEnabled(
        val product: CartProductModel,
        val enabled: Boolean,
    ) : CartCommand
}