package com.example.productsStore.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.productsStore.data.local.entity.CartProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CartProductDao {
    /** достаем товары с активным напоминанем */
    @Query(
        """
            SELECT * FROM cart_products
            WHERE reminderEnabled = 1
        """
    )
    abstract suspend fun getProductsWithEnabledReminders(): List<CartProductEntity>

    /** обновление поля установки напоминания */
    @Query(
        """
            UPDATE cart_products
            SET reminderEnabled = :enabled
            WHERE productId = :productId
        """
    )
    abstract suspend fun updateReminderEnabled(productId: Int, enabled: Boolean)

    /** получение корзины товаров */
    @Query(
        """
            SELECT * FROM cart_products
            ORDER BY title
        """
    )
    abstract fun observeCartProducts(): Flow<List<CartProductEntity>>

    /** добавление товара в корзину */
    @Transaction
    open suspend fun addProductToCart(
        productId: Int,
        title: String,
        price: Double,
        brand: String?,
    ) {
        val currentQuantity = getQuantityByProductId(productId)

        if (currentQuantity == null) {
            insertCartProduct(
                CartProductEntity(
                    productId = productId,
                    title = title,
                    price = price,
                    brand = brand,
                    quantity = 1,
                )
            )
        } else {
            updateQuantity(
                productId = productId,
                quantity = currentQuantity + 1,
            )
        }
    }

    /** очистка корзины */
    @Query("DELETE FROM cart_products")
    abstract suspend fun clearCart()

    /** вспомогательный метод получения количества товаров в корзине */
    @Query(
        """
            SELECT quantity FROM cart_products
            WHERE productId = :productId
            LIMIT 1
        """
    )
    protected abstract suspend fun getQuantityByProductId(productId: Int) : Int?

    /** вспомогательный метод добавления товара в корзину, если его там не было */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract suspend fun insertCartProduct(cartProduct: CartProductEntity)

    /** вспомогательный метод изменения количества товаров в корзине, если он там был */
    @Query(
        """
            UPDATE cart_products
            SET quantity = :quantity
            WHERE productId = :productId
        """
    )
    protected abstract suspend fun updateQuantity(
        productId: Int,
        quantity: Int,
    )
}