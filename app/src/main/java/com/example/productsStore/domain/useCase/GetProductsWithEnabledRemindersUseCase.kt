package com.example.productsStore.domain.useCase

import com.example.productsStore.domain.model.CartProductModel
import com.example.productsStore.domain.repository.cart.CartProductsRepository
import javax.inject.Inject

class GetProductsWithEnabledRemindersUseCase @Inject constructor(
    private val cartProductsRepository: CartProductsRepository,
) {
    suspend operator fun invoke(): List<CartProductModel> {
        return cartProductsRepository.getProductsWithEnabledReminders()
    }
}