package com.example.productsStore.presentation.elm.productDetails

import ru.tinkoff.kotea.core.dsl.DslUpdate

class ProductDetailsUpdate :
    DslUpdate<ProductDetailsState, ProductDetailsEvent, ProductDetailsCommand, ProductDetailsNews>() {

    override fun DslUpdate<ProductDetailsState, ProductDetailsEvent, ProductDetailsCommand, ProductDetailsNews>.NextBuilder.update(
        event: ProductDetailsEvent
    ) {
        when (event) {
            is ProductDetailsEvent.UserIntent -> {
                handleIntent(event.intent)
            }

            is ProductDetailsEvent.CachedProductDetailsLoaded -> {
                handleCachedProductDetailsIsLoaded(event)
            }

            is ProductDetailsEvent.CachedProductDetailsLoadingFailed -> {
                state {
                    copy(
                        contentState = ProductDetailsContentState.Error(
                            message = event.message,
                        )
                    )
                }
            }

            ProductDetailsEvent.ProductDetailsRefreshed -> {
                // репозиторий обновит
            }

            is ProductDetailsEvent.ProductDetailsRefreshingFailed -> {
                handleProductDetailsRefreshingFailed(event = event)
            }

            is ProductDetailsEvent.ProductAddedToCart -> {
                news(
                    ProductDetailsNews.ShowMessage(
                        message = "Товар добавлен в корзину",
                    )
                )
            }

            is ProductDetailsEvent.ProductAddingToCartFailed -> {
                news(
                    ProductDetailsNews.ShowMessage(
                        message = event.message,
                    )
                )
            }
        }
    }

    private fun DslUpdate<ProductDetailsState, ProductDetailsEvent, ProductDetailsCommand, ProductDetailsNews>.NextBuilder.handleIntent(
        intent: ProductDetailsIntent,
    ) {
        when (intent) {
            ProductDetailsIntent.BackClicked -> {
                news(ProductDetailsNews.NavigateBack)
            }

            ProductDetailsIntent.RetryClicked -> {
                state {
                    copy(
                        contentState = ProductDetailsContentState.Loading,
                    )
                }

                commands(
                    ProductDetailsCommand.RefreshProductDetailsIfNeeded(
                        productId = state.productId,
                    )
                )
            }

            ProductDetailsIntent.AddToCartClicked -> {
                val currentContentState = state.contentState as? ProductDetailsContentState.Success

                if (currentContentState != null) {
                    commands(
                        ProductDetailsCommand.AddProductToCart(
                            product = currentContentState.product,
                        )
                    )
                } else {
                    news(
                        ProductDetailsNews.ShowMessage(
                            message = "Товар еще не загружен"
                        )
                    )
                }
            }
        }
    }

    private fun DslUpdate<ProductDetailsState, ProductDetailsEvent, ProductDetailsCommand, ProductDetailsNews>.NextBuilder.handleCachedProductDetailsIsLoaded(
        event: ProductDetailsEvent.CachedProductDetailsLoaded,
    ) {
        val cachedProductDetails = event.cachedProductDetails

        if (cachedProductDetails == null) {
            if (state !is ProductDetailsContentState.Success) {
                state {
                    copy(
                        contentState = ProductDetailsContentState.Loading
                    )
                }
            }

            return
        }

        state {
            copy (
                contentState = ProductDetailsContentState.Success(
                    product = cachedProductDetails.product,
                    isStale = cachedProductDetails.isStale,
                )
            )
        }
    }

    private fun DslUpdate<ProductDetailsState, ProductDetailsEvent, ProductDetailsCommand, ProductDetailsNews>.NextBuilder.handleProductDetailsRefreshingFailed(
        event: ProductDetailsEvent.ProductDetailsRefreshingFailed,
    ) {
        if (state.contentState is ProductDetailsContentState.Success) {
            news(
                ProductDetailsNews.ShowMessage(
                    message = event.message,
                )
            )
        } else {
            state {
                copy(
                    contentState = ProductDetailsContentState.Error(
                        message = event.message,
                    )
                )
            }
        }
    }
}