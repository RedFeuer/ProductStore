package com.example.productsStore.data.remote.mapper

import com.example.productsStore.data.remote.dto.ProductDetailsDto
import com.example.productsStore.domain.model.ProductDetailsModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.Test

class ProductDetailsDtoMapperTest {
    @Test
    fun `GIVEN product details dto WHEN map to domain THEN return product details model`() {
        // GIVEN
        val mapper = ProductDetailsDtoMapper()

        val dto = ProductDetailsDto(
            id = 1,
            title = "Essence Mascara Lash Princess",
            price = 9.99,
            brand = "Essence",
            description = "Popular mascara",
            rating = 4.94,
            weight = 2,
            availabilityStatus = "In Stock",
            warrantyInformation = "1 week warranty",
            thumbnail = "https://example.com/thumbnail.png",
            images = listOf("https://example.com/image1.png"),
        )

        // WHEN
        val actual = mapper.toDomainModel(dto)

        //THEN
        assertEquals(1, actual.id)
        assertEquals("Essence Mascara Lash Princess", actual.title)
        assertEquals(9.99, actual.price, 0.0)
        assertEquals("Essence", actual.brand)
        assertEquals("Popular mascara", actual.description)
        assertEquals(4.94, actual.rating, 0.0)
        assertEquals(2, actual.weight)
        assertEquals("In Stock", actual.availabilityStatus)
        assertEquals("1 week warranty", actual.warrantyInformation)
        assertEquals("https://example.com/thumbnail.png", actual.imageUrl)
    }

    @Test
    fun `GIVEN product details model WHEN map to dto THEN return product details dto`() {
        // GIVEN
        val mapper = ProductDetailsDtoMapper()

        val domain = ProductDetailsModel(
            id = 1,
            title = "Essence Mascara Lash Princess",
            price = 9.99,
            brand = "Essence",
            description = "Popular mascara",
            rating = 4.94,
            weight = 2,
            availabilityStatus = "In Stock",
            warrantyInformation = "1 week warranty",
            imageUrl = "https://example.com/image1.png",
        )

        // WHEN
        val actual = mapper.toDto(domain)

        //THEN
        assertEquals(1, actual.id)
        assertEquals("Essence Mascara Lash Princess", actual.title)
        assertEquals(9.99, actual.price, 0.0)
        assertEquals("Essence", actual.brand)
        assertEquals("Popular mascara", actual.description)
        assertEquals(4.94, actual.rating, 0.0)
        assertEquals(2, actual.weight)
        assertEquals("In Stock", actual.availabilityStatus)
        assertEquals("1 week warranty", actual.warrantyInformation)
    }

    @Test
    fun `GIVEN dto without thumbnail WHEN map to domain THEN use first image as image url`() {
        // GIVEN
        val mapper = ProductDetailsDtoMapper()

        val dto = ProductDetailsDto(
            id = 1,
            title = "Product",
            price = 10.0,
            brand = "Brand",
            description = "Description",
            rating = 4.5,
            weight = 3,
            availabilityStatus = "In Stock",
            warrantyInformation = "Warranty",
            thumbnail = null,
            images = listOf("https://example.com/image1.png"),
        )

        // WHEN
        val actual = mapper.toDomainModel(dto)

        // THEN
        assertEquals("https://example.com/image1.png", actual.imageUrl)
    }

    @Test
    fun `GIVEN dto without thumbnail and images WHEN map to domain THEN image url is null`() {
        // GIVEN
        val mapper = ProductDetailsDtoMapper()

        val dto = ProductDetailsDto(
            id = 1,
            title = "Product",
            price = 10.0,
            brand = "Brand",
            description = "Description",
            rating = 4.5,
            weight = 3,
            availabilityStatus = "In Stock",
            warrantyInformation = "Warranty",
            thumbnail = null,
            images = emptyList(),
        )

        // WHEN
        val actual = mapper.toDomainModel(dto)

        // THEN
        assertNull(actual.imageUrl)
    }
}