package com.example.productsStore.presentation.state

import com.example.productsStore.domain.model.ProductPreviewModel

sealed interface ProductListUiState {
    data object Loading : ProductListUiState
    data object Empty : ProductListUiState
    data class Success(
        val products : List<ProductPreviewModel>,
        val isPageLoading: Boolean = false, // при true пограничное состояние с Loading
        val pageErrorMessage: String? = null, // при не null пограничное состояние с Error
        val endReached: Boolean = false,
    ) : ProductListUiState
    data class Error(
        val message : String
    ) : ProductListUiState
}