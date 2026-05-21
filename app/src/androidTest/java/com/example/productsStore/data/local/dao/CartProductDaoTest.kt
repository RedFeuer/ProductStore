package com.example.productsStore.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.productsStore.data.local.database.ProductDatabase
import junit.framework.TestCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CartProductDaoTest {
    private lateinit var database: ProductDatabase
    private lateinit var cartProductDao: CartProductDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ProductDatabase::class.java,
        )
            .allowMainThreadQueries()
            .build()

        cartProductDao = database.cartProductDao()
    }

    @After
    fun clear() {
        database.close()
    }

    /* Первое добавление товара → создаётся новая запись. */
    @Test
    fun givenEmptyCartWhenAddProductThenCreateNewRecord() = runBlocking {
        // GIVEN
        val productId = 1
        val title = "Essence Mascara Lash Princess"
        val price = 9.99
        val brand = "Essence"

        // WHEN
        cartProductDao.addProductToCart(
            productId = productId,
            title = title,
            price = price,
            brand = brand,
        )

        val actual = cartProductDao.observeCartProducts().first()

        // THEN
        TestCase.assertEquals(1, actual.size)

        val cartProduct = actual.first()

        TestCase.assertEquals(productId, cartProduct.productId)
        TestCase.assertEquals(title, cartProduct.title)
        TestCase.assertEquals(price, cartProduct.price)
        TestCase.assertEquals(brand, cartProduct.brand)
        TestCase.assertEquals(1, cartProduct.quantity)
    }
}