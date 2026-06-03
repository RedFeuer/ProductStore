package com.example.productsStore.presentation.elm.productsList

import com.example.productsStore.domain.model.ProductPreviewModel
import com.example.productsStore.presentation.state.ProductListUiState
import kotlinx.collections.immutable.toImmutableList

data class ProductsListState(
    val products: List<ProductPreviewModel> = emptyList(),

    val isInitialLoading: Boolean = true,
    val isEmptyConfirmed: Boolean = false,
    val errorMessage: String? = null,

    val isPageLoading: Boolean = false,
    val pageErrorMessage: String? = null,
    val endReached: Boolean = false,

    val pageSize: Int? = null,
    val loadedLimit: Int = 0,
    val nextSkip: Int = 0,
    val totalProducts: Int? = null,
)

/** маппер в UI-state для экрана */
fun ProductsListState.toUiState(): ProductListUiState {
    return when {
        products.isNotEmpty() -> {
            ProductListUiState.Success(
                products = products.toImmutableList(),
                isPageLoading = isPageLoading,
                pageErrorMessage = pageErrorMessage,
                endReached = endReached,
            )
        }

        errorMessage != null -> {
            ProductListUiState.Error(
                message = errorMessage,
            )
        }

        isEmptyConfirmed -> {
            ProductListUiState.Empty
        }

        else -> {
            ProductListUiState.Loading
        }
    }
}