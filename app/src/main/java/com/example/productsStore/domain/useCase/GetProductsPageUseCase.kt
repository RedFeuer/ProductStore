package com.example.productsStore.domain.useCase

import com.example.productsStore.domain.model.ProductsPageModel
import com.example.productsStore.domain.repository.ProductsRepository
import javax.inject.Inject

class GetProductsPageUseCase @Inject constructor(
    private val productsRepository: ProductsRepository,
) {
    suspend operator fun invoke(limit : Int, skip : Int) : ProductsPageModel {
        return productsRepository.getProductsPage(
            limit = limit,
            skip = skip,
        )
    }
}