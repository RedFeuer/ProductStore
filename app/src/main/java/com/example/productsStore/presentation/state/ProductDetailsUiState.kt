package com.example.productsStore.presentation.state

import com.example.productsStore.domain.model.ProductDetailsModel

sealed interface ProductDetailsUiState {
    data object Loading : ProductDetailsUiState
    data object Empty : ProductDetailsUiState
    data class Success(
        val product : ProductDetailsModel,
        val isStale : Boolean,
    ) : ProductDetailsUiState
    data class Error(
        val message : String,
    ) : ProductDetailsUiState
}