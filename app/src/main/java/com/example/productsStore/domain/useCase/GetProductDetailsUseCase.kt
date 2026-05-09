package com.example.productsStore.domain.useCase

import com.example.productsStore.domain.model.ProductDetailsModel
import com.example.productsStore.domain.repository.ProductsRepository

class GetProductDetailsUseCase(
    private val productsRepository: ProductsRepository,
) {
    suspend operator fun invoke(id : Int) : ProductDetailsModel {
        return productsRepository.getProductsDetails(id)
    }
}