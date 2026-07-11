package com.example.productsStore.data.repositoryImpl.cart

import com.example.productsStore.data.local.dao.CartProductDao
import com.example.productsStore.data.local.mapper.CartProductEntityMapper
import com.example.productsStore.domain.model.CartProductModel
import com.example.productsStore.domain.model.ProductDetailsModel
import com.example.productsStore.domain.repository.cart.CartProductsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartProductsRepositoryImpl @Inject constructor(
    private val cartProductDao: CartProductDao,
    private val cartProductEntityMapper: CartProductEntityMapper,
) : CartProductsRepository {
    /** достаем товары с активным напоминанем */
    override suspend fun getProductsWithEnabledReminders(): List<CartProductModel> {
        return cartProductDao.getProductsWithEnabledReminders()
            .map { entity ->
                cartProductEntityMapper.toDomainModel(entity)
            }
    }

    /** обновление поля установки напоминания */
    override suspend fun setProductReminderEnabled(productId: Int, enabled: Boolean) {
        cartProductDao.updateReminderEnabled(
            productId = productId,
            enabled = enabled,
        )
    }

    override fun observeCartProducts(): Flow<List<CartProductModel>> {
        return cartProductDao.observeCartProducts()
            .map { entities ->
                entities.map { cartProductEntity -> cartProductEntityMapper.toDomainModel(cartProductEntity) }
            }
            .distinctUntilChanged()
    }

    override suspend fun addProductToCart(product: ProductDetailsModel) {
        cartProductDao.addProductToCart(
            productId = product.id,
            title = product.title,
            price = product.price,
            brand = product.brand,
        )
    }

    override suspend fun clearCart() {
        cartProductDao.clearCart()
    }
}