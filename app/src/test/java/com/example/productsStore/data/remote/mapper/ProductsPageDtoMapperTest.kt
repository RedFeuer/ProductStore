package com.example.productsStore.data.remote.mapper

import com.example.productsStore.data.remote.dto.ProductPreviewDto
import com.example.productsStore.data.remote.dto.ProductsPageDto
import com.example.productsStore.domain.model.ProductPreviewModel
import com.example.productsStore.domain.model.ProductsPageModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.Test

class ProductsPageDtoMapperTest {
    @Test
    fun `GIVEN products page dto WHEN map to domain THEN return products page model`() {
        // GIVEN
        val productPreviewDtoMapper = ProductPreviewDtoMapper()
        val mapper = ProductsPageDtoMapper(productPreviewDtoMapper = productPreviewDtoMapper)

        val dto = ProductsPageDto(
            products = listOf(
                ProductPreviewDto(
                    id = 1,
                    title = "Product 1",
                    price = 10.0,
                    brand = "Brand 1",
                ),
                ProductPreviewDto(
                    id = 2,
                    title = "Product 2",
                    price = 20.0,
                    brand = null,
                ),
            ),
            total = 100,
            skip = 20,
            limit = 10,
        )

        // WHEN
        val actual = mapper.toDomainModel(dto)

        // THEN
        assertEquals(2, actual.products.size)

        assertEquals(1, actual.products[0].id)
        assertEquals("Product 1", actual.products[0].title)
        assertEquals(10.0, actual.products[0].price, 0.0)
        assertEquals("Brand 1", actual.products[0].brand)

        assertEquals(2, actual.products[1].id)
        assertEquals("Product 2", actual.products[1].title)
        assertEquals(20.0, actual.products[1].price, 0.0)
        assertNull(actual.products[1].brand)

        assertEquals(100, actual.total)
        assertEquals(20, actual.skip)
        assertEquals(10, actual.limit)
    }

    @Test
    fun `GIVEN products page domain WHEN map to dto THEN return products page dto`() {
        // GIVEN
        val productPreviewDtoMapper = ProductPreviewDtoMapper()
        val mapper = ProductsPageDtoMapper(productPreviewDtoMapper = productPreviewDtoMapper)

        val domain = ProductsPageModel(
            products = listOf(
                ProductPreviewModel(
                    id = 1,
                    title = "Product 1",
                    price = 10.0,
                    brand = "Brand 1",
                ),
                ProductPreviewModel(
                    id = 2,
                    title = "Product 2",
                    price = 20.0,
                    brand = null,
                ),
            ),
            total = 100,
            skip = 20,
            limit = 10,
        )

        // WHEN
        val actual = mapper.toDto(domain)

        // THEN
        assertEquals(2, actual.products.size)

        assertEquals(1, actual.products[0].id)
        assertEquals("Product 1", actual.products[0].title)
        assertEquals(10.0, actual.products[0].price, 0.0)
        assertEquals("Brand 1", actual.products[0].brand)

        assertEquals(2, actual.products[1].id)
        assertEquals("Product 2", actual.products[1].title)
        assertEquals(20.0, actual.products[1].price, 0.0)
        assertNull(actual.products[1].brand)

        assertEquals(100, actual.total)
        assertEquals(20, actual.skip)
        assertEquals(10, actual.limit)
    }
}