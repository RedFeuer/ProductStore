package com.example.productsStore.presentation.elm.cart

import com.example.productsStore.domain.model.CartProductModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test

        // GIVEN

        // WHEN

        // THEN

class CartUpdateTest {
    private val update = CartUpdate()

    @Test
    fun `GIVEN cart products loaded WHEN update THEN state is success`() {
        // GIVEN
        val initialState = CartState.Loading

        val products = listOf(
            createCartProductModel(
                productId = 1,
                title = "Product 1",
                quantity = 2,
            )
        )

        val event = CartEvent.CartProductsLoaded(
            products = products,
        )

        // WHEN
        val actual = update.update(
            state = initialState,
            event = event,
        )

        // THEN
        val actualState = actual.state as CartState.Success

        assertEquals(products, actualState.products)
        assertTrue(actual.commands.isEmpty())
        assertTrue(actual.news.isEmpty())
    }

    @Test
    fun `GIVEN empty cart products loaded WHEN update THEN state is empty`() {
        // GIVEN
        val initialState = CartState.Loading

        val event = CartEvent.CartProductsLoaded(
            products = emptyList()
        )

        // WHEN
        val actual = update.update(
            state = initialState,
            event = event,
        )

        // THEN
        assertEquals(CartState.Empty, actual.state)
        assertTrue(actual.commands.isEmpty())
        assertTrue(actual.news.isEmpty())
    }

    @Test
    fun `GIVEN clear cart intent WHEN clear cart command THEN cart cleared command generated`() {
        // GIVEN
        val initialState = CartState.Success(
            products = listOf(createCartProductModel())
        )

        val event = CartEvent.UserIntent(
            intent = CartIntent.ClearCartClicked
        )

        // WHEN
        val actual = update.update(
            state = initialState,
            event = event,
        )

        // THEN
        assertEquals(initialState, actual.state)
        assertEquals(1, actual.commands.size)
        assertEquals(CartCommand.ClearCart, actual.commands.first())
        assertTrue(actual.news.isEmpty())
    }



    private fun createCartProductModel(
        productId: Int = 1,
        title: String = "Product",
        price: Double = 10.0,
        brand: String? = "Brand",
        quantity: Int = 1,
    ) : CartProductModel {
        return CartProductModel(
            productId = productId,
            title = title,
            price = price,
            brand = brand,
            quantity = quantity,
        )
    }
}