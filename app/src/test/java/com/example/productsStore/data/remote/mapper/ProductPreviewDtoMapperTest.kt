package com.example.productsStore.data.remote.mapper

import com.example.productsStore.data.remote.dto.ProductPreviewDto
import junit.framework.TestCase.assertEquals
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
        assertEquals(12.99, actual.price)
        assertEquals("Chic Cosmetics", actual.brand)
    }
}