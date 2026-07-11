package com.example.productsStore.domain.useCase

import com.example.productsStore.domain.model.CartProductModel
import com.example.productsStore.domain.repository.ProductsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCartProductsUseCase @Inject constructor(
    private val productsRepository: ProductsRepository,
) {
    operator fun invoke(): Flow<List<CartProductModel>> {
        return productsRepository.observeCartProducts()
    }
}