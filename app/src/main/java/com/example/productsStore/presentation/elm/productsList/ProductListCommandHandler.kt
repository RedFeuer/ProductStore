package com.example.productsStore.presentation.elm.productsList

import com.example.productsStore.domain.useCase.ObserveProductPreviewsUseCase
import com.example.productsStore.domain.useCase.RefreshProductsPageUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import ru.tinkoff.kotea.core.CommandsFlowHandler
import javax.inject.Inject

class ProductListCommandHandler @Inject constructor(
    private val observeProductPreviewsUseCase: ObserveProductPreviewsUseCase,
    private val refreshProductsPageUseCase: RefreshProductsPageUseCase,
) : CommandsFlowHandler<ProductsListCommand, ProductsListEvent> {
    override fun handle(
        commands: Flow<ProductsListCommand>
    ): Flow<ProductsListEvent> {
        return channelFlow {
            var observeProductsJob: Job? = null

            commands.collect { command ->
                when (command) {
                    is ProductsListCommand.ObserveCachedProducts -> {
                        observeProductsJob?.cancel()

                        observeProductsJob = launch {
                            observeCachedProducts(
                                limit = command.limit
                            ).collect { event ->
                                send(event)
                            }
                        }
                    }

                    is ProductsListCommand.RefreshPage -> {
                        launch {
                            refreshPage(
                                command = command,
                            ).collect { event ->
                                send(event)
                            }
                        }
                    }
                }
            }
            awaitClose {
                observeProductsJob?.cancel()
            }
        }
    }

    private fun observeCachedProducts(
        limit: Int,
    ) : Flow<ProductsListEvent> {
        return channelFlow {
            try {
                observeProductPreviewsUseCase(limit = limit)
                    .collect { products ->
                        send(
                            ProductsListEvent.CachedProductsLoaded(
                                products = products
                            )
                        )
                    }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                send(
                    ProductsListEvent.CachedProductsLoadingFailed(
                        message = exception.message ?: DEFAULT_CACHE_ERROR_MESSAGE,
                    )
                )
            }
        }
    }

    private fun refreshPage(
        command: ProductsListCommand.RefreshPage,
    ) : Flow<ProductsListEvent> {
        return channelFlow {
            try {
                val productsPage = refreshProductsPageUseCase(
                    limit = command.limit,
                    skip = command.skip,
                )

                send(
                    ProductsListEvent.PageRefreshed(
                        productsPage = productsPage,
                        isInitialLoading = command.isInitialLoading,
                    )
                )
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                send(ProductsListEvent.PageRefreshingFailed(
                    message = exception.message ?: DEFAULT_REFRESH_ERROR_MESSAGE,
                    isInitialLoading = command.isInitialLoading,
                ))
            }
        }
    }

    private companion object {
        const val DEFAULT_CACHE_ERROR_MESSAGE = "Не удалось загрузить товары из кэша"
        const val DEFAULT_REFRESH_ERROR_MESSAGE = "Не удалось обновить товары"
    }
}