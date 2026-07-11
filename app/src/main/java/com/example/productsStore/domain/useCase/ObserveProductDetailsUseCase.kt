package com.example.productsStore.domain.useCase

import com.example.productsStore.domain.model.CachedProductDetailsModel
import com.example.productsStore.domain.repository.ProductsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveProductDetailsUseCase @Inject constructor(
    private val productsRepository: ProductsRepository,
) {
    operator fun invoke(productId: Int): Flow<CachedProductDetailsModel?> {
        return productsRepository.observeProductDetails(productId)
    }
}