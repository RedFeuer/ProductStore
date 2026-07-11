package com.example.productsStore.domain.useCase

import com.example.productsStore.domain.model.CartProductModel
import com.example.productsStore.domain.repository.ProductsRepository
import javax.inject.Inject

class GetProductsWithEnabledRemindersUseCase @Inject constructor(
    private val productsRepository: ProductsRepository,
) {
    suspend operator fun invoke(): List<CartProductModel> {
        return productsRepository.getProductsWithEnabledReminders()
    }
}