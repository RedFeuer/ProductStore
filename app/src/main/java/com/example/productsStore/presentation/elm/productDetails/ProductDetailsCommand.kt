package com.example.productsStore.presentation.elm.productDetails

import com.example.productsStore.domain.model.ProductDetailsModel

sealed interface ProductDetailsCommand {
    data class ObserveCachedProductDetails(
        val productId: Int,
    ) : ProductDetailsCommand

    data class RefreshProductDetailsIfNeeded(
        val productId: Int,
    ) : ProductDetailsCommand

    data class AddProductToCart(
        val product: ProductDetailsModel,
    ) : ProductDetailsCommand
}