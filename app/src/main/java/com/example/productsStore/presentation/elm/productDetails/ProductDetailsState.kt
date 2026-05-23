package com.example.productsStore.presentation.elm.productDetails

import com.example.productsStore.domain.model.ProductDetailsModel
import com.example.productsStore.presentation.state.ProductDetailsUiState

data class ProductDetailsState(
    val productId: Int,
    val contentState: ProductDetailsContentState = ProductDetailsContentState.Loading,
)

sealed interface ProductDetailsContentState {
    data object Loading : ProductDetailsContentState

    data object Empty : ProductDetailsContentState

    data class Success(
        val product: ProductDetailsModel, val isStale: Boolean
    ) : ProductDetailsContentState

    data class Error(
        val message: String,
    ) : ProductDetailsContentState
}

fun ProductDetailsState.toUiState(): ProductDetailsUiState {
    return when (val currentContentState = contentState) {
        ProductDetailsContentState.Loading -> {
            ProductDetailsUiState.Loading
        }

        ProductDetailsContentState.Empty -> {
            ProductDetailsUiState.Empty
        }

        is ProductDetailsContentState.Success -> {
            ProductDetailsUiState.Success(
                product = currentContentState.product,
                isStale = currentContentState.isStale,
            )
        }

        is ProductDetailsContentState.Error -> {
            ProductDetailsUiState.Error(
                message = currentContentState.message,
            )
        }
    }
}