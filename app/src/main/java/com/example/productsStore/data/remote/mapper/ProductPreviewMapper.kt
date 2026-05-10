package com.example.productsStore.data.remote.mapper

import com.example.productsStore.data.remote.dto.ProductPreviewDto
import com.example.productsStore.domain.model.ProductPreviewModel
import javax.inject.Inject

class ProductPreviewMapper @Inject constructor() {
    fun toDomainModel(previewDto: ProductPreviewDto) : ProductPreviewModel {
        return ProductPreviewModel(
            id = previewDto.id,
            title = previewDto.title,
            price = previewDto.price,
            brand =  previewDto.brand,
        )
    }

    fun toDto(previewModel : ProductPreviewModel) : ProductPreviewDto {
        return ProductPreviewDto(
            id = previewModel.id,
            title = previewModel.title,
            price = previewModel.price,
            brand =  previewModel.brand,
        )
    }
}