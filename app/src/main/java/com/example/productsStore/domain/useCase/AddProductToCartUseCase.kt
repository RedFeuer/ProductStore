package com.example.productsStore.domain.useCase

import com.example.productsStore.domain.model.ProductPreviewModel
import com.example.productsStore.domain.repository.ProductsRepository
import javax.inject.Inject

class AddProductToCartUseCase @Inject constructor(
    private val productsRepository: ProductsRepository,
) {
    suspend operator fun invoke(product: ProductPreviewModel) {
        productsRepository.addProductToCart(product)
    }
}