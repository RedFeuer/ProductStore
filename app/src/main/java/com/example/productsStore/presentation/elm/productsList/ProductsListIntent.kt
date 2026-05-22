package com.example.productsStore.presentation.elm.productsList

sealed interface ProductsListIntent {
    data class PageSizeCalculated(
        val pageSize: Int,
    ) : ProductsListIntent

    data class ProductClicked(
        val productId: Int,
    ) : ProductsListIntent

    data object CartClicked : ProductsListIntent

    data object LoadNextPage : ProductsListIntent

    data object RetryInitialLoading : ProductsListIntent

    data object RetryNextPage : ProductsListIntent
}