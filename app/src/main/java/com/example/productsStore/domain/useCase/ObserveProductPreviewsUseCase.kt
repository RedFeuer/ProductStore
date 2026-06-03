package com.example.productsStore.domain.useCase

import com.example.productsStore.domain.model.ProductPreviewModel
import com.example.productsStore.domain.repository.productsList.ProductsListRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveProductPreviewsUseCase @Inject constructor(
    private val productsListRepository: ProductsListRepository,
) {
    suspend operator fun invoke(limit: Int): Flow<List<ProductPreviewModel>> {
        return productsListRepository.observeProductPreviews(limit)
    }
}