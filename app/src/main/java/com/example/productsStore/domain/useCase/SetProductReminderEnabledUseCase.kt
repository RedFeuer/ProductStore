package com.example.productsStore.domain.useCase

import com.example.productsStore.domain.repository.ProductsRepository
import javax.inject.Inject

class SetProductReminderEnabledUseCase @Inject constructor(
    private val productsRepository: ProductsRepository,
) {
    suspend operator fun invoke(productId: Int, enabled: Boolean) {
        productsRepository.setProductReminderEnabled(
            productId = productId,
            enabled = enabled,
        )
    }
}