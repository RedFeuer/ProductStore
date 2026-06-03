package com.example.productsStore.domain.useCase

import com.example.productsStore.domain.repository.productDetails.ProductDetailsRepository
import javax.inject.Inject

class RefreshProductDetailsIfNeededUseCase @Inject constructor(
    private val productDetailsRepository: ProductDetailsRepository,
) {
    suspend operator fun invoke(productId: Int) {
        productDetailsRepository.refreshProductDetailsIfNeeded(productId)
    }
}