package com.example.productsStore.presentation.elm.cart

import com.example.productsStore.domain.useCase.ClearCartUseCase
import com.example.productsStore.domain.useCase.ObserveCartProductsUseCase
import com.example.productsStore.domain.useCase.SetProductReminderEnabledUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import ru.tinkoff.kotea.core.CommandsFlowHandler
import javax.inject.Inject

class CartCommandHandler @Inject constructor(
    private val observeCartProductsUseCase: ObserveCartProductsUseCase,
    private val clearCartUseCase: ClearCartUseCase,
    private val setProductReminderEnabledUseCase: SetProductReminderEnabledUseCase,
) : CommandsFlowHandler<CartCommand, CartEvent> {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun handle(commands: Flow<CartCommand>): Flow<CartEvent> {
        /* жесть крутой метод: Flow<CartCommand> -> Flow<Flow<CartEvent>> -> Flow<CartEvent> */
        return commands.flatMapMerge { command ->
            when (command) {
                CartCommand.ObserveCartProducts -> {
                    observeCartProducts()
                }

                CartCommand.ClearCart -> {
                    clearCart()
                }

                is CartCommand.SetProductReminderEnabled -> {
                    setProductReminderEnabled(command = command)
                }
            }
        }
    }

    private fun setProductReminderEnabled(
        command: CartCommand.SetProductReminderEnabled,
    ): Flow<CartEvent> {
        return flow {
            try {
                setProductReminderEnabledUseCase(
                    productId = command.productId,
                    enabled = command.enabled,
                )

                emit(CartEvent.ProductReminderChanged)
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                emit(
                    CartEvent.ProductReminderChangingFailed(
                        message = exception.message ?: DEFAULT_REMINDER_ERROR_MESSAGE,
                    )
                )
            }
        }
    }

    private fun observeCartProducts(): Flow<CartEvent> {
        return observeCartProductsUseCase()
            .map { productModels ->
                val event: CartEvent = CartEvent.CartProductsLoaded(
                    products = productModels,
                )

                event
            }
            .catch { exception ->
                if (exception is CancellationException) {
                    throw exception
                }

                emit(
                    CartEvent.CartProductsLoadingFailed(
                        message = exception.message ?: DEFAULT_LOAD_ERROR_MESSAGE
                    )
                )
            }
    }

    private fun clearCart(): Flow<CartEvent> {
        return flow {
            try {
                clearCartUseCase()
                emit(CartEvent.CartCleared)
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                emit(
                    CartEvent.CartClearingFailed(
                        message = exception.message ?: DEFAULT_CLEAR_ERROR_MESSAGE,
                    )
                )
            }
        }
    }

    private companion object {
        const val DEFAULT_REMINDER_ERROR_MESSAGE = "Не удалось изменить напоминание"
        const val DEFAULT_LOAD_ERROR_MESSAGE = "Не удалось загрузить корзину"
        const val DEFAULT_CLEAR_ERROR_MESSAGE = "Не удалось очистить корзину"
    }
}