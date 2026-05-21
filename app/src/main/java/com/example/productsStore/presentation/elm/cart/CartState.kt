package com.example.productsStore.presentation.elm.cart

import com.example.productsStore.domain.model.CartProductModel

sealed interface CartState {
    data object Loading : CartState

    data object Empty : CartState

    data class Success(
        val products: List<CartProductModel>,
    ) : CartState

    data class Error(
        val message: String,
    ) : CartState
}
