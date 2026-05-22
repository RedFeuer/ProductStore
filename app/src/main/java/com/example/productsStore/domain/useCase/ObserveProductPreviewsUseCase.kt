package com.example.productsStore.domain.useCase

import com.example.productsStore.domain.model.ProductPreviewModel
import com.example.productsStore.domain.repository.ProductsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveProductPreviewsUseCase @Inject constructor(
    private val productsRepository: ProductsRepository,
) {
    suspend operator fun invoke(limit: Int) : Flow<List<ProductPreviewModel>> {
        return productsRepository.observeProductPreviews(limit)
    }
}