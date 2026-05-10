package com.example.productsStore.domain.useCase

import com.example.productsStore.domain.model.ProductDetailsModel
import com.example.productsStore.domain.repository.ProductsRepository
import javax.inject.Inject

class GetProductDetailsUseCase @Inject constructor(
    private val productsRepository: ProductsRepository,
) {
    suspend operator fun invoke(id : Int) : ProductDetailsModel {
        return productsRepository.getProductsDetails(id)
    }
}