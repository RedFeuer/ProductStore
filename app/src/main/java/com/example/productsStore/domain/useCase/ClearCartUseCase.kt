package com.example.productsStore.domain.useCase

import com.example.productsStore.domain.repository.cart.CartProductsRepository
import javax.inject.Inject

class ClearCartUseCase @Inject constructor(
    private val cartProductsRepository: CartProductsRepository,
) {
    suspend operator fun invoke() {
        cartProductsRepository.clearCart()
    }
}