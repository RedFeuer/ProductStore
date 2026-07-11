package com.example.productsStore.presentation.elm.productsList

import ru.tinkoff.kotea.core.dsl.DslUpdate

class ProductsListUpdate :
    DslUpdate<ProductsListState, ProductsListEvent, ProductsListCommand, ProductsListNews>() {

    override fun DslUpdate<ProductsListState, ProductsListEvent, ProductsListCommand, ProductsListNews>.NextBuilder.update(
        event: ProductsListEvent
    ) {
        when (event) {
            is ProductsListEvent.UserIntent -> {
                handleIntent(event.intent)
            }

            is ProductsListEvent.CachedProductsLoaded -> {
                handleCachedProductsLoaded(event)
            }

            is ProductsListEvent.CachedProductsLoadingFailed -> {
                handleCachedProductsLoadingFailed(event)
            }

            is ProductsListEvent.PageRefreshed -> {
                handlePageRefreshed(event)
            }

            is ProductsListEvent.PageRefreshingFailed -> {
                handlePageRefreshingFailed(event)
            }
        }
    }

    private fun DslUpdate<ProductsListState, ProductsListEvent, ProductsListCommand, ProductsListNews>.NextBuilder.handleIntent(
        intent: ProductsListIntent,
    ) {
        when (intent) {
            is ProductsListIntent.PageSizeCalculated -> {
                handlePageSizeCalculated(intent.pageSize)
            }

            is ProductsListIntent.ProductClicked -> {
                news(
                    ProductsListNews.OpenProductsDetails(
                        productId = intent.productId
                    )
                )
            }

            ProductsListIntent.CartClicked -> {
                news(ProductsListNews.OpenCart)
            }

            ProductsListIntent.LoadNextPage -> {
                handleLoadNextPage()
            }

            ProductsListIntent.RetryInitialLoading -> {
                handleRetryInitialLoading()
            }

            ProductsListIntent.RetryNextPage -> {
                handleRetryNextPage()
            }
        }
    }

    private fun DslUpdate<ProductsListState, ProductsListEvent, ProductsListCommand, ProductsListNews>.NextBuilder.handlePageSizeCalculated(
        calculatedPageSize: Int,
    ) {
        if (state.pageSize != null) return

        state {
            copy(
                pageSize = calculatedPageSize,
                loadedLimit = calculatedPageSize,
                nextSkip = FIRST_PAGE_SKIP,
                totalProducts = null,
                isInitialLoading = true,
                isEmptyConfirmed = false,
                errorMessage = null,
                isPageLoading = false,
                pageErrorMessage = null,
                endReached = false,
            )
        }

        commands(
            ProductsListCommand.ObserveCachedProducts(
                limit = calculatedPageSize,
            )
        )

        commands(
            ProductsListCommand.RefreshPage(
                limit = calculatedPageSize,
                skip = FIRST_PAGE_SKIP,
                isInitialLoading = true,
            )
        )
    }

    private fun DslUpdate<ProductsListState, ProductsListEvent, ProductsListCommand, ProductsListNews>.NextBuilder.handleRetryInitialLoading() {
        val currentPageSize = state.pageSize ?: return

        state {
            copy(
                loadedLimit = currentPageSize,
                nextSkip = FIRST_PAGE_SKIP,
                totalProducts = null,
                isInitialLoading =  true,
                isEmptyConfirmed = false,
                errorMessage = null,
                isPageLoading = false,
                pageErrorMessage = null,
                endReached = false,
            )
        }

         commands(
             ProductsListCommand.ObserveCachedProducts(
                 limit = currentPageSize
             )
         )

        commands(
            ProductsListCommand.RefreshPage(
                limit = currentPageSize,
                skip = FIRST_PAGE_SKIP,
                isInitialLoading = true,
            )
        )
    }

    private fun DslUpdate<ProductsListState, ProductsListEvent, ProductsListCommand, ProductsListNews>.NextBuilder.handleLoadNextPage() {
        val currentPageSize = state.pageSize ?: return

        if (state.products.isEmpty()) return
        if (state.isPageLoading) return
        if (state.endReached) return
        if (state.pageErrorMessage != null) return

        val total = state.totalProducts

        if (total != null && state.nextSkip >= total) {
            state {
                copy(endReached = true)
                return
            }
        }

        val requestSkip = state.nextSkip
        val newLoadedLimit = state.loadedLimit + currentPageSize

        state {
            copy(
                loadedLimit = newLoadedLimit,
                isPageLoading = true,
                pageErrorMessage = null,
            )
        }

        commands(
            ProductsListCommand.ObserveCachedProducts(
                limit = newLoadedLimit
            )
        )

        commands(
            ProductsListCommand.RefreshPage(
                limit = currentPageSize,
                skip = requestSkip,
                isInitialLoading = false,
            )
        )
    }

    private fun DslUpdate<ProductsListState, ProductsListEvent, ProductsListCommand, ProductsListNews>.NextBuilder.handleRetryNextPage() {
        val currentPageSize = state.pageSize ?: return

        if (state.isPageLoading) return
        if (state.endReached) return

        state {
            copy(
                isPageLoading = true,
                pageErrorMessage = null,
            )
        }

        commands(
            ProductsListCommand.RefreshPage(
                limit = currentPageSize,
                skip = state.nextSkip,
                isInitialLoading = false,
            )
        )
    }

    private fun DslUpdate<ProductsListState, ProductsListEvent, ProductsListCommand, ProductsListNews>.NextBuilder.handleCachedProductsLoaded(
        event: ProductsListEvent.CachedProductsLoaded,
    ) {
        if (event.products.isEmpty()) {
            if (state.products.isEmpty() && !state.isEmptyConfirmed && state.errorMessage == null) {
                state {
                    copy(
                        isInitialLoading = true,
                    )
                }
            }

            return
        }

        val endReachedByTotal = state.totalProducts?.let { total ->
            event.products.size >= total
        } ?: false

        state {
            copy(
                products = event.products,
                isInitialLoading = false,
                isEmptyConfirmed = false,
                errorMessage = null,
                nextSkip = maxOf(nextSkip, event.products.size),
                endReached = endReached || endReachedByTotal,
            )
        }
    }

    private fun DslUpdate<ProductsListState, ProductsListEvent, ProductsListCommand, ProductsListNews>.NextBuilder.handleCachedProductsLoadingFailed(
        event: ProductsListEvent.CachedProductsLoadingFailed,
    ) {
        if (state.products.isNotEmpty()) {
            state {
                copy(
                    pageErrorMessage = event.message,
                    isPageLoading = false,
                )
            }
        } else {
            state {
                copy(
                    errorMessage = event.message,
                    isInitialLoading = false,
                )
            }
        }
    }

    private fun DslUpdate<ProductsListState, ProductsListEvent, ProductsListCommand, ProductsListNews>.NextBuilder.handlePageRefreshed(
        event: ProductsListEvent.PageRefreshed,
    ) {
        val loadedProducts = event.productsPage.products

        val newNextSkip = maxOf(state.nextSkip, (event.productsPage.skip + loadedProducts.size))

        val isEndReached = loadedProducts.isEmpty() || newNextSkip >= event.productsPage.total

        state {
            copy(
                totalProducts = event.productsPage.total,
                nextSkip = newNextSkip,
                isPageLoading = false,
                pageErrorMessage = null,
                endReached = isEndReached,
                isEmptyConfirmed = products.isEmpty() || loadedProducts.isEmpty(),
                isInitialLoading = products.isEmpty() || loadedProducts.isNotEmpty(),
            )
        }
    }

    private fun DslUpdate<ProductsListState, ProductsListEvent, ProductsListCommand, ProductsListNews>.NextBuilder.handlePageRefreshingFailed(
        event: ProductsListEvent.PageRefreshingFailed,
    ) {
        if (state.products.isNotEmpty()) {
            state {
                copy(
                    isPageLoading = false,
                    pageErrorMessage = event.message,
                    isInitialLoading = false
                )
            }
        } else {
            state {
                copy(
                    isPageLoading = false,
                    errorMessage = event.message,
                    isInitialLoading = false
                )
            }
        }
    }

    private companion object {
        const val FIRST_PAGE_SKIP = 0
    }
}