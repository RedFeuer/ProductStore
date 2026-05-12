package com.example.productsStore.data.local.mapper

import com.example.productsStore.data.local.entity.ProductPreviewEntity
import com.example.productsStore.domain.model.ProductPreviewModel
import javax.inject.Inject

class ProductPreviewEntityMapper @Inject constructor() {
    fun toDomainModel(previewEntity: ProductPreviewEntity) : ProductPreviewModel {
        return ProductPreviewModel(
            id = previewEntity.id,
            title = previewEntity.title,
            price = previewEntity.price,
            brand =  previewEntity.brand,
        )
    }

    fun toEntity(previewModel : ProductPreviewModel) : ProductPreviewEntity {
        return ProductPreviewEntity(
            id = previewModel.id,
            title = previewModel.title,
            price = previewModel.price,
            brand =  previewModel.brand,
        )
    }
}