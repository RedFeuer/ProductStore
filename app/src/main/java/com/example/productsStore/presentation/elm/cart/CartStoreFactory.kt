package com.example.productsStore.presentation.elm.cart

import ru.tinkoff.kotea.core.KoteaStore
import ru.tinkoff.kotea.core.Store
import javax.inject.Inject

class CartStoreFactory @Inject constructor(
    private val cartCommandHandler: CartCommandHandler,
) {
    fun create(): Store<CartState, CartEvent, CartNews> {
        return KoteaStore(
            initialState = CartState.Loading,
            initialCommands = listOf(CartCommand.ObserveCartProducts),
            commandsFlowHandlers = listOf(cartCommandHandler),
            update = CartUpdate(),
        )
    }
}