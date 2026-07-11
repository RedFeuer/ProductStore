package com.example.productsStore.presentation.elm.productsList

sealed interface ProductsListCommand {
    data class ObserveCachedProducts(
        val limit: Int,
    ) : ProductsListCommand

    data class RefreshPage(
        val limit: Int,
        val skip: Int,
        val isInitialLoading: Boolean,
    ) : ProductsListCommand
}