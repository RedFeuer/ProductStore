package com.example.productsStore.data.remote.mapper

import com.example.productsStore.data.remote.dto.ProductPreviewDto
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.Test

class ProductPreviewDtoMapperTest {
    @Test
    fun `GIVEN ProductPreviewDto WHEN map to domain THEN ProductPreviewModel`() {
        // GIVEN
        val mapper = ProductPreviewDtoMapper()

        val dto = ProductPreviewDto(
            id = 10,
            title = "Red Lipstick",
            price = 12.99,
            brand = "Chic Cosmetics",
        )

        // WHEN
        val actual = mapper.toDomainModel(dto)

        // THEN
        assertEquals(10, actual.id)
        assertEquals("Red Lipstick", actual.title)
        assertEquals(12.99, actual.price, 0.0)
        assertEquals("Chic Cosmetics", actual.brand)
    }

    @Test
    fun `GIVEN product preview dto with null brand WHEN map to domain THEN return model with null brand`() {
        // GIVEN
        val mapper = ProductPreviewDtoMapper()

        val dto = ProductPreviewDto(
            id = 11,
            title = "Unknown Product",
            price = 15.0,
            brand = null,
        )

        // WHEN
        val actual = mapper.toDomainModel(dto)

        // THEN
        assertEquals(11, actual.id)
        assertEquals("Unknown Product", actual.title)
        assertEquals(15.0, actual.price, 0.0)
        assertNull(actual.brand)
    }
}