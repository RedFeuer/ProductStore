package com.example.productsStore.data.local.mapper

import com.example.productsStore.data.local.entity.CartProductEntity
import com.example.productsStore.domain.model.CartProductModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.Test

class CartProductsEntityMapperTest {
    @Test
    fun `GIVEN cart product entity WHEN map to domain THEN return cart product model`() {
        // GIVEN
        val mapper = CartProductEntityMapper()

        val entity = CartProductEntity(
            productId = 1,
            title = "Essence Mascara Lash Princess",
            price = 9.99,
            brand = "Essence",
            quantity = 3,
        )

        // WHEN
        val actual = mapper.toDomainModel(entity)

        // THEN
        assertEquals(1, actual.productId)
        assertEquals("Essence Mascara Lash Princess", actual.title)
        assertEquals(9.99, actual.price, 0.0)
        assertEquals("Essence", actual.brand)
        assertEquals(3, actual.quantity)
    }

    @Test
    fun `GIVEN cart product model WHEN map to entity THEN return cart product entity`() {
        // GIVEN
        val mapper = CartProductEntityMapper()

        val domain = CartProductModel(
            productId = 1,
            title = "Essence Mascara Lash Princess",
            price = 9.99,
            brand = "Essence",
            quantity = 3,
        )

        // WHEN
        val actual = mapper.toEntity(domain)

        // THEN
        assertEquals(1, actual.productId)
        assertEquals("Essence Mascara Lash Princess", actual.title)
        assertEquals(9.99, actual.price, 0.0)
        assertEquals("Essence", actual.brand)
        assertEquals(3, actual.quantity)
    }

    @Test
    fun `GIVEN cart item entity with null brand WHEN map to domain THEN return model with null brand`() {
        // GIVEN
        val mapper = CartProductEntityMapper()

        val entity = CartProductEntity(
            productId = 2,
            title = "Unknown Product",
            price = 15.0,
            brand = null,
            quantity = 1,
        )

        // WHEN
        val actual = mapper.toDomainModel(entity)

        // THEN
        assertEquals(2, actual.productId)
        assertEquals("Unknown Product", actual.title)
        assertEquals(15.0, actual.price, 0.0)
        assertNull(actual.brand)
        assertEquals(1, actual.quantity)
    }
}