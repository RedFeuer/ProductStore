package com.example.productsStore.presentation.state

import com.example.productsStore.domain.model.ProductPreviewModel

sealed interface ProductListUiState {
    data object Loading : ProductListUiState
    data object Empty : ProductListUiState
    data class Success(
        val products : List<ProductPreviewModel>
    ) : ProductListUiState
    data class Error(
        val message : String
    ) : ProductListUiState
}