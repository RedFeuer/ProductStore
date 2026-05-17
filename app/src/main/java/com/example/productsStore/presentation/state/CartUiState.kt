package com.example.productsStore.presentation.state

import com.example.productsStore.domain.model.CartProductModel

sealed interface CartUiState {
    data object Loading: CartUiState

    data object Empty: CartUiState

    data class Success(
        val products: List<CartProductModel>,
    ): CartUiState
}