package com.example.productsStore.presentation.elm.productDetails

import com.example.productsStore.domain.model.CachedProductDetailsModel

sealed interface ProductDetailsEvent {
    data class UserIntent(
        val intent: ProductDetailsIntent,
    ): ProductDetailsEvent

    data class CachedProductDetailsLoaded(
        val cachedProductDetails: CachedProductDetailsModel?,
    ) : ProductDetailsEvent

    data class CachedProductDetailsLoadingFailed(
        val message: String,
    ) : ProductDetailsEvent

    data object ProductDetailsRefreshed : ProductDetailsEvent

    data class ProductDetailsRefreshingFailed(
        val message: String,
    ) : ProductDetailsEvent

    data object ProductAddedToCart : ProductDetailsEvent

    data class ProductAddingToCartFailed(
        val message: String,
    ) : ProductDetailsEvent
}