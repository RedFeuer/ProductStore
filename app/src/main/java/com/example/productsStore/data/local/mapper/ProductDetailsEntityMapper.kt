package com.example.productsStore.data.local.mapper

import com.example.productsStore.data.local.entity.ProductDetailsEntity
import com.example.productsStore.domain.model.ProductDetailsModel
import javax.inject.Inject

class ProductDetailsEntityMapper @Inject constructor() {
    fun toDomainModel(detailsEntity : ProductDetailsEntity) : ProductDetailsModel {
        return ProductDetailsModel(
            id = detailsEntity.id,
            title = detailsEntity.title,
            price = detailsEntity.price,
            brand =  detailsEntity.brand,
            description = detailsEntity.description,
            rating = detailsEntity.rating,
            weight = detailsEntity.weight,
            availabilityStatus = detailsEntity.availabilityStatus,
            warrantyInformation = detailsEntity.warrantyInformation,
            imageUrl = detailsEntity.imageUrl,
        )
    }

    fun toEntity(detailsModel : ProductDetailsModel, loadedAtMillis: Long) : ProductDetailsEntity {
        return ProductDetailsEntity(
            id = detailsModel.id,
            title = detailsModel.title,
            price = detailsModel.price,
            brand =  detailsModel.brand,
            description = detailsModel.description,
            rating = detailsModel.rating,
            weight = detailsModel.weight,
            availabilityStatus = detailsModel.availabilityStatus,
            warrantyInformation = detailsModel.warrantyInformation,
            imageUrl = detailsModel.imageUrl,
            loadedAtMillis = loadedAtMillis,
        )
    }
}