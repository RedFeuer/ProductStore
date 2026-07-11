package com.example.productsStore.domain.useCase

import com.example.productsStore.domain.model.CachedProductDetailsModel
import com.example.productsStore.domain.repository.productDetails.ProductDetailsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveProductDetailsUseCase @Inject constructor(
    private val productDetailsRepository: ProductDetailsRepository,
) {
    operator fun invoke(productId: Int): Flow<CachedProductDetailsModel?> {
        return productDetailsRepository.observeProductDetails(productId)
    }
}