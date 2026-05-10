package com.example.productsStore.data.remote.mapper

import com.example.productsStore.data.remote.dto.ProductsPageDto
import com.example.productsStore.domain.model.ProductsPageModel
import javax.inject.Inject

class ProductsPageMapper @Inject constructor (
    private val productPreviewMapper: ProductPreviewMapper,
) {
    fun toDomainModel(productsDto : ProductsPageDto) : ProductsPageModel {
        return ProductsPageModel(
            products = productsDto.products.map { productPreviewDto ->
                productPreviewMapper.toDomainModel(productPreviewDto)
            },
            total = productsDto.total,
            skip = productsDto.skip,
            limit = productsDto.limit,
        )
    }

    fun toDto(productsModel : ProductsPageModel) : ProductsPageDto {
        return ProductsPageDto(
            products = productsModel.products.map { productPreviewModel ->
                productPreviewMapper.toDto(productPreviewModel)
            },
            total = productsModel.total,
            skip = productsModel.skip,
            limit = productsModel.limit,
        )
    }
}