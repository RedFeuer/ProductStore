package com.example.productsStore.presentation.elm.productDetails

import com.example.productsStore.domain.useCase.AddProductToCartUseCase
import com.example.productsStore.domain.useCase.ObserveProductDetailsUseCase
import com.example.productsStore.domain.useCase.RefreshProductDetailsIfNeededUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import ru.tinkoff.kotea.core.CommandsFlowHandler
import javax.inject.Inject

class ProductDetailsCommandHandler @Inject constructor(
    private val observeProductsDetailsUseCase: ObserveProductDetailsUseCase,
    private val refreshProductDetailsIfNeededUseCase: RefreshProductDetailsIfNeededUseCase,
    private val addProductToCartUseCase: AddProductToCartUseCase,
) : CommandsFlowHandler<ProductDetailsCommand, ProductDetailsEvent> {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun handle(commands: Flow<ProductDetailsCommand>): Flow<ProductDetailsEvent> {
        return commands.flatMapMerge { command ->
            when (command) {
                is ProductDetailsCommand.ObserveCachedProductDetails -> {
                    observeCachedProductDetails(
                        productId = command.productId,
                    )
                }

                is ProductDetailsCommand.RefreshProductDetailsIfNeeded -> {
                    refreshProductDetailsIfNeeded(
                        productId = command.productId,
                    )
                }

                is ProductDetailsCommand.AddProductToCart -> {
                    addProductToCart(
                        command = command,
                    )
                }
            }
        }
    }

    private fun observeCachedProductDetails(
        productId: Int,
    ) : Flow<ProductDetailsEvent> {
        return observeProductsDetailsUseCase(productId = productId)
            .map { cachedProductDetailsModel ->
                val event: ProductDetailsEvent =
                    ProductDetailsEvent.CachedProductDetailsLoaded(
                        cachedProductDetails = cachedProductDetailsModel,
                    )

                event
            }
            .catch { exception ->
                if (exception is CancellationException) {
                    throw exception
                }

                emit(
                    ProductDetailsEvent.CachedProductDetailsLoadingFailed(
                        message = exception.message ?: DEFAULT_CACHE_ERROR_MESSAGE,
                    )
                )
            }
    }

    private fun refreshProductDetailsIfNeeded(
        productId: Int,
    ): Flow<ProductDetailsEvent> {
        return flow {
            try {
                refreshProductDetailsIfNeededUseCase(
                    productId = productId,
                )

                emit(ProductDetailsEvent.ProductDetailsRefreshed)
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                emit(
                    ProductDetailsEvent.ProductDetailsRefreshingFailed(
                        message = exception.message ?: DEFAULT_REFRESH_ERROR_MESSAGE,
                    )
                )
            }
        }
    }

    private fun addProductToCart(
        command: ProductDetailsCommand.AddProductToCart,
    ): Flow<ProductDetailsEvent> {
        return flow {
            try {
                addProductToCartUseCase(
                    product = command.product,
                )

                emit(ProductDetailsEvent.ProductAddedToCart)
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                emit(
                    ProductDetailsEvent.ProductAddingToCartFailed(
                        message = exception.message ?: DEFAULT_ADD_TO_CART_ERROR_MESSAGE,
                    )
                )
            }
        }
    }

    private companion object {
        const val DEFAULT_CACHE_ERROR_MESSAGE = "Не удалось загрузить товар из кэша"
        const val DEFAULT_REFRESH_ERROR_MESSAGE = "Не удалось обновить товар"
        const val DEFAULT_ADD_TO_CART_ERROR_MESSAGE = "Не удалось добавить товар в корзину"
    }
}