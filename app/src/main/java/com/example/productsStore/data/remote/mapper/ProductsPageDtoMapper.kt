package com.example.productsStore.data.remote.mapper

import com.example.productsStore.data.remote.dto.ProductsPageDto
import com.example.productsStore.domain.model.ProductsPageModel
import javax.inject.Inject

class ProductsPageDtoMapper @Inject constructor (
    private val productPreviewDtoMapper: ProductPreviewDtoMapper,
) {
    fun toDomainModel(productsDto : ProductsPageDto) : ProductsPageModel {
        return ProductsPageModel(
            products = productsDto.products.map { productPreviewDto ->
                productPreviewDtoMapper.toDomainModel(productPreviewDto)
            },
            total = productsDto.total,
            skip = productsDto.skip,
            limit = productsDto.limit,
        )
    }

    fun toDto(productsModel : ProductsPageModel) : ProductsPageDto {
        return ProductsPageDto(
            products = productsModel.products.map { productPreviewModel ->
                productPreviewDtoMapper.toDto(productPreviewModel)
            },
            total = productsModel.total,
            skip = productsModel.skip,
            limit = productsModel.limit,
        )
    }
}