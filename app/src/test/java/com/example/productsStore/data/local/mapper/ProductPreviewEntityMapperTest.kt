package com.example.productsStore.data.local.mapper

import com.example.productsStore.data.local.entity.ProductPreviewEntity
import com.example.productsStore.domain.model.ProductPreviewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.Test

class ProductPreviewEntityMapperTest {
    @Test
    fun `GIVEN product preview entity WHEN map to domain THEN return product preview model`() {
        // GIVEN
        val mapper = ProductPreviewEntityMapper()

        val entity = ProductPreviewEntity(
            id = 5,
            title = "Red Nail Polish",
            price = 8.99,
            brand = "Nail Couture",
        )

        // WHEN
        val actual = mapper.toDomainModel(entity)

        // THEN
        assertEquals(5, actual.id)
        assertEquals("Red Nail Polish", actual.title)
        assertEquals(8.99, actual.price, 0.0)
        assertEquals("Nail Couture", actual.brand)
    }

    @Test
    fun `GIVEN product preview model WHEN map to entity THEN return product preview entity`() {
        // GIVEN
        val mapper = ProductPreviewEntityMapper()

        val domain = ProductPreviewModel(
            id = 5,
            title = "Red Nail Polish",
            price = 8.99,
            brand = "Nail Couture",
        )

        // WHEN
        val actual = mapper.toEntity(domain)

        // THEN
        assertEquals(5, actual.id)
        assertEquals("Red Nail Polish", actual.title)
        assertEquals(8.99, actual.price, 0.0)
        assertEquals("Nail Couture", actual.brand)
    }

    @Test
    fun `GIVEN product preview entity with null brand WHEN map to domain THEN return model with null brand`() {
        // GIVEN
        val mapper = ProductPreviewEntityMapper()

        val entity = ProductPreviewEntity(
            id = 6,
            title = "Unknown Product",
            price = 5.99,
            brand = null,
        )

        // WHEN
        val actual = mapper.toDomainModel(entity)

        // THEN
        assertEquals(6, actual.id)
        assertEquals("Unknown Product", actual.title)
        assertEquals(5.99, actual.price, 0.0)
        assertNull(actual.brand)
    }
}