package com.example.productsStore.presentation.elm.cart

sealed interface CartCommand {
    data object ObserveCartProducts : CartCommand

    data object ClearCart : CartCommand
}