package com.example.productsStore.domain.useCase

import com.example.productsStore.domain.model.ProductsPageModel
import com.example.productsStore.domain.repository.ProductsRepository
import javax.inject.Inject

class RefreshProductsPageUseCase @Inject constructor(
    private val productsRepository: ProductsRepository,
) {
    suspend operator fun invoke(limit: Int, skip: Int) : ProductsPageModel {
        return productsRepository.refreshProductsPage(limit, skip)
    }
}