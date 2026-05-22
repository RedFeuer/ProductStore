package com.example.productsStore.data.mapperChain

import com.example.productsStore.data.local.mapper.ProductDetailsEntityMapper
import com.example.productsStore.data.remote.dto.ProductDetailsDto
import com.example.productsStore.data.remote.mapper.ProductDetailsDtoMapper
import junit.framework.TestCase.assertEquals
import org.junit.Test

class ProductDetailsMappingChainTest {
    @Test
    fun `GIVEN product details dto WHEN map dto to domain to entity to domain THEN check if dto to domain and entity to domain are the same`() {
        // GIVEN
        val dtoMapper = ProductDetailsDtoMapper()
        val entityMapper = ProductDetailsEntityMapper()

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
            images = listOf("https://example.com/image.png"),
        )

        // WHEN
        val domainFromDto = dtoMapper.toDomainModel(dto)
        val loadedAtMillis = 123_456L
        val entity = entityMapper.toEntity(
            detailsModel = domainFromDto,
            loadedAtMillis = loadedAtMillis,
        )

        val domainFromEntity = entityMapper.toDomainModel(entity)

        // THEN
        assertEquals(domainFromDto.id, domainFromEntity.id)
        assertEquals(domainFromDto.title, domainFromEntity.title)
        assertEquals(domainFromDto.price, domainFromEntity.price, 0.0)
        assertEquals(domainFromDto.brand, domainFromEntity.brand)
        assertEquals(domainFromDto.description, domainFromEntity.description)
        assertEquals(domainFromDto.rating, domainFromEntity.rating, 0.0)
        assertEquals(domainFromDto.weight, domainFromEntity.weight)
        assertEquals(domainFromDto.availabilityStatus, domainFromEntity.availabilityStatus)
        assertEquals(domainFromDto.warrantyInformation, domainFromEntity.warrantyInformation)
        assertEquals(domainFromDto.imageUrl, domainFromEntity.imageUrl)
    }
}