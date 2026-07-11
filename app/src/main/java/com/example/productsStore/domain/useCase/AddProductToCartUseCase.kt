package com.example.productsStore.domain.useCase

import com.example.productsStore.domain.model.ProductDetailsModel
import com.example.productsStore.domain.repository.cart.CartProductsRepository
import javax.inject.Inject

class AddProductToCartUseCase @Inject constructor(
    private val cartProductsRepository: CartProductsRepository,
) {
    suspend operator fun invoke(product: ProductDetailsModel) {
        cartProductsRepository.addProductToCart(product)
    }
}