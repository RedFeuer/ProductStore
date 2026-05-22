package com.example.productsStore.presentation.elm.productsList

import com.example.productsStore.domain.model.ProductPreviewModel
import com.example.productsStore.domain.model.ProductsPageModel

sealed interface ProductsListEvent {
    data class UserIntent(
        val intent: ProductsListIntent,
    ) : ProductsListEvent

    data class CachedProductsLoaded(
        val products: List<ProductPreviewModel>,
    ) : ProductsListEvent

    data class CachedProductsLoadingFailed(
        val message: String,
    ) : ProductsListEvent

    data class PageRefreshed(
        val productsPage: ProductsPageModel,
        val isInitialLoading: Boolean,
    ) : ProductsListEvent

    data class PageRefreshingFailed(
        val message: String,
        val isInitialLoading: Boolean,
    ) : ProductsListEvent
}