package com.example.productsStore.data.remote.mapper

import com.example.productsStore.data.remote.dto.ProductDetailsDto
import com.example.productsStore.domain.model.ProductDetailsModel
import javax.inject.Inject

class ProductDetailsDtoMapper @Inject constructor() {
    fun toDomainModel(detailsDto : ProductDetailsDto) : ProductDetailsModel {
        return ProductDetailsModel(
            id = detailsDto.id,
            title = detailsDto.title,
            price = detailsDto.price,
            brand =  detailsDto.brand,
            description = detailsDto.description,
            rating = detailsDto.rating,
            weight = detailsDto.weight,
            availabilityStatus = detailsDto.availabilityStatus,
            warrantyInformation = detailsDto.warrantyInformation,
        )
    }

    fun toDto(detailsModel : ProductDetailsModel) : ProductDetailsDto {
        return ProductDetailsDto(
            id = detailsModel.id,
            title = detailsModel.title,
            price = detailsModel.price,
            brand =  detailsModel.brand,
            description = detailsModel.description,
            rating = detailsModel.rating,
            weight = detailsModel.weight,
            availabilityStatus = detailsModel.availabilityStatus,
            warrantyInformation = detailsModel.warrantyInformation,
        )
    }
}