package com.example.productsStore.presentation.elm.productDetails

sealed interface ProductDetailsIntent {
    data object BackClicked : ProductDetailsIntent

    data object RetryClicked : ProductDetailsIntent

    data object AddToCartClicked : ProductDetailsIntent
}