package com.example.productsStore.data.local.mapper

import com.example.productsStore.data.local.entity.ProductDetailsEntity
import com.example.productsStore.domain.model.ProductDetailsModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.Test

class ProductDetailsEntityMapperTest {
    @Test
    fun `GIVEN product details entity WHEN map to domain THEN return product details model`() {
        // GIVEN
        val mapper = ProductDetailsEntityMapper()

        val entity = ProductDetailsEntity(
            id = 1,
            title = "Essence Mascara Lash Princess",
            price = 9.99,
            brand = "Essence",
            description = "Popular mascara",
            rating = 4.94,
            weight = 2,
            availabilityStatus = "In Stock",
            warrantyInformation = "1 week warranty",
            imageUrl = "https://example.com/image.png",
            loadedAtMillis = 123_456L,
        )

        // WHEN
        val actual = mapper.toDomainModel(entity)

        // THEN
        assertEquals(1, actual.id)
        assertEquals("Essence Mascara Lash Princess", actual.title)
        assertEquals(9.99, actual.price, 0.0)
        assertEquals("Essence", actual.brand)
        assertEquals("Popular mascara", actual.description)
        assertEquals(4.94, actual.rating, 0.0)
        assertEquals(2, actual.weight)
        assertEquals("In Stock", actual.availabilityStatus)
        assertEquals("1 week warranty", actual.warrantyInformation)
        assertEquals("https://example.com/image.png", actual.imageUrl)
    }

    @Test
    fun `GIVEN product details model WHEN map to entity THEN return product details entity`() {
        // GIVEN
        val mapper = ProductDetailsEntityMapper()

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
            imageUrl = "https://example.com/image.png",
        )

        // WHEN
        val actual = mapper.toEntity(detailsModel = domain, loadedAtMillis = 123_456L)

        // THEN
        assertEquals(1, actual.id)
        assertEquals("Essence Mascara Lash Princess", actual.title)
        assertEquals(9.99, actual.price, 0.0)
        assertEquals("Essence", actual.brand)
        assertEquals("Popular mascara", actual.description)
        assertEquals(4.94, actual.rating, 0.0)
        assertEquals(2, actual.weight)
        assertEquals("In Stock", actual.availabilityStatus)
        assertEquals("1 week warranty", actual.warrantyInformation)
        assertEquals("https://example.com/image.png", actual.imageUrl)
        assertEquals(123_456L, actual.loadedAtMillis)
    }

    @Test
    fun `GIVEN product details entity with null brand and image WHEN map to domain THEN return model with null fields`() {
        // GIVEN
        val mapper = ProductDetailsEntityMapper()

        val entity = ProductDetailsEntity(
            id = 2,
            title = "Product",
            price = 15.0,
            brand = null,
            description = "Description",
            rating = 4.5,
            weight = 3,
            availabilityStatus = "In Stock",
            warrantyInformation = "Warranty",
            imageUrl = null,
            loadedAtMillis = 123_456L,
        )

        // WHEN
        val actual = mapper.toDomainModel(entity)

        // THEN
        assertEquals(2, actual.id)
        assertEquals("Product", actual.title)
        assertEquals(15.0, actual.price, 0.0)
        assertNull(actual.brand)
        assertEquals("Description", actual.description)
        assertEquals(4.5, actual.rating, 0.0)
        assertEquals(3, actual.weight)
        assertEquals("In Stock", actual.availabilityStatus)
        assertEquals("Warranty", actual.warrantyInformation)
        assertNull(actual.imageUrl)
    }

    @Test
    fun `GIVEN fresh product details entity WHEN map to cached domain THEN return not stale model`() {
        // GIVEN
        val mapper = ProductDetailsEntityMapper()

        val entity = ProductDetailsEntity(
            id = 1,
            title = "Product",
            price = 10.0,
            brand = "Brand",
            description = "Description",
            rating = 4.5,
            weight = 3,
            availabilityStatus = "In Stock",
            warrantyInformation = "Warranty",
            imageUrl = "https://example.com/image.png",
            loadedAtMillis = 1_000L,
        )

        val currentTimeMillis = 2_000L
        val cacheTtlMillis = 24 * 60 * 60 * 1000L

        // WHEN
        val actual = mapper.toCachedDomainModel(
            detailsEntity = entity,
            currentTimeMillis = currentTimeMillis,
            cacheTtlMillis = cacheTtlMillis,
        )

        // THEN
        assertEquals(false, actual.isStale)
        assertEquals(1, actual.product.id)
        assertEquals("Product", actual.product.title)
    }

    @Test
    fun `GIVEN stale product details entity WHEN map to cached domain THEN return stale model`() {
        // GIVEN
        val mapper = ProductDetailsEntityMapper()

        val entity = ProductDetailsEntity(
            id = 1,
            title = "Product",
            price = 10.0,
            brand = "Brand",
            description = "Description",
            rating = 4.5,
            weight = 3,
            availabilityStatus = "In Stock",
            warrantyInformation = "Warranty",
            imageUrl = "https://example.com/image.png",
            loadedAtMillis = 1_000L,
        )

        val cacheTtlMillis = 24 * 60 * 60 * 1000L
        val currentTimeMillis = entity.loadedAtMillis + cacheTtlMillis + 1L

        // WHEN
        val actual = mapper.toCachedDomainModel(
            detailsEntity = entity,
            currentTimeMillis = currentTimeMillis,
            cacheTtlMillis = cacheTtlMillis,
        )

        // THEN
        assertEquals(true, actual.isStale)
        assertEquals(1, actual.product.id)
        assertEquals("Product", actual.product.title)
    }
}