package com.example.productsStore.presentation.elm.cart

sealed interface CartMessage {
    data object CartCleared : CartMessage

    data object ProductNotFound : CartMessage

    data class Raw(
        val message: String,
    ) : CartMessage
}