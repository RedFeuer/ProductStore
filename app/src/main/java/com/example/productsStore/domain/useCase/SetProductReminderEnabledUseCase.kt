package com.example.productsStore.domain.useCase

import com.example.productsStore.domain.repository.cart.CartProductsRepository
import javax.inject.Inject

class SetProductReminderEnabledUseCase @Inject constructor(
    private val cartProductsRepository: CartProductsRepository,
) {
    suspend operator fun invoke(productId: Int, enabled: Boolean) {
        cartProductsRepository.setProductReminderEnabled(
            productId = productId,
            enabled = enabled,
        )
    }
}