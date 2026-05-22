package com.example.productsStore.presentation.elm.productsList

import ru.tinkoff.kotea.core.KoteaStore
import ru.tinkoff.kotea.core.Store
import javax.inject.Inject

class ProductsListStoreFactory @Inject constructor(
    private val productListCommandHandler: ProductListCommandHandler,
) {
    fun create(): Store<ProductsListState, ProductsListEvent, ProductsListNews> {
        return KoteaStore(
            initialState = ProductsListState(),
            initialCommands = emptyList(),
            commandsFlowHandlers = listOf(productListCommandHandler),
            update = ProductsListUpdate(),
        )
    }
}