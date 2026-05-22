package com.example.productsStore.presentation.elm.cart

import ru.tinkoff.kotea.core.dsl.DslUpdate

class CartUpdate : DslUpdate<CartState, CartEvent, CartCommand, CartNews>() {

    override fun NextBuilder<CartState, CartEvent, CartCommand, CartNews>.update(event: CartEvent) {
        when (event) {
            is CartEvent.UserIntent -> {
                handleIntent(event.intent)
            }

            is CartEvent.CartProductsLoaded -> {
                state {
                    if (event.products.isEmpty()) {
                        CartState.Empty
                    } else {
                        CartState.Success(
                            products = event.products,
                        )
                    }
                }
            }

            is CartEvent.CartProductsLoadingFailed -> {
                state {
                    CartState.Error(
                        message = event.message,
                    )
                }
            }

            CartEvent.CartCleared -> {
                news(
                    CartNews.ShowMessage(
                        message = "Корзина очищена"
                    )
                )
            }

            is CartEvent.CartClearingFailed -> {
                news(
                    CartNews.ShowMessage(
                        message = event.message,
                    )
                )
            }
        }
    }

    private fun NextBuilder<CartState, CartEvent, CartCommand, CartNews>.handleIntent(intent: CartIntent) {
        when (intent) {
            CartIntent.BackClicked -> {
                news(CartNews.NavigateBack)
            }

            is CartIntent.ProductClicked -> {
                news(
                    CartNews.OpenProductDetails(
                        productId = intent.productId,
                    )
                )
            }

            CartIntent.ClearCartClicked -> {
                commands(CartCommand.ClearCart)
            }
        }
    }
}