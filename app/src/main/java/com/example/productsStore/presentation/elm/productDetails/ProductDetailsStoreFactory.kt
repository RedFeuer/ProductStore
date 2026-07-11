package com.example.productsStore.presentation.elm.productDetails

import ru.tinkoff.kotea.core.KoteaStore
import ru.tinkoff.kotea.core.Store
import javax.inject.Inject

class ProductDetailsStoreFactory @Inject constructor(
    private val productDetailsCommandHandler: ProductDetailsCommandHandler,
) {
    fun create(
        productId: Int,
    ): Store<ProductDetailsState, ProductDetailsEvent, ProductDetailsNews> {
        return KoteaStore(
            initialState = ProductDetailsState(
                productId = productId,
            ),
            initialCommands = listOf(
                ProductDetailsCommand.ObserveCachedProductDetails(
                    productId = productId,
                ),
                ProductDetailsCommand.RefreshProductDetailsIfNeeded(
                    productId = productId,
                ),
            ),
            commandsFlowHandlers = listOf(productDetailsCommandHandler),
            update = ProductDetailsUpdate(),
        )
    }
}