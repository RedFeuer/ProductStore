package com.example.productsStore.domain.useCase

import com.example.productsStore.domain.model.ProductsPageModel
import com.example.productsStore.domain.repository.productsList.ProductsListRepository
import javax.inject.Inject

class RefreshProductsPageUseCase @Inject constructor(
    private val productsListRepository: ProductsListRepository,
) {
    suspend operator fun invoke(limit: Int, skip: Int): ProductsPageModel {
        return productsListRepository.refreshProductsPage(limit, skip)
    }
}