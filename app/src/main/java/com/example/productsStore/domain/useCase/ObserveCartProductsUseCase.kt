package com.example.productsStore.domain.useCase

import com.example.productsStore.domain.model.CartProductModel
import com.example.productsStore.domain.repository.cart.CartProductsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCartProductsUseCase @Inject constructor(
    private val cartProductsRepository: CartProductsRepository,
) {
    operator fun invoke(): Flow<List<CartProductModel>> {
        return cartProductsRepository.observeCartProducts()
    }
}