package com.example.productsStore.presentation.elm.productDetails

import com.example.productsStore.domain.model.ProductDetailsModel
import com.example.productsStore.presentation.state.ProductDetailsUiState

sealed interface ProductDetailsState {
    data object Loading : ProductDetailsState

    data object Empty : ProductDetailsState

    data class Success(
        val product: ProductDetailsModel,
        val isStale: Boolean
    ) : ProductDetailsState

    data class Error(
        val message: String,
    ) : ProductDetailsState
}

fun ProductDetailsState.toUiState(): ProductDetailsUiState {
    return when (this) {
        ProductDetailsState.Loading -> {
            ProductDetailsUiState.Loading
        }

        ProductDetailsState.Empty -> {
            ProductDetailsUiState.Empty
        }

        is ProductDetailsState.Success -> {
            ProductDetailsUiState.Success(
                product = this.product,
                isStale = this.isStale,
            )
        }

        is ProductDetailsState.Error -> {
            ProductDetailsUiState.Error(
                message = this.message,
            )
        }
    }
}