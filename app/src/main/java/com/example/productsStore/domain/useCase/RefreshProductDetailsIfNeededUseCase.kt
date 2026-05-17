package com.example.productsStore.domain.useCase

import com.example.productsStore.domain.repository.ProductsRepository
import javax.inject.Inject

class RefreshProductDetailsIfNeededUseCase @Inject constructor(
    private val productsRepository: ProductsRepository,
) {
    suspend operator fun invoke(productId: Int) {
        productsRepository.refreshProductDetailsIfNeeded(productId)
    }
}